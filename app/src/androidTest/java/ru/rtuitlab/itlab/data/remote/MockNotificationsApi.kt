package ru.rtuitlab.itlab.data.remote

import retrofit2.Response
import ru.rtuitlab.itlab.data.remote.api.notifications.NotificationsApi
import ru.rtuitlab.itlab.data.remote.api.notifications.models.UserPushDevice

class MockNotificationsApi: NotificationsApi {
    override suspend fun addDevice(device: UserPushDevice): Response<Unit> {
        TODO("Not yet implemented")
    }
}