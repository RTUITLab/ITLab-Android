package ru.rtuitlab.itlab.data.remote.api.users.models

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import ru.rtuitlab.itlab.R

@Parcelize
@Serializable
data class UserPropertyTypeModel (
	val id : String,
	val title : String,
	val description : String? = null,
	val instancesCount : Int,
	val isLocked : Boolean
) : Parcelable {
	fun toUiPropertyType() = when(title) {
		"Учебная группа" -> UserPropertyType.StudyGroup(id)
		"VKID" -> UserPropertyType.VkId(id)
		"Skype" -> UserPropertyType.Skype(id)
		else -> UserPropertyType.Other(id, title)
	}
}

sealed class UserPropertyType(
	@StringRes val nameResource: Int? = null,
	open val id: String,
	open val icon: ImageVector? = null,
	open val vectorResource: Int? = null,
	open val name: String? = null
) {
	class StudyGroup(override val id: String) : UserPropertyType(R.string.study_group, id, Icons.Default.School)
	class VkId(override val id: String) : UserPropertyType(R.string.vk_id, id, vectorResource = R.drawable.ic_vk)
	class Skype(override val id: String) : UserPropertyType(R.string.skype_id, id, vectorResource = R.drawable.ic_skype)
	class Other(override val id: String, override val name: String) : UserPropertyType(id = id, icon = Icons.Default.Help, name = name)
}