package ru.rtuitlab.itlab.repositories

import ru.rtuitlab.itlab.api.ResponseHandler
import ru.rtuitlab.itlab.api.users.UserApi
import javax.inject.Inject

class UserRepository @Inject constructor(
        private val userApi: UserApi,
        private val responseHandler: ResponseHandler
) {
    suspend fun getUserInfo(url: String, accessToken: String) =
            responseHandler.handle { userApi.getUserInfo(url, "Bearer $accessToken") }

    suspend fun getUser(userId: String) =
            responseHandler.handle { userApi.getUser(userId) }
}