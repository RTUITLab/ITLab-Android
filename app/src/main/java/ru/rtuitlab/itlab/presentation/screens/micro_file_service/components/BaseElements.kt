package ru.rtuitlab.itlab.presentation.screens.micro_file_service.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.MFSViewModel

@Composable
fun BaseElements(
	mfsViewModel: MFSViewModel
) {
	val file = mfsViewModel.file.collectAsState().value
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
}