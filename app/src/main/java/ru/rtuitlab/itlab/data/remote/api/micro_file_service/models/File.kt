package ru.rtuitlab.itlab.data.remote.api.micro_file_service.models

data class File (
	val chunkSize: Int,
	val filename: String,
	val id: String,
	val length: Int,
	val metadata: Metadata,
	val uploadDate: String
)
{
}