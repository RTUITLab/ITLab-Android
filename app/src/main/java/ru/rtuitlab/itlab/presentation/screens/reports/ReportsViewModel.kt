package ru.rtuitlab.itlab.presentation.screens.reports

import androidx.compose.material.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.common.persistence.AuthStateStorage
import ru.rtuitlab.itlab.data.remote.api.reports.models.Report
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportDto
import ru.rtuitlab.itlab.data.remote.api.reports.models.ReportSalary
import ru.rtuitlab.itlab.data.remote.api.users.models.UserResponse
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

	private val _searchQuery = MutableStateFlow("")
	val searchQuery = _searchQuery.asStateFlow()

	@Suppress("UNCHECKED_CAST")
	fun fetchReports() = viewModelScope.launch(Dispatchers.IO) {
		var resource: Resource<List<Report>> = Resource.Loading
		_reportsResponseFlow.emit(resource)
		// Executing in parallel, because any one data set does not depend on another
		val reports = async { repository.fetchReportsAboutUser() }
		val salaries = async { repository.fetchPricedReports() }
		val users = async { usersRepository.fetchUsers() }

		(reports.await() + salaries.await() + users.await()).handle(
			onError = {
				resource = Resource.Error(it)
			},
			onSuccess = { (r, s, u) ->
				resource = try {
					Resource.Success(
						(r as List<ReportDto>).map { reportDto ->
							val salary = (s as List<ReportSalary>).find { it.reportId == reportDto.id }
							reportDto.toReport(
								salary = salary,
								applicant = (u as List<UserResponse>).find { it.id == reportDto.assignees.reporterId }!!,
								approver = u.find { it.id == salary?.approverId },
								implementer = u.find { it.id == reportDto.assignees.implementerId }!!
							)
						}
					)
				} catch (e: Throwable) {
					Resource.Error(e.localizedMessage ?: "Parsing error")
				}
			}
		)

		_reportsResponseFlow.emit(resource)
	}

	fun onSearch(query: String) {
		_searchQuery.value = query
	}

}

fun List<Report>.performQuery(query: String): List<Report> {
	val q = query.trim()
	return filter {
		"${it.applicant.lastName} ${it.applicant.firstName} ${it.applicant.middleName}".contains(q, true) ||
				"${it.implementer.lastName} ${it.implementer.firstName} ${it.implementer.middleName}".contains(q, true) ||
				it.title.contains(q, true)
	}
}
