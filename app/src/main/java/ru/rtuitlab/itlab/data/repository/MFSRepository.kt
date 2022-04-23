package ru.rtuitlab.itlab.data.repository

import ru.rtuitlab.itlab.common.ResponseHandler
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.MFSApi
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
	suspend fun deleteFile(fileId: String) = handler {
		MFSApi.deleteFile(fileId)
	}
}