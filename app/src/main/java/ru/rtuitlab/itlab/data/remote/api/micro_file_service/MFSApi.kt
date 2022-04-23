package ru.rtuitlab.itlab.data.remote.api.micro_file_service

import retrofit2.Response
import retrofit2.http.*
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.File
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.Message

interface MFSApi {
	@GET("/mfs/download/{id}")
	suspend fun downloadFile(
		@Path("id") fileId:String //required
	): Response<Unit>

	@GET("/mfs/files")
	suspend fun getFilesInfo(
		@Query("user") userId:String? = null,
		@Query("sorted_by") sortedBy:String? = null
	):List<File>

	@POST("/mfs/files/upload")
	suspend fun uploadFile(
		//
	):File

	@GET("/mfs/files/{id}")
	suspend fun getFileInfo(
		@Path("id") fileId:String //required
	):File

	@DELETE("/mfs/files/{id}")
	suspend fun deleteFile(
		@Path("id") fileId:String //required
	):Response<Unit>


}