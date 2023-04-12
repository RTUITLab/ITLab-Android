package ru.rtuitlab.itlab.presentation.screens.projects

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.rtuitlab.itlab.data.local.projects.models.ProjectWithVersionsOwnersAndRepos
import ru.rtuitlab.itlab.data.local.projects.models.Version
import ru.rtuitlab.itlab.data.local.projects.models.VersionWithEverything
import ru.rtuitlab.itlab.domain.use_cases.projects.GetProjectUseCase
import ru.rtuitlab.itlab.domain.use_cases.projects.GetVersionUseCase
import ru.rtuitlab.itlab.domain.use_cases.projects.UpdateProjectUseCase
import ru.rtuitlab.itlab.domain.use_cases.projects.UpdateVersionUseCase
import ru.rtuitlab.itlab.presentation.screens.projects.state.ProjectScreenState
import ru.rtuitlab.itlab.presentation.utils.UiEvent
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    getProject: GetProjectUseCase,
    private val updateProject: UpdateProjectUseCase,
    private val updateVersion: UpdateVersionUseCase,
    private val getVersion: GetVersionUseCase,
    state: SavedStateHandle
): ViewModel() {
    val projectId: String? = state["projectId"]
    val projectName: String = state["projectName"] ?: ""

    private val _uiState = MutableStateFlow(ProjectScreenState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val selectedVersionInfo = _uiState.flatMapLatest {
        it.selectedVersion?.version?.id?.let {
            getVersion(it)
        } ?: emptyFlow()
    }
        .onEach { version ->
            _uiState.update {
                it.copy(
                    selectedVersion = version
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

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
                   it.selectedVersion?.version?.id
               }
                   .distinctUntilChanged()
                   .collect {
                       it?.let {
                           onVersionChanged(it)
                       }
                   }
           }
       }
    }

    fun onRefresh() = viewModelScope.launch {
        projectId ?: return@launch
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

    private fun onProjectUpdate(isUpdating: Boolean) {
        _uiState.update {
            it.copy(isProjectUpdating = isUpdating)
        }
    }

    private fun onVersionUpdate(isUpdating: Boolean) {
        _uiState.update {
            it.copy(isVersionUpdating = isUpdating)
        }
    }

    fun onVersionSelected(version: Version) {
        _uiState.update {
            it.copy(
                selectedVersion = VersionWithEverything(version)
            )
        }
    }

    private fun onVersionChanged(versionId: String) = viewModelScope.launch {
        if (projectId == null) return@launch
        onVersionUpdate(true)
        (updateVersion.workers(projectId, versionId) + updateVersion.tasks(projectId, versionId))
            .handle(
                onError = {
                    onVersionUpdate(false)
                    viewModelScope.launch { _uiEvents.emit(UiEvent.Snackbar(it)) }
                },
                onSuccess = { onVersionUpdate(false) }
            )
    }
}