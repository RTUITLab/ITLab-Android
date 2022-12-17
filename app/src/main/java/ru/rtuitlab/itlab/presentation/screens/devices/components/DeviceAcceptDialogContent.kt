package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.ui.components.IconizedRow
import ru.rtuitlab.itlab.presentation.ui.components.PrimaryTextButton
import java.util.*

@Composable
fun DeviceAcceptDialogContent(

        title:String,
        serialNumber:String,
        description:String,
        acceptActions: () -> Unit
) {



        Card(
                shape = RoundedCornerShape(10.dp)
        ) {
                Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                        top = 20.dp,
                                        start = 20.dp,
                                        bottom = 10.dp,
                                        end = 20.dp
                                )
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                        text = "Проверьте информацию",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(.8f),

                                        )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        IconizedRow(painter = painterResource(R.drawable.ic_title), contentDescription = stringResource(R.string.title)) {
                                Text(
                                        text = "$title",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                                )
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        IconizedRow(painter = painterResource(R.drawable.ic_serial_number), contentDescription = stringResource(R.string.serial_number)) {
                                Text(
                                        text = "$serialNumber",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                                )
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        IconizedRow(painter = painterResource(R.drawable.ic_edit), contentDescription = stringResource(R.string.description)) {
                                Text(
                                        text = "$description",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                                )
                        }


                        PrimaryTextButton(
                                modifier = Modifier
                                        .align(Alignment.End)
                                        .clipToBounds(),
                                onClick = {
                                        acceptActions()
                                },
                                text = stringResource(R.string.confirm).uppercase(Locale.getDefault()),

                                )

                }
        }
}
