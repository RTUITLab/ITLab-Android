package ru.rtuitlab.itlab.presentation.screens.projects

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.data.local.projects.models.VersionWithEverything
import ru.rtuitlab.itlab.domain.use_cases.projects.GetProjectUseCase
import ru.rtuitlab.itlab.domain.use_cases.projects.UpdateProjectUseCase
import ru.rtuitlab.itlab.presentation.screens.projects.state.ProjectScreenState
import ru.rtuitlab.itlab.presentation.utils.UiEvent
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    getProject: GetProjectUseCase,
    private val updateProject: UpdateProjectUseCase,
    state: SavedStateHandle
): ViewModel() {
    val projectId: String? = state["projectId"]
    val projectName: String = state["projectName"] ?: ""

    private val _uiState = MutableStateFlow(ProjectScreenState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {

       projectId?.let {
           viewModelScope.launch {
               getProject(projectId).collect { project: ProjectWithVersionsOwnersAndRepos? ->
                   _uiState.update {
                       it.copy(
                           projectInfo = project,
                           selectedVersion = it.selectedVersion
                               ?: project?.versions?.maxByOrNull { it.creationDateTime }?.let {
                                   VersionWithEverything(
                                       version = it
                                   )
                               }
                       )
                   }
               }
           }

           viewModelScope.launch {
               onProjectUpdate(true)
               updateProject(projectId).handle(
                   onSuccess = {
                       onProjectUpdate(false)
                   },
                   onError = {
                       onProjectUpdate(false)
                       _uiEvents.emit(UiEvent.Snackbar(it))
                   }
               )
           }

           viewModelScope.launch {
               _uiState.map {
                   it.selectedVersion?.version
               }
                   .distinctUntilChanged()
                   .collect {
                       it?.let {
                           onVersionSelected(it)
                       }
                   }
           }
       }
    }

    private fun onProjectUpdate(isUpdating: Boolean) {
        _uiState.update {
            it.copy(isProjectUpdating = isUpdating)
        }
    }
}