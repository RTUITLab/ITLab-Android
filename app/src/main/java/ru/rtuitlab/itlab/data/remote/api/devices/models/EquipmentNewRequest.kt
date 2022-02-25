package ru.rtuitlab.itlab.data.remote.api.devices.models

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentNewRequest(
        var serialNumber: String? = null,
        var equipmentTypeId: String? = null,
        var description:String? = null,
        var children: List<String> = emptyList()
) {
}
