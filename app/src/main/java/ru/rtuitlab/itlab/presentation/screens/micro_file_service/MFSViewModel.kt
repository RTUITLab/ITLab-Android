package ru.rtuitlab.itlab.presentation.screens.micro_file_service

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.emitInIO
import ru.rtuitlab.itlab.data.remote.api.events.models.EventModel
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.FileInfo
import ru.rtuitlab.itlab.data.repository.MFSRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MFSViewModel @Inject constructor(
	private val repository: MFSRepository
): ViewModel() {

	private var _requestPermissionLauncher = MutableStateFlow< ActivityResultLauncher<String>?>(null)
	val requestPermissionLauncher = _requestPermissionLauncher.asStateFlow()

	private var _activity = MutableStateFlow<Activity?>(null)
	val activity = _activity.asStateFlow()

	private var _mfsContract = MutableStateFlow<ActivityResultLauncher<Int?>?>(null)
	val mfsContract = _mfsContract.asStateFlow()

	private var _accessPermission = MutableStateFlow<Boolean>(false)
	val accessPermission = _accessPermission.asStateFlow()

	private val MFSCONST = 69
	private val _fileUri = MutableStateFlow<Uri?>(null)
	val fileUri = _fileUri.asStateFlow()

	private val _file = MutableStateFlow<File?>(null)
	val file = _file.asStateFlow()

	private val _listFileInfoResponseFlow = MutableStateFlow<Resource<List<FileInfo>>>(Resource.Loading)
	val listFileInfoResponseFlow = _listFileInfoResponseFlow.asStateFlow().also {fetchResponseListFileInfo() }

	private var cachedFileInfoList = emptyList<FileInfo>()

	private var _listFileInfoFlow = MutableStateFlow(cachedFileInfoList)
	val listFileInfoFlow = _listFileInfoFlow.asStateFlow()

	private val _listSortedBy = MutableStateFlow<String?>(null)

	private val _listUserId = MutableStateFlow<String?>(null)

	fun onRefreshEquipmentTypes() = fetchResponseListFileInfo()

	fun onSearch(query: String) {
		_listFileInfoFlow.value = cachedFileInfoList.filter { filterSearchResult(it, query) }

	}

	private fun fetchResponseListFileInfo(userId:String?=null,sortedBy:String?=null) = _listFileInfoResponseFlow.emitInIO(viewModelScope) {
		repository.fetchFilesInfo(userId,sortedBy)
	}

	//sortedBy - date or name
	//userId - id of user
	fun setUserIdAndSortedBy(userId:String, sortedBy:String) {
		_listSortedBy.value = sortedBy
		_listUserId.value = userId
		fetchResponseListFileInfo(
			userId = userId,
			sortedBy = sortedBy
		)
	}

	fun changeAccess(access: Boolean){
		_accessPermission.value = access
		if(access)
			provideFile()
	}

	fun provideRequestPermissionLauncher(activity: Activity,requestPermissionLauncher: ActivityResultLauncher<String>){
		_activity.value = activity
		_requestPermissionLauncher.value = requestPermissionLauncher
		_accessPermission.value = (ContextCompat.checkSelfPermission(_activity.value!!.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
		//onRequestPermission(_activity.value!!)
		Log.d("MFS","access ${_accessPermission.value}")
	}

	fun provideMFSContract(contract: ActivityResultLauncher<Int?>){
		_mfsContract.value = contract
	}

	fun setFilePath(filePath: Uri?){
		_fileUri.value = filePath

		_file.value = File(filePath!!.path!!)
		Log.d("MFS","${_file.value!!.name} ----------")

	}

	fun setFileNull(){
		_fileUri.value = null
		_file.value = null
	}
	fun provideFile() = viewModelScope.launch(Dispatchers.IO) {

		if(_activity.value!=null) {
			if (_accessPermission.value) {
				_mfsContract.value?.launch(MFSCONST)
			}
			else {
				Log.d("MFS","${_activity.value} ----------")

				onRequestPermission(_activity.value!!)
			}
		}
	}
	fun uploadFile(fileDescription: String) = viewModelScope.launch(Dispatchers.IO){
		var resource: Resource<FileInfo> = Resource.Loading

		if(_file.value != null) {
			Log.d("MFS", _file.value!!.absolutePath)

			repository.uploadFile(_file.value!!, fileDescription).handle (
				onError = {
					resource = Resource.Error(it)
				},
				onSuccess = { fileInfo ->
						//Toast.makeText(activity,"Complete Success",Toast.LENGTH_SHORT).show()
					Log.d("MFS","$fileInfo")
					resource = Resource.Success(fileInfo)
				}
			)
		}
	}

	private fun onRequestPermission(activity: Activity) {
		when {
			(ContextCompat.checkSelfPermission(_activity.value!!.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ->
			{
				Log.d("MFS----","here")
				_accessPermission.value = true
			}

			ActivityCompat.shouldShowRequestPermissionRationale(
				activity,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
			) -> {

				_requestPermissionLauncher.value?.launch(
					Manifest.permission.WRITE_EXTERNAL_STORAGE)


			}

			else -> {

				_requestPermissionLauncher.value?.launch(
					Manifest.permission.WRITE_EXTERNAL_STORAGE
				)

				Log.d("MFS----","here3")

			}
		}
	}

	private fun filterSearchResult(fileInfo: FileInfo, query: String) = fileInfo.run {
		filename.contains(query.trim(), ignoreCase = true) ||
				id.contains(query.trim(), ignoreCase = true) ||
				uploadDate.contains(query.trim(), ignoreCase = true)
	}
}