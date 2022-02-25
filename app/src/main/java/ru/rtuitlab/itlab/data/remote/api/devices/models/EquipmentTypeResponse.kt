package ru.rtuitlab.itlab.data.remote.api.devices.models

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentTypeResponse(
        val id:String,
        var title:String,
        var shortTitle:String? = null,
        var description:String? = null,
        val rootId:String? = null,
        val parentId:String? = null
) {
}
