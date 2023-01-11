package ru.rtuitlab.itlab.data.remote

import okhttp3.RequestBody
import retrofit2.Response
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.MfsApi
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.FileInfoResponse

class MockMfsApi: MfsApi {
    override suspend fun downloadFile(fileId: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getFilesInfo(userId: String?, sortedBy: String?): List<FileInfoResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadFile(body: RequestBody): FileInfoResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getFileInfo(fileId: String): FileInfoResponse {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFile(fileId: String): Response<Unit> {
        TODO("Not yet implemented")
    }
}