package ru.rtuitlab.itlab.presentation.screens.employees.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import androidx.compose.material.*
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.InteractiveField

@Composable
fun EmployeeCard(
	user: User,
	modifier: Modifier
) {
	val context = LocalContext.current
	Card(
		modifier = modifier,
		elevation = 2.dp,
		shape = RoundedCornerShape(5.dp)
	) {
		user.run {
			Column(
				modifier = Modifier
					.padding(
						top = 10.dp,
						bottom = 8.dp,
						start = 15.dp,
						end = 15.dp
					)
					.fillMaxWidth()
			) {
				Text(
					text = "$lastName $firstName $middleName",
					fontWeight = FontWeight(500),
					fontSize = 17.sp,
					lineHeight = 22.sp
				)
				Spacer(Modifier.height(10.dp))
				if (email != null) {
					IconizedRow(
						painter = painterResource(R.drawable.ic_mail),
						contentDescription = stringResource(R.string.email),
						imageWidth = 16.dp,
						imageHeight = 12.dp,
						spacing = 0.dp
					) {
						InteractiveField(value = email) {
							val intent = Intent(Intent.ACTION_SENDTO).apply {
								data = Uri.parse("mailto:$email")
							}
							context.startActivity(intent)
						}
					}
					Spacer(Modifier.height(8.dp))
				}

				if (phoneNumber != null) {
					IconizedRow(
						painter = painterResource(R.drawable.ic_phone),
						contentDescription = stringResource(R.string.phone_number),
						imageWidth = 16.dp,
						imageHeight = 12.dp,
						spacing = 0.dp
					) {
						PhoneField(user = this@run, context = context)
					}
					Spacer(Modifier.height(8.dp))
				}
				/*if (properties.isNotEmpty()) {
					Spacer(modifier = Modifier.height(14.dp))
					LazyRow {
						items(properties) { prop ->
							if (prop.value != null) {
								UserTagComponent(tag = prop.value)
								Spacer(modifier = Modifier.width(10.dp))
							}
						}
					}
				}*/
			}

		}
	}
}
