package ru.rtuitlab.itlab.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import net.openid.appauth.AuthorizationService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.create
import ru.rtuitlab.itlab.BuildConfig
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.common.persistence.IAuthStateStorage
import ru.rtuitlab.itlab.data.remote.api.TokenInterceptor
import ru.rtuitlab.itlab.data.remote.api.devices.DevicesApi
import ru.rtuitlab.itlab.data.remote.api.events.EventsApi
import ru.rtuitlab.itlab.data.remote.api.feedback.FeedbackApi
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.MfsApi
import ru.rtuitlab.itlab.data.remote.api.notifications.NotificationsApi
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchasesApi
import ru.rtuitlab.itlab.data.remote.api.reports.ReportsApi
import ru.rtuitlab.itlab.data.remote.api.users.UsersApi
import javax.inject.Singleton

@ExperimentalSerializationApi
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideTokenInterceptor(
        authStateStorage: IAuthStateStorage,
        authService: AuthorizationService
    ) = TokenInterceptor(authStateStorage, authService)

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        tokenInterceptor: TokenInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()



    private val defaultJson = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @ExperimentalSerializationApi
    @Singleton
    @Provides
    fun provideConverterFactory(): Converter.Factory = defaultJson.asConverterFactory("application/json".toMediaType())

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
    fun provideUserApi(retrofit: Retrofit): UsersApi = retrofit.create()

    @Singleton
    @Provides
    fun provideFeedbackApi(retrofit: Retrofit): FeedbackApi = retrofit.create()

    @Singleton
    @Provides
    fun provideNotificationsApi(retrofit: Retrofit): NotificationsApi = retrofit.create()

    @Singleton
    @Provides
    fun provideEventsApi(retrofit: Retrofit): EventsApi = retrofit.create()

    @Singleton
    @Provides
    fun provideDevicesApi(retrofit: Retrofit): DevicesApi = retrofit.create()

    @Singleton
    @Provides
    fun provideReportApi(retrofit: Retrofit): ReportsApi = retrofit.create()

    @Singleton
    @Provides
    fun provideMFSApi(retrofit: Retrofit): MfsApi = retrofit.create()

    @Singleton
    @Provides
    fun providePurchasesApi(retrofit: Retrofit): PurchasesApi = retrofit.create()
}