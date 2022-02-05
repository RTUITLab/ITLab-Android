package ru.rtuitlab.itlab.data.remote.api.devices.models

import kotlinx.serialization.Serializable

@Serializable
data class CompactDeviceTypeModel (
    val id: String,
    val title: String? = null,
    val shortTitle: String? = null,
    val description: String? = null,
    val rootId: String? = null,
    val parentId: String? = null
)