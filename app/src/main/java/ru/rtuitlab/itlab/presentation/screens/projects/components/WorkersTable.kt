package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rtuitlab.itlab.R
import ru.rtuitlab.itlab.data.local.projects.models.UserWorker
import ru.rtuitlab.itlab.presentation.ui.components.UserLink

private val BORDER_COLOR
    @Composable get() = LocalContentColor.current.copy(.5f)

private val CELL_TEXT_COLOR
    @Composable get() = BORDER_COLOR.copy(.8f)
@Composable
fun WorkersTable(
    modifier: Modifier = Modifier,
    workers: List<UserWorker>
) {
    val users = remember(workers) {
        workers.map { it.user.toUser() }
    }
    val roles = remember(workers) {
        workers.map { it.worker.role }
    }
    val monthlySalaries = remember(workers) {
        workers.map { it.worker.monthlySalary }
    }
    val hourlySalaries = remember(workers) {
        workers.map { it.worker.hourlyRate }
    }
    Row(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(.5.dp, BORDER_COLOR)
                    .padding(4.dp),
                text = stringResource(R.string.version_employee),
                textAlign = TextAlign.Center,
                color = CELL_TEXT_COLOR,
                fontSize = 14.sp
            )

            users.forEach {

                UserLink(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, BORDER_COLOR)
                        .padding(4.dp),
                    user = it,
                    style = LocalTextStyle.current.copy(fontSize = 12.sp)
                )
                /*Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, BORDER_COLOR)
                        .padding(4.dp),
                    text = it.abbreviatedName,
                    textAlign = TextAlign.Center,
                    color = CELL_TEXT_COLOR,
                    fontSize = 12.sp
                )*/
            }
        }

        Column(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(.5.dp, BORDER_COLOR)
                    .padding(4.dp),
                text = stringResource(R.string.version_role),
                textAlign = TextAlign.Center,
                color = CELL_TEXT_COLOR,
                fontSize = 14.sp
            )
            roles.forEach {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, BORDER_COLOR)
                        .padding(4.dp),
                    text = it,
                    textAlign = TextAlign.Center,
                    color = CELL_TEXT_COLOR,
                    fontSize = 12.sp
                )
            }
        }

        Column(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(.5.dp, BORDER_COLOR)
                    .padding(4.dp),
                text = stringResource(R.string.version_salary),
                textAlign = TextAlign.Center,
                color = CELL_TEXT_COLOR,
                fontSize = 14.sp
            )
            monthlySalaries.forEach {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, BORDER_COLOR)
                        .padding(4.dp),
                    text = it.toString(),
                    textAlign = TextAlign.Center,
                    color = CELL_TEXT_COLOR,
                    fontSize = 12.sp
                )
            }
        }

        Column(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(.5.dp, BORDER_COLOR)
                    .padding(4.dp),
                text = stringResource(R.string.version_hourly_rate),
                textAlign = TextAlign.Center,
                color = CELL_TEXT_COLOR,
                fontSize = 14.sp
            )
            hourlySalaries.forEach {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(.5.dp, BORDER_COLOR)
                        .padding(4.dp),
                    text = it.toString(),
                    textAlign = TextAlign.Center,
                    color = CELL_TEXT_COLOR,
                    fontSize = 12.sp
                )
            }
        }
    }
}