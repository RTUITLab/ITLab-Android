package ru.rtuitlab.itlab.presentation.screens.projects

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.common.Resource
import ru.rtuitlab.itlab.data.remote.api.projects.models.ProjectDetailsDto
import ru.rtuitlab.itlab.data.remote.api.projects.models.version.ProjectVersions
import ru.rtuitlab.itlab.domain.repository.ProjectsRepository
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repo: ProjectsRepository,
    state: SavedStateHandle
): ViewModel() {
    private val projectId: String = state["projectId"]!!

    val project = MutableStateFlow<Resource<ProjectDetailsDto>>(Resource.Loading)

    val versions = MutableStateFlow<Resource<ProjectVersions>>(Resource.Loading)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            project.update {
                repo.getProject(projectId)
            }
            versions.update {
                repo.getProjectVersions(projectId)
            }
        }
    }
}