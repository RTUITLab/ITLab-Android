package ru.rtuitlab.itlab

import ru.rtuitlab.itlab.data.local.events.models.EventDetailEntity
import ru.rtuitlab.itlab.data.local.events.models.EventEntity
import ru.rtuitlab.itlab.data.local.events.models.PlaceEntity
import ru.rtuitlab.itlab.data.local.events.models.ShiftEntity
import ru.rtuitlab.itlab.data.local.users.models.PropertyWithType
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.data.local.users.models.UserPropertyEntity
import ru.rtuitlab.itlab.data.local.users.models.UserWithProperties
import ru.rtuitlab.itlab.data.remote.api.events.models.EventTypeModel
import ru.rtuitlab.itlab.data.remote.api.users.models.UserPropertyTypeModel

object Generator {

    // Users
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


    // Events

    val eventTypes = Array(3) {
        EventTypeModel(
            id = it.toString(),
            title = "Выставка $it",
            description = "Description $it"
        )
    }

    val eventEntities = Array(5) {
        EventEntity(
            id = it.toString(),
            title = "Event #$it",
            beginTime = "2022-0${it + 1}-25T11:07:44Z",
            endTime = "2022-0${it + 1}-25T18:07:44Z",
            typeId = eventTypes[it % 3].id,
            address = "Вернадского $it",
            shiftsCount = 3,
            currentParticipantsCount = 3,
            targetParticipantsCount = 5,
            participating = false
        )
    }

    val eventShifts = mutableListOf<ShiftEntity>()
    val shiftPlaces = mutableListOf<PlaceEntity>()

    val eventDetails = eventEntities.map { event ->

        repeat(3) { shiftNum ->
            eventShifts.add(
                ShiftEntity(
                    id = "Event ${event.id} Shift $shiftNum",
                    beginTime = event.beginTime,
                    endTime = event.endTime ?: "",
                    description = "Shift description $shiftNum",
                    eventId = event.id
                )
            )
            repeat(3) {
                shiftPlaces.add(
                    PlaceEntity(
                        id = "Shift ${eventShifts[shiftNum].id} place $it",
                        targetParticipantsCount = 3,
                        description = "Place $it description",
                        shiftId = eventShifts[shiftNum].id
                    )
                )
            }
        }

        EventDetailEntity(
            id = event.id,
            title = event.title,
            description = "Detailed description ${event.id}",
            address = event.address,
            eventId = event.id
        )
    }
}