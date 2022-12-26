package ru.rtuitlab.itlab.presentation.screens.micro_file_service

import ru.rtuitlab.itlab.presentation.utils.RealPathUtil
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.extensions.collectUntil
import ru.rtuitlab.itlab.common.extensions.emitInIO
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.FileInfo
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.FileInfoResponse
import ru.rtuitlab.itlab.data.repository.MfsRepository
import ru.rtuitlab.itlab.domain.use_cases.users.GetCurrentUserUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUsersUseCase
import ru.rtuitlab.itlab.presentation.utils.DownloadFileFromWeb
import java.io.File
import javax.inject.Inject

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FilesViewModel @Inject constructor(
    private val repository: MfsRepository,
    getUsers: GetUsersUseCase,
    getCurrentUser: GetCurrentUserUseCase
    ) : ViewModel() {

    private val currentUserId = getCurrentUser().map {
        it?.id
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var fileSelectionContract: ActivityResultLauncher<Array<String>>

    private var fileSelectedForUpload: File? = null

    private val _filesResponse =
        MutableStateFlow<Resource<List<FileInfoResponse>>>(Resource.Loading)
    val filesResponse = _filesResponse.asStateFlow()

    init{
        fetchFiles()
    }

    val users = getUsers()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * A list of callbacks that are invoked on every successfully obtained result of [fileSelectionContract]
     * and then cleared.
     */
    private val onFileSelectedListeners = mutableListOf<(File) -> Unit>()

    /**
     * A list of callbacks that are invoked on result of Android permissions request
     */
    private val onPermissionResultListeners = mutableListOf<(Boolean) -> Unit>()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var _files = MutableStateFlow(emptyList<FileInfo>())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val files = searchQuery.combine(_files) { query, files ->
        query to files
    }.flatMapLatest { (query, files) ->
        flowOf(
            files
                .filter { filterSearchResult(it, query) }
                .sortedByDescending { it.uploadDate }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    enum class SortingMethod(
        val apiString: String,
        @StringRes val nameResource: Int
    ) {
        DATE("date", R.string.by_date),
        NAME("name", R.string.by_user)
    }

    private val _selectedSortingMethod = MutableStateFlow(SortingMethod.DATE)
    val selectedSortingMethod = _selectedSortingMethod.asStateFlow()

    private val selectedUserId = MutableStateFlow<String?>(null)

    fun onRefresh() = viewModelScope.launch {

        fetchFiles(
            userId = selectedUserId.value,
            sortedBy = selectedSortingMethod.value.apiString
        )
    }

    fun onSearch(query: String) {
        _searchQuery.value = query
    }

    private fun fetchFiles(userId: String? = null, sortedBy: String? = null) =
        _filesResponse.emitInIO(viewModelScope) {
            repository.fetchFilesInfo(userId = userId, sortedBy = sortedBy).also {
                it.handle(
                    onSuccess = {
                        _files.value = it.mapNotNull { file ->
                            users.value.find { it.id == file.metadata.fileSender }?.toUser()?.let {
                                file.toFileInfo(
                                    sender = it
                                )
                            }
                        }
                    }
                )
            }
        }

    fun onUserSelected(userId: String) {
        selectedUserId.value = userId
        fetchFiles(userId = userId, sortedBy = _selectedSortingMethod.value.apiString)
    }

    fun onSortingChanged(sortedBy: SortingMethod) {
        _selectedSortingMethod.value = sortedBy
        fetchFiles(userId = selectedUserId.value, sortedBy = sortedBy.apiString)
    }

    fun onPermissionResult(isGranted: Boolean) {
        onPermissionResultListeners.forEach {
            it.invoke(isGranted)
        }
        onPermissionResultListeners.clear()
    }

    fun onLocalFileSelected(context: Context, filePath: Uri?) {
        if (filePath == null) return
        RealPathUtil.writeFileContent(context, filePath)?.let {
            val file = File(Uri.parse(it).toString())
            fileSelectedForUpload = file
            invokeListeners(file)
        }
    }

    private fun invokeListeners(file: File) {
        onFileSelectedListeners.forEach {
            it.invoke(file)
        }
        onFileSelectedListeners.clear()
    }

    fun provideRequestPermissionLauncher(
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        this.requestPermissionLauncher = requestPermissionLauncher
    }

    fun provideFileSelectionContract(contract: ActivityResultLauncher<Array<String>>) {
        fileSelectionContract = contract
    }

    fun provideFile(
        mimeTypes: Array<String> = arrayOf("*/*"),
        activity: Activity,
        onFileProvided: ((File) -> Unit)
    ) = viewModelScope.launch(Dispatchers.IO) {
        ensurePermissionGranted(activity) {
            if (!it) return@ensurePermissionGranted
            onFileSelectedListeners.add(onFileProvided)
            fileSelectionContract.launch(mimeTypes)
        }
    }

    fun uploadFile(
        fileDescription: String = "",
        onError: ((message: String) -> Unit)? = null,
        onSuccess: ((FileInfoResponse) -> Unit)? = null
    ) = viewModelScope.launch(Dispatchers.IO) {
        fileSelectedForUpload?.let {
            repository.uploadFile(it, fileDescription).handle(
                onError = {
                    onError?.invoke(it)
                },
                onSuccess = { fileInfo ->
                    onSuccess?.invoke(fileInfo)
                }
            )
        }
    }

    fun downloadFile(activity: Activity, fileInfo: FileInfo) =
        ensurePermissionGranted(activity) {
            if (!it) return@ensurePermissionGranted
            viewModelScope.launch(Dispatchers.IO) {
                DownloadFileFromWeb.downloadFile(
                    activity,
                    repository.fetchFile(fileInfo.id),
                    fileInfo,
                    "ITLab"
                )
            }
        }

    private fun ensurePermissionGranted(activity: Activity, onResult: (Boolean) -> Unit) {
        val isPermissionGranted =
            ContextCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

        val shouldShowRequestPermissionRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        if (!isPermissionGranted && !shouldShowRequestPermissionRationale) onResult(false)

        when {
            isPermissionGranted -> {
                onResult(true)
            }
            shouldShowRequestPermissionRationale -> {
                onPermissionResultListeners.add(onResult)
                requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
            else -> {
                onPermissionResultListeners.add(onResult)
                requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }

    private fun filterSearchResult(fileInfo: FileInfo, query: String) = fileInfo.run {
        filename.contains(query.trim(), ignoreCase = true) ||
                uploadDate.contains(query.trim(), ignoreCase = true) ||
                applicant.toString().contains(query.trim(), ignoreCase = true)
    }
}