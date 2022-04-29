package ru.rtuitlab.itlab.data.remote.api.micro_file_service.models

import kotlinx.serialization.Serializable

@Serializable
data class FileInfo (
	val chunkSize: Int,
	val filename: String,
	val id: String,
	val length: Int,
	val metadata: Metadata,
	val uploadDate: String
)
{
}