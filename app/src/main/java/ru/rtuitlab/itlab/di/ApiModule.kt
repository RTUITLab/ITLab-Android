package ru.rtuitlab.itlab.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.rtuitlab.itlab.BuildConfig
import ru.rtuitlab.itlab.persistence.AuthStateStorage
import ru.rtuitlab.itlab.api.ResponseHandler
import ru.rtuitlab.itlab.api.users.UserApi
import ru.rtuitlab.itlab.api.TokenInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideTokenInterceptor(
        authStateStorage: AuthStateStorage,
        authService: AuthorizationService
    ) = TokenInterceptor(authStateStorage, authService)

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: TokenInterceptor): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .build()

    @Singleton
    @Provides
    fun provideConverterFactory(): Converter.Factory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, converter: Converter.Factory): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URI)
            .client(client)
            .addConverterFactory(converter)
            .build()

    @Singleton
    @Provides
    fun provideResponseHandler() = ResponseHandler()

    @Singleton
    @Provides
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create()
}