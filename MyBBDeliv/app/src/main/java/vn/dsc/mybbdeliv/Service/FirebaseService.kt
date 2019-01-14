package dsc.vn.mybbdeliv.Service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dsc.vn.mybbdeliv.Base.BaseActivity
import dsc.vn.mybbdeliv.R


class FirebaseService : FirebaseMessagingService() {
    private val TAG = "Service"
    @SuppressLint("ShowToast")
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
//        val gson = Gson().toJson(remoteMessage!!.notification.bodyLocalizationArgs)
//        Log.v(TAG, "notification: " + gson.toString())
//        Log.v(TAG, "collapseKey: " + remoteMessage.collapseKey)
//        Log.v(TAG, "to: " + remoteMessage.to)
//        Log.v(TAG, "messageType: " + remoteMessage.messageType)
//        Log.v(TAG, "messageId: " + remoteMessage.messageId)
//        Log.v(TAG, "sentTime: " + remoteMessage.sentTime)
//        Log.v(TAG, "ttl: " + remoteMessage.ttl)
//        Log.v(TAG, "From: " + remoteMessage!!.from)
//        Log.v(TAG, "data badge: " + remoteMessage!!.data.toString())
        sendNotification(remoteMessage!!)
//        val intent = Intent(this@FirebaseService, BaseActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        intent.putExtra("message", remoteMessage.notification.body!!)
//        startActivity(intent)
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        //Setting up Notification channels for android O and above
//        val notificationId:Int = Random().nextInt(60000)
//        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.logo) //a resource for your custom small icon
//                .setContentTitle(remoteMessage.notification.title) //the "title" value you sent in your notification
//                .setContentText(remoteMessage.notification.body) //ditto
//                .setBadgeIconType()
//                .setAutoCancel(true) //dismisses the notification on click
//                .setSound(defaultSoundUri)
//        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build())
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, BaseActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this)
                .setContentText(remoteMessage.notification!!.body)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setNumber(1)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}