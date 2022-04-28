package ru.rtuitlab.itlab.presentation.screens.micro_file_service.components

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.presentation.screens.micro_file_service.MFSViewModel

@Composable
fun BaseElements(
	mfsViewModel: MFSViewModel
) {

	Column(

	){

		val fileDescription = remember{ mutableStateOf("") }

		//provideFile
		Button(onClick = {
			mfsViewModel.provideFile()
		}
		) {
			Text(text = stringResource(R.string.Select_a_file))
		}

		//UploadFile
		Button(onClick = {
			mfsViewModel.uploadFile(fileDescription.value)
		}){
			Text(text = stringResource(R.string.Upload_a_file))

		}

		//FileNull
		Button(onClick = {
			mfsViewModel.setFileNull()
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