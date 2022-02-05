package ru.rtuitlab.itlab.data.remote.api.users.models

object UserClaimCategories {
	val FEEDBACK = UserClaimCategory()
	val REPORTS = UserClaimCategory()
	val SALARY = UserClaimCategory()
	val PROJECTS = UserClaimCategory()

	object USER
	object PURCHASE

	fun obtainClaimFrom(string: String): Any? = when {
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
		val USER: ClaimLevel.USER = ClaimLevel.USER()
	)

	sealed class ClaimLevel {
		class ADMIN : ClaimLevel()
		class USER : ClaimLevel()
	}

}
