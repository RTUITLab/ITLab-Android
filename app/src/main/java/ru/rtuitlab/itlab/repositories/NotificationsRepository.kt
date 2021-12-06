package ru.rtuitlab.itlab.repositories

import ru.rtuitlab.itlab.api.notifications.NotificationsApi
import ru.rtuitlab.itlab.api.notifications.models.UserPushDevice
import javax.inject.Inject

class NotificationsRepository @Inject constructor(
	val api: NotificationsApi
) {
	suspend fun addFirebaseToken(token: String) =
		api.addDevice(UserPushDevice(token, "android"))
}