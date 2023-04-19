package ru.rtuitlab.itlab.presentation.screens.projects.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import ru.rtuitlab.itlab.common.extensions.fromIso8601ToDateTime
import ru.rtuitlab.itlab.common.extensions.toIsoString
import ru.rtuitlab.itlab.data.local.users.models.UserEntity
import ru.rtuitlab.itlab.presentation.navigation.LocalNavController
import ru.rtuitlab.itlab.presentation.ui.components.UserLink
import ru.rtuitlab.itlab.presentation.ui.components.datetime.DateTimeLabel
import ru.rtuitlab.itlab.presentation.ui.components.markdown.MarkdownTextArea
import ru.rtuitlab.itlab.presentation.ui.components.shimmer.ShimmerBox
import ru.rtuitlab.itlab.presentation.ui.theme.ITLabTheme
import java.time.ZonedDateTime

@Composable
fun ThreadRecord(
    textMd: String,
    dateTime: String,
    author: UserEntity
) {
    Card {
        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 18.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            MarkdownTextArea(
                textMd = textMd,
                paddingValues = PaddingValues(0.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (date, time) = dateTime.fromIso8601ToDateTime(LocalContext.current)
                DateTimeLabel(date, time)
                UserLink(author)
            }
        }
    }
}

@Composable
fun ShimmeredThreadRecord() {
    Card {
        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 18.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            repeat(3) {
                ShimmerBox(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            ShimmerBox(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(.4f)
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .height(20.dp)
                        .width(120.dp)
                )
                ShimmerBox(
                    modifier = Modifier
                        .height(20.dp)
                        .width(120.dp),
                    color = MaterialTheme.colorScheme.primary.copy(.6f)
                )
            }
        }
    }
}

@Preview
@Composable
fun ThreadRecordPreview() {
    ITLabTheme {
        Surface {
            CompositionLocalProvider(
                LocalNavController provides rememberNavController()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ThreadRecord(
                        textMd = "[Источник по картинке](https://ru.gravatar.com/site/implement/images/)  \n[Источник по хэшу](https://ru.gravatar.com/site/implement/hash/)  \nКак получить аватар пользователя:\n1. Убрать пробелы в начале и конце почты пользователя\n2. Перевести почту в нижний регистр\n3. Получить md5 хэш от почты\n4. Вставить хэш в ссылку https://www.gravatar.com/avatar/HASH\n\nПолучить аватарку нужного размера:  \n`https://www.gravatar.com/avatar/HASH?s=200`  \nВместо `s=200` можно вставить значение от 1 до 2048 (пикселей)",
                        dateTime = ZonedDateTime.now().toIsoString(),
                        author = UserEntity(
                            id = "",
                            firstName = "Никита",
                            middleName = "Максимович",
                            lastName = "Миронов"
                        )
                    )
                    
                    ShimmeredThreadRecord()
                }
            }
        }
    }
}