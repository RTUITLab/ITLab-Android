package ru.rtuitlab.itlab.utils

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.os.bundleOf
import ru.rtuitlab.itlab.R

sealed class AppTab(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Events: AppTab("events_tab", R.string.events, Icons.Default.Event)
    object Projects: AppTab("projects_tab", R.string.projects, Icons.Default.Code)
    object Devices: AppTab("devices_tab", R.string.devices, Icons.Default.DevicesOther)
    object Employees: AppTab("employees_tab", R.string.employees, Icons.Default.People)
    object Profile: AppTab("profile_tab", R.string.profile, Icons.Default.AccountCircle)

    fun saveState() = bundleOf(SCREEN_KEY to route)

    companion object {
        const val SCREEN_KEY = "SCREEN_KEY"

        fun saver() = Saver<AppTab, Bundle>(
            save = { it.saveState() },
            restore = { restoreState(it) }
        )

        private fun restoreState(bundle: Bundle) = when (bundle.getString(SCREEN_KEY, null)) {
            Events.route    -> Events
            Projects.route  -> Projects
            Devices.route   -> Devices
            Employees.route -> Employees
            Profile.route   -> Profile
            else            -> Events
        }
    }
}