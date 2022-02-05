package ru.rtuitlab.itlab.presentation.utils

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.os.bundleOf
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.users.models.UserClaimCategories

sealed class AppTab(val route: String, @StringRes val resourceId: Int, val icon: ImageVector, var accessible: Boolean = true) {
    object Events: AppTab("events_tab", R.string.events, Icons.Default.EventNote)
    object Projects: AppTab("projects_tab", R.string.projects, Icons.Default.Widgets, false)
    object Devices: AppTab("devices_tab", R.string.devices, Icons.Default.DevicesOther, false)
    object Employees: AppTab("employees_tab", R.string.employees, Icons.Default.People)
    object Feedback: AppTab("feedback_tab", R.string.feedback, Icons.Default.Feedback)
    object Profile: AppTab("profile_tab", R.string.profile, Icons.Default.AccountCircle, false)

    fun saveState() = bundleOf(SCREEN_KEY to route)

    fun asScreen() = when (this) {
        Events -> AppScreen.Events
        Projects -> AppScreen.Projects
        Devices -> AppScreen.Devices
        Employees -> AppScreen.Employees
        Feedback -> AppScreen.Feedback
        Profile -> AppScreen.Profile
    }

    companion object {
        const val SCREEN_KEY = "SCREEN_KEY"

        val all
            get() = listOf(
                Events,
                Projects,
                Devices,
                Employees,
                Feedback,
                Profile
            )

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
            Feedback.route  -> Feedback
            else            -> {throw IllegalArgumentException("Invalid route. Maybe you forgot to add a new screen to AppTabSaver.kt?")}
        }

        fun applyClaims(claims: List<Any>) {
            Feedback.accessible = claims.contains(UserClaimCategories.FEEDBACK.ADMIN)
        }
    }
}

// This class represents any screen - tabs and their subscreens.
// It is needed to appropriately change top app bar behavior
sealed class AppScreen(
    @StringRes val screenNameResource: Int,
    val route: String,
    val navLink: String = route.substringBefore("/{")
) {
    // Employee-related
    object Employees: AppScreen(R.string.employees, "employees")
    object EmployeeDetails: AppScreen(R.string.profile, "employee/{userId}") // Has back button

    // Feedback-related
    object Feedback: AppScreen(R.string.feedback, "feedback")

    // Events-related
    object Events: AppScreen(R.string.events, "events")
    class  EventDetails(val title: String): AppScreen(R.string.event, "event/{eventId}") { // Has back button
        companion object {
            const val route = "event/{eventId}"
            val navLink: String = route.substringBefore("/{")
        }
    }
    object EventNew: AppScreen(R.string.event_new, "event/new") // Has back button

    // Projects-related
    object Projects: AppScreen(R.string.projects, "projects")

    // Devices-related
    object Devices: AppScreen(R.string.devices, "devices")

    // Profile-related
    object Profile: AppScreen(R.string.profile, "profile")
}