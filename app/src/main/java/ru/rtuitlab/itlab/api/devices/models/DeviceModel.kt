package ru.rtuitlab.itlab.api.devices.models

import kotlinx.serialization.Serializable

@Serializable
data class DeviceModel (
    val id: String,
    val serialNumber: String? = null,
    val description: String? = null,
    val number: Int,
    val equipmentType: CompactDeviceTypeModel,
    val equipmentTypeId: String,
    val ownerId: String? = null,
    val parentId: String? = null,
    val children: List<DeviceModel>? = null
)