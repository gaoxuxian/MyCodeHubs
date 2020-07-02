package com.xx.androiddemo.inke

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.xx.androiddemo.R
import com.xx.androiddemo.inke.giftplayer.GiftCompletionListener
import com.xx.androiddemo.inke.giftplayer.GiftErrorListener
import com.xx.androiddemo.inke.giftplayer.GiftVideoView
import kotlinx.android.synthetic.main.activity_android_demo.button
import kotlinx.android.synthetic.main.activity_inke_gift_demo.*

class InKeGiftDemoActivity : AppCompatActivity() {

    var view : GiftVideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inke_gift_demo)

        //image.setBackgroundColor(Color.BLACK)

        button.setOnClickListener {
//            val intent = Intent()
//            intent.action = Intent.ACTION_PICK
//            intent.type = "video/*"
//            startActivityForResult(intent, 2)
            textureview.setVideoUri(Uri.parse("android.resource://com.xx.androiddemo/"+ R.raw.love_gift))
//            textureview.setVideoPath("/storage/emulated/0/tencent/MicroMsg/WeiXin/1589447670567.mp4")
            textureview.start()
        }
        textureview.setVideoUri(Uri.parse("android.resource://com.xx.androiddemo/"+ R.raw.love_gift))
        textureview.start()
//        textureview.setVideoPath("/storage/emulated/0/DCIM/Camera/VID_20200504_121250.mp4")
//        textureview.setVideoPath("/storage/emulated/0/DCIM/Camera/VID_20200324_153317.mp4")
        textureview.setCompletionListener(object : GiftCompletionListener {
            override fun onCompletion() {
                Log.e("xxx", "播完 + Thread = ${Thread.currentThread().name}")
//                textureview?.visibility = View.GONE
//                textureview.postDelayed(Runnable {
//                    textureview.setVideoUri(Uri.parse("android.resource://com.xx.androiddemo/"+R.raw.love_gift))
//                    textureview.start()
//                }, 1000)
            }
        })
        textureview.setErrorListener(object : GiftErrorListener {
            override fun onError(what: Int, extra: Int) {
                Log.e("xxx", "错误 + Thread = ${Thread.currentThread().name}")
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            var cursor: Cursor? = null
            try {
                if (data != null && data.data != null) {
                    cursor = contentResolver.query(
                        data.data!!,
                        arrayOf(MediaStore.Video.Media.DATA),
                        null,
                        null,
                        null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        val path =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                        Log.d("xxxx", "onActivityResult , 选中的视频绝对路径 == $path")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (cursor != null && !cursor.isClosed) {
                    cursor.close()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        textureview?.onPause()
    }

    override fun onResume() {
        super.onResume()
        textureview?.onResume()
    }

    override fun onStop() {
        super.onStop()
        textureview?.stopPlayback()
    }

    override fun onDestroy() {
        super.onDestroy()
        textureview?.stopPlayback()
    }

}
