package ru.rtuitlab.itlab.data.remote.api.users.models

object UserClaimCategories {
	val FEEDBACK = UserClaimCategory()
	val REPORTS = UserClaimCategory()
	val SALARY = UserClaimCategory()
	val PROJECTS = UserClaimCategory()
	val DEVICES = UserRole.DEVICES()

	object USER
	object PURCHASE

	private val roleMap = mapOf(
		"CanEditEquipment" to DEVICES.EDIT,
		"CanEditEquipmentOwner" to DEVICES.OWNER.EDIT,
		"CanEditEquipmentType" to DEVICES.TYPE.EDIT

		// later?
		/*"CanEditRoles",
		"CanEditEvent",
		"CanEditEventType",
		"CanDeleteEventRole",
		"CanEditUserPropertyTypes",
		"Administrator"*/
	)

	fun obtainClaimFrom(string: String): Any? = when {
		roleMap.containsKey(string) -> {
			roleMap[string]
		}
		string.startsWith("feedback") -> {
			when {
				string.contains("admin") -> FEEDBACK.ADMIN
				string.contains("user") -> FEEDBACK.USER
				else -> FEEDBACK
			}
		}
		string.startsWith("user") -> USER
		string.startsWith("reports") -> {
			when {
				string.contains("admin") -> REPORTS.ADMIN
				string.contains("user") -> REPORTS.USER
				else -> REPORTS
			}
		}
		string.startsWith("salary") -> {
			when {
				string.contains("admin") -> SALARY.ADMIN
				string.contains("user") -> SALARY.USER
				else -> SALARY
			}
		}
		string.startsWith("projects") -> {
			when {
				string.contains("admin") -> PROJECTS.ADMIN
				string.contains("user") -> PROJECTS.USER
				else -> PROJECTS
			}
		}
		string.startsWith("purchase") -> PURCHASE
		else -> null
	}

	data class UserClaimCategory(
		val ADMIN: ClaimLevel.ADMIN = ClaimLevel.ADMIN(),
		val USER: ClaimLevel.USER = ClaimLevel.USER(),
		val EDIT: RoleAction.EDIT = RoleAction.EDIT(),
		val DELETE: RoleAction.DELETE = RoleAction.DELETE()
	)

	sealed class ClaimLevel {
		class ADMIN : ClaimLevel()
		class USER : ClaimLevel()
	}

	sealed class RoleAction {
		class EDIT : RoleAction()
		class DELETE : RoleAction()
	}

	sealed class RoleSubject(
		val EDIT: RoleAction.EDIT = RoleAction.EDIT(),
		val DELETE: RoleAction.DELETE = RoleAction.DELETE()
	) {
		class OWNER : RoleSubject()
		class TYPE : RoleSubject()
	}

	sealed class UserRole {
		class DEVICES(
			val OWNER: RoleSubject = RoleSubject.OWNER(),
			val TYPE: RoleSubject = RoleSubject.TYPE(),
			val EDIT: RoleAction.EDIT = RoleAction.EDIT()
		): UserRole()
	}

}
