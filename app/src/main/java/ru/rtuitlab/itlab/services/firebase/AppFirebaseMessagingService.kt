package ru.rtuitlab.itlab.services.firebase

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception

class AppFirebaseMessagingService : FirebaseMessagingService() {
	override fun getStartCommandIntent(p0: Intent): Intent {
		return super.getStartCommandIntent(p0)
	}

	override fun handleIntent(intent: Intent) {
		super.handleIntent(intent)
	}

	override fun onMessageReceived(message: RemoteMessage) {
		super.onMessageReceived(message)
		Log.v("Firebase", "Message received")
		for ((key, value) in message.data) {
			Log.v("Firebase", "$key : $value")
		}
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
		Log.v("Firebase", token)
	}
}