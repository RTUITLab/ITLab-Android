package ru.rtuitlab.itlab.data.repository

import ru.rtuitlab.itlab.data.remote.api.notifications.NotificationsApi
import ru.rtuitlab.itlab.data.remote.api.notifications.models.UserPushDevice
import javax.inject.Inject

class NotificationsRepository @Inject constructor(
	val api: NotificationsApi
) {
	suspend fun addFirebaseToken(token: String) =
		api.addDevice(UserPushDevice(token, "android"))
}