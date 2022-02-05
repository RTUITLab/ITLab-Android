package ru.rtuitlab.itlab.data.remote.api.notifications

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.rtuitlab.itlab.data.remote.api.notifications.models.UserPushDevice

interface NotificationsApi {
	@POST("push/user/addDevice")
	suspend fun addDevice(
		@Body device: UserPushDevice
	): Response<Unit>
}