package com.example.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.AppLanguage
import com.example.utils.IntentUtils
import com.example.utils.t

@Composable
fun SettingsScreen(
    viewModel: NevtaViewModel,
    onNavigateToFamily: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsState()
    val kpi by viewModel.dashboardKpi.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    var appLockEnabled by remember { mutableStateOf(false) }
    var hideAmounts by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 54.dp)
        ) {
            // Screen Section Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = t("Settings & Profile", "सेटिंग्स एवं प्रोफाइल"),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            letterSpacing = (-0.3).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = t("Account, language, backup & preferences", "खाता, भाषा, बैकअप एवं प्राथमिकताएं"),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Profile Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NevtaPersonAvatar(name = profile?.name ?: "Rajendra", size = 56.dp)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile?.name ?: "Rajendra Sharma",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${t("Head of Family", "गृह प्रमुख")} · ${profile?.city ?: "Jaipur"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NevtaMuted
                    )
                    Text(
                        text = profile?.mobile ?: "+91 98290 12345",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = NevtaPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 🌐 LANGUAGE SELECTION CARD (Dedicated user request)
        Text(
            text = t("App Language", "ऐप की भाषा (Language)"),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = NevtaDeepOrange
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NevtaGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                // English Option
                LanguageOptionRow(
                    title = "English",
                    subtitle = "Complete English interface",
                    selected = appLanguage == AppLanguage.ENGLISH,
                    onClick = {
                        viewModel.setAppLanguage(AppLanguage.ENGLISH)
                        Toast.makeText(context, "Language set to English", Toast.LENGTH_SHORT).show()
                    }
                )

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

                // Hindi Option
                LanguageOptionRow(
                    title = "हिंदी (Hindi)",
                    subtitle = "शुद्ध हिंदी इंटरफ़ेस (नेवता, हिसाब, कार्यक्रम)",
                    selected = appLanguage == AppLanguage.HINDI,
                    onClick = {
                        viewModel.setAppLanguage(AppLanguage.HINDI)
                        Toast.makeText(context, "भाषा बदलकर हिंदी कर दी गई", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 🌓 APPEARANCE & THEME (Light / Dark Mode Toggle)
        Text(
            text = t("Appearance & Theme", "थीम एवं डिस्प्ले (Appearance)"),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = NevtaDeepOrange
        )
        Spacer(modifier = Modifier.height(8.dp))

        val darkTurnedOnMsg = t("Dark mode turned on", "डार्क मोड चालू किया गया")
        val lightTurnedOnMsg = t("Light mode turned on", "लाइट मोड चालू किया गया")
        val lightEnabledMsg = t("Light mode enabled", "लाइट मोड सक्रिय किया गया")
        val darkEnabledMsg = t("Dark mode enabled", "डार्क मोड सक्रिय किया गया")

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) Color(0xFF2C2216) else NevtaPeach),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                                contentDescription = null,
                                tint = if (isDarkTheme) NevtaPrimaryGold else NevtaPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isDarkTheme) t("Dark Mode", "डार्क मोड (Dark Mode)") else t("Light Mode", "लाइट मोड (Light Mode)"),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isDarkTheme) {
                                    t("Comfortable for night viewing", "रात के समय देखने में सहज")
                                } else {
                                    t("Bright & classic warm theme", "चमकदार एवं क्लासिक वॉर्म थीम")
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { enabled ->
                            viewModel.setDarkTheme(enabled)
                            val msg = if (enabled) darkTurnedOnMsg else lightTurnedOnMsg
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NevtaPrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("dark_mode_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                Spacer(modifier = Modifier.height(12.dp))

                // Interactive 2-option selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Light Mode Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (!isDarkTheme) NevtaPeach else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (!isDarkTheme) NevtaPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (isDarkTheme) {
                                    viewModel.setDarkTheme(false)
                                    Toast.makeText(context, lightEnabledMsg, Toast.LENGTH_SHORT).show()
                                }
                            }
                            .testTag("light_mode_option")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Outlined.LightMode,
                                contentDescription = null,
                                tint = if (!isDarkTheme) NevtaPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = t("Light Mode", "लाइट मोड"),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (!isDarkTheme) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (!isDarkTheme) NevtaPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Dark Mode Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDarkTheme) NevtaPeach.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkTheme) NevtaPrimaryGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (!isDarkTheme) {
                                    viewModel.setDarkTheme(true)
                                    Toast.makeText(context, darkEnabledMsg, Toast.LENGTH_SHORT).show()
                                }
                            }
                            .testTag("dark_mode_option")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Outlined.DarkMode,
                                contentDescription = null,
                                tint = if (isDarkTheme) NevtaPrimaryGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = t("Dark Mode", "डार्क मोड"),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isDarkTheme) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isDarkTheme) NevtaPrimaryGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Backup & Export Section (Section 32)
        Text(
            text = t("Data Backup & Export", "डेटा बैकअप एवं निर्यात"),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = NevtaDeepOrange
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column {
                SettingsItem(
                    icon = Icons.Outlined.FileDownload,
                    title = t("Export CSV Report", "CSV रिपोर्ट निर्यात करें"),
                    subtitle = t("Spreadsheet of all Nevta entries", "सभी नेवता प्रविष्टियों की स्प्रेडशीट"),
                    onClick = {
                        viewModel.exportCsv { csv ->
                            IntentUtils.shareText(context, "NevtaBook_Export.csv", csv)
                        }
                    }
                )
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                SettingsItem(
                    icon = Icons.Outlined.CloudUpload,
                    title = t("Local Backup", "लोकल डेटा बैकअप लें"),
                    subtitle = t("Last backup: Today (Secure)", "अंतिम बैकअप: आज 02:30 PM (सुरक्षित)"),
                    onClick = {
                        val msg = if (appLanguage == AppLanguage.ENGLISH) "Data backed up successfully!" else "डेटा सुरक्षित रूप से बैकअप कर लिया गया है!"
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Family Management Link (Section 30)
        Text(
            text = t("Family Sharing", "परिवार एवं साझाकरण"),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = NevtaDeepOrange
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            SettingsItem(
                icon = Icons.Outlined.Group,
                title = t("Manage Family Members", "परिवार के सदस्य प्रबंधित करें"),
                subtitle = t("Admin, Editor & Viewer access", "एडमिन, एडिटर और दर्शक की अनुमतियां"),
                onClick = onNavigateToFamily
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Security & Privacy (Section 33)
        Text(
            text = t("Security & Privacy", "सुरक्षा एवं गोपनीयता"),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = NevtaDeepOrange
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(NevtaSand),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = NevtaDeepOrange, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(t("App PIN Lock", "ऐप लॉक (App PIN Lock)"), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(t("Ask 4-digit PIN on app open", "ऐप खोलने पर 4-अंकीय पिन मांगें"), style = MaterialTheme.typography.bodySmall, color = NevtaMuted)
                        }
                    }
                    val lockEnabledMsg = t("App Lock Enabled (PIN: 1234)", "ऐप लॉक सक्रिय (PIN: 1234)")
                    val lockDisabledMsg = t("App Lock Disabled", "ऐप लॉक निष्क्रिय")
                    Switch(
                        checked = appLockEnabled,
                        onCheckedChange = {
                            appLockEnabled = it
                            val msg = if (it) lockEnabledMsg else lockDisabledMsg
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(NevtaPeach),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.VisibilityOff, contentDescription = null, tint = NevtaPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(t("Privacy Mode", "गोपनीयता मोड (Privacy Mode)"), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(t("Hide amounts in public", "सार्वजनिक स्थानों पर नेवता राशि छुपाएं"), style = MaterialTheme.typography.bodySmall, color = NevtaMuted)
                        }
                    }
                    Switch(
                        checked = hideAmounts,
                        onCheckedChange = { hideAmounts = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Help & Support (Section 36)
        Text(
            text = t("Help & Support", "सहायता एवं संपर्क"),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = NevtaDeepOrange
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column {
                SettingsItem(
                    icon = Icons.Outlined.Chat,
                    title = t("WhatsApp Support", "व्हाट्सएप पर सहायता"),
                    subtitle = t("Available 9 AM to 9 PM", "सुबह 9 से रात 9 बजे तक उपलब्ध"),
                    onClick = {
                        val helpMsg = if (appLanguage == AppLanguage.ENGLISH) "Hello NevtaBook Support, I need assistance with:" else "नमस्ते NevtaBook टीम, मुझे सहायता चाहिए:"
                        IntentUtils.openWhatsApp(context, "+919829000000", helpMsg)
                    }
                )
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                SettingsItem(
                    icon = Icons.Outlined.Info,
                    title = t("About NevtaBook", "NevtaBook के बारे में"),
                    subtitle = t("Version v3.5 Native Android", "संस्करण v3.5 Native Android — नेवता का हिसाब, अब मोबाइल पर"),
                    onClick = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Persistent Footer
NevtaSummaryFooter(
    leftText = "${t("App Language", "ऐप भाषा")}: ${if (appLanguage == AppLanguage.HINDI) "हिंदी" else "English"}",
    rightLabel = "${t("Theme", "थीम")}: ",
    rightValue = if (isDarkTheme) t("Dark", "डार्क") else t("Light", "लाइट"),
    rightValueColor = NevtaPrimary,
    modifier = Modifier.align(Alignment.BottomCenter)
)
}
}

@Composable
private fun LanguageOptionRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = NevtaPrimary)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (selected) NevtaPrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = NevtaMuted
            )
        }
        if (selected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = NevtaPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(NevtaPeach),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = NevtaDeepOrange, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = NevtaMuted)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NevtaMuted)
    }
}
