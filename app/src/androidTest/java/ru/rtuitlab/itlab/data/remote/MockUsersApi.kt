package ru.rtuitlab.itlab.data.remote

import ru.rtuitlab.itlab.Constants
import ru.rtuitlab.itlab.Generator
import ru.rtuitlab.itlab.data.local.users.models.UserPropertyEntity
import ru.rtuitlab.itlab.data.remote.api.devices.models.DeviceModel
import ru.rtuitlab.itlab.data.remote.api.users.UsersApi
import ru.rtuitlab.itlab.data.remote.api.users.models.*

class MockUsersApi: UsersApi {

    val currentUserId = Constants.CURRENT_USER_ID

    val propertyTypes = Array(3) {
        UserPropertyTypeModel(
            id = it.toString(),
            title = when(it % 3) {
                0 -> "VK ID"
                1 -> "Discord"
                2 -> "Skype"
                else -> ""
            },
            description = "Whatever",
            instancesCount = 7,
            isLocked = false
        )
    }.toMutableList()

    val users = Array(15) {
        UserResponse(
            id = it.toString(),
            email = "example$it@test.test",
            phoneNumber = "899${it}6665544",
            firstName = "Whatever$it",
            middleName = "Whatever$it",
            lastName = "Whatever$it",
            properties = makeProps(it)
        )
    }.toMutableList()

    val props = users.map {
        makeProp(it.id.toInt())
    }

    fun makeProp(id: Int) = UserPropertyModel(
        value = when(id % 3) {
            0 -> "vk.com/id$id"
            1 -> "discord.gg/id$id"
            2 -> "skype.com/id$id"
            else -> ""
        },
        status = null,
        userPropertyType = propertyTypes[id % 3]
    )

    fun makeProps(id: Int) = Array(1) {
        UserPropertyModel(
            value = when(id % 3) {
                0 -> "vk.com/id$id"
                1 -> "discord.gg/id$id"
                2 -> "skype.com/id$id"
                else -> ""
            },
            status = null,
            userPropertyType = propertyTypes[id % 3]
        )
    }.toMutableList()

    override suspend fun getUserInfo(url: String, token: String): UserInfoModel {
        return UserInfoModel(
            sub = "dafnapfing ipgasdfdas",
            preferredUsername = "Whatever",
            name = "What Ever"
        )
    }

    override suspend fun getUser(userId: String): UserResponse {
        return users.find { it.id == userId }!!
    }

    override suspend fun getUserDevices(userId: String): List<DeviceModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserEvents(
        userId: String,
        beginTime: String,
        endTime: String
    ): List<UserEventModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers(): List<UserResponse> {
        return users
    }

    override suspend fun editUserInfo(info: UserEditRequest): UserResponse {
        return users[currentUserId].copy(
            firstName = info.firstName,
            middleName = info.middleName,
            lastName = info.lastName,
            phoneNumber = info.phoneNumber
        ).apply {
            users[currentUserId] = this
        }
    }

    override suspend fun getPropertyTypes(): List<UserPropertyTypeModel> {
        return propertyTypes
    }

    override suspend fun editUserProperty(property: UserPropertyEditRequest): UserPropertyModel {
        return users[currentUserId].properties[0].copy(
            value = property.value
        )
    }
}