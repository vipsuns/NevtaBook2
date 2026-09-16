package com.example.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.CurrencyUtils
import com.example.utils.IntentUtils
import com.example.utils.t

@Composable
fun ReportsScreen(
    viewModel: NevtaViewModel,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val kpi by viewModel.dashboardKpi.collectAsState()
    val allEntries by viewModel.allEntries.collectAsState()
    val allGiven by viewModel.allGiven.collectAsState()
    val allEvents by viewModel.allEvents.collectAsState()

    var selectedReportType by remember { mutableStateOf("Overall") } // Overall, By Event, By City, Gifts

    // Calculation for City/Village breakdown
    val cityBreakdown = remember(allEntries) {
        allEntries
            .groupBy { if (it.person.city.isNotBlank()) it.person.city else "अन्य" }
            .mapValues { entry -> entry.value.sumOf { it.entry.amount } }
            .toList()
            .sortedByDescending { it.second }
    }

    // Top contributors
    val topContributors = remember(allEntries) {
        allEntries
            .sortedByDescending { it.entry.amount }
            .take(5)
    }

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = t("Reports & Analysis", "रिपोर्ट एवं विश्लेषण"),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                letterSpacing = (-0.3).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = t("Family balance, income-expense & statistics", "पारिवारिक आय-व्यय व सांख्यिकी"),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            viewModel.exportCsv { csvContent ->
                                IntentUtils.shareText(
                                    context = context,
                                    title = "NevtaBook Report",
                                    content = csvContent
                                )
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NevtaPrimary,
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("Export CSV", "निर्यात करें"), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Report Type Tabs
                val reportTypes = listOf(
                    "Overall" to t("Overall", "संपूर्ण विवरण"),
                    "By City" to t("By City", "शहर अनुसार"),
                    "Top" to t("Top Contributors", "शीर्ष नेवतादाता"),
                    "Gifts" to t("Gifts List", "उपहार सूची")
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(reportTypes) { (key, label) ->
                        NevtaChip(
                            text = label,
                            selected = selectedReportType == key,
                            onClick = { selectedReportType = key }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

        // Financial Overview Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = t("Financial Comparison", "वित्तीय तुलना (Financial Comparison)"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Received Bar
                val maxAmount = maxOf(kpi.totalReceivedAmount, kpi.totalGivenAmount, 1.0)
                val receivedRatio = (kpi.totalReceivedAmount / maxAmount).toFloat().coerceIn(0.05f, 1f)
                val givenRatio = (kpi.totalGivenAmount / maxAmount).toFloat().coerceIn(0.05f, 1f)

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(t("Total Received Nevta", "कुल प्राप्त नेवता (Received)"), style = MaterialTheme.typography.bodySmall)
                        Text(CurrencyUtils.formatRupee(kpi.totalReceivedAmount), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = NevtaSuccess))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(receivedRatio)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .background(NevtaSuccess)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Given Bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(t("Total Given Nevta", "कुल दिया नेवता (Given)"), style = MaterialTheme.typography.bodySmall)
                        Text(CurrencyUtils.formatRupee(kpi.totalGivenAmount), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = NevtaDeepOrange))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(givenRatio)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .background(NevtaDeepOrange)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = t("Net Surplus / Balance:", "शुद्ध बचत / अधिशेष (Net Surplus):"),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Text(
                        text = CurrencyUtils.formatRupee(kpi.totalReceivedAmount - kpi.totalGivenAmount),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = NevtaPrimary)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        when (selectedReportType) {
            "By City" -> {
                Text(
                    text = t("Nevta Collection by City", "शहर अनुसार नेवता संग्रह (City Breakdown)"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))
                cityBreakdown.forEach { (city, amount) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(NevtaSky),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.LocationCity, contentDescription = null, tint = NevtaBlue, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = city, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Text(
                                text = CurrencyUtils.formatRupee(amount),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NevtaPrimary)
                            )
                        }
                    }
                }
            }
            "Top" -> {
                Text(
                    text = t("Top 5 Nevta Contributors", "शीर्ष 5 नेवता प्रविष्टियां (Top 5 Contributors)"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))
                topContributors.forEachIndexed { index, item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (index == 0) NevtaGold else NevtaPeach),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (index == 0) Color.White else NevtaDeepOrange
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = item.person.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    Text(text = item.person.city, style = MaterialTheme.typography.bodySmall, color = NevtaMuted)
                                }
                            }
                            Text(
                                text = CurrencyUtils.formatRupee(item.entry.amount),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = NevtaSuccess)
                            )
                        }
                    }
                }
            }
            "Gifts" -> {
                Text(
                    text = t("Gifts Received & Given", "प्राप्त एवं दिए गए उपहार (Gifts Summary)"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))
                val giftsList = allEntries.filter { it.entry.gift.isNotBlank() }
                if (giftsList.isEmpty()) {
                    Text(t("No gifts recorded.", "कोई उपहार रिकॉर्ड नहीं है।"), style = MaterialTheme.typography.bodySmall, color = NevtaMuted)
                } else {
                    giftsList.forEach { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(NevtaMint),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Outlined.CardGiftcard, contentDescription = null, tint = NevtaSuccess, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = item.entry.gift, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                        Text(text = "${item.person.name} (${item.person.city})", style = MaterialTheme.typography.bodySmall, color = NevtaMuted)
                                    }
                                }
                                Text(
                                    text = CurrencyUtils.formatRupee(item.entry.amount),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                // Overall Summary Stats
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NevtaStatCard(
                        title = t("Avg Nevta Amount", "औसत नेवता राशि"),
                        value = CurrencyUtils.formatRupee(kpi.averageEntryAmount),
                        subtitle = t("per entry", "प्रति प्रविष्टि"),
                        accentColor = NevtaPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    NevtaStatCard(
                        title = t("Total Events", "कुल कार्यक्रम"),
                        value = "${allEvents.size}",
                        subtitle = t("recorded events", "दर्ज समारोह"),
                        accentColor = NevtaBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Persistent Footer: Net Balance
NevtaSummaryFooter(
    leftText = "${t("Net Family Balance", "शुद्ध पारिवारिक संतुलन")}",
    rightLabel = "${t("Net", "शुद्ध")}: ",
    rightValue = CurrencyUtils.formatRupee(kpi.totalReceivedAmount - kpi.totalGivenAmount),
    rightValueColor = if (kpi.totalReceivedAmount >= kpi.totalGivenAmount) NevtaSuccess else NevtaDanger,
    modifier = Modifier.align(Alignment.BottomCenter)
)
}
}
