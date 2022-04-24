package ru.rtuitlab.itlab.presentation.screens.micro_file_service

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.MFSContract
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.FileInfo
import ru.rtuitlab.itlab.data.remote.api.reports.models.Report
import ru.rtuitlab.itlab.data.repository.MFSRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MFSViewModel @Inject constructor(
	private val repository: MFSRepository
): ViewModel() {

	var _mfsContract = MutableStateFlow<ActivityResultLauncher<Int?>?>(null)
	val mfsContract = _mfsContract.asStateFlow()

	val MFSCONST = 69
	private val _fileUri = MutableStateFlow<Uri?>(null)
	val fileUri = _fileUri.asStateFlow()

	private val _file = MutableStateFlow<File?>(null)
	val file = _file.asStateFlow()


	fun setFileUri(fileUri: Uri?){
		_fileUri.value = fileUri
	}

	fun setFileNull(){
		_fileUri.value = null
	}
	fun provideFile() = viewModelScope.launch(Dispatchers.IO) {
		_mfsContract.value?.launch(MFSCONST)
	}
	fun uploadFile(fileDescription: String) = viewModelScope.launch(Dispatchers.IO){
		var resource: Resource<FileInfo> = Resource.Loading

		if(_fileUri.value != null) {
			_file.value = File(_fileUri.value!!.path!!)
			repository.uploadFile(_file.value!!, fileDescription).handle (
				onError = {
					resource = Resource.Error(it)
				},
				onSuccess = { fileInfo ->
						//Toast.makeText(activity,"Complete Success",Toast.LENGTH_SHORT).show()
					resource = Resource.Success(fileInfo)
				}
			)
		}
	}
}