package ru.rtuitlab.itlab.data.repository

import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.remote.api.devices.DevicesApi
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentEditRequest
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentIdRequest
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentNewRequest
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentTypeNewRequest
import javax.inject.Inject

class DevicesRepository @Inject constructor(
    private val devicesApi: DevicesApi,
    private val handler: ResponseHandler
) {


    suspend fun fetchDevices() = handler {
        devicesApi.getDevices()
    }

    suspend fun fetchOwner(ownerId: String) = handler {
        devicesApi.getOwner(ownerId)
    }

    suspend fun createDevice(equipmentNewRequest: EquipmentNewRequest) = handler {
        devicesApi.createEquipment(equipmentNewRequest)
    }

    suspend fun editDevice(equipmentEditRequest: EquipmentEditRequest) = handler {
        devicesApi.updateEquipment(equipmentEditRequest)
    }

    suspend fun deleteDevice(id: String) = handler {
        devicesApi.deleteEquipment(EquipmentIdRequest(id))
    }

    //EquipmentType
    suspend fun createEquipmentType(equipmentTypeNewRequest: EquipmentTypeNewRequest) = handler {
        devicesApi.createEquipmentType(equipmentTypeNewRequest)
    }

    suspend fun fetchEquipmentTypes(match: String, all: Boolean) = handler {
        devicesApi.getEquipmentTypes(match, all)
    }

    //EquipmentOwner
    suspend fun setEquipmentOwner(ownerId: String, equipmentId: String) = handler {
        devicesApi.setOwner(ownerId, EquipmentIdRequest(equipmentId))
    }

    suspend fun fetchEquipmentOwnerPickUp(ownerId: String, equipmentId: String) = handler {
        devicesApi.deleteOwner(ownerId, EquipmentIdRequest(equipmentId))
    }

    //Filtering
    suspend fun fetchFreeEquipment() = handler {
        devicesApi.getFreeEquipment()
    }

}

