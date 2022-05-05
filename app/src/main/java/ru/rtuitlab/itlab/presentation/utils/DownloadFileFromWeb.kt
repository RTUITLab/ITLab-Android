package ru.rtuitlab.itlab.presentation.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.FileInfo
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL
import java.util.*

object DownloadFileFromWeb {
	private var msg: String? = ""
	private var lastMsg = ""

	@SuppressLint("Range")
	fun downloadFile(context:Context, url: String,fileInfo: FileInfo) {

		val directory = File(Environment.DIRECTORY_DOWNLOADS)

		val file = context.getExternalFilesDir(null)!!

		if (!directory.exists()) {
			directory.mkdirs()
		}

		val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

		val downloadUri = Uri.parse(url)

		val request = DownloadManager.Request(downloadUri).apply {
			setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
				.setAllowedOverRoaming(false)
				.setTitle(fileInfo.filename)
				.setDescription(fileInfo.fileDescription)
				.setDestinationInExternalPublicDir(
					directory.toString(),
					fileInfo.filename
				)
			Log.d("DOWN",fileInfo.filename )
		}

		val downloadId = downloadManager.enqueue(request)
		val query = DownloadManager.Query().setFilterById(downloadId)
		runBlocking {
			var downloading = true
			while (downloading) {
				val cursor: Cursor = downloadManager.query(query)
				cursor.moveToFirst()
				if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
					downloading = false
				}
				val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

				cursor.close()
			}
		}
	}

	private fun statusMessage(url: String, directory: File, status: Int): String? {
		var msg = ""
		msg = when (status) {
			DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
			DownloadManager.STATUS_PAUSED -> "Paused"
			DownloadManager.STATUS_PENDING -> "Pending"
			DownloadManager.STATUS_RUNNING -> "Downloading..."
			DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
				url.lastIndexOf("/") + 1
			)
			else -> "There's nothing to download"
		}
		return msg
	}

	// extension function to get / download bitmap from url
	fun URL.toBitmap(): Bitmap?{
		return BitmapFactory.decodeStream(openStream())
	}
	// extension function to save an image to internal storage
	fun Bitmap.saveToInternalStorage(context : Context,fileInfo: FileInfo?):Uri? {
		// get the context wrapper instance
		val wrapper = ContextWrapper(context)

		// initializing a new file
		// bellow line return a directory in internal storage
		var file = wrapper.getDir("images", Context.MODE_PRIVATE)

		// create a file to save the image
		file = File(file, fileInfo?.id.toString())

		// get the file output stream
		val stream: OutputStream = FileOutputStream(file)

		// compress bitmap
		compress(Bitmap.CompressFormat.JPEG, 10, stream)

		// flush the stream
		stream.flush()

		// close stream
		stream.close()

		// return the saved image uri
		return Uri.parse(file.absolutePath)

	}
}