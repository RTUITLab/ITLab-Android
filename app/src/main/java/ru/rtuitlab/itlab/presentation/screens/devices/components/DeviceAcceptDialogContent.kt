package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import java.util.*

@ExperimentalMaterialApi
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

                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        painter = painterResource(R.drawable.ic_title),
                                        contentDescription = stringResource(R.string.title),
                                        modifier = Modifier
                                                .width(16.dp)
                                                .height(16.dp),

                                        )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                        text = "$title",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                                )

                        }
                        Spacer(modifier = Modifier.height(5.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        painter = painterResource(R.drawable.ic_serial_number),
                                        contentDescription = stringResource(R.string.serial_number),
                                        modifier = Modifier
                                                .width(16.dp)
                                                .height(16.dp),

                                        )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                        text = "$serialNumber",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                                )

                        }
                        Spacer(modifier = Modifier.height(5.dp))

                        Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                        painter = painterResource(R.drawable.ic_edit),
                                        contentDescription = stringResource(R.string.description),
                                        modifier = Modifier
                                                .width(16.dp)
                                                .height(16.dp),

                                        )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                        text = "$description",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                                )

                        }

                        Button(
                                modifier = Modifier
                                        .align(Alignment.End)
                                        .clipToBounds(),
                                onClick = {
                                        acceptActions()
                                },
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 0.dp,
                                        pressedElevation = 0.dp
                                )
                        ) {
                                Text(
                                        text = stringResource(R.string.confirm).uppercase(Locale.getDefault()),
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleMedium,
                                )
                        }

                }
        }
}
