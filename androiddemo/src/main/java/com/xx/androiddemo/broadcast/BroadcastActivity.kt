package com.xx.androiddemo.broadcast

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xx.androiddemo.R
import kotlinx.android.synthetic.main.activity_broadcast.*

class BroadcastActivity: AppCompatActivity() {

    var disorderlyReceiver1: DisorderlyReceiver1? = null
    var disorderlyReceiver2: DisorderlyReceiver2? = null
    var orderlyReceiver1: OrderlyReceiver1? = null
    var orderlyReceiver2: OrderlyReceiver2? = null
    var orderlyReceiver3: OrderlyReceiver3? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast)

        send_msg_static_broadcast.setOnClickListener {
            val intent = Intent(this, StaticReceiver::class.java)
            intent.putExtra("msg", "这是一条发给静态注册广播的信息")
            sendBroadcast(intent)
        }


        register_dync_broadcast.setOnClickListener {
            disorderlyReceiver1 = DisorderlyReceiver1()
            disorderlyReceiver2 = DisorderlyReceiver2()
            val disorderly_intentFilter = IntentFilter()
            disorderly_intentFilter.addAction("com.xx.disorderly.broadcast")
            registerReceiver(disorderlyReceiver1, disorderly_intentFilter)
            registerReceiver(disorderlyReceiver2, disorderly_intentFilter)

            orderlyReceiver1 = OrderlyReceiver1()
            var orderly_intentFilter = IntentFilter()
            orderly_intentFilter.priority = 10_000
            orderly_intentFilter.addAction("com.xx.orderly.broadcast")
            registerReceiver(orderlyReceiver1, orderly_intentFilter)

            orderlyReceiver2 = OrderlyReceiver2()
            orderly_intentFilter = IntentFilter()
            orderly_intentFilter.priority = 1_000
            orderly_intentFilter.addAction("com.xx.orderly.broadcast")
            registerReceiver(orderlyReceiver2, orderly_intentFilter)

            orderlyReceiver3 = OrderlyReceiver3()
            orderly_intentFilter = IntentFilter()
            orderly_intentFilter.priority = 100
            orderly_intentFilter.addAction("com.xx.orderly.broadcast")
            registerReceiver(orderlyReceiver2, orderly_intentFilter)
            registerReceiver(orderlyReceiver3, orderly_intentFilter)
        }

        send_msg_dync_broadcast_disorderly.setOnClickListener {
            val intent = Intent()
            intent.setAction("com.xx.disorderly.broadcast")
            intent.putExtra("msg", "这是一条发给动态注册广播的无序信息")
            sendBroadcast(intent)
        }

        send_msg_dync_broadcast_orderly.setOnClickListener {
            val intent = Intent()
            intent.setAction("com.xx.orderly.broadcast")
            intent.putExtra("msg", "这是一条发给动态注册广播的有序信息")
            sendOrderedBroadcast(intent, null)
        }

        unregister_dync_broadcast.setOnClickListener {
            unregisterReceiver(disorderlyReceiver1)
            unregisterReceiver(disorderlyReceiver2)
            unregisterReceiver(orderlyReceiver1)
            unregisterReceiver(orderlyReceiver2)
            unregisterReceiver(orderlyReceiver3)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(disorderlyReceiver1)
        unregisterReceiver(disorderlyReceiver2)
        unregisterReceiver(orderlyReceiver1)
        unregisterReceiver(orderlyReceiver2)
        unregisterReceiver(orderlyReceiver3)
    }
}