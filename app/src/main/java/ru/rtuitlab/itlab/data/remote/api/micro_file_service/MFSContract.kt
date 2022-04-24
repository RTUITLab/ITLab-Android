package ru.rtuitlab.itlab.data.remote.api.micro_file_service

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.MFSViewModel
import javax.inject.Inject

class MFSContract @Inject constructor(
	private val mfsViewModel: MFSViewModel
) : ActivityResultContract<Int, Unit>() {

	override fun createIntent(context: Context, input: Int?): Intent {
		return Intent()
			.setType("*/*")
			.setAction(Intent.ACTION_GET_CONTENT)
	}

	override fun parseResult(resultCode: Int, intent: Intent?): Unit? = when {
		resultCode != Activity.RESULT_OK -> null
		else -> {
			val selectedFile = intent?.data //The uri with the location of the file
			mfsViewModel.setFileUri(selectedFile)
		}
	}

	/*override fun getSynchronousResult(context: Context, input: String?): SynchronousResult<Int?>? {
		return if (input.isNullOrEmpty()) SynchronousResult(42) else null
	}*/
}