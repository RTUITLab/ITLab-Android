package ru.rtuitlab.itlab.data.remote.api.devices

import retrofit2.Response
import retrofit2.http.*
import ru.rtuitlab.itlab.data.remote.api.devices.models.*
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse


interface DevicesApi {

        @GET("equipment?count=-1")
        suspend fun getDevices() : List<DeviceDetailDto>

        @DELETE("equipment/{id}")
        suspend fun deleteDevice(
                @Path("id") id: Int
        ): Response<Unit>

        @GET("equipment/{deviceId}")
        suspend fun getDevice(
                @Path("deviceId") deviceId: String
        ) : DeviceDetailDto

        @GET("user/{ownerid}")
        suspend fun getOwner(
                @Path("ownerid") ownerId: String
        ): UserResponse

        @POST("EquipmentType")
        suspend fun createEquipmentType(
                @Body equipmentTypeNewRequest: EquipmentTypeNewRequest
        ):Response<Unit>

        @POST("Equipment")
        suspend fun createEquipment(
                @Body equipmentNewRequest: EquipmentNewRequest
        ):Response<Unit>

        @PUT("Equipment")
        suspend fun updateEquipment(
                @Body equipmentEditRequest: EquipmentEditRequest
        ):Response<Unit>

        @HTTP(method = "DELETE",  path = "Equipment",hasBody = true)
        suspend fun deleteEquipment(
                @Body id:EquipmentIdRequest
        ):Response<Unit>

        @GET("EquipmentType")
        suspend fun getListEquipmentType(
                @Query("match")match:String,
                @Query("all")all:Boolean
        ): List<EquipmentTypeResponse>
}

