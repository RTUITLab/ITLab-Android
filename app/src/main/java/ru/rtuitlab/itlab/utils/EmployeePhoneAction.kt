package ru.rtuitlab.itlab.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.annotation.StringRes
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.api.users.models.User
import ru.rtuitlab.itlab.api.users.models.UserResponse

enum class EmployeePhoneAction(@StringRes val resourceId: Int) {
	DIAL(R.string.dial),
	SAVE(R.string.save)
}

fun Context.doPhoneAction(action: EmployeePhoneAction, user: User) = when (action) {
	EmployeePhoneAction.DIAL -> dialNumberIntent(user.phoneNumber!!)
	EmployeePhoneAction.SAVE -> saveContactIntent(user, getString(R.string.rtuitlab))
}.let {
	startActivity(it)
}

private fun dialNumberIntent(phoneNumber: String) = Intent(Intent.ACTION_DIAL).apply {
	data = Uri.parse("tel:$phoneNumber")
}

private fun saveContactIntent(user: User, company: String) = Intent(Intent.ACTION_INSERT).apply {
	type = ContactsContract.Contacts.CONTENT_TYPE
	putExtra(ContactsContract.Intents.Insert.NAME, user.run { "$lastName $firstName $middleName" })
	putExtra(ContactsContract.Intents.Insert.PHONE, user.phoneNumber)
	putExtra(ContactsContract.Intents.Insert.COMPANY, company)
	putExtra(ContactsContract.Intents.Insert.EMAIL, user.email)
}