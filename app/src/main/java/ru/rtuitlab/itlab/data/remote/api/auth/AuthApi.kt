package ru.rtuitlab.itlab.data.remote.api.auth

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Url

interface AuthApi {
	@GET
	suspend fun logout(
		@Url endSessionEndpoint: String
	) : Response<Unit>
}