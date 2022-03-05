package ru.rtuitlab.itlab.data.remote.api.devices.models

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentEditRequest (
        var serialNumber: String? = null,
        var equipmentTypeId: String? = null,
        var description:String? = null,
        var parentId:String? = null,
        var delete:Boolean = true,
        var id:String? = null
)
