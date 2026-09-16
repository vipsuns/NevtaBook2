package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DashboardKpi
import com.example.data.model.EntryWithPersonAndEvent
import com.example.data.model.ReturnStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.AppLanguage
import com.example.utils.CurrencyUtils
import com.example.utils.DateUtils
import com.example.utils.t

@Composable
fun HomeScreen(
    viewModel: NevtaViewModel,
    onNavigateToEntries: () -> Unit,
    onNavigateToGiven: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToReturnNevta: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onViewEntryDetail: (String) -> Unit,
    onOpenAddEntry: () -> Unit,
    onOpenAddGiven: () -> Unit,
    onOpenAddEvent: () -> Unit,
    onOpenAddReminder: () -> Unit
) {
    val kpi by viewModel.dashboardKpi.collectAsState()
    val upcomingEvents by viewModel.upcomingEvents.collectAsState()
    val allEntries by viewModel.allEntries.collectAsState()
    val personBalances by viewModel.allPersonBalances.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val currentLanguage by viewModel.appLanguage.collectAsState()

    val unreadNotifications = notifications.count { !it.isRead }
    val netBalance = kpi.totalReceivedAmount - kpi.totalGivenAmount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Hero Balance Card (Section 55: Orange/Gold Gradient Hero)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NevtaBrandGradient)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = t("Net Nevta Balance", "कुल नेवता बैलेंस"),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = CurrencyUtils.formatRupee(netBalance),
                                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.22f)
                        ) {
                            Text(
                                text = t("256-bit Secure", "256-bit सुरक्षित"),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.White.copy(alpha = 0.25f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = t("Received", "प्राप्त नेवता"),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = CurrencyUtils.formatRupee(kpi.totalReceivedAmount),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(Color.White.copy(alpha = 0.25f))
                        )

                        Column {
                            Text(
                                text = t("Given", "दिया नेवता"),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = CurrencyUtils.formatRupee(kpi.totalGivenAmount),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(Color.White.copy(alpha = 0.25f))
                        )

                        Column {
                            Text(
                                text = t("Total Gifts", "कुल उपहार"),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${kpi.totalGiftsCount} ${t("gifts", "भेंट")}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions (Section 12: Horizontal Rounded Icon Tiles)
        Text(
            text = t("Quick Actions", "त्वरित कार्य"),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HomeQuickActionButton(
                title = t("New Nevta", "नया नेवता"),
                subtitle = t("Received", "मिला"),
                icon = Icons.Default.Add,
                backgroundColor = NevtaPrimary,
                iconTint = Color.White,
                isPrimary = true,
                onClick = onOpenAddEntry
            )
            HomeQuickActionButton(
                title = t("Given", "दिया हुआ"),
                subtitle = t("Nevta", "नेवता"),
                icon = Icons.Outlined.ArrowOutward,
                backgroundColor = NevtaPeach,
                iconTint = NevtaDeepOrange,
                onClick = onOpenAddGiven
            )
            HomeQuickActionButton(
                title = t("Event", "कार्यक्रम"),
                subtitle = t("Add", "जोड़ें"),
                icon = Icons.Outlined.Celebration,
                backgroundColor = NevtaSky,
                iconTint = NevtaBlue,
                onClick = onOpenAddEvent
            )
            HomeQuickActionButton(
                title = t("Reminder", "रिमाइंडर"),
                subtitle = t("Alert", "अलर्ट"),
                icon = Icons.Outlined.Alarm,
                backgroundColor = NevtaMint,
                iconTint = NevtaSuccess,
                onClick = onOpenAddReminder
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dashboard KPI 4-Card Grid (Section 11)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NevtaStatCard(
                title = t("Total Received", "कुल मिला नेवता"),
                value = CurrencyUtils.formatRupeeCompact(kpi.totalReceivedAmount),
                subtitle = "${kpi.totalReceivedCount} ${t("entries", "प्रविष्टियां")}",
                accentColor = NevtaPrimary,
                icon = Icons.Outlined.AccountBalanceWallet,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToEntries
            )
            NevtaStatCard(
                title = t("Total Given", "कुल दिया नेवता"),
                value = CurrencyUtils.formatRupeeCompact(kpi.totalGivenAmount),
                subtitle = "${kpi.totalGivenCount} ${t("given records", "दिए गए")}",
                accentColor = NevtaSuccess,
                icon = Icons.Outlined.Payments,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToGiven
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NevtaStatCard(
                title = t("Upcoming Events", "आने वाले कार्यक्रम"),
                value = kpi.upcomingEventsCount.toString(),
                subtitle = "${kpi.upcomingEventsCount} ${t("scheduled", "समारोह शेड्यूल")}",
                accentColor = NevtaBlue,
                icon = Icons.Outlined.EventAvailable,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToEvents
            )
            NevtaStatCard(
                title = t("Pending Return", "वापसी बाकी"),
                value = kpi.pendingReturnCount.toString(),
                subtitle = "${kpi.pendingReturnCount} ${t("pending returns", "नेवता रिटर्न बाकी")}",
                accentColor = NevtaViolet,
                icon = Icons.Outlined.SwapHoriz,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToReturnNevta
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Smart Insight Card (Section 56)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = NevtaSand,
            border = androidx.compose.foundation.BorderStroke(1.dp, NevtaGold.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NevtaGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = NevtaGold, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = t("Smart Insight", "स्मार्ट अंतर्दृष्टि"),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = NevtaDeepOrange
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (kpi.pendingReturnCount > 0) {
                            t(
                                "${kpi.pendingReturnCount} returns pending. 3 family events upcoming in Jaipur & Sikar.",
                                "${kpi.pendingReturnCount} लोगों का नेवता रिटर्न बाकी है। सीकर और जयपुर में 3 कार्यक्रम जल्द हैं।"
                            )
                        } else {
                            t(
                                "Most of your Nevta records are from Jaipur and Sikar.",
                                "आपके सबसे अधिक नेवते जयपुर और सीकर से दर्ज हैं।"
                            )
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Upcoming Events Section (Section 13)
        NevtaSectionHeader(
            titleHindi = "आने वाले कार्यक्रम",
            titleEnglish = "Upcoming Events",
            actionText = "${t("View All", "सभी देखें")} (${upcomingEvents.size})",
            onActionClick = onNavigateToEvents
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (upcomingEvents.isEmpty()) {
            NevtaEmptyState(
                title = t("No upcoming events scheduled", "कोई आगामी कार्यक्रम नहीं है"),
                subtitle = t("Tap below to schedule weddings or family functions", "पारिवारिक शादी या समारोह जोड़ने के लिए नीचे बटन दबाएं"),
                buttonText = t("Add Event", "कार्यक्रम जोड़ें"),
                onButtonClick = onOpenAddEvent,
                icon = Icons.Outlined.Celebration
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                upcomingEvents.take(2).forEach { event ->
                    NevtaEventCard(
                        event = event,
                        onClick = onNavigateToEvents,
                        onDelete = { viewModel.deleteEvent(event) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Entries Section (Section 16, 17)
        NevtaSectionHeader(
            titleHindi = "हाल की नेवता प्रविष्टियां",
            titleEnglish = "Recent Entries",
            actionText = "${t("View All", "सभी देखें")} (${allEntries.size})",
            onActionClick = onNavigateToEntries
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (allEntries.isEmpty()) {
            NevtaEmptyState(
                title = t("No recent entries yet", "अभी कोई नेवता प्रविष्टि नहीं है"),
                subtitle = t("Tap below to record your first received Nevta", "पहला नेवता जोड़ने के लिए नीचे बटन दबाएं"),
                buttonText = t("Add First Entry", "पहला नेवता जोड़ें"),
                onButtonClick = onOpenAddEntry,
                icon = Icons.Outlined.EditNote
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                allEntries.take(3).forEach { item ->
                    NevtaEntryCard(
                        item = item,
                        onViewDetail = { onViewEntryDetail(item.entry.entryId) },
                        onDelete = { viewModel.deleteEntry(item.entry) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun HomeQuickActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconTint: Color,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier
                .size(62.dp)
                .then(if (isPrimary) Modifier.shadow(4.dp, RoundedCornerShape(18.dp)) else Modifier),
            shape = RoundedCornerShape(18.dp),
            color = backgroundColor,
            border = if (!isPrimary) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)) else null
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
