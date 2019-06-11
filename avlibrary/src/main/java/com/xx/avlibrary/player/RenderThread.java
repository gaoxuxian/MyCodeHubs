package com.xx.avlibrary.player;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.view.Surface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xx.avlibrary.gl.egl.EglCoreV2;
import com.xx.avlibrary.gl.egl.WindowSurface;
import com.xx.avlibrary.player.port.IControl;

import java.lang.ref.WeakReference;

public class RenderThread extends Thread {

	private static final int MSG_SURFACE_CREATED = 1;
	private static final int MSG_SURFACE_CHANGED = 2;
	private static final int MSG_REQUEST_RENDER = 3;
	private static final int MSG_SURFACE_DESTROY = 4;
	private static final int MSG_RELEASE = 5;

	private int mPriority;
	private RenderHandler mRenderHandler;

	@NonNull
	private final WeakReference<PluginVideoView> mRendererViewWRF;

	private volatile boolean isQuit;

	RenderThread(PluginVideoView rendererView) {
		super("Render Thread");

		mRendererViewWRF = new WeakReference<>(rendererView);

		mPriority = Process.THREAD_PRIORITY_DEFAULT;
	}

	@Override
	public void run() {
		isQuit = false;
		Looper.prepare();
		synchronized (this) {
			mRenderHandler = new RenderHandler(mRendererViewWRF);
			notifyAll();
		}
		Process.setThreadPriority(mPriority);
		Looper.loop();
		isQuit = true;
	}

	@NonNull
	Handler getThreadHandler() {
		synchronized (this) {
			while (mRenderHandler == null) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return mRenderHandler;
	}

	public void onSurfaceCreated(Object surface) {
		if (!isQuit) {
			Message.obtain(mRenderHandler, MSG_SURFACE_CREATED, surface).sendToTarget();
		}
	}

	public void onSurfaceChanged(int width, int height) {
		if (!isQuit) {
			Message.obtain(mRenderHandler, MSG_SURFACE_CHANGED, width, height).sendToTarget();
		}
	}

	public void requestRender(PluginVideoView.DrawInfo info) {
		if (Thread.currentThread() == this) {
			mRenderHandler.requestRender(info);
		} else {
			mRenderHandler.removeMessages(MSG_REQUEST_RENDER);
			if (!isQuit) {
				Message.obtain(mRenderHandler, MSG_REQUEST_RENDER, info).sendToTarget();
			}
		}
	}

	void onSurfaceDestroy() {
		if (!isQuit) {
			Message.obtain(mRenderHandler, MSG_SURFACE_DESTROY).sendToTarget();
		}
	}

	void requestExitAndWait() {
		if (!isQuit) {
			Message.obtain(mRenderHandler, MSG_RELEASE).sendToTarget();
		}
	}

	void setPreserveEGLOnPause(boolean preserve) {
		if (mRenderHandler != null) {
			mRenderHandler.isPreserveEGLOnPause = preserve;
		}
	}

	public static class RenderHandler extends Handler {

		@NonNull
		private final WeakReference<PluginVideoView> mRendererViewWRF;

		@Nullable
		private EglCoreV2 mEglCore;

		@Nullable
		private WindowSurface mWindowSurface;

		private boolean mShouldCallDestroy;

		private int mWidth;
		private int mHeight;

		private boolean mWaitingSurface;

		private volatile boolean isPreserveEGLOnPause = true;

		private boolean mPendingRender;

		private RenderHandler(@NonNull WeakReference<PluginVideoView> renderer) {
			mRendererViewWRF = renderer;
			mEglCore = new EglCoreV2(null, EglCoreV2.FLAG_RECORDABLE | EglCoreV2.FLAG_TRY_GLES3);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_SURFACE_CREATED:
					onSurfaceCreated(msg.obj);
					break;
				case MSG_SURFACE_CHANGED:
					onSurfaceChanged(msg.arg1, msg.arg2);
					break;
				case MSG_REQUEST_RENDER:
					requestRender((PluginVideoView.DrawInfo) msg.obj);
					break;
				case MSG_SURFACE_DESTROY:
					onSurfaceDestroy();
					break;
				case MSG_RELEASE:
					onRelease();
					break;
			}
		}

		private void onSurfaceCreated(Object surface) {
			if (mWindowSurface != null) {
				throw new IllegalStateException("mWindowSurface is not null.");
			}

			if (mEglCore == null) { // Recreate EGL
				mEglCore = new EglCoreV2(null, EglCoreV2.FLAG_RECORDABLE | EglCoreV2.FLAG_TRY_GLES3);
			}

			if (surface instanceof SurfaceTexture) {
				mWindowSurface = new WindowSurface(mEglCore, (SurfaceTexture)surface);
			} else if (surface instanceof Surface) {
				Surface s = (Surface)surface;
				if (!s.isValid()) {
					return;
				}
				mWindowSurface = new WindowSurface(mEglCore, (Surface)surface, false);
			} else {
				throw new RuntimeException();
			}

			mWindowSurface.makeCurrent();
			PluginVideoView.Renderer renderer = getRenderer();
			if (renderer != null && !mWaitingSurface) {
				renderer.onSurfaceCreated();
				mShouldCallDestroy = true;
			}
		}

		private void onSurfaceChanged(int width, int height) {
			if (mWindowSurface == null) {
				return;
			}

			mWidth = width;
			mHeight = height;
			PluginVideoView.Renderer renderer = getRenderer();
			if (renderer != null && !mWaitingSurface) {
				renderer.onSurfaceChanged(mWidth, mHeight);
			}

			final boolean doRender = mWaitingSurface || mPendingRender;
			mWaitingSurface = false;
			if (doRender) {
				requestRender(null);
			}
		}

		private void requestRender(PluginVideoView.DrawInfo info) {
			mPendingRender = false;
			PluginVideoView.Renderer renderer = getRenderer();
			if (renderer != null && canRender()) {
				renderer.onDrawFrame(info);
				if (mWindowSurface != null) {
					mWindowSurface.swapBuffers();
				}
			} else {
				mPendingRender = true;
			}
		}

		private boolean canRender() {
			return mWindowSurface != null && mWidth > 0 && mHeight > 0;
		}

		private void onSurfaceDestroy() {
			if (isPreserveEGLOnPause) {
				releaseSurface();
				mWaitingSurface = true;
			} else {
				// Meizu M5 Note 一定要释放 EGL 上下文
				PluginVideoView.Renderer renderer = getRenderer();
				if (renderer != null && mShouldCallDestroy) {
					mShouldCallDestroy = false;
					renderer.onSurfaceDestroyed();
				}
				releaseSurface();
				mWaitingSurface = false;
				releaseGL();
			}
		}

		private void onRelease() {
			removeCallbacksAndMessages(null);

			PluginVideoView.Renderer renderer = getRenderer();
			if (renderer != null && mShouldCallDestroy) {
				mShouldCallDestroy = false;
				renderer.onSurfaceDestroyed();
			}
			releaseSurface();
			releaseGL();

			quitSafely();
		}

		private PluginVideoView.Renderer getRenderer() {
			PluginVideoView.Renderer renderer = null;
			PluginVideoView rendererView = mRendererViewWRF.get();
			if (rendererView != null) {
				IControl controller = rendererView.getPlayerController();
				if (controller != null) {
					renderer = controller.getRenderer();
				}
			}
			return renderer;
		}

		private void releaseSurface() {
			if (mWindowSurface != null) {
				mWindowSurface.release();
				mWindowSurface = null;
			}
			mWidth = 0;
			mHeight = 0;
			mPendingRender = false;
		}

		private void releaseGL() {
			if (mEglCore != null) {
				mEglCore.release();
				mEglCore = null;
			}
		}

		private void quitSafely() {
			Looper looper = Looper.myLooper();
			if (looper != null) {
				looper.quitSafely();
			}
		}
	}
}
