package ru.rtuitlab.itlab.repositories

import ru.rtuitlab.itlab.api.ResponseHandler
import ru.rtuitlab.itlab.api.users.UsersApi
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val usersApi: UsersApi,
    private val handler: ResponseHandler
) {
    suspend fun loadUserInfo(url: String, accessToken: String) = handler {
        usersApi.getUserInfo(url, "Bearer $accessToken")
    }

    suspend fun loadUserCredentials(userId: String) = handler {
        usersApi.getUser(userId)
    }

    suspend fun loadUserDevices(userId: String) = handler {
        usersApi.getUserDevices(userId)
    }

    suspend fun loadUserEvents(userId: String, beginTime: String, endTime: String) = handler {
        usersApi.getUserEvents(userId, beginTime, endTime)
    }
}