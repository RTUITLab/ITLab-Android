package ru.rtuitlab.itlab.api.users

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Url
import ru.rtuitlab.itlab.api.users.models.UserInfo
import ru.rtuitlab.itlab.api.users.models.UserModel

interface UserApi {

    @GET
    suspend fun getUserInfo(@Url url: String, @Header("Authorization") token: String): UserInfo

    @GET("User/{id}")
    suspend fun getUser(@Path("id") userId: String): UserModel
}