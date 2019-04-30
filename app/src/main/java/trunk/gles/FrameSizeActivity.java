package trunk.gles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lib.gl.filter.rhythm.*;
import lib.gl.util.GLUtil;
import trunk.BaseActivity;
import trunk.R;
import util.PxUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

public class FrameSizeActivity extends BaseActivity implements GLSurfaceView.Renderer {
    RecyclerView mFrameSizeListView;
    GLSurfaceView mGlView;
    private Button startBtn;
    private Button pauseBtn;
    private Button fullInBtn;
    private Button notFullInBtn;

    ArrayList<FrameSizeInfo> mFrameSizeData;

    boolean mCanDraw;

    @Override
    public void onCreateBaseData() throws Exception {
        mFrameSizeData = new ArrayList<>();
        FrameSizeInfo info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_1_1;
        info.name = "1:1";
        mFrameSizeData.add(info);

        info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_3_4;
        info.name = "3:4";
        mFrameSizeData.add(info);

        info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_4_3;
        info.name = "4:3";
        mFrameSizeData.add(info);

        info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_9_16;
        info.name = "9:16";
        mFrameSizeData.add(info);

        info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_16_9;
        info.name = "16:9";
        mFrameSizeData.add(info);

        info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_235_1;
        info.name = "2.35:1";
        mFrameSizeData.add(info);
    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        ConstraintLayout layout = new ConstraintLayout(context);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(layout, params);
        {
            ConstraintLayout.LayoutParams cl = null;

            mFrameSizeListView = new RecyclerView(context);
            mFrameSizeListView.setId(View.generateViewId());
            mFrameSizeListView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(200));
            cl.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            cl.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            layout.addView(mFrameSizeListView, cl);

            MyAdapter adapter = new MyAdapter();
            adapter.setData(mFrameSizeData);
            adapter.setListener(new MyAdapter.Listener() {
                @Override
                public void onClick(FrameSizeInfo info) {
                    setFrameSize(info.frameSizeType);
                }
            });
            mFrameSizeListView.setAdapter(adapter);

            mGlView = new GLSurfaceView(context);
            mGlView.setId(View.generateViewId());
            mGlView.setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
            mGlView.setRenderer(this);
            mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            cl = new ConstraintLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(1080));
            cl.topToBottom = mFrameSizeListView.getId();
            cl.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            layout.addView(mGlView, cl);

            startBtn = new Button(context);
            startBtn.setId(View.generateViewId());
            startBtn.setAllCaps(false);
            startBtn.setText("start");
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCanDraw = true;
                    mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                    mGlView.requestRender();
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = mGlView.getId();
            cl.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cl.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            layout.addView(startBtn, cl);

            pauseBtn = new Button(context);
            pauseBtn.setId(View.generateViewId());
            pauseBtn.setAllCaps(false);
            pauseBtn.setText("pause");
            pauseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCanDraw = false;
                    mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = mGlView.getId();
            cl.leftToRight = startBtn.getId();
            layout.addView(pauseBtn, cl);

            fullInBtn = new Button(context);
            fullInBtn.setId(View.generateViewId());
            fullInBtn.setAllCaps(false);
            fullInBtn.setText("fullIn");
            fullInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setScaleFullIn(true);
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = startBtn.getId();
            cl.leftToLeft = startBtn.getId();
            layout.addView(fullInBtn, cl);

            notFullInBtn = new Button(context);
            notFullInBtn.setId(View.generateViewId());
            notFullInBtn.setAllCaps(false);
            notFullInBtn.setText("not fullIn");
            notFullInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setScaleFullIn(false);
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = pauseBtn.getId();
            cl.leftToRight = fullInBtn.getId();
            layout.addView(notFullInBtn, cl);
        }
    }

    @Override
    public void onCreateFinish() {

    }

    int mFrameSize = FrameSizeType.size_1_1;

    private void setFrameSize(int size) {
        mFrameSize = size;
        setScaleFullIn(true);
    }

    boolean mScaleFullIn = true;

    private void setScaleFullIn(boolean fullIn) {
        mScaleFullIn = fullIn;
    }

    TextureFilter mBmpToTextureFilter;
    FrameSizeFilter mFrameSizeFilter;
    DisplayFilter mDisplayFilter;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mBmpToTextureFilter = new TextureFilter(this);
        mBmpToTextureFilter.onSurfaceCreated(null);

        mFrameSizeFilter = new FrameSizeFilter(this);
        mFrameSizeFilter.onSurfaceCreated(null);

        mDisplayFilter = new DisplayFilter(this);
        mDisplayFilter.onSurfaceCreated(null);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mBmpToTextureFilter.onSurfaceChanged(width, height);

        mFrameSizeFilter.onSurfaceChanged(width, height);
        mFrameSizeFilter.initFrameBuffer(width, height);

        mDisplayFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mCanDraw) {
            int texture = mBmpToTextureFilter.createGlTexture(R.drawable.open_test_9);
            mBmpToTextureFilter.initFrameBufferOfTextureSize(); // FIXME: 2019/4/30 调整纹理尺寸, 调整成图片一半，或者指定大小
            texture = mBmpToTextureFilter.onDrawBuffer(texture);

            if (mFrameSizeFilter != null) {
                mFrameSizeFilter.setVideoFrameSize(mFrameSize);
                mFrameSizeFilter.setScaleFullIn(mScaleFullIn);
                mFrameSizeFilter.setTextureWH(mBmpToTextureFilter.getTextureW(), mBmpToTextureFilter.getTextureH());
                texture = mFrameSizeFilter.onDrawBuffer(texture);
            }

            mDisplayFilter.onDrawFrame(texture);
        }
    }

    private static class FrameSizeInfo {
        int frameSizeType;
        String name;
    }

    private static class MyAdapter extends RecyclerView.Adapter implements RecyclerView.OnClickListener {

        ArrayList<FrameSizeInfo> mData;
        Listener mListener;

        public interface Listener {
            void onClick(FrameSizeInfo info);
        }

        public void setListener(Listener listener) {
            mListener = listener;
        }

        public void setData(ArrayList<FrameSizeInfo> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Button item = new Button(parent.getContext());
            item.setOnClickListener(this);
            ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            item.setLayoutParams(params);
            return new RecyclerView.ViewHolder(item) {};
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Button itemView = (Button) holder.itemView;
            itemView.setTag(position);

            FrameSizeInfo info = mData.get(position);
            itemView.setText(info.name);
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.size() : 0;
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            FrameSizeInfo info = mData.get(position);
            if (mListener != null) {
                mListener.onClick(info);
            }
        }
    }
}
