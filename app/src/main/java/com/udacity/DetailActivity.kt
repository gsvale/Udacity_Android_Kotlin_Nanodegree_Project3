package com.udacity

import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        // Cancel notification clicked
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancel(NOTIFICATION_ID)

        // Get item from Parcelable extras bundle
        val item =
            intent.getParcelableExtra<DownloadItem>(DownloadItem::class.java.simpleName as String)

        // Get var motionLayout
        val motionLayout: MotionLayout = findViewById(R.id.motion_layout)

        var success = true

        // Set item's info
        if (item != null) {
            file_name_tv.text = item.fileName
            status_tv.text = item.status
            success = (item.status == getString(R.string.status_success))
        }

        // Set status text color due to item status
        if (success) {
            status_tv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        } else {
            status_tv.setTextColor(Color.RED)
        }

        // On OkButton click listener to finish activity and return to main activity
        ok_button.setOnClickListener {
            finish()
        }

    }

    companion object {
        private const val NOTIFICATION_ID = 0
    }

}
