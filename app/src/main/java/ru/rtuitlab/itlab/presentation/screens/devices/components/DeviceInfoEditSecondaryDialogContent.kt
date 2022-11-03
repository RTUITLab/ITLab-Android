package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R

@ExperimentalMaterialApi
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
                    top = 20.dp,
                    start = 20.dp,
                    bottom = 10.dp,
                    end = 20.dp
                )
        ) {
            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {


                OutlinedTextField(
                    value = description.value,
                    onValueChange = {
                        description.value = it
                    },
                    placeholder = { Text(text = hint) },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = MaterialTheme.colors.background,
                        focusedBorderColor = MaterialTheme.colors.onSurface

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
                    Text(
                        text = stringResource(id = R.string.to_choose),
                        modifier = Modifier.clickable {
                            onConfirm(description.value)
                        }

                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }

    }
}
