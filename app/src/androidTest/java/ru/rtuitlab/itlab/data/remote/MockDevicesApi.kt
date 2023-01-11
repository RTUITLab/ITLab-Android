package ru.rtuitlab.itlab.data.remote

import retrofit2.Response
import ru.rtuitlab.itlab.data.remote.api.devices.DevicesApi
import ru.rtuitlab.itlab.data.remote.api.devices.models.*
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse

class MockDevicesApi: DevicesApi {
    override suspend fun getDevices(): List<DeviceDetailDto> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDevice(id: Int): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getDevice(deviceId: String): DeviceDetailDto {
        TODO("Not yet implemented")
    }

    override suspend fun getOwner(ownerId: String): UserResponse {
        TODO("Not yet implemented")
    }

    override suspend fun createEquipment(equipmentNewRequest: EquipmentNewRequest): DeviceDetailDto {
        TODO("Not yet implemented")
    }

    override suspend fun updateEquipment(equipmentEditRequest: EquipmentEditRequest): DeviceDetailDto {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEquipment(id: EquipmentIdRequest): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createEquipmentType(equipmentTypeNewRequest: EquipmentTypeNewRequest): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getEquipmentTypes(
        match: String,
        all: Boolean
    ): List<EquipmentTypeResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun setOwner(userId: String, id: EquipmentIdRequest): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOwner(userId: String, id: EquipmentIdRequest): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getFreeEquipment(): List<DeviceDetailDto> {
        TODO("Not yet implemented")
    }
}