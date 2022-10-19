package ru.rtuitlab.itlab

import ru.rtuitlab.itlab.data.local.users.models.PropertyWithType
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserPropertyEntity
import ru.rtuitlab.itlab.data.local.users.models.UserWithProperties
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyTypeModel

object Generator {
    val users = Array(15) {
        UserEntity(
            id = it.toString(),
            email = "example$it@test.test",
            phoneNumber = "899${it}6665544",
            firstName = "Whatever$it",
            middleName = "Whatever$it",
            lastName = "Whatever$it"
        )
    }.toMutableList()

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

    fun makeProp(id: Int) = UserPropertyEntity(
        value = when(id % 3) {
            0 -> "vk.com/id$id"
            1 -> "discord.gg/id$id"
            2 -> "skype.com/id$id"
            else -> ""
        },
        typeId = propertyTypes[id % 3].id,
        userId = id.toString()
    )

    val props: List<UserPropertyEntity> = users.map {
        makeProp(it.id.toInt())
    }

    val propsWithTypes = props.map { prop ->
        PropertyWithType(
            property = prop,
            type = propertyTypes.find { it.id == prop.typeId }!!
        )
    }

    val usersWithProps = users.map { user ->
        UserWithProperties(
            userEntity = user,
            properties = propsWithTypes.filter { it.property.userId == user.id }
        )
    }
}