package ru.rtuitlab.itlab.data.remote.api.devices.models

import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.data.remote.api.users.models.User


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

){
        fun toDeviceDtoAndUser(deviceDetailDto:(DeviceDetailDto) -> Unit,owner: (User?) -> Unit) {
               deviceDetailDto( DeviceDetailDto(
                        id = id,
                        serialNumber = serialNumber,
                        description = description,
                        number = number,
                        equipmentTypeId = equipmentTypeId,
                        equipmentType = equipmentType,
                        parentId = parentId,
                        children = children,
                        ownerId = ownerId
                ))
                var user: User? = null
                if(ownerId != null)
                       user = User(
                                id = ownerId!!,
                                firstName = ownerfirstName,
                                lastName = ownerlastName,
                                middleName = ownermiddleName,
                                phoneNumber = ownerphoneNumber,
                                email = owneremail,
                                vkId = ownervkId,
                                group = ownergroup,
                                skypeId = ownerskypeId,
                                discordId = ownerdiscordId
                        )
                owner(user)
        }
}
