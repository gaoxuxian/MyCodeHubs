package trunk.android;

import androidx.annotation.Nullable;
import lib.gl.decode.MediaPlayer;
import trunk.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import util.PxUtil;

public class MyMediaPlayerActivity extends BaseActivity implements View.OnClickListener {
    private Button btn;
    private MediaPlayer mMediaPlayer;
    private boolean mPause = true;
    private boolean mRepeat;

    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        mMediaPlayer = new MediaPlayer(context);
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(1920));
        params.gravity = Gravity.CENTER;
        parent.addView(mMediaPlayer, params);

        btn = new Button(context);
        btn.setText("选视频");
        btn.setOnClickListener(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parent.addView(btn, params);

        Button nextFrame = new Button(context);
        nextFrame.setText("暂停 or not");
        nextFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPause = !mPause;
                if (mMediaPlayer != null)
                {
                    mMediaPlayer.pause(mPause);
                }
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(nextFrame, params);

        Button repeat = new Button(context);
        repeat.setText("重复");
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRepeat = !mRepeat;
                if (mMediaPlayer != null) {
                    mMediaPlayer.setRepeatMode(mRepeat);
                }
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        parent.addView(repeat, params);
    }

    @Override
    public void onCreateFinish() {

    }

    @Override
    public void onClick(View v) {
        if (v == btn) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("video/*");
            startActivityForResult(intent, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Cursor cursor = null;
            try {
                if (data != null && data.getData() != null) {
                    cursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Video.Media.DATA}, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        Log.d("xxxx", "onActivityResult , 选中的视频绝对路径 == " + path);

                        if (btn != null) {
                            btn.setVisibility(View.GONE);
                        }

                        if (mMediaPlayer != null){
                            mMediaPlayer.setMediaPath(path, true);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.destroy();
        }
        super.onDestroy();
    }
}
