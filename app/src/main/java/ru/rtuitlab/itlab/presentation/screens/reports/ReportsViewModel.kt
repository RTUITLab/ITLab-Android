package ru.rtuitlab.itlab.presentation.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.reports.models.Report
import ru.rtuitlab.itlab.data.repository.ReportsRepository
import ru.rtuitlab.itlab.data.repository.UsersRepository
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
	private val repository: ReportsRepository,
	private val usersRepository: UsersRepository
) : ViewModel() {

	private val _reportsResponseFlow: MutableStateFlow<Resource<List<Report>>> =
		MutableStateFlow<Resource<List<Report>>>(Resource.Loading).also { fetchReports() }
	val reportsResponseFlow = _reportsResponseFlow.asStateFlow()

	fun fetchReports() = viewModelScope.launch(Dispatchers.IO) {
		var resource: Resource<List<Report>> = Resource.Loading
		// Executing in parallel, because any one data set does not depend on another
		val reports = async { repository.fetchReportsAboutUser() }
		val salaries = async { repository.fetchPricedReports() }
		val users = async { usersRepository.fetchUsers() }

		delay(500)

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
								resource = Resource.Success(
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

							}
						)
					}
				)
			}
		)

		_reportsResponseFlow.emit(resource)
	}
}