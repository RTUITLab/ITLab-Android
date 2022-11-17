package ru.rtuitlab.itlab.presentation.ui.components.top_app_bars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.rtuitlab.itlab.domain.use_cases.user.GetUserClaimsUseCase
import ru.rtuitlab.itlab.presentation.utils.AppTab
import javax.inject.Inject

@HiltViewModel
class AppTabsViewModel @Inject constructor(
    getUserClaims: GetUserClaimsUseCase
) : ViewModel() {
    val appTabs = getUserClaims().map {
        AppTab.getAccessibleTabs(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}