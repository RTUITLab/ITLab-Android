package ru.rtuitlab.itlab.presentation.screens.devices.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentTypeNewRequest
import ru.rtuitlab.itlab.data.remote.api.devices.models.EquipmentTypeResponse
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalTransitionApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun DeviceInfoEditEquipmentTypeDialogContent(
    devicesViewModel: DevicesViewModel,
    onConfirm: (EquipmentTypeResponse) -> Unit
) {
    val searchQuery by devicesViewModel.typeSearchQuery.collectAsState()

    val (equipmentTypeNewCardBool, setEquipmentTypeNewCardBool) = remember { mutableStateOf(false) }

    val typesResource = devicesViewModel.equipmentTypeResponsesFlow.collectAsState().value

    val allTypes by devicesViewModel.equipmentTypes.collectAsState()

    val queriedTypes by devicesViewModel.queriedEquipmentTypes.collectAsState()

    val (extendedEquipmentNewCard, setExtendedEquipmentNewCard) = remember { mutableStateOf(false) }


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
                    .height(340.dp)
                    .padding(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        devicesViewModel.onTypesSearch(query)
                        setEquipmentTypeNewCardBool(!allTypes.any { it.title == query })
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.equipmentType),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(.8f),)
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()

                )
                AnimatedVisibility(equipmentTypeNewCardBool) {

                    Spacer(modifier = Modifier.height(5.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                setExtendedEquipmentNewCard(
                                    !extendedEquipmentNewCard
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Icon(
                                if (extendedEquipmentNewCard) Icons.Default.ExpandLess else Icons.Default.Add,
                                contentDescription = stringResource(
                                    id = R.string.add_device
                                ),
                                modifier = Modifier
                                    .width(30.dp)
                                    .clickable {
                                        setExtendedEquipmentNewCard(
                                            !extendedEquipmentNewCard
                                        )
                                    }
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = searchQuery,
                                maxLines = 1,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(.8f),)
                        }
                    }
                }


                AnimatedVisibility(extendedEquipmentNewCard) {
                    val titleEquipmentNew =
                        rememberSaveable { mutableStateOf(searchQuery) }
                    val errorTitle = rememberSaveable { mutableStateOf(false) }
                    val shortTitle =
                        rememberSaveable { mutableStateOf(titleEquipmentNew.value) }
                    val description =
                        rememberSaveable { mutableStateOf(titleEquipmentNew.value) }
                    val errorDescription =
                        rememberSaveable { mutableStateOf(false) }
                    val sizeDescription =
                        rememberSaveable { mutableStateOf(description.value.length) }
                    val sizeMaxDescription = 280

                    val scrollState = rememberScrollState()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                    ) {
                        OutlinedTextField(
                            value = titleEquipmentNew.value,
                            onValueChange = {
                                titleEquipmentNew.value = it
                                errorTitle.value = queriedTypes.any { it.title == titleEquipmentNew.value }
                            },
                            placeholder = { Text(text = stringResource(R.string.equipmentType)) },
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = if (errorTitle.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                containerColor = MaterialTheme.colorScheme.background,
                                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                focusedLabelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            label = { Text(
                                text = stringResource(id = R.string.title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(.8f)) }
                        )

                        OutlinedTextField(
                            value = shortTitle.value,
                            onValueChange = {
                                shortTitle.value = it
                            },
                            placeholder = { Text(text = stringResource(R.string.shortTitle)) },
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                textColor = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            label = {
                                Text(text = stringResource(id = R.string.shortTitle),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(.8f),) }
                        )
                        OutlinedTextField(
                            value = description.value,
                            onValueChange = {
                                description.value = it
                                sizeDescription.value =
                                    description.value.length
                                errorDescription.value =
                                    sizeDescription.value >= sizeMaxDescription
                            },
                            placeholder = { Text(text = stringResource(R.string.description)) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = if (errorDescription.value) MaterialTheme.colorScheme.error else Color.Gray,
                                containerColor = MaterialTheme.colorScheme.background,
                                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            label = { Text(
                                text = stringResource(id = R.string.description) + " " + "${sizeDescription.value}" + "/" + sizeMaxDescription,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(.8f)
                            )},
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = stringResource(id = R.string.to_create),
                                modifier = Modifier.clickable {
                                    if (!errorDescription.value && !errorTitle.value) {
                                        val equipmentTypeNewRequest =
                                            EquipmentTypeNewRequest(
                                                titleEquipmentNew.value,
                                                shortTitle.value,
                                                description.value
                                            )

                                        devicesViewModel.createEquipmentType(
                                            equipmentTypeNewRequest
                                        ) { isSuccessful ->
                                            if (isSuccessful) {
                                                setExtendedEquipmentNewCard(false)
                                                setEquipmentTypeNewCardBool(false)
                                            }
                                        }
                                    }
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))

                    }
                }
                AnimatedVisibility(
                    !extendedEquipmentNewCard,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                        .fillMaxWidth()) {
                        typesResource.handle(
                            onLoading = {},
                            onError = { msg ->
                                LoadingError(msg = msg)
                            },
                            onSuccess = {
                                EquipmentList(
                                    devicesViewModel = devicesViewModel,
                                    onTypeSelected = {
                                        devicesViewModel.onTypesSearch(it.title)
                                    },
                                    setNewCardBool = setEquipmentTypeNewCardBool,
                                    setExtended = setExtendedEquipmentNewCard,
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = stringResource(id = R.string.to_choose),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                            modifier = Modifier.clickable {
                                val eq = queriedTypes.find { it ->
                                    it.title == searchQuery
                                }
                                if (eq != null) {
                                    onConfirm(eq)
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }

            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
private fun EquipmentList(
    devicesViewModel: DevicesViewModel,
    onTypeSelected: (EquipmentTypeResponse) -> Unit,
    setNewCardBool: (Boolean) -> Unit,
    setExtended: (Boolean) -> Unit,
) {
    val types by devicesViewModel.queriedEquipmentTypes.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .padding(0.dp, 0.dp, 0.dp, 0.dp)
    ) {
        items(types) { type ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onTypeSelected(type)
                        setNewCardBool(!types.any { it.title == type.title })
                        setExtended(false)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    Text(
                        text = type.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(.8f),
                        modifier = Modifier.padding(5.dp, 0.dp)
                    )
                }
                Divider(
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

