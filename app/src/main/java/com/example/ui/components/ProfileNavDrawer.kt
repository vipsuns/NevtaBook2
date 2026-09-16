package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.DashboardKpi
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.utils.AppLanguage
import com.example.utils.CurrencyUtils
import com.example.utils.t

@Composable
fun ProfileNavDrawerContent(
    userProfile: UserProfileEntity?,
    dashboardKpi: DashboardKpi,
    unreadNotifications: Int,
    currentRoute: String?,
    currentLanguage: AppLanguage,
    isDarkTheme: Boolean,
    onNavigateToRoute: (String) -> Unit,
    onToggleLanguage: () -> Unit,
    onToggleDarkTheme: () -> Unit,
    onEditProfile: () -> Unit,
    onTriggerBackup: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .widthIn(max = 330.dp)
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. GORGEOUS TOP PROFILE HERO CARD (Warm Orange-Gold Gradient)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NevtaPrimary,
                            NevtaDeepOrange,
                            NevtaGold
                        )
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column {
                // Top Row: App branding + Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "नेवता बुक · NevtaBook",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.3.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onCloseDrawer,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Menu",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Profile Identity Row: Avatar + Name + Contact + Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Big Circular Avatar with initial letters
                    val initials = userProfile?.name
                        ?.split(" ")
                        ?.filter { it.isNotBlank() }
                        ?.take(2)
                        ?.mapNotNull { it.firstOrNull()?.uppercase() }
                        ?.joinToString("")
                        ?: "RS"

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, NevtaPrimaryGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            ),
                            color = NevtaDeepOrange
                        )

                        // Online green dot
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(NevtaSuccess)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userProfile?.name ?: "Rajendra Sharma",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = "Verified",
                                tint = NevtaPrimaryGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = userProfile?.mobile ?: "+91 98290 12345",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Text(
                            text = "${t("Head of Family", "गृह प्रमुख")} · ${userProfile?.city ?: "Jaipur"}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Edit Profile Action Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onEditProfile)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = NevtaDeepOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = t("Edit Profile & Family Details", "प्रोफाइल व पारिवारिक जानकारी बदलें"),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = NevtaDeepOrange
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = NevtaDeepOrange,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // 2. FINANCIAL & LEDGER QUICK SNAPSHOT MINI-CARD
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = t("Family Nevta Summary", "पारिवारिक नेवता सारांश"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DrawerStatColumn(
                        label = t("Received", "मिला"),
                        value = CurrencyUtils.formatRupee(dashboardKpi.totalReceivedAmount),
                        valueColor = NevtaSuccess
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )
                    DrawerStatColumn(
                        label = t("Given", "दिया"),
                        value = CurrencyUtils.formatRupee(dashboardKpi.totalGivenAmount),
                        valueColor = NevtaDeepOrange
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )
                    DrawerStatColumn(
                        label = t("Net Balance", "बैलेंस"),
                        value = CurrencyUtils.formatRupee(dashboardKpi.totalReceivedAmount - dashboardKpi.totalGivenAmount),
                        valueColor = NevtaPrimary
                    )
                }
            }
        }

        // 3. NAVIGATION SECTION: CORE LEDGER
        DrawerSectionHeader(title = t("CORE NEVTA LEDGER", "मुख्य नेवता खाता"))

        DrawerNavigationItem(
            label = t("Home Dashboard", "होम डैशबोर्ड"),
            icon = Icons.Outlined.Home,
            selected = currentRoute == Screen.Home.route,
            onClick = { onNavigateToRoute(Screen.Home.route) }
        )

        DrawerNavigationItem(
            label = t("Received Nevta (Slips)", "नेवता मिला (रसीदें)"),
            icon = Icons.Outlined.AccountBalanceWallet,
            badgeText = if (dashboardKpi.totalReceivedCount > 0) "${dashboardKpi.totalReceivedCount}" else null,
            selected = currentRoute == Screen.Entries.route,
            onClick = { onNavigateToRoute(Screen.Entries.route) }
        )

        DrawerNavigationItem(
            label = t("Given Nevta", "दिया गया नेवता"),
            icon = Icons.Outlined.Payments,
            badgeText = if (dashboardKpi.totalGivenCount > 0) "${dashboardKpi.totalGivenCount}" else null,
            selected = currentRoute == Screen.Given.route,
            onClick = { onNavigateToRoute(Screen.Given.route) }
        )

        DrawerNavigationItem(
            label = t("Return Ledger", "नेवता वापसी खाता"),
            icon = Icons.Outlined.SwapHoriz,
            badgeText = if (dashboardKpi.pendingReturnCount > 0) "${dashboardKpi.pendingReturnCount} ${t("Pending", "बाकी")}" else null,
            badgeColor = NevtaDeepOrange,
            selected = currentRoute == Screen.Returns.route,
            onClick = { onNavigateToRoute(Screen.Returns.route) }
        )

        // 4. NAVIGATION SECTION: EVENTS & MANAGEMENT
        DrawerSectionHeader(title = t("EVENTS & FAMILY", "मांगलिक उत्सव व परिवार"))

        DrawerNavigationItem(
            label = t("Events & Celebrations", "मांगलिक कार्यक्रम"),
            icon = Icons.Outlined.Celebration,
            badgeText = if (dashboardKpi.upcomingEventsCount > 0) "${dashboardKpi.upcomingEventsCount}" else null,
            badgeColor = NevtaPrimary,
            selected = currentRoute == Screen.Events.route,
            onClick = { onNavigateToRoute(Screen.Events.route) }
        )

        DrawerNavigationItem(
            label = t("Reminders & Alerts", "रिमाइंडर व अलर्ट"),
            icon = Icons.Outlined.Alarm,
            selected = currentRoute == Screen.Reminders.route,
            onClick = { onNavigateToRoute(Screen.Reminders.route) }
        )

        DrawerNavigationItem(
            label = t("Family Members", "परिवार के सदस्य"),
            icon = Icons.Outlined.Group,
            selected = currentRoute == Screen.Family.route,
            onClick = { onNavigateToRoute(Screen.Family.route) }
        )

        DrawerNavigationItem(
            label = t("Reports & Statistics", "रिपोर्ट व सांख्यिकी"),
            icon = Icons.Outlined.BarChart,
            selected = currentRoute == Screen.Reports.route,
            onClick = { onNavigateToRoute(Screen.Reports.route) }
        )

        // 5. NAVIGATION SECTION: COMMUNICATIONS & PREFERENCES
        DrawerSectionHeader(title = t("COMMUNICATION & SETTINGS", "सूचना व सेटिंग्स"))

        DrawerNavigationItem(
            label = t("Notifications", "सूचनाएं"),
            icon = Icons.Outlined.Notifications,
            badgeText = if (unreadNotifications > 0) "$unreadNotifications" else null,
            badgeColor = NevtaDanger,
            selected = currentRoute == Screen.Notifications.route,
            onClick = { onNavigateToRoute(Screen.Notifications.route) }
        )

        DrawerNavigationItem(
            label = t("Settings & Preferences", "सेटिंग्स व प्राथमिकताएं"),
            icon = Icons.Outlined.Settings,
            selected = currentRoute == Screen.Settings.route,
            onClick = { onNavigateToRoute(Screen.Settings.route) }
        )

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // 6. QUICK INTERACTIVE CONTROLS DIRECTLY INSIDE DRAWER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            // Language Switcher Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggleLanguage)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Translate,
                            contentDescription = null,
                            tint = NevtaDeepOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = t("App Language", "ऐप की भाषा"),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NevtaSand,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NevtaGold.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "English (हिन्दी)" else "हिन्दी (English)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = NevtaDeepOrange,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dark Mode Toggle Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggleDarkTheme)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = null,
                            tint = if (isDarkTheme) NevtaPrimaryGold else NevtaPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isDarkTheme) t("Dark Mode", "डार्क मोड") else t("Light Mode", "लाइट मोड"),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleDarkTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NevtaPrimaryGold,
                            checkedTrackColor = NevtaPeach,
                            uncheckedThumbColor = NevtaPrimary,
                            uncheckedTrackColor = NevtaSand
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Instant Cloud Backup Trigger
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NevtaMint,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onTriggerBackup)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CloudDone,
                            contentDescription = null,
                            tint = NevtaSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = t("Cloud Sync & Backup", "क्लाउड बैकअप सुरक्षित"),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = NevtaSuccess
                        )
                    }
                    Text(
                        text = t("Backup Now", "अभी करें"),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp),
                        color = NevtaSuccess
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 7. FOOTER & APP INFO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NevtaBook · पारिवारिक नेवता डायरी v1.0.4",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "100% Offline Vault · 256-bit Encrypted",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = NevtaMuted
            )
        }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp
        ),
        color = NevtaDeepOrange,
        modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 4.dp)
    )
}

@Composable
private fun DrawerNavigationItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    badgeText: String? = null,
    badgeColor: Color = NevtaPrimary
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) NevtaPeach else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) NevtaPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.5.sp
                    ),
                    color = if (selected) NevtaPrimary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (badgeText != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerStatColumn(
    label: String,
    value: String,
    valueColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            ),
            color = valueColor
        )
    }
}

@Composable
fun EditProfileDialog(
    currentProfile: UserProfileEntity?,
    onDismiss: () -> Unit,
    onSave: (UserProfileEntity) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile?.name ?: "Rajendra Sharma") }
    var mobile by remember { mutableStateOf(currentProfile?.mobile ?: "+91 98290 12345") }
    var email by remember { mutableStateOf(currentProfile?.email ?: "rajendra.sharma@example.com") }
    var city by remember { mutableStateOf(currentProfile?.city ?: "Jaipur") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NevtaPeach),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = NevtaPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = t("Edit Profile", "प्रोफाइल विवरण"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = t("Family head & contact details", "गृह प्रमुख एवं संपर्क विवरण"),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(t("Full Name (नाम)", "पूरा नाम")) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it },
                    label = { Text(t("Mobile Number (मोबाइल नंबर)", "मोबाइल नंबर")) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text(t("City / Village (शहर / गाँव)", "शहर अथवा गाँव")) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(t("Email Address (ईमेल)", "ईमेल")) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = (currentProfile ?: UserProfileEntity()).copy(
                        name = name.ifBlank { "Rajendra Sharma" },
                        mobile = mobile.ifBlank { "+91 98290 12345" },
                        city = city.ifBlank { "Jaipur" },
                        email = email.ifBlank { "rajendra.sharma@example.com" }
                    )
                    onSave(updated)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NevtaPrimary)
            ) {
                Text(t("Save Changes", "सहेजें"), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(t("Cancel", "रद्द करें"))
            }
        }
    )
}
