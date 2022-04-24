package ru.rtuitlab.itlab.data.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.MFSApi
import java.io.File
import javax.inject.Inject

class MFSRepository @Inject constructor(
	private val MFSApi: MFSApi,
	private val handler: ResponseHandler
) {



	suspend fun fetchFile(fileId:String) = handler {
		MFSApi.downloadFile(fileId)
	}
	suspend fun fetchFilesInfo(userId:String,sortedBy:String) = handler {
		MFSApi.getFilesInfo(userId,sortedBy)
	}
	suspend fun fetchFileInfo(fileId:String) = handler {
		MFSApi.getFileInfo(fileId)
	}
	suspend fun uploadFile(file:File, fileDescription:String) = handler {
		val fields: HashMap<String,RequestBody> = HashMap()
		fields["uploadingForm"] = (file).asRequestBody("text/plain".toMediaTypeOrNull())
		fields["fileDescription"] = (fileDescription).toRequestBody("text/plain".toMediaTypeOrNull())
		MFSApi.uploadFile(fields)
	}
	suspend fun deleteFile(fileId: String) = handler {
		MFSApi.deleteFile(fileId)
	}
}