package ru.rtuitlab.itlab.presentation.screens.reports

import androidx.compose.material.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.persistence.AuthStateStorage
import ru.rtuitlab.itlab.data.remote.api.reports.models.Report
import ru.rtuitlab.itlab.data.repository.ReportsRepository
import ru.rtuitlab.itlab.data.repository.UsersRepository
import javax.inject.Inject

@ExperimentalPagerApi
@HiltViewModel
class ReportsViewModel @Inject constructor(
	private val repository: ReportsRepository,
	private val usersRepository: UsersRepository,
	private val authStateStorage: AuthStateStorage
) : ViewModel() {

	val userIdFlow = authStateStorage.userIdFlow.stateIn(
		viewModelScope,
		SharingStarted.Lazily,
		runBlocking { authStateStorage.userIdFlow.first() }
	)

	val pagerState = PagerState()
	val snackbarHostState = SnackbarHostState()

	private val _reportsResponseFlow: MutableStateFlow<Resource<List<Report>>> =
		MutableStateFlow<Resource<List<Report>>>(Resource.Loading).also { fetchReports() }
	val reportsResponseFlow = _reportsResponseFlow.asStateFlow()

	fun fetchReports() = viewModelScope.launch(Dispatchers.IO) {
		var resource: Resource<List<Report>> = Resource.Loading
		_reportsResponseFlow.emit(resource)
		// Executing in parallel, because any one data set does not depend on another
		val reports = async { repository.fetchReportsAboutUser() }
		val salaries = async { repository.fetchPricedReports() }
		val users = async { usersRepository.fetchUsers() }

		delay(5000)

		reports.await().handle(
			onError = {
				resource = Resource.Error(it)
			},
			onSuccess = { reportsList ->
				salaries.await().handle(
					onError = {
						resource = Resource.Error(it)
					},
					onSuccess = { salaries ->
						users.await().handle(
							onError = {
								resource = Resource.Error(it)
							},
							onSuccess = { users ->
								resource = try {
									Resource.Success(
										reportsList.map { reportDto ->
											val salary = salaries.find { it.reportId == reportDto.id }
											reportDto.toReport(
												salary = salary,
												applicant = users.find { it.id == reportDto.assignees.reporterId }!!,
												approver = users.find { it.id == salary?.approverId },
												implementer = users.find { it.id == reportDto.assignees.implementerId }!!
											)
										}
									)
								} catch (e: NullPointerException) {
									Resource.Error(e.localizedMessage ?: "Parsing error")
								}

							}
						)
					}
				)
			}
		)

		_reportsResponseFlow.emit(resource)
	}

	fun onSearch(query: String) {
		TODO("Not yet implemented")
	}
}