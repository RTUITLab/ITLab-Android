package ru.rtuitlab.itlab.data.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.rtuitlab.itlab.BuildConfig
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.MfsApi
import java.io.File
import javax.inject.Inject


class MfsRepository @Inject constructor(
	private val mfsApi: MfsApi,
	private val handler: ResponseHandler
) {

	fun fetchFile(fileId:String?):String {
		return (BuildConfig.API_URI+"mfs/download/"+fileId)
	}

	suspend fun fetchFilesInfo(userId:String?=null,sortedBy:String?=null) = handler {
		mfsApi.getFilesInfo(userId,sortedBy)
	}
	suspend fun fetchFileInfo(fileId:String) = handler {
		mfsApi.getFileInfo(fileId)
	}
	suspend fun uploadFile(file:File, fileDescription:String) = handler {
		val requestBody: RequestBody = MultipartBody.Builder()
			.setType(MultipartBody.FORM)
			.addFormDataPart("uploadingForm", file.name,(file).asRequestBody("text/plain".toMediaTypeOrNull()))
			.addFormDataPart("fileDescription", fileDescription)
			.build()

		mfsApi.uploadFile(requestBody)
	}
	suspend fun deleteFile(fileId: String) = handler {
		mfsApi.deleteFile(fileId)
	}
}