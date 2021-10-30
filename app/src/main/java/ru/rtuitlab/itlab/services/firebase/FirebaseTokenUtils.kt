package ru.rtuitlab.itlab.services.firebase

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

object FirebaseTokenUtils {

	fun getToken(onSuccess: (String) -> Unit) {
		FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
			if (!task.isSuccessful) {
				Log.w("FIREBASE", "Fetching FCM registration token failed", task.exception)
				return@OnCompleteListener
			}
			onSuccess(task.result!!)
		})
	}
}