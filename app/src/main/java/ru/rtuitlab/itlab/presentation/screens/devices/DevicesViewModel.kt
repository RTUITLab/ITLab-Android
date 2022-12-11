package ru.rtuitlab.itlab.presentation.screens.devices

import android.util.Log
import androidx.compose.material.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.extensions.emitInIO
import ru.rtuitlab.itlab.data.remote.api.devices.models.*
import ru.rtuitlab.itlab.data.remote.api.users.models.UserClaimCategories
import ru.rtuitlab.itlab.data.repository.DevicesRepository
import ru.rtuitlab.itlab.domain.use_cases.user.GetUserClaimsUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUsersUseCase
import ru.rtuitlab.itlab.presentation.utils.UiEvent
import javax.inject.Inject

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val devicesRepo: DevicesRepository,
    getUserClaims: GetUserClaimsUseCase,
    getUsers: GetUsersUseCase,
) : ViewModel() {

    val isAccessible = getUserClaims().map {
        it.contains(UserClaimCategories.DEVICES.EDIT)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val users = getUsers()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val devicesSearchQuery = MutableStateFlow("")

    private val _dialogSearchQuery = MutableStateFlow("")
    val dialogSearchQuery = _dialogSearchQuery.asStateFlow()

    val queriedUsers = dialogSearchQuery.flatMapLatest {
        getUsers.search(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _isFreeFilterChecked = MutableStateFlow(false)
    val isFreeFilterChecked = _isFreeFilterChecked.asStateFlow()

    private val _devicesResponsesFlow =
        MutableStateFlow<Resource<List<DeviceDetailDto>>>(Resource.Loading)
    val deviceResponsesFlow = _devicesResponsesFlow.asStateFlow()


    private var cachedDevices = emptyList<DeviceDetails>()

    private val _devicesFlow = MutableStateFlow(cachedDevices)
    val devicesFlow = devicesSearchQuery.combine(_devicesFlow) { query, devices ->
        query to devices
    }.flatMapLatest { (query, devices) ->
        flowOf(
            devices.filter { device ->
                "${device.equipmentType?.title} ${device.serialNumber} ${device.number} ${device.ownerlastName} ${device.ownerfirstName}".contains(
                    query.trim(),
                    ignoreCase = true
                )
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _uiDevices = MutableSharedFlow<UiEvent>()
    val uiDevices = _uiDevices.asSharedFlow()


    private val _selectedDevice: MutableStateFlow<DeviceDetails?> = MutableStateFlow(null)
    val selectedDevice = _selectedDevice.asStateFlow()

    fun onDeviceSelected(device: DeviceDetails) {
        _selectedDevice.value = device
    }


    private val _equipmentTypeResponsesFlow =
        MutableStateFlow<Resource<List<EquipmentTypeResponse>>>(Resource.Loading)
    val equipmentTypeResponsesFlow = _equipmentTypeResponsesFlow.asStateFlow()

    var cachedEquipmentTypes = emptyList<EquipmentTypeResponse>()

    private val _equipmentTypes: MutableStateFlow<List<EquipmentTypeResponse>> =
        MutableStateFlow(emptyList())
    val equipmentTypes = _equipmentTypes.asStateFlow()

    private val _typeSearchQuery = MutableStateFlow("")
    val typeSearchQuery = _typeSearchQuery.asStateFlow()

    val queriedEquipmentTypes = typeSearchQuery.combine(equipmentTypes) { query, types ->
        query to types
    }.flatMapLatest { (query, types) ->
        flowOf(
            types.filter { equipment ->
                equipment.title.contains(query.trim(), ignoreCase = true)
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val snackbarHostState = SnackbarHostState()


    init {
        fetchDevices()
        fetchEquipmentTypes()
    }

    private fun fetchEquipmentTypes() = _equipmentTypeResponsesFlow.emitInIO(viewModelScope) {
        devicesRepo.fetchEquipmentTypes("", true).also {
            it.handle(
                onSuccess = {
                    _equipmentTypes.value = it
                }
            )
        }
    }

    fun createEquipmentType(
        equipmentTypeNewRequest: EquipmentTypeNewRequest,
        onFinish: (Boolean) -> Unit
    ) = viewModelScope.launch {
        devicesRepo.createEquipmentType(equipmentTypeNewRequest).handle(
            onError = { msg ->
                onFinish(false)
                snackbarHostState.showSnackbar(msg)
            },
            onSuccess = {
                onFinish(true)
                fetchEquipmentTypes()
                snackbarHostState.showSnackbar("Successful")
            }
        )
    }

    fun createEquipment(
        equipmentNewRequest: EquipmentNewRequest,
        createdDevice: (DeviceDetailDto?) -> Unit
    ) = viewModelScope.launch {
        devicesRepo.createDevice(equipmentNewRequest).handle(
            onError = { msg ->
                createdDevice(null)
                Log.d("DeviceCreate", msg)

                snackbarHostState.showSnackbar(
                    message = msg
                )
            },
            onSuccess = { deviceDto ->
                createdDevice(deviceDto)
                Log.d("DeviceCreate", "$deviceDto")
                snackbarHostState.showSnackbar(
                    message = "Successful"
                )
            }
        )
    }

    fun updateEquipment(
        equipmentEditRequest: EquipmentEditRequest
    ) = viewModelScope.launch {
        devicesRepo.editDevice(equipmentEditRequest).handle(
            onError = { msg ->
                snackbarHostState.showSnackbar(msg)
            },
            onSuccess = { deviceDto ->
                snackbarHostState.showSnackbar("Successful")
                updateCachedDevice(deviceDto)
            }
        )
    }

    fun onDeleteEquipment(id: String, onFinish: (Boolean) -> Unit) = viewModelScope.launch {
        devicesRepo.deleteDevice(id).handle(
            onError = { msg ->
                onFinish(false)
                snackbarHostState.showSnackbar(
                    message = msg
                )
            },
            onSuccess = {
                onFinish(true)
                snackbarHostState.showSnackbar(
                    message = "Successful"
                )
            }
        )
    }

    fun onSearch(query: String) {
        devicesSearchQuery.value = query
    }

    fun onTypesSearch(query: String) {
        _typeSearchQuery.value = query
    }

    fun onRefresh() = viewModelScope.launch {
        _isRefreshing.emit(true)
        if (_isFreeFilterChecked.value) {
            fetchFreeDevices()
        } else {
            fetchDevices()
        }
        _isRefreshing.emit(false)
    }

    private fun fetchDevices() = _devicesResponsesFlow.emitInIO(viewModelScope) {
        var resource: Resource<List<DeviceDetailDto>> = Resource.Loading

        devicesRepo.fetchDevices().handle(
            onSuccess = { details ->
                cachedDevices = details.map { detail ->
                    detail.toDevice(users.value.find { it.id == detail.ownerId }?.toUser())
                }
                _devicesFlow.value = cachedDevices
                resource = Resource.Success(details)
            },
            onError = { resource = Resource.Error(it) }
        )
        resource
    }

    //Owner

    fun onDialogQueryChanged(query: String) {
        _dialogSearchQuery.value = query
    }

    fun onChangeEquipmentOwner(
        ownerId: String, equipmentId: String,
        onFinish: (Boolean) -> Unit
    ) = viewModelScope.launch {
        devicesRepo.setEquipmentOwner(ownerId, equipmentId).handle(
            onError = { msg ->
                onFinish(false)
                snackbarHostState.showSnackbar(
                    message = msg
                )
            },
            onSuccess = {
                onFinish(true)
                snackbarHostState.showSnackbar(
                    message = "Successful"
                )
            }
        )
    }

    fun onPickUpEquipment(
        ownerId: String, equipmentId: String,
        onFinish: (Boolean) -> Unit
    ) = viewModelScope.launch {
        devicesRepo.fetchEquipmentOwnerPickUp(ownerId, equipmentId).handle(
            onError = { msg ->
                onFinish(false)
                snackbarHostState.showSnackbar(
                    message = msg
                )
            },
            onSuccess = {
                onFinish(true)
                snackbarHostState.showSnackbar(
                    message = "Successful"
                )
            }
        )
    }

    //Filtering


    fun onFilteringChanged(showOnlyFree: Boolean) {
        _isFreeFilterChecked.value = showOnlyFree
        onRefresh()
    }


    private fun fetchFreeDevices() = viewModelScope.launch(Dispatchers.IO) {
        var resource: Resource<List<DeviceDetailDto>> = Resource.Loading
        _devicesResponsesFlow.emit(resource)

        devicesRepo.fetchFreeEquipment().handle(
            onSuccess = { details ->
                cachedDevices = details.map { detail ->
                    detail.toDevice(users.value.find { it.id == detail.ownerId }?.toUser())
                }
                _devicesFlow.value = cachedDevices
                resource = Resource.Success(details)
            },
            onError = { resource = Resource.Error(it) }
        )
        _devicesResponsesFlow.emit(resource)
    }

    private fun updateCachedDevice(newDeviceData: DeviceDetailDto) = viewModelScope.launch {
        val cachedDevice: DeviceDetails = newDeviceData
            .toDevice(users.value.find { it.id == newDeviceData.ownerId }?.toUser())

        val index = cachedDevices.indexOf(cachedDevices.find { it.id == cachedDevice.id })
        cachedDevices = cachedDevices.toMutableList().apply {
            this[index] = cachedDevice
        }
        _devicesFlow.value = cachedDevices
        _selectedDevice.value = cachedDevice
    }

    fun onDeleteCachedDevice(id: String) = viewModelScope.launch {
        val oldDevice = cachedDevices.find { it.id == id }
        cachedDevices = cachedDevices.toMutableList().apply {
            remove(oldDevice)
        }
        _devicesFlow.value = cachedDevices
    }

    fun onCreateCachedDevice(tempDevice: DeviceDetailDto) = viewModelScope.launch {
        val cachedDevice: DeviceDetails = tempDevice.toDevice(
            users.value.find { it.id == tempDevice.ownerId }?.toUser()
        )
        cachedDevices = cachedDevices.toMutableList().apply {
            add(cachedDevice)
        }
        _devicesFlow.value = cachedDevices
    }
    private val searchQuery = MutableStateFlow("")




}
