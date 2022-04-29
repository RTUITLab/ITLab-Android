package ru.rtuitlab.itlab.data.remote.api.micro_file_service

import RealPathUtil
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract


class MFSContract: ActivityResultContract<Int, Uri>() {
	private lateinit var context: Context
	override fun createIntent(context: Context, input: Int?): Intent {
		this.context = context
		return Intent()
			.setType("video/*")
			.addCategory(Intent.CATEGORY_OPENABLE)
			.setAction(Intent.ACTION_OPEN_DOCUMENT)
	}

	override fun parseResult(resultCode: Int, intent: Intent?): Uri? = when {
		resultCode != Activity.RESULT_OK -> null
		else -> {
			val selectedFile = intent?.data!! //The uri with the location of the file
			Log.d("MFS","$selectedFile")
			Uri.parse(RealPathUtil.writeFileContent(context,selectedFile)!!)


		}
	}

	/*override fun getSynchronousResult(context: Context, input: String?): SynchronousResult<Int?>? {
		return if (input.isNullOrEmpty()) SynchronousResult(42) else null
	}*/


}