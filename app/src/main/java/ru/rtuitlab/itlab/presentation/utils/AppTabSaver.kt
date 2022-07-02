package ru.rtuitlab.itlab.presentation.utils

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.os.bundleOf
import kotlinx.parcelize.Parcelize
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.users.models.UserClaimCategories

sealed class AppTab(
    val route: String,
    val startDestination: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector,
    var accessible: Boolean = true
) {
    object Events: AppTab("events_tab", AppScreen.Events.route, R.string.events, Icons.Default.EventNote)
    object Projects: AppTab("projects_tab", AppScreen.Projects.route, R.string.projects, Icons.Default.Widgets, false)
    object Devices: AppTab("devices_tab", AppScreen.Devices.route, R.string.devices, Icons.Default.DevicesOther,)
    object Employees: AppTab("employees_tab", AppScreen.Employees.route, R.string.employees, Icons.Default.People,)
    object Feedback: AppTab("feedback_tab", AppScreen.Feedback.route, R.string.feedback, Icons.Default.Feedback,)
    object Profile: AppTab("profile_tab", AppScreen.Profile.route, R.string.profile, Icons.Default.AccountCircle, false)
    object Reports: AppTab("reports_tab", AppScreen.Reports.route, R.string.reports, Icons.Default.Description)
    object Purchases: AppTab("purchases_tab", AppScreen.Purchases.route, R.string.purchases, Icons.Default.Payments)


    fun saveState() = bundleOf(SCREEN_KEY to route)

    fun asScreen() = when (this) {
        Events -> AppScreen.Events
        Projects -> AppScreen.Projects
        Devices -> AppScreen.Devices
        Employees -> AppScreen.Employees
        Feedback -> AppScreen.Feedback
        Profile -> AppScreen.Profile
        Reports -> AppScreen.Reports
        Purchases -> AppScreen.Purchases
    }

    companion object {
        const val SCREEN_KEY = "SCREEN_KEY"

        val all
            get() = listOf(
                Events,
                Projects,
                Purchases,
                Devices,
                Employees,
                Feedback,
                Profile,
                Reports
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
            Reports.route   -> Reports
            else            -> {throw IllegalArgumentException("Invalid route. Maybe you forgot to add a new screen to AppTabSaver.kt?")}
        }

        fun applyClaims(claims: List<Any>) {
            Feedback.accessible = claims.contains(UserClaimCategories.FEEDBACK.ADMIN)
        }
    }
}

// This class represents any screen - tabs and their subscreens.
// It is needed to appropriately change top app bar behavior

@Parcelize
open class AppScreen(
    @StringRes val screenNameResource: Int,
    val route: String,
    val navLink: String = route.substringBefore("/{")
) : Parcelable {
    // Employee-related
    object Employees: AppScreen(R.string.employees, "employees")
    object EmployeeDetails: AppScreen(R.string.profile, "employee/{userId}") // Has back button

    // Feedback-related
    object Feedback: AppScreen(R.string.feedback, "feedback")

    // Events-related
    object Events: AppScreen(R.string.events, "events")
    @Parcelize
    class EventDetails(val title: String): AppScreen(R.string.details_name, "event/{eventId}") { // Has back button
        companion object {
            const val route = "event/{eventId}"
            val navLink: String = route.substringBefore("/{")
        }
    }
    object EventNew: AppScreen(R.string.event_new, "event/new") // Has back button
    object EventsNotifications: AppScreen(R.string.notifications, "events/notifications") // Has back button

    // Projects-related
    object Projects: AppScreen(R.string.projects, "projects")

    // Devices-related
    object Devices: AppScreen(R.string.devices, "devices")

    // Profile-related
    object Profile: AppScreen(R.string.profile, "profile")


    // Reports-related
    object Reports: AppScreen(R.string.reports, "reports")
    class ReportDetails(val title: String): AppScreen(R.string.details_name, "report/{reportId}") {
        companion object {
            const val route = "report/{reportId}"
            val navLink: String = route.substringBefore("/{")
        }
    }
    object NewReport: AppScreen(R.string.report_new, "reports/new")


    // Purchases-related
    object Purchases: AppScreen(R.string.purchases, "purchases")
    class PurchaseDetails(val title: String): AppScreen(R.string.details_name, "purchases/{purchaseId}") {
        companion object {
            const val route = "purchases/{purchaseId}"
            val navLink: String = route.substringBefore("/{")
        }
    }


    companion object {
        fun getAll(context: Context) = listOf(
            Employees,
            EmployeeDetails,
            Feedback,
            Events,
            EventDetails(context.resources.getString(R.string.event)),
            EventsNotifications,
            Projects,
            Devices,
            Profile,
            Reports,
            ReportDetails(context.resources.getString(R.string.report)),
            NewReport,
            Purchases,
            PurchaseDetails(context.resources.getString(R.string.purchase))
        )
    }
}