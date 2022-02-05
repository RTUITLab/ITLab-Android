package ru.rtuitlab.itlab.domain.services.firebase

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import androidx.core.app.NotificationCompat
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.constraintlayout.compose.ExperimentalMotionApi
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.MainActivity

@ExperimentalMotionApi
@ExperimentalMaterialApi
@ExperimentalStdlibApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalTransitionApi
class AppFirebaseMessagingService : FirebaseMessagingService() {
	override fun getStartCommandIntent(p0: Intent): Intent {
		return super.getStartCommandIntent(p0)
	}

	override fun handleIntent(intent: Intent) {
		super.handleIntent(intent)
	}

	override fun onMessageReceived(message: RemoteMessage) {
		Log.v("Firebase", "Message notification contents: ${message.notification}")
		// Check if message contains a notification payload.
		message.notification?.let {
			sendNotification(it.title, it.body)
		}

		super.onMessageReceived(message)
	}

	override fun onDeletedMessages() {
		super.onDeletedMessages()
	}

	override fun onMessageSent(p0: String) {
		super.onMessageSent(p0)
	}

	override fun onSendError(p0: String, p1: Exception) {
		super.onSendError(p0, p1)
	}

	/**
	 * Every scenario this method is called in ensures that
	 * user is not logged in, so there's no need to
	 * send new token to server from here.
	 */
	override fun onNewToken(token: String) {
		super.onNewToken(token)
	}

	private fun sendNotification(title: String?, message: String?) {
		if (title == null || message == null) {
			Log.e("Firebase", "Null data provided for notification")
			return
		}
		val intent = Intent(this, MainActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		val pendingIntent = PendingIntent.getActivity(
			this,
			0,
			intent,
			PendingIntent.FLAG_ONE_SHOT
		)
		val channelId = getString(R.string.feedback)
		val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		val notificationBuilder: NotificationCompat.Builder =
			NotificationCompat.Builder(this, channelId)
				.setSmallIcon(R.drawable.ic_notification_default)
				.setContentTitle(title)
				.setContentText(message)
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setContentIntent(pendingIntent)
		val notificationManager =
			getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				channelId,
				getString(R.string.feedback),
				NotificationManager.IMPORTANCE_HIGH
			)
			notificationManager.createNotificationChannel(channel)
		}
		notificationManager.notify(0, notificationBuilder.build())
	}
}