package ru.rtuitlab.itlab.utils

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.os.bundleOf
import ru.rtuitlab.itlab.R

sealed class AppTab(val route: String, @StringRes val resourceId: Int, val icon: ImageVector, val accessible: Boolean = true) {
    object Events: AppTab("events_tab", R.string.events, Icons.Default.EventNote, false)
    object Projects: AppTab("projects_tab", R.string.projects, Icons.Default.Widgets, false)
    object Devices: AppTab("devices_tab", R.string.devices, Icons.Default.DevicesOther, false)
    object Employees: AppTab("employees_tab", R.string.employees, Icons.Default.People)
    object Profile: AppTab("profile_tab", R.string.profile, Icons.Default.AccountCircle, false)

    fun saveState() = bundleOf(SCREEN_KEY to route)

    fun asScreen() = when (this) {
        Events -> AppScreen.Events
        Projects -> AppScreen.Projects
        Devices -> AppScreen.Devices
        Employees -> AppScreen.Employees
        Profile -> AppScreen.Profile
    }

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

// This class represents any screen - tabs and their subscreens.
// It is needed to appropriately change top app bar behavior
sealed class AppScreen(@StringRes val screenNameResource: Int) {
    // Employee-related
    object Employees: AppScreen(R.string.employees)
    object EmployeeDetails: AppScreen(R.string.profile) // Has back button

    // Events-related
    object Events: AppScreen(R.string.events)
    object EventDetails: AppScreen(R.string.event) // Has back button
    object EventNew: AppScreen(R.string.event_new) // Has back button

    // Projects-related
    object Projects: AppScreen(R.string.projects)

    // Devices-related
    object Devices: AppScreen(R.string.devices)

    // Profile-related
    object Profile: AppScreen(R.string.profile)
}