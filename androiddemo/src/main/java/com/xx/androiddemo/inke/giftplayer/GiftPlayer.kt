package com.xx.androiddemo.inke.giftplayer

import android.app.AlertDialog
import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.opengl.EGLSurface
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Surface
import android.view.TextureView
import com.xx.avlibrary.gl.egl.EglCore
import java.io.IOException

internal class GiftPlayer {
    private val TAG = "GiftPlayer"

    private val STATE_ERROR = -1
    private val SURFACE_CREATE = 1000
    private val SURFACE_CHANGE = 1001
    private val SURFACE_CLEAR = 1002
    private val SURFACE_DESTROY = 1003
    private val QUIT = 1004

    private val STATE_IDLE = 0
    private val STATE_PREPARING = 1
    private val STATE_PREPARED = 2
    private val STATE_PLAYING = 3
    private val STATE_PAUSE = 4
    private val STATE_COMPLETED = 5

    @Volatile
    private var currentState = STATE_IDLE
    @Volatile
    private var targetState = STATE_IDLE

    private var glThread: HandlerThread? = null
    private var glHandler: GLHandler? = null
    private var mainHandler: Handler? = null

    @Volatile
    private var isPreserveEGLOnPause = false

    private var rendererView: TextureView? = null
    private var renderer: GiftRenderer? = null
    private var textureId = -1
    private var mediaPlayer: MediaPlayer? = null
    private var mediaPlayerTexture: SurfaceTexture? = null
    private var surfaceListener: TextureView.SurfaceTextureListener? = null

    @Volatile
    private var videoUri: Uri? = null
    @Volatile
    private var videoUriBSet = false
    private var videoWidth = 0
    private var videoHeight = 0
    private var videoRotation = 0
    private val textureMatrix = FloatArray(16)

    @Volatile
    private var isOnResume = false
    private var isOnPause = false
    private var isQuit = false

    private val rendererData = ExtraData(0, 0, 0, textureMatrix)

    private val completionRunnable: Runnable by lazy {
        Runnable { completionListener?.onCompletion() }
    }

    private var completionListener: GiftCompletionListener? = null
    private var errorListener: GiftErrorListener? = null

    private val videoPrepareListener: MediaPlayer.OnPreparedListener by lazy {
        MediaPlayer.OnPreparedListener {
            currentState = STATE_PREPARED
            if (targetState == STATE_PLAYING) {
                it.start()
                currentState = STATE_PLAYING
            }
        }
    }

    private val videoCompletionListener: MediaPlayer.OnCompletionListener by lazy {
        MediaPlayer.OnCompletionListener {
            currentState = STATE_COMPLETED
            targetState = STATE_COMPLETED
            clearTextureContent()
            mainHandler?.post(completionRunnable)
        }
    }

    private val videoSizeChangedListener: MediaPlayer.OnVideoSizeChangedListener by lazy {
        MediaPlayer.OnVideoSizeChangedListener { _, width, height ->
            videoWidth = width
            videoHeight = height
        }
    }

    private val videoErrorListener = MediaPlayer.OnErrorListener { mp, what, extra ->
        releaseMediaPlayer()
        currentState = STATE_ERROR
        targetState = STATE_ERROR

        errorListener?.apply {
            mainHandler?.post { onError(what, extra) }
            return@OnErrorListener true
        }

        rendererView?.let {
            if (it.windowToken != null) {
                AlertDialog.Builder(it.context)
                        .setMessage("播放失败：$what, $extra")
                        .setPositiveButton("确定") {_, _ ->
                            mainHandler?.post(completionRunnable)
                        }
                        .setCancelable(false)
                        .show()
            }
        }

        true
    }

    fun init(): Boolean {
        if (isQuit || glThread == null) {
            isQuit = false
            glThread = HandlerThread("gift-gl-thread").apply {
                start()
            }
            glHandler = GLHandler(glThread?.looper)
            mainHandler = Handler(Looper.getMainLooper())
            currentState = STATE_IDLE
            targetState = STATE_IDLE
            return true
        }
        return false
    }

    fun isPlaying(): Boolean {
        return isInPlaybackState() && mediaPlayer?.isPlaying as Boolean
    }

    fun start() {
        if (isInPlaybackState()) {
            mediaPlayer?.start()
            currentState = STATE_PLAYING
        }
        targetState = STATE_PLAYING
    }

    fun pause() {
        isOnPause = true
        if (currentState == STATE_PLAYING) {
            mediaPlayer?.pause()
            currentState = STATE_PAUSE
        }
    }

    fun resume() {
        if (isOnPause) {
            isOnPause = false
            isOnResume = true
        }
        when (currentState) {
            STATE_PAUSE -> {
                mediaPlayer?.start()
                currentState = STATE_PLAYING
            }
            STATE_IDLE  -> {
                openVideo()
            }
        }
    }

    fun stopPlayback() {
        mediaPlayer?.apply {
            releaseMediaPlayer()
            clearTextureContent()
        }
    }

    private fun clearTextureContent() {
        // 解决残影的问题：当前帧仍需要绘制，延迟清屏
        glHandler?.sendEmptyMessageDelayed(SURFACE_CLEAR, 10)
    }

    fun quit() {
        if (isQuit) return
        isQuit = true
        releaseMediaPlayer()
        glHandler?.sendEmptyMessage(QUIT)
        glThread?.quitSafely()
        glThread = null
        completionListener = null
        errorListener = null
    }

    fun setCompletionListener(listener: GiftCompletionListener) {
        completionListener = listener
    }

    fun setErrorListener(listener: GiftErrorListener) {
        errorListener = listener
    }

    fun setPreserveEGLOnPause(preserve: Boolean) {
        isPreserveEGLOnPause = preserve
    }

    fun setRenderer(renderer: GiftRenderer?) {
        this.renderer = renderer
    }

    fun setTextureView(textureView: TextureView) {
        if (textureView == rendererView) return
        rendererView?.surfaceTextureListener = null
        this.rendererView = textureView.apply {
            surfaceListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
                    glHandler?.sendMessage(Message.obtain().apply {
                        what = SURFACE_CHANGE
                        arg1 = width
                        arg2 = height
                    })
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                    if (isOnResume) {
                        isOnResume = false
                        clearTextureContent()
                    }
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                    glHandler?.sendEmptyMessage(SURFACE_DESTROY)
                    return true
                }

                override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                    glHandler?.sendMessage(Message.obtain().apply {
                        what = SURFACE_CREATE
                        obj = EGLData(Surface(surface), width, height)
                    })
                }
            }
            surfaceTextureListener = surfaceListener
        }
    }

    fun setVideoPath(path: String) {
        setVideoURI(Uri.parse(path))
    }

    fun setVideoURI(uri: Uri) {
        videoUri = uri
        videoUriBSet = true
        currentState = STATE_IDLE
        openVideo()
    }

    private fun isInPlaybackState(): Boolean {
        return mediaPlayer != null && currentState != STATE_IDLE && currentState != STATE_PREPARING
    }

    private fun openVideo() {
        if (!videoUriBSet || mediaPlayerTexture == null) {
            return
        }
        videoUriBSet = false
        releaseMediaPlayer()
        val uri = videoUri as Uri
        try {
            mediaPlayer = MediaPlayer().apply {
                setOnErrorListener(videoErrorListener)
                setOnPreparedListener(videoPrepareListener)
                setOnCompletionListener(videoCompletionListener)
                setOnVideoSizeChangedListener(videoSizeChangedListener)
                setDataSource(rendererView?.context as Context, uri)
                setSurface(Surface(mediaPlayerTexture))
                prepareAsync()
                currentState = STATE_PREPARING
            }
        } catch (ex: IOException) {
            Log.w(TAG, "Unable to open content: $uri", ex)
            currentState = STATE_ERROR
            targetState = STATE_ERROR
            videoErrorListener.onError(mediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0)
            return
        } catch (ex: IllegalArgumentException) {
            Log.w(TAG, "Unable to open content: $uri", ex)
            currentState = STATE_ERROR
            targetState = STATE_ERROR
            videoErrorListener.onError(mediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0)
            return
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.run {
            reset()
            release()
            currentState = STATE_IDLE
        }
        mediaPlayer = null
    }

    private inner class GLHandler(looper: Looper?) : Handler(looper) {

        private var eglCore: EglCore? = null
        private var eglSurface: EGLSurface? = null

        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                SURFACE_CREATE  -> {
                    initEgl(msg.obj as EGLData)
                    openVideo()
                }
                SURFACE_CHANGE  -> {
                    renderer?.onSurfaceChanged(msg.arg1, msg.arg2)
                }
                SURFACE_CLEAR   -> {
                    GLES20.glClearColor(0f, 0f, 0f, 0f)
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                    eglCore?.swapBuffers(eglSurface)
                }
                SURFACE_DESTROY -> {
                    releaseEGLRes()
                }
                QUIT            -> {
                    releaseEGLRes()
                    removeCallbacksAndMessages(null)
                    mainHandler?.removeCallbacksAndMessages(null)

                    renderer?.onEGLDestroy()
                    renderer = null

                    rendererView?.surfaceTextureListener = null
                    rendererView = null
                }
            }
        }

        private fun initEgl(data: EGLData) {
            if (eglCore == null) {
                eglCore = EglCore().apply {
                    setConfig(rendererView?.context, 8, 8, 8, 8, 0, 0, false)
                    initEglContext(null)
                    eglSurface = createWindowSurface(data.surface)
                    makeCurrent(eglSurface)
                }

                if (!GLES20.glIsTexture(textureId)) {
                    textureId = createTexture()
                    mediaPlayerTexture = SurfaceTexture(textureId).apply {
                        setOnFrameAvailableListener {
                            it?.updateTexImage()
                            requestRenderer(textureId)
                        }
                    }
                }

                renderer?.run {
                    onSurfaceCreated()
                    onSurfaceChanged(data.with, data.height)
                }
            }
        }

        private fun createTexture(): Int {
            return IntArray(1).let {
                GLES20.glGenTextures(1, it, 0)
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, it[0])
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
                GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
                it[0]
            }
        }

        private fun requestRenderer(textureId: Int) {
            renderer?.apply {
                onDraw(textureId, rendererData.also {
                    it.rotation = videoRotation
                    it.textureW = videoWidth
                    it.textureH = videoHeight
                    mediaPlayerTexture?.getTransformMatrix(textureMatrix)
                    it.textureMatrix = textureMatrix
                })
                eglCore?.swapBuffers(eglSurface)
            }
        }

        private fun releaseEGLRes() {
            eglCore?.releaseSurface(eglSurface)
            eglSurface = null

            if (!isPreserveEGLOnPause) {
                mediaPlayerTexture?.apply {
                    setOnFrameAvailableListener(null)
                    release()
                }
                mediaPlayerTexture = null

                eglCore?.release()
                eglCore = null
            }
        }
    }

    data class EGLData(val surface: Surface?, val with: Int, val height: Int)

    data class ExtraData(var textureW: Int, var textureH: Int, var rotation: Int, var textureMatrix: FloatArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ExtraData

            if (textureW != other.textureW) return false
            if (textureH != other.textureH) return false
            if (rotation != other.rotation) return false
            if (!textureMatrix.contentEquals(other.textureMatrix)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = textureW
            result = 31 * result + textureH
            result = 31 * result + rotation
            result = 31 * result + textureMatrix.contentHashCode()
            return result
        }
    }

    interface GiftRenderer {

        fun onSurfaceCreated()

        fun onSurfaceChanged(width: Int, height: Int)

        fun onDraw(textureId: Int, extraData: ExtraData)

        fun onEGLDestroy()
    }
}