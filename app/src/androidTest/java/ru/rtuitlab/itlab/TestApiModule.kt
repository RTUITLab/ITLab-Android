package ru.rtuitlab.itlab

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.di.ApiModule
import ru.rtuitlab.itlab.data.remote.*
import ru.rtuitlab.itlab.data.remote.api.devices.DevicesApi
import ru.rtuitlab.itlab.data.remote.api.events.EventsApi
import ru.rtuitlab.itlab.data.remote.api.feedback.FeedbackApi
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.MfsApi
import ru.rtuitlab.itlab.data.remote.api.notifications.NotificationsApi
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchasesApi
import ru.rtuitlab.itlab.data.remote.api.reports.ReportsApi
import ru.rtuitlab.itlab.data.remote.api.users.UsersApi
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApiModule::class]
)
object TestApiModule {


    @Singleton
    @Provides
    fun provideResponseHandler() = ResponseHandler()

    @Singleton
    @Provides
    fun provideUserApi(): UsersApi = MockUsersApi()

    @Singleton
    @Provides
    fun provideFeedbackApi(): FeedbackApi = MockFeedbackApi()

    @Singleton
    @Provides
    fun provideNotificationsApi(): NotificationsApi = MockNotificationsApi()

    @Singleton
    @Provides
    fun provideEventsApi(): EventsApi = MockEventsApi()

    @Singleton
    @Provides
    fun provideDevicesApi(): DevicesApi = MockDevicesApi()

    @Singleton
    @Provides
    fun provideReportApi(): ReportsApi = MockReportsApi()

    @Singleton
    @Provides
    fun provideMFSApi(): MfsApi = MockMfsApi()

    @Singleton
    @Provides
    fun providePurchasesApi(): PurchasesApi = MockPurchasesApi()

}