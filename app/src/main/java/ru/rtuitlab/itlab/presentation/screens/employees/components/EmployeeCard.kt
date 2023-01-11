package ru.rtuitlab.itlab.presentation.screens.employees.components

import androidx.compose.foundation.layout.*
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

@Composable
fun EmployeeCard(
    user: User,
    modifier: Modifier
) {
    Card(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .then(modifier)
    ) {
        user.run {
            Row(
                modifier = Modifier
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .width(40.dp)
                        .height(40.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(getGravatarWithSize(80))
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_itlab),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = stringResource(R.string.description)
                )

                Spacer(Modifier.width(16.dp))

                Text(
                    modifier = Modifier.weight(1f),
                    text = "$lastName $firstName $middleName",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.width(16.dp))

                email?.let {
                    EmailButton(it)
                }

                phoneNumber?.let {
                    PhoneButton(user)
                }
            }

        }
    }
}
