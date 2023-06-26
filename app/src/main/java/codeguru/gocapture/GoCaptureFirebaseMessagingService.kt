package codeguru.gocapture

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStream

class GoCaptureFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG: String = javaClass.name
    private val CHANNEL_ID: String = "Go Capture"
    private var nextNotificationId: Int = 1

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            val sgfData: String? = remoteMessage.data["sgf"]
            val imageFilename: String = remoteMessage.data["image_filename"]!!
            val sgfFilename: String = File(imageFilename).nameWithoutExtension + ".sgf"
            writeSgfFile(sgfData, sgfFilename, "application/x-go-sgf sgf")
            Log.d(TAG, "SGF file written")
            sendNotification(imageFilename)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    private fun writeSgfFile(input: String?, filename: String, contentType: String) {
        val resolver: ContentResolver = this.contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, contentType)
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            var output: OutputStream? = null
            try {
                output = resolver.openOutputStream(uri)
                output?.write(input?.toByteArray())
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (output != null) {
                    try {
                        output.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun sendNotification(imageFilename: String) {
        val notificationTitle = getString(R.string.notification_title)
        val notificationContext = getString(R.string.notification_content)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContext)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(nextNotificationId++, builder.build())
        }
    }
}
