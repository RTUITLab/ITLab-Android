package ru.rtuitlab.itlab.presentation.ui.theme

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppColors {


	var isLightTheme: Boolean = true
		set(value) {
			field = value
			_greyText.value =
				if (isLightTheme) Color.Black.copy(alpha = .6f)
				else Color.White.copy(alpha = .6f)
		}

	private var _accent = MutableStateFlow(if (isLightTheme) Color(0xFF007AFF) else Color(0xFF4FA3FF))
	val accent = _accent.asStateFlow()

	private var _greyText = MutableStateFlow(
		if (isLightTheme) Color.Black.copy(alpha = .6f)
		else Color.White.copy(alpha = .6f)
	)
	val green = Color(0xff28a745)
	val orange = Color(0xffFF8F00)
	val greyText = _greyText.asStateFlow()
	val red = Color(0xFFFF3B30)
	val tag = Color(0xFFE0E0E0)
	val appBarsLight = Color.White
	val appBarsDark = Color.Black
	val background = Color(0xFFF5F5F5)
}

// M3 color system uses tones to distinguish colors, but ITLab design sheet
// uses saturation. This causes irregular tones below. I did my best.
// Design sheet naming is specified in comments next to the colors
val Blue0   = Color(0xFF000000)
val Blue16  = Color(0xFF001550) // MD3 On Primary Container
val Blue45  = Color(0xFF004ee8) // MD3 Primary
val Blue56  = Color(0xFF1E5EFF) // Primary100
val Blue60  = Color(0xFF336DFF) // Primary90
val Blue65  = Color(0xFF4F81FF) // Primary80
val Blue69  = Color(0xFF4F81FF) // Primary70
val Blue77  = Color(0xFF89ABFF) // Primary60
val Blue86  = Color(0xFFB6CBFF) // Primary50
val Blue93  = Color(0xFFD9E4FF) // Primary40 MD3 Primary Container
val Blue96  = Color(0xFFECF2FF) // Primary30
val Blue100 = Color(0xFFFFFFFF)

val BlueSecondary13  = Color(0xFF001a43) // MD3 On Secondary Container
val BlueSecondary43  = Color(0xFF345ca8) // MD3 Secondary
val BlueSecondary92  = Color(0xFFd8e2ff) // MD3 Secondary Container

val BlueTertiary11 = Color(0xFF001c37) // MD3 On Tertiary Container
val BlueTertiary35 = Color(0xFF1060a4) // MD3 Tertiary
val BlueTertiary91 = Color(0xFFd2e4ff) // MD3 Tertiary Container

val LightBlue7 = Color(0xFF001f24) // MD3 On Background + On Surface
val LightBlue98 = Color(0xFFf6feff) // MD3 Background + Surface

val Red13 = Color(0xFF410002) // MD3 On Error Container
val Red42 = Color(0xFFba1a1a) // MD3 Error
val Red51 = Color(0xFFF0142F) // Red100
val Red56 = Color(0xFFF12B43) // Red90
val Red61 = Color(0xFFF34359) // Red80
val Red65 = Color(0xFFF45A6D) // Red70
val Red77 = Color(0xFFF8919D) // Red60
val Red84 = Color(0xFFFAB3BC) // Red50
val Red91 = Color(0xFFFCD5D9) // Red40
val Red92 = Color(0xFFffdad6) // MD3 Error Container
val Red95 = Color(0xFFFDE7EA) // Red30

val Green27 = Color(0xFF05874F) // Green100
val Green47 = Color(0xFF1FD286) // Green90
val Green51 = Color(0xFF25E191) // Green80
val Green60 = Color(0xFF48E9A5) // Green70
val Green70 = Color(0xFF74EFB9) // Green60
val Green77 = Color(0xFF95F6CC) // Green50
val Green87 = Color(0xFFC4F8E2) // Green40
val Green92 = Color(0xFFDAF9EC) // Green30

val Grey29 = Color(0xFF45464f) // MD3 On Surface-Variant
val Grey48 = Color(0xFF767680) // MD3 Outline

val Neutral11 = Color(0xFF131523) // General100
val Neutral26 = Color(0xFF333752) // General90
val Neutral43 = Color(0xFF5A607F) // General80
val Neutral57 = Color(0xFF7E84A3) // General70
val Neutral70 = Color(0xFFA1A7C4) // General60
val Neutral88 = Color(0xFFD7DBEC) // General50
val Neutral90 = Color(0xFFe2e1ec) // MD3 Surface-Variant
val Neutral93 = Color(0xFFE6E9F4) // General40
val Neutral97 = Color(0xFFF5F6FA) // General30