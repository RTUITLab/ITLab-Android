package ru.rtuitlab.itlab.data.remote.api.devices.models

import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.remote.api.users.models.User

@Serializable
data class DeviceDetailDto (
        val id : String,
        val serialNumber : String? = null,
        val description : String? = null,
        val number: Int,
        val equipmentTypeId : String,
        val equipmentType : EquipmentTypeResponse,
        val ownerId : String? = null,
        val parentId: String? = null,
        val children: List<DeviceDetailDto>? = null,

        ) {
        fun toDevice(owner: User?) =
                DeviceDetails(
                        id = id,
                        serialNumber = serialNumber,
                        description = description,
                        number = number,
                        equipmentTypeId = equipmentTypeId,
                        equipmentType = equipmentType,
                        parentId = parentId,
                        children = children,
                        ownerId = ownerId,
                        ownerfirstName = owner?.firstName,
                        ownerlastName = owner?.lastName,
                        ownermiddleName = owner?.middleName,
                        ownerphoneNumber = owner?.phoneNumber,
                        owneremail = owner?.email,
                        ownervkId = owner?.vkId,
                        ownergroup = owner?.group,
                        ownerskypeId = owner?.skypeId,
                        ownerdiscordId = owner?.discordId
                )
}

