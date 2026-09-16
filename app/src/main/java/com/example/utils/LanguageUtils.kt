package com.example.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

enum class AppLanguage(val code: String, val labelEn: String, val labelHi: String, val subtitle: String) {
    ENGLISH("en", "English", "अंग्रेज़ी", "Complete English interface"),
    HINDI("hi", "Hindi", "हिंदी", "शुद्ध हिंदी इंटरफ़ेस")
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

@Composable
fun t(english: String, hindi: String): String {
    val currentLang = LocalAppLanguage.current
    return when (currentLang) {
        AppLanguage.ENGLISH -> english
        AppLanguage.HINDI -> hindi
    }
}

fun resolveText(english: String, hindi: String, language: AppLanguage): String {
    return when (language) {
        AppLanguage.ENGLISH -> english
        AppLanguage.HINDI -> hindi
    }
}
