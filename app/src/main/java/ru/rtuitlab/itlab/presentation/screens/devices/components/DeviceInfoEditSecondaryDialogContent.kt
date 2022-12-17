package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentTypeNewRequest
import ru.rtuitlab.itlab.presentation.ui.components.PrimaryTextButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceInfoEditSecondaryDialogContent(
    line: String,
    hint: String,
    onConfirm: (String) -> Unit
) {
    val description = rememberSaveable { mutableStateOf(line) }
    Card(
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 10.dp,
                    start = 20.dp,
                    bottom = 10.dp,
                    end = 20.dp
                )
        ) {
            Column(

                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {


                OutlinedTextField(
                    value = description.value,
                    onValueChange = {
                        description.value = it
                    },
                    placeholder = {
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(.8f)
                        ) },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface

                    ),
                    modifier = Modifier
                        .fillMaxWidth()

                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    PrimaryTextButton(
                        onClick = {
                            onConfirm(description.value)
                        },
                        text = stringResource(id = R.string.to_choose),
                    )


                }
            }
        }

    }
}
