package ru.rtuitlab.itlab.data.remote.api.devices.models

import kotlinx.serialization.Serializable


@Serializable
data class DeviceDetails (
        val id: String,
        var serialNumber: String? = null,
        val description : String? = null,
        val number: Int,
        var equipmentType: EquipmentTypeResponse,
        val equipmentTypeId: String,
        var ownerId: String? = null,
        val parentId: String? = null,
        val children: List<DeviceDetailDto>? = null,

        val ownerfirstName : String? = null,
        val ownerlastName : String? = null,
        val ownermiddleName : String? = null,
        val ownerphoneNumber : String? = null,
        val owneremail : String? = null,
        val ownervkId : String? = null,
        val ownergroup: String? = null,
        val ownerdiscordId: String? = null,
        val ownerskypeId: String? = null
)
