package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// NevtaBook Core Brand Colors
val NevtaPrimary = Color(0xFFFF7418)
val NevtaPrimaryGold = Color(0xFFFFB347)
val NevtaDeepOrange = Color(0xFFD9530F)
val NevtaBackground = Color(0xFFFAF7F2)
val NevtaSurface = Color(0xFFFFFFFF)
val NevtaPrimaryText = Color(0xFF1A1A1A)
val NevtaSecondaryText = Color(0xFF555B67)
val NevtaMuted = Color(0xFF757575)
val NevtaBorder = Color(0xFFEAE5DE)
val NevtaCardBorder = Color(0xFFF0EDE7)
val NevtaGold = Color(0xFFD4A24C)

// Accents
val NevtaSuccess = Color(0xFF00A86B)
val NevtaSuccessBackground = Color(0xFFEAF8F0)
val NevtaDanger = Color(0xFFE11D48)
val NevtaDangerBackground = Color(0xFFFFEBF2)
val NevtaBlue = Color(0xFF2563EB)
val NevtaViolet = Color(0xFF7C3AED)
val NevtaPeach = Color(0xFFFFF1E8)
val NevtaSand = Color(0xFFFBF6EE)
val NevtaMint = Color(0xFFEAF8F0)
val NevtaSky = Color(0xFFEAF2FF)
val NevtaLavender = Color(0xFFF3EBFF)
val NevtaRose = Color(0xFFFFEBF2)

// Dark Theme Colors
val NevtaDarkBackground = Color(0xFF121417)
val NevtaDarkSurface = Color(0xFF1C1F26)
val NevtaDarkSurfaceElevated = Color(0xFF252932)
val NevtaDarkBorder = Color(0xFF2E333D)
val NevtaDarkTextPrimary = Color(0xFFF4F5F7)
val NevtaDarkTextSecondary = Color(0xFFA0A6B2)

// Gradients
val NevtaBrandGradient = Brush.horizontalGradient(
    colors = listOf(NevtaPrimary, NevtaPrimaryGold)
)
val NevtaHeroGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFF7A1A), Color(0xFFFFA83B))
)
val NevtaGoldGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFD4A24C), Color(0xFFFFC766))
)
val NevtaDarkCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF252932), Color(0xFF1C1F26))
)

