package ru.rtuitlab.itlab.presentation.screens.employees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.PhoneEnabled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.users.models.User
import ru.rtuitlab.itlab.presentation.screens.employees.components.EmailField
import ru.rtuitlab.itlab.presentation.screens.employees.components.PhoneField
import ru.rtuitlab.itlab.presentation.screens.employees.components.UserEvents
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.backdrop.BackdropScaffold
import ru.rtuitlab.itlab.presentation.ui.components.backdrop.BackdropValue
import ru.rtuitlab.itlab.presentation.ui.components.backdrop.rememberBackdropScaffoldState

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun Employee(
    employeeViewModel: EmployeeViewModel
) {
    val user by employeeViewModel.user.collectAsState()

    val screenWidth = LocalView.current.width

    val screenHeightDp = with(LocalDensity.current) {
        LocalView.current.height.toDp()
    }
    val backdropScaffoldState = rememberBackdropScaffoldState(
        initialValue = BackdropValue.Peeking
    )

    BackdropScaffold(
        scaffoldState = backdropScaffoldState,
        backLayerContent = {
            user?.let { user ->
                SubcomposeAsyncImage(
                    modifier = Modifier.offset {
                        IntOffset(0, (backdropScaffoldState.offset.value.toInt() - screenWidth + 16.dp.toPx().toInt()) / 2)
                    },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.getGravatarWithSize(screenWidth))
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.gravatar),
                    loading = {
                        AsyncImage(
                            modifier = Modifier.fillMaxWidth(),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user.getGravatarWithSize(80))
                                .crossfade(false)
                                .build(),
                            placeholder = painterResource(R.drawable.ic_itlab),
                            contentScale = ContentScale.FillWidth,
                            contentDescription = stringResource(R.string.gravatar)
                        )
                    }
                )
            }
        },
        peekHeight = 0.01.dp,
        partialPeekHeight = screenHeightDp / 3,
        frontLayerContent = {
            user?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    EmployeeCredentials(it)

                    UserEvents(
                        userViewModel = employeeViewModel
                    )
                }
            }
        },
        frontLayerBackgroundColor = MaterialTheme.colorScheme.background,
        frontLayerShape = MaterialTheme.shapes.large,
        frontLayerElevation = 16.dp
    )
}

@Composable
fun EmployeeCredentials(
    user: User,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "${user.lastName} ${user.firstName} ${user.middleName}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))

        IconizedRow(
            imageVector = Icons.Default.MailOutline,
            contentDescription = stringResource(R.string.email),
            spacing = 0.dp
        ) {
            EmailField(value = user.email, context = context)
        }
        Spacer(Modifier.height(8.dp))

        IconizedRow(
            imageVector = Icons.Default.PhoneEnabled,
            contentDescription = stringResource(R.string.phone_number),
            spacing = 0.dp
        ) {
            PhoneField(user = user, context = context)
        }
        Spacer(Modifier.height(8.dp))

        if (user.vkId != null) {
            IconizedRow(
                painter = painterResource(R.drawable.ic_vk),
                contentDescription = stringResource(R.string.vk_id),
                opacity = .6f
            ) {
                Text(text = user.vkId)
            }
            Spacer(Modifier.height(8.dp))
        }

        if (user.group != null) {
            IconizedRow(
                imageVector = Icons.Default.PeopleOutline,
                contentDescription = stringResource(R.string.study_group),
                opacity = .6f
            ) {
                Text(text = user.group)
            }
            Spacer(Modifier.height(8.dp))
        }

        if (user.discordId != null) {
            IconizedRow(
                painter = painterResource(R.drawable.ic_discord),
                contentDescription = stringResource(R.string.discord_id),
                opacity = .6f
            ) {
                Text(text = user.discordId)
            }
            Spacer(Modifier.height(8.dp))
        }

        if (user.skypeId != null) {
            IconizedRow(
                painter = painterResource(R.drawable.ic_skype),
                contentDescription = stringResource(R.string.skype_id),
                opacity = .6f
            ) {
                Text(
                    text = user.skypeId
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}