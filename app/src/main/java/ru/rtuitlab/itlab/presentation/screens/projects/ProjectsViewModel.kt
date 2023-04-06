package ru.rtuitlab.itlab.presentation.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectsPaginationResult
import ru.rtuitlab.itlab.domain.repository.ProjectsRepository
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val repo: ProjectsRepository
): ViewModel() {
    val projects = MutableStateFlow<Resource<ProjectsPaginationResult>>(Resource.Loading)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            projects.update {
                repo.getProjectsPaginated(80, false, 0, false, "name", "created_at:desc")
            }
        }
    }
}