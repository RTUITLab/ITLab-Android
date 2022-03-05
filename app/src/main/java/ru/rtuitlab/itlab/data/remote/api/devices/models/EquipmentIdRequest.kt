package ru.rtuitlab.itlab.data.remote.api.devices.models

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentIdRequest (
        var id:String?=null
)

