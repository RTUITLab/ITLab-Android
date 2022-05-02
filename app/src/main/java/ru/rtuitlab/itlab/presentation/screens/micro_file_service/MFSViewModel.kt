package ru.rtuitlab.itlab.presentation.screens.micro_file_service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Retrofit
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.emitInIO
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.FileInfo
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.FileInfoResponse
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
import ru.rtuitlab.itlab.data.repository.MFSRepository
import ru.rtuitlab.itlab.data.repository.UsersRepository
import ru.rtuitlab.itlab.presentation.utils.DownloadFileFromWeb
import ru.rtuitlab.itlab.presentation.utils.DownloadFileFromWeb.saveToInternalStorage
import ru.rtuitlab.itlab.presentation.utils.DownloadFileFromWeb.toBitmap
import java.io.File
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class MFSViewModel @Inject constructor(
	private val repository: MFSRepository,
	private val usersRepository: UsersRepository
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

	private val _listFileInfoResponseFlow = MutableStateFlow<Resource<MutableList<Pair<FileInfoResponse, UserResponse?>>>>(Resource.Loading)
	val listFileInfoResponseFlow = _listFileInfoResponseFlow.asStateFlow().also {fetchListFileInfoResponseWithUser() }

	private var cachedFileInfoList = emptyList<FileInfo>()

	private var _listFileInfoFlow = MutableStateFlow(cachedFileInfoList)
	val listFileInfoFlow = _listFileInfoFlow.asStateFlow()

	private val _listSortedBy = MutableStateFlow<String?>(null)

	private val _listUserId = MutableStateFlow<String?>(null)

	private val _whenOKcode = MutableStateFlow<(suspend () -> Unit)?>(null)
	fun onRefresh() = fetchListFileInfoResponseWithUser()

	fun onSearch(query: String) {
		_listFileInfoFlow.value = cachedFileInfoList.filter { filterSearchResult(it, query) }

	}

	fun onResourceSuccess(files: List<Pair<FileInfoResponse,UserResponse?>>) {

		cachedFileInfoList = files.map {
			if(it.second!=null){
			it.first.toFileInfo(it.second!!.toUser())
		}else{
			it.first.toFileInfo(null)
		}}

		_listFileInfoFlow.value = cachedFileInfoList
	}

	/*private fun fetchListFileInfoResponseWithoutUser(userId:String?=null,sortedBy:String?=null) = _listFileInfoResponseFlow.emitInIO(viewModelScope) {
		repository.fetchFilesInfo(userId,sortedBy)
	}*/
	private fun fetchListFileInfoResponseWithUser(userId:String?=null,sortedBy:String?=null) = _listFileInfoResponseFlow.emitInIO(viewModelScope) {

		var resources: Resource<MutableList<Pair<FileInfoResponse, UserResponse?>>> =
			Resource.Loading
		var usersList: MutableList<UserResponse>? = null
		usersRepository.fetchUsers().handle(
			onSuccess = { users ->
				usersList = users as MutableList<UserResponse>
			}
		)
		repository.fetchFilesInfo(userId,sortedBy).handle(
			onSuccess = { files ->
				val listPair = mutableListOf<Pair<FileInfoResponse, UserResponse?>>()

				files.map { file ->
					var resource: Pair<FileInfoResponse, UserResponse?>

					val sender = usersList?.find { it.id == file.metadata.fileSender }
					resource = file to sender
					listPair.add(resource)
				}
				resources = Resource.Success(listPair)

			},
			onError = { resources = Resource.Error(it) }
		)
		resources
	}
	//sortedBy - date or name
	//userId - id of user
	fun setUserIdAndSortedBy(userId:String, sortedBy:String) {
		_listSortedBy.value = sortedBy
		_listUserId.value = userId
		fetchListFileInfoResponseWithUser(
			userId = userId,
			sortedBy = sortedBy
		)
	}

	fun changeAccess(access: Boolean){
		_accessPermission.value = access
		if(access)
			viewModelScope.launch(Dispatchers.IO) {
				_whenOKcode.value?.invoke()
			}
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

				onRequestPermission(_activity.value!!){
					_mfsContract.value?.launch(MFSCONST)
				}
			}
		}
	}
	fun uploadFile(fileDescription: String) = viewModelScope.launch(Dispatchers.IO){
		var resource: Resource<FileInfoResponse> = Resource.Loading

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
	fun downloadFile(context: Context, fileInfo: FileInfo) = viewModelScope.launch(Dispatchers.IO){
		val repFetchFile = suspend {
			Log.d("MFSstring", repository.fetchFile(fileInfo.id))
			DownloadFileFromWeb.downloadFile(context,repository.fetchFile(fileInfo.id),fileInfo)

		}

		if (_accessPermission.value) {
			repFetchFile()
		}
		else {
			onRequestPermission(_activity.value!!){
				repFetchFile()
			}
		}

	}
	fun getBitmapFromFile(context: Context,fileInfo: FileInfo,setBitmap: (Bitmap?) ->Unit) {

		var urlImage: URL? = URL(repository.fetchFile(fileInfo.id))

		viewModelScope.launch(Dispatchers.IO) {
			// get saved bitmap internal storage uri
			val j = async { urlImage?.toBitmap() }
			val bitmap = j.await()
			val savedUri : Uri? = bitmap?.saveToInternalStorage(context)

			setBitmap(BitmapFactory.decodeFile(savedUri?.path))
		}
	}

	private fun onRequestPermission(activity: Activity, whenOKcode:suspend () ->Unit) {
		when {
			(ContextCompat.checkSelfPermission(_activity.value!!.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ->
			{
				_accessPermission.value = true
				_whenOKcode.value = { whenOKcode() }

			}

			ActivityCompat.shouldShowRequestPermissionRationale(
				activity,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
			) -> {
				_whenOKcode.value = { whenOKcode() }

				_requestPermissionLauncher.value?.launch(
					Manifest.permission.WRITE_EXTERNAL_STORAGE)


			}

			else -> {
				_whenOKcode.value = { whenOKcode() }

				_requestPermissionLauncher.value?.launch(
					Manifest.permission.WRITE_EXTERNAL_STORAGE
				)


			}
		}
	}

	private fun filterSearchResult(fileInfo: FileInfo, query: String) = fileInfo.run {
		filename.contains(query.trim(), ignoreCase = true) ||
				id.contains(query.trim(), ignoreCase = true) ||
				uploadDate.contains(query.trim(), ignoreCase = true)
	}

	fun parseUploadDate(uploadDate: String): String {
		val date = uploadDate.split("T")
		val YYMMDD = date[0]
		val hhmmss = date[1].split(".")[0]

		return "$YYMMDD $hhmmss"
	}
}