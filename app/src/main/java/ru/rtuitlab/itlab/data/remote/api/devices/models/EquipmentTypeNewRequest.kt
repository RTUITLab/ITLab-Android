package ru.rtuitlab.itlab.data.remote.api.devices.models

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentTypeNewRequest (
         var title: String? = null,
         var shortTitle: String? = null,
         var description:String? = null,
         var parentId:String? =null
)
