package ru.rtuitlab.itlab.presentation.screens.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmailField
import ru.rtuitlab.itlab.presentation.screens.employees.components.PhoneField
import ru.rtuitlab.itlab.presentation.screens.employees.components.UserEvents
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun Employee(
    employeeViewModel: EmployeeViewModel,
    bottomSheetViewModel: BottomSheetViewModel
) {
    val user by employeeViewModel.user.collectAsState()
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            user?.let {
                EmployeeCredentials(it)
            }
            UserEvents(
                employeeViewModel,
                bottomSheetViewModel
            )
        }
    }
}

@Composable
fun EmployeeCredentials(
    user: User,
) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 10.dp,
            ),
    ) {

        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .width(150.dp)
                .height(150.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.getGravatarWithSize(800))
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_itlab),
            contentScale = ContentScale.FillBounds,
            contentDescription = stringResource(R.string.description),

            )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 15.dp, horizontal = 20.dp)
    ) {
        Text(
            text = "${user.lastName} ${user.firstName} ${user.middleName}",
            fontWeight = FontWeight(500),
            fontSize = 20.sp,
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        IconizedRow(
            painter = painterResource(R.drawable.ic_mail),
            contentDescription = stringResource(R.string.email),
            spacing = 0.dp
        ) {
            EmailField(value = user.email, context = context)
        }
        Spacer(Modifier.height(8.dp))

        IconizedRow(
            painter = painterResource(R.drawable.ic_phone),
            contentDescription = stringResource(R.string.phone_number),
            spacing = 0.dp
        ) {
            PhoneField(user = user, context = context)
        }
        Spacer(Modifier.height(8.dp))

        if (user.group != null) {
            IconizedRow(
                painter = painterResource(R.drawable.ic_hat),
                contentDescription = stringResource(R.string.study_group)
            ) {
                Text(text = user.group)
            }
            Spacer(Modifier.height(8.dp))
        }
        if (user.vkId != null) {
            IconizedRow(
                painter = painterResource(R.drawable.ic_vk),
                contentDescription = stringResource(R.string.vk_id)
            ) {
                Text(text = user.vkId)
            }
            Spacer(Modifier.height(8.dp))
        }

        if (user.discordId != null) {
            IconizedRow(
                painter = painterResource(R.drawable.ic_discord),
                contentDescription = stringResource(R.string.discord_id)
            ) {
                Text(text = user.discordId)
            }
            Spacer(Modifier.height(8.dp))
        }

        if (user.skypeId != null) {
            IconizedRow(
                painter = painterResource(R.drawable.ic_skype),
                contentDescription = stringResource(R.string.skype_id)
            ) {
                Text(
                    text = user.skypeId
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

    }
}