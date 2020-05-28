package com.xx.avlibrary.inke.giftplayer

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.TextureView
import android.view.View

open class GiftVideoView: TextureView {

    private val giftPlayer: GiftPlayer by lazy {
        GiftPlayer()
    }

    constructor(context: Context): super(context) {
        initPlayer()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initPlayer()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initPlayer()
    }

    private fun initPlayer() {
        giftPlayer.let {
            if (it.init()) {
                it.setTextureView(this)
                it.setRenderer(Renderer())
            }
        }
        isOpaque = false
    }

    fun setVideoPath(path: String) {
        giftPlayer.setVideoPath(path)
    }

    fun setVideoUri(uri: Uri) {
        giftPlayer.setVideoURI(uri)
    }

    fun onPause() {
        giftPlayer.pause()
    }

    fun onResume() {
        giftPlayer.resume()
    }

    fun start() {
        giftPlayer.start()
        this.visibility = View.VISIBLE
    }

    fun stopPlayback() {
        giftPlayer.stopPlayback()
    }

    fun isPlaying(): Boolean {
        return giftPlayer.isPlaying()
    }

    fun setCompletionListener(listener: GiftCompletionListener) {
        giftPlayer.setCompletionListener(listener)
    }

    fun setErrorListener(listener: GiftErrorListener) {
        giftPlayer.setErrorListener(listener)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initPlayer()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        giftPlayer.quit()
    }

    private inner class Renderer: GiftPlayer.GiftRenderer {
        var giftFilter: GPUImageGiftFilter? = null

        override fun onSurfaceCreated() {
            giftFilter = GPUImageGiftFilter(context).also {
                it.onSurfaceCreated(null)
            }
        }

        override fun onSurfaceChanged(width: Int, height: Int) {
            giftFilter?.onSurfaceChanged(width, height)
        }

        override fun onDraw(textureId: Int, extraData: GiftPlayer.ExtraData) {
            giftFilter?.apply {
                setTextureMatrix(extraData.textureMatrix)
                setTextureWH(extraData.textureW / 2, extraData.textureH)
                onDrawFrame(textureId)
            }
        }

        override fun onEGLDestroy() {
            giftFilter?.destroy()
        }
    }
}