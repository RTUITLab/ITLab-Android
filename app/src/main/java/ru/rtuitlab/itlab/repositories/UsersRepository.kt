package ru.rtuitlab.itlab.repositories

import ru.rtuitlab.itlab.api.ResponseHandler
import ru.rtuitlab.itlab.api.users.UsersApi
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val usersApi: UsersApi,
    private val handler: ResponseHandler
) {
    suspend fun fetchUserInfo(url: String, accessToken: String) = handler {
        usersApi.getUserInfo(url, "Bearer $accessToken")
    }

    suspend fun fetchUserCredentials(userId: String) = handler {
        usersApi.getUser(userId)
    }

    suspend fun fetchUserDevices(userId: String) = handler {
        usersApi.getUserDevices(userId)
    }

    suspend fun fetchUserEvents(userId: String, beginTime: String, endTime: String) = handler {
        usersApi.getUserEvents(userId, beginTime, endTime)
    }

    suspend fun fetchUsers() = handler {
        usersApi.getUsers()
    }
}