package ru.rtuitlab.itlab.api.users

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Url
import ru.rtuitlab.itlab.api.users.models.UserInfoModel
import ru.rtuitlab.itlab.api.users.models.UserModel

interface UsersApi {

    @GET
    suspend fun getUserInfo(@Url url: String, @Header("Authorization") token: String): UserInfoModel

    @GET("User/{id}")
    suspend fun getUser(@Path("id") userId: String): UserModel
}