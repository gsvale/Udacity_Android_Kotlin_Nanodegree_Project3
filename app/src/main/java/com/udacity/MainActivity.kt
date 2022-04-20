package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager

    private lateinit var radioGroup: RadioGroup

    private var url: String? = ""

    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Init Download Manager variable

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager


        // Init Notification Manager variable

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager


        // Receiver called when download is completed

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radioGroup = findViewById<RadioGroup>(R.id.radio_group)


        // ClickListener for Custom button view implemented

        custom_button.setOnClickListener {
            val id = radioGroup.checkedRadioButtonId

            // If not option is selected, show toast message
            if (id == -1) {
                Toast.makeText(this, getString(R.string.warning_select_file), Toast.LENGTH_LONG)
                    .show()
            } else {

                // Get correct url from option selected
                when (id) {
                    R.id.radio_btn_1 -> {
                        url = URL_1
                    }
                    R.id.radio_btn_2 -> {
                        url = URL_2
                    }
                    R.id.radio_btn_3 -> {
                        url = URL_3
                    }
                }

                // Call download method
                download()
            }

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            custom_button.setDownloadComplete()
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id != null) {

                // Get information regarding the status of the specific download completed or not

                val query = DownloadManager.Query()

                query.setFilterById(id)

                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {

                    // Send notification of downloaded item with correct status

                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            val downloadItem = DownloadItem(
                                id,
                                getFileName(),
                                getStatusString(true)
                            )
                            sendNotification(downloadItem)
                        }
                        DownloadManager.STATUS_FAILED -> {
                            val downloadItem = DownloadItem(
                                id,
                                getFileName(),
                                getStatusString(false)
                            )
                            sendNotification(downloadItem)
                        }

                    }

                }
            }

        }
    }

    private fun sendNotification(item: DownloadItem) {

        // Cancel previous notifications

        notificationManager.cancelAll()

        // Create Channel

        createChannel()


        // Create Pending intent with downloaded item as extra

        val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        contentIntent.putExtra(DownloadItem::class.java.simpleName as String, item)
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build Notification

        val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(
                applicationContext
                    .getString(R.string.notification_title)
            )
            .setContentText(
                applicationContext
                    .getString(R.string.notification_description)
            )
            .setAutoCancel(true)
            .addAction(
                0,
                applicationContext
                    .getString(R.string.notification_button),
                contentPendingIntent
            )

        // Notify notification

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    // method to create channel for notification to be created and sent

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // Method to get File name String due option selected

    private fun getFileName(): String {
        var fileName = ""
        if (!TextUtils.isEmpty(url)) {
            when (url) {
                URL_1 -> {
                    fileName = getString(R.string.title_radio_button_1)
                }
                URL_2 -> {
                    fileName = getString(R.string.title_radio_button_2)
                }
                URL_3 -> {
                    fileName = getString(R.string.title_radio_button_3)
                }
            }
        }
        return fileName
    }

    // Method to get Status String due to success or fail of download

    private fun getStatusString(success: Boolean): String {
        return if (success) {
            getString(R.string.status_success)
        } else {
            getString(R.string.status_fail)
        }
    }

    // Download method

    private fun download() {
        if(!TextUtils.isEmpty(url)) {
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
    }

    // Company variables, regarding urls and channel/notification info

    companion object {
        private const val URL_1 =
            "https://github.com/bumptech/glide"
        private const val URL_2 =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val URL_3 =
            "https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "channelName"
        private const val NOTIFICATION_ID = 0
    }

}
