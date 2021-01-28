package ru.rtuitlab.itlab.utils

import android.os.Bundle
import androidx.compose.runtime.savedinstancestate.Saver
import ru.rtuitlab.itlab.ui.AppTab

// Saver to save and restore the current tab across config change and process death.
fun appTabSaver() = Saver<AppTab, Bundle>(
    save = { it.saveState() },
    restore = { AppTab.restoreState(it) }
)