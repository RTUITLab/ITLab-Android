package ru.rtuitlab.itlab.presentation.screens.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import ru.rtuitlab.itlab.BuildConfig
import ru.rtuitlab.itlab.common.extensions.collectUntil
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.FileInfoResponse
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingDirection
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseSortingOrder
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusApi
import ru.rtuitlab.itlab.data.remote.api.purchases.PurchaseStatusUi
import ru.rtuitlab.itlab.data.remote.api.purchases.models.Purchase
import ru.rtuitlab.itlab.data.remote.api.purchases.models.PurchaseCreateRequest
import ru.rtuitlab.itlab.data.remote.pagination.Paginator
import ru.rtuitlab.itlab.data.repository.PurchasesRepository
import ru.rtuitlab.itlab.presentation.screens.purchases.state.NewPurchaseUiState
import ru.rtuitlab.itlab.presentation.screens.purchases.state.PurchaseUiState
import ru.rtuitlab.itlab.presentation.screens.purchases.state.PurchasesUiState
import ru.rtuitlab.itlab.common.toIso8601
import ru.rtuitlab.itlab.common.toMoscowDateTime
import ru.rtuitlab.itlab.data.remote.api.users.models.UserClaimCategories
import ru.rtuitlab.itlab.domain.use_cases.user.GetUserClaimsUseCase
import ru.rtuitlab.itlab.domain.use_cases.users.GetUsersUseCase
import ru.rtuitlab.itlab.presentation.ui.extensions.stateIn
import ru.rtuitlab.itlab.presentation.utils.UiEvent
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PurchasesViewModel @Inject constructor(
    private val repository: PurchasesRepository,
    private val savedStateHandle: SavedStateHandle,
    getUsers: GetUsersUseCase,
    getUserClaims: GetUserClaimsUseCase
): ViewModel() {

    val userClaimsFlow = authStateStorage.userClaimsFlow

    val isSolvingAccessible = getUserClaims().map {
        it.contains(UserClaimCategories.PURCHASES.ADMIN)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val users = getUsers().map {
        it.map { it.toUser() }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // This is unused now, but will be used eventually
//    private val searchQuery = MutableStateFlow("")

    val pageSize = 10

    private val _state = MutableStateFlow(PurchasesUiState())
    val state = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val paginator = Paginator(
        initialKey = state.value.page,
        onLoadingUpdated = {
            _state.value = _state.value.copy(isLoading = it)
        },
        onRequest = { nextPage ->
            _state.value = _state.value.copy(errorMessage = null)
            repository.fetchPurchases(
                pageNumber = nextPage,
                pageSize = pageSize,
                sortingDirection = _state.value.selectedSortingDirection,
                sortBy = _state.value.selectedSortingOrder,
                purchaseStartDate = _state.value.startDate,
                purchaseEndDate = _state.value.endDate,
                purchaseStatus = _state.value.selectedStatus,
            )
        },
        getNextKey = { _state.value.page + 1 },
        onError = {
            _state.value = _state.value.copy(
                errorMessage = it,
                isLoading = false,
                isRefreshing = false
            )
        },
        onSuccess = { result, newPage ->
            _state.value = _state.value.copy(
                page = newPage,
                purchases = _state.value.purchases + result.content.map { purchaseDto ->
                    purchaseDto.toPurchase(
                        purchaser = users.value.find { it.id == purchaseDto.purchaserId }!!,
                        solver = users.value.find { it.id == purchaseDto.solution.solverId }
                    )
                },
                paginationState = result,
                errorMessage = null,
                isLoading = false,
                endReached = result.content.isEmpty()
            )
        }
    )

    init {
        viewModelScope.launch {
            users.collectUntil(
                condition = { it.isNotEmpty() },
                action = {
                    fetchNextItems()
                }
            )
        }
    }

    fun fetchNextItems() = viewModelScope.launch(Dispatchers.IO) {
        paginator.fetchNext()
    }

    fun onRefresh() {
        paginator.reset()
        _state.value = _state.value.copy(
            purchases = emptyList(),
            paginationState = null
        )
        fetchNextItems()
    }

    fun onStatusChange(status: PurchaseStatusUi) {
        _state.value = _state.value.copy(
            selectedStatus = status
        )
        onRefresh()
    }

    fun onSortingOrderChange(order: PurchaseSortingOrder) {
        _state.value = _state.value.copy(
            selectedSortingOrder = order
        )
        onRefresh()
    }

    fun onSortingDirectionChange(direction: PurchaseSortingDirection) {
        _state.value = _state.value.copy(
            selectedSortingDirection = direction
        )
        onRefresh()
    }


    // Single-purchase-related methods

    fun onPurchaseOpened(purchase: Purchase) {
        _state.value = _state.value.copy(
            selectedPurchaseState = PurchaseUiState(purchase)
        )
    }

    fun showDeletingDialog() {
        _state.value = _state.value.copy(
            selectedPurchaseState = _state.value.selectedPurchaseState?.copy(
                isDeletionDialogShown = true
            )
        )
    }

    fun hideDeletingDialog() {
        _state.value = _state.value.copy(
            selectedPurchaseState = _state.value.selectedPurchaseState?.copy(
                isDeletionDialogShown = false
            )
        )
    }

    private fun setIsDeletionInProgress(isIt: Boolean) {
        _state.value = _state.value.copy(
            selectedPurchaseState = _state.value.selectedPurchaseState?.copy(
                isDeletionInProgress = isIt
            )
        )
    }

    fun onDeletePurchase(
        purchase: Purchase,
        successMessage: String,
        onFinish: (isSuccessful: Boolean) -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        setIsDeletionInProgress(true)
        repository.deletePurchase(purchase.id).handle(
            onSuccess = {
                withContext(Dispatchers.Main) {
                    onFinish(true)
                }
                _state.value = _state.value.copy(
                    purchases = _state.value.purchases - purchase,
                    paginationState = _state.value.paginationState?.copy(
                        totalElements = _state.value.paginationState?.totalElements?.minus(1) ?: 0
                    )
                )
                delay(500)
                _uiEvents.emit(UiEvent.Snackbar(successMessage))
            },
            onError = {
                withContext(Dispatchers.Main) {
                    onFinish(false)
                }
                _uiEvents.emit(UiEvent.Snackbar(it))
            }
        )
        setIsDeletionInProgress(false)
    }

    fun onApprove(
        successMessage: String
    ) {
        _state.value = _state.value.copy(
            selectedPurchaseState = _state.value.selectedPurchaseState?.copy(
                isApprovingInProgress = true
            )
        )

        onResolve(
            status = PurchaseStatusApi.ACCEPT,
            successMessage = successMessage
        )
    }

    fun onReject(
        successMessage: String
    ) {
        _state.value = _state.value.copy(
            selectedPurchaseState = _state.value.selectedPurchaseState?.copy(
                isRejectingInProgress = true
            )
        )

        onResolve(
            status = PurchaseStatusApi.DECLINE,
            successMessage = successMessage
        )
    }

    private fun onResolve(
        status: PurchaseStatusApi,
        successMessage: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        repository.resolvePurchase(
            id = _state.value.selectedPurchaseState!!.purchase.id,
            status = status
        ).handle(
            onSuccess = { newPurchase ->
                val purchase = newPurchase.toPurchase(
                    purchaser = users.value.find { it.id == newPurchase.purchaserId }!!,
                    solver = users.value.find { it.id == newPurchase.solution.solverId }!!
                )
                _uiEvents.emit(UiEvent.Snackbar(successMessage))
                _state.value = _state.value.copy(
                    selectedPurchaseState = _state.value.selectedPurchaseState?.copy(
                        purchase = purchase
                    ),
                    purchases = _state.value.purchases - _state.value.purchases.find { it.id == purchase.id }!!,
                    paginationState = _state.value.paginationState?.copy(
                        totalElements = _state.value.paginationState!!.totalElements - 1
                    )
                )
            },
            onError = {
                _uiEvents.emit(UiEvent.Snackbar(it))
            }
        )
        onLoadingStop()
    }

    private fun onLoadingStop() {
        _state.value = _state.value.copy(
            selectedPurchaseState = _state.value.selectedPurchaseState?.copy(
                isRejectingInProgress = false,
                isApprovingInProgress = false
            )
        )
    }


    // New purchase

    sealed class FileType(val mimeTypes: Array<String>) {
        object Check: FileType(arrayOf("image/*", "application/pdf"))
        object Photo: FileType(arrayOf("image/*"))
    }

    fun onNameChange(name: String) {
        if (name.length > 63) return
        _state.value = _state.value.copy(
            newPurchaseState = _state.value.newPurchaseState.copy(
                name = name
            )
        )
    }

    fun onPriceChange(price: String) {
        val formattedInput = price.filter { it.isDigit() }
        if (formattedInput.isBlank()) {
            _state.value = _state.value.copy(
                newPurchaseState = _state.value.newPurchaseState.copy(
                    price = null
                )
            )
            return
        }
        if (formattedInput.length > 5) return
        val priceInt = formattedInput.toInt()
        if (priceInt < 1) return
        _state.value = _state.value.copy(
            newPurchaseState = _state.value.newPurchaseState.copy(
                price = priceInt
            )
        )
    }

    fun onDescriptionChange(description: String) {
        if (description.length > 255) return
        _state.value = _state.value.copy(
            newPurchaseState = _state.value.newPurchaseState.copy(
                description = description
            )
        )
    }

    fun onAttachFile(type: FileType, file: File) {
        _state.value = _state.value.copy(
            newPurchaseState =
            if (type == FileType.Check)
                _state.value.newPurchaseState.copy(
                    checkFile = file,
                    isCheckFileDialogShown = true
                )
            else _state.value.newPurchaseState.copy(
                purchasePhotoFile = file,
                isPurchasePhotoDialogShown = true
            )
        )
    }

    fun onUploadFile(type: FileType) {
        _state.value = _state.value.copy(
            newPurchaseState = _state.value.newPurchaseState.copy(
                isCheckFileUploading = type == FileType.Check,
                isPurchasePhotoUploading = type == FileType.Photo
            )
        )
    }

    fun onFileUploadingError(message: String) = viewModelScope.launch {
        _uiEvents.emit(UiEvent.Snackbar(message))
    }

    fun onConfirmationDialogDismissed(ofType: FileType) {
        _state.value = _state.value.copy(
            newPurchaseState = _state.value.newPurchaseState.copy(
                isPurchasePhotoDialogShown = if (ofType == FileType.Photo) false
                    else _state.value.newPurchaseState.isPurchasePhotoDialogShown,
                isCheckFileDialogShown = if (ofType == FileType.Check) false
                    else _state.value.newPurchaseState.isCheckFileDialogShown
            )
        )
    }

    fun onFileUploaded(fileInfo: FileInfoResponse, type: FileType) {
        _state.value = _state.value.copy(
            newPurchaseState =
            if (type == FileType.Check)
                _state.value.newPurchaseState.copy(
                    checkFileId = fileInfo.id
                )
            else _state.value.newPurchaseState.copy(
                purchasePhotoId = fileInfo.id
            )
        )
    }

    fun onRemoveFile(type: FileType) {
        _state.value = _state.value.copy(
            newPurchaseState =
            if (type == FileType.Check)
                _state.value.newPurchaseState.copy(
                    checkFileId = null,
                    checkFile = null
                )
            else _state.value.newPurchaseState.copy(
                purchasePhotoId = null,
                purchasePhotoFile = null
            )
        )
    }

    fun onSendPurchase(
        successMessage: String,
        onFinish: (newPurchase: Purchase?) -> Unit
    ) {
        _state.value = _state.value.copy(
            newPurchaseState = _state.value.newPurchaseState.copy(
                isPurchaseUploading = true
            )
        )
        val state = _state.value.newPurchaseState
        val request = PurchaseCreateRequest(
            name = state.name,
            price = state.price!!.toInt(),
            description = state.description,
            purchaseDate = state.purchaseDate.toIso8601(),
            receiptPhotoUrl = "${BuildConfig.API_URI}mfs/download/${state.checkFileId}",
            itemPhotoUrl = state.purchasePhotoId?.let {
                "${BuildConfig.API_URI}mfs/download/$it"
            }
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.createPurchase(request).handle(
                onSuccess = { newPurchase ->
                    val purchase = newPurchase.toPurchase(
                        purchaser = users.value.find { it.id == newPurchase.purchaserId }!!
                    )
                    withContext(Dispatchers.Main) {
                        onFinish(purchase)
                    }
                    _state.value = _state.value.copy(
                        purchases = listOf(purchase) + _state.value.purchases,
                        paginationState = _state.value.paginationState?.copy(
                            totalElements = _state.value.paginationState!!.totalElements + 1
                        ),
                        newPurchaseState = NewPurchaseUiState()
                    )
                    delay(500)
                    _uiEvents.emit(UiEvent.Snackbar(successMessage))
                },
                onError = {
                    withContext(Dispatchers.Main) {
                        onFinish(null)
                    }
                    _uiEvents.emit(UiEvent.Snackbar(it))
                }
            )
            onPurchaseUploadingFinished()
        }
    }

    private fun onPurchaseUploadingFinished() {
        _state.value = _state.value.copy(
            newPurchaseState = _state.value.newPurchaseState.copy(
                isPurchaseUploading = false
            )
        )
    }

    fun onDateSelected(epochMilliseconds: Long) {
        _state.value = _state.value.copy(
            newPurchaseState = _state.value.newPurchaseState.copy(
                purchaseDate = epochMilliseconds.toMoscowDateTime().toInstant(UtcOffset.ZERO)
            )
        )
    }

}