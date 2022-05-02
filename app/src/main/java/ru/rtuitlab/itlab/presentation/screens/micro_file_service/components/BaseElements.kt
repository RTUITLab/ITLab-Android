package ru.rtuitlab.itlab.presentation.screens.micro_file_service.components

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.remote.api.micro_file_service.models.FileInfo
import ru.rtuitlab.itlab.presentation.screens.devices.DevicesViewModel
import ru.rtuitlab.itlab.presentation.screens.devices.components.DeviceCard
import ru.rtuitlab.itlab.presentation.screens.devices.components.FloatActionButton
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.MFSViewModel
import ru.rtuitlab.itlab.presentation.ui.components.LoadingError
import ru.rtuitlab.itlab.presentation.ui.components.bottom_sheet.BottomSheetViewModel

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun BaseElements(
	mfsViewModel: MFSViewModel
) {
	val file = mfsViewModel.file.collectAsState().value

	val filesResource by mfsViewModel.listFileInfoResponseFlow.collectAsState()
	var isRefreshing by remember { mutableStateOf(false) }




	Scaffold(
		modifier = Modifier
			.fillMaxSize(),

	) {
		Column(

		) {

			val fileDescription = remember { mutableStateOf("") }

			Row() {

				//provideFile
				Button(
					modifier = Modifier
						.weight(3f),
					onClick = {
						mfsViewModel.provideFile()
					}
				) {
					Text(text = stringResource(R.string.Select_a_file))
				}

				if(file!=null) {
					Text(
						modifier = Modifier
							.weight(3f),
						text = file.name,
						overflow = TextOverflow.Ellipsis
					)
					//FileNull
					IconButton(
						modifier = Modifier
							.background(Color.Cyan)
							.weight(1f),
						onClick = {
							mfsViewModel.setFileNull()
						}
					) {
						Icon(Icons.Outlined.Delete, contentDescription = null)

					}
				}
			}

			//UploadFile
			Button(onClick = {
				mfsViewModel.uploadFile(fileDescription.value)
			}){
				Text(text = stringResource(R.string.Upload_a_file))

			}

			//fileDescription
			OutlinedTextField(
				value = fileDescription.value,
				onValueChange = {
					fileDescription.value = it

				},
				placeholder = {
					Text(text = stringResource(R.string.description))
				},
				colors = TextFieldDefaults.outlinedTextFieldColors(
					backgroundColor = MaterialTheme.colors.background,
					focusedBorderColor = MaterialTheme.colors.onSurface

				),
				modifier = Modifier
					.fillMaxWidth()
			)
		}

		SwipeRefresh(
			modifier = Modifier
				.fillMaxWidth()
				.offset(0.dp, 200.dp),
			state = rememberSwipeRefreshState(isRefreshing),
			onRefresh = mfsViewModel::onRefresh
		) {
			Column(
				modifier = Modifier
					.fillMaxSize()
			) {
				filesResource.handle(
					onLoading = {
						isRefreshing = true
					},
					onError = { msg ->
						isRefreshing = false
						LoadingError(msg = msg)
					},
					onSuccess = {
						isRefreshing = false
						mfsViewModel.onResourceSuccess(it)

						FileList(mfsViewModel)


					}

				)
			}


		}


	}
}
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
private fun FileList(
	mfsViewModel: MFSViewModel
) {
	val files = mfsViewModel.listFileInfoFlow.collectAsState().value.reversed()



	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(10.dp),
		contentPadding = PaddingValues(horizontal = 15.dp, vertical = 15.dp),
		modifier = Modifier.fillMaxSize()
	) {


		items(files) { file ->

			FileCard(
				mfsViewModel = mfsViewModel,
				file = file,
				modifier = Modifier
					.fillMaxWidth()

			)


		}


	}

}

@Composable
fun FileCard(mfsViewModel: MFSViewModel, file: FileInfo, modifier: Modifier) {
	val context = LocalContext.current
	val exp = file.filename.substring(file.filename.lastIndexOf(".") + 1)

	Card(
		modifier = modifier
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(10.dp),
		) {
			Text(
				text = mfsViewModel.parseUploadDate(file.uploadDate)
			)
			Row {
				Column(
					modifier = Modifier
						.weight(1f)
						.padding(10.dp),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = file.senderfirstName + " " + file.senderlastName
					)
					TextButton(
						onClick = { mfsViewModel.downloadFile(context,file) }){
						Text(text = stringResource(R.string.download))
					}
				}
				Column(
					modifier = Modifier
						.weight(1f)
						.padding(10.dp),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					val( bitmap,setBitmap) = rememberSaveable{mutableStateOf<Bitmap?>(null)}
					Log.d("BASE","$exp")
					if(exp == "png" || exp == "jpeg" || exp == "jpg"|| exp == "gif") {
						mfsViewModel.getBitmapFromFile(context,file,setBitmap)
						if(bitmap != null){
							Image(
								bitmap = bitmap.asImageBitmap(),
								contentDescription = stringResource(R.string.report)
							)
						}else{
							Icon(
								painter = painterResource(R.drawable.ic_attach),
								contentDescription = stringResource(R.string.report)
							)
							Text(
								text = file.filename,
								overflow = TextOverflow.Clip
							)
							}
					}else {
						Icon(
							painter = painterResource(R.drawable.ic_attach),
							contentDescription = stringResource(R.string.report)
						)
						Text(
							text = file.filename,
							overflow = TextOverflow.Clip
						)
					}

				}
			}
		}
	}
}
