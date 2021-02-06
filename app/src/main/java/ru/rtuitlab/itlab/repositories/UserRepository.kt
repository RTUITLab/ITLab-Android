package ru.rtuitlab.itlab.repositories

import ru.rtuitlab.itlab.api.ResponseHandler
import ru.rtuitlab.itlab.api.users.UsersApi
import javax.inject.Inject

class UserRepository @Inject constructor(
        private val usersApi: UsersApi,
        private val responseHandler: ResponseHandler
) {
    suspend fun getUserInfo(url: String, accessToken: String) =
            responseHandler.handle { usersApi.getUserInfo(url, "Bearer $accessToken") }

    suspend fun getUser(userId: String) =
            responseHandler.handle { usersApi.getUser(userId) }
}