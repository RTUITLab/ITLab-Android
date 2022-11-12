package ru.rtuitlab.itlab.presentation.screens.employees.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow

@Composable
fun EmployeeCard(
    user: User,
    modifier: Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .then(modifier)
    ) {
        user.run {
            Row {
                Column(
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 10.dp,
                        ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        modifier = Modifier
	                        .clip(RoundedCornerShape(10.dp))
	                        .width(50.dp)
	                        .height(50.dp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(getGravatarWithSize(200))
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_itlab),
                        contentScale = ContentScale.FillBounds,
                        contentDescription = stringResource(R.string.description),

                        )
                }
                Column(
                    modifier = Modifier
	                    .padding(
		                    top = 10.dp,
		                    bottom = 8.dp,
		                    start = 15.dp,
		                    end = 15.dp
	                    )
	                    .fillMaxWidth(),
                ) {
                    Text(
                        text = "$lastName $firstName $middleName",
                        style = MaterialTheme.typography.titleMedium
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
                            EmailField(value = email, context = LocalContext.current)
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
}
