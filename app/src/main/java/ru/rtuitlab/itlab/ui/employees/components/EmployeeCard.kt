package ru.rtuitlab.itlab.ui.employees.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.api.users.models.UserModel
import ru.rtuitlab.itlab.R

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext


@Composable
fun EmployeeCard(
	user: UserModel,
	modifier: Modifier
) {
	val context = LocalContext.current
	Card(
		modifier = modifier,
		elevation = 16.dp,
		shape = RoundedCornerShape(5.dp)
	) {
		user.run {
			Column(
				modifier = Modifier
					.padding(
						top = 10.dp,
						bottom = 15.dp,
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
					ContactMethodRow(
						painter = painterResource(R.drawable.ic_mail),
						contentDescription = stringResource(R.string.email)
					) {
						InteractableField(value = email) {
							val intent = Intent(Intent.ACTION_SENDTO).apply {
								data = Uri.parse("mailto:$email")
							}
							context.startActivity(intent)
						}
					}
				}

				if (phoneNumber != null) {
					ContactMethodRow(
						painter = painterResource(R.drawable.ic_phone),
						contentDescription = stringResource(R.string.phone_number)
					) {
						PhoneField(user = this@run, context = context)
					}
				}
				if (properties.isNotEmpty()) {
					Spacer(modifier = Modifier.height(14.dp))
					LazyRow {
						items(properties) { prop ->
							if (prop.value != null) {
								UserTagComponent(tag = prop.value)
								Spacer(modifier = Modifier.width(10.dp))
							}
						}
					}
				}
			}

		}
	}
}

@Composable
fun ContactMethodRow(
	painter: Painter,
	contentDescription: String,
	content: @Composable RowScope.() -> Unit
) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Image(
			modifier = Modifier
				.width(16.dp)
				.height(12.dp),
			painter = painter,
			contentDescription = contentDescription,
			contentScale = ContentScale.Fit
		)
		Spacer(Modifier.width(8.dp))
		content()
	}
	Spacer(Modifier.height(8.dp))
}