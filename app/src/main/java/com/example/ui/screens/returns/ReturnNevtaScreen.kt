package com.example.ui.screens.returns

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CompareArrows
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
import com.example.data.model.PersonBalanceSummary
import com.example.data.model.ReturnStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.CurrencyUtils
import com.example.utils.IntentUtils
import com.example.utils.t

@Composable
fun ReturnNevtaScreen(
    viewModel: NevtaViewModel,
    onOpenAddGiven: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val personBalances by viewModel.allPersonBalances.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") } // All, Pending, Settled, Advance
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = personBalances.filter { item ->
        val matchesQuery = searchQuery.isBlank() ||
                item.person.name.contains(searchQuery, true) ||
                item.person.city.contains(searchQuery, true) ||
                item.person.mobile.contains(searchQuery)

        val matchesFilter = when (selectedFilter) {
            "Pending" -> item.returnStatus == ReturnStatus.PENDING
            "Settled" -> item.returnStatus == ReturnStatus.SETTLED
            "Advance" -> item.returnStatus == ReturnStatus.ADVANCE
            else -> true
        }
        matchesQuery && matchesFilter
    }

    val totalPendingCount = personBalances.count { it.returnStatus == ReturnStatus.PENDING }
    val totalSettledCount = personBalances.count { it.returnStatus == ReturnStatus.SETTLED }

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
                        text = t("Return Ledger", "नेवता वापसी खाता"),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            letterSpacing = (-0.3).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = t("Individual reciprocity & family balance tracking", "व्यक्तिगत लेन-देन व बाकी नेवता संतुलन"),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

        // Summary Bar
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(t("Pending Returns", "वापसी बाकी (Pending)"), style = MaterialTheme.typography.labelSmall, color = NevtaMuted)
                    Text("$totalPendingCount ${t("families", "परिवार")}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NevtaDeepOrange))
                }
                Box(modifier = Modifier.height(28.dp).width(1.dp).background(MaterialTheme.colorScheme.outline))
                Column {
                    Text(t("Settled Returns", "बराबर (Settled)"), style = MaterialTheme.typography.labelSmall, color = NevtaMuted)
                    Text("$totalSettledCount ${t("families", "परिवार")}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NevtaSuccess))
                }
                Box(modifier = Modifier.height(28.dp).width(1.dp).background(MaterialTheme.colorScheme.outline))
                Column {
                    Text(t("Total Relatives", "कुल रिश्तेदार"), style = MaterialTheme.typography.labelSmall, color = NevtaMuted)
                    Text("${personBalances.size} ${t("families", "परिवार")}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        NevtaSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholderText = t("Search person or city...", "व्यक्ति या शहर खोजें...")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("All", "Pending", "Settled", "Advance")) { f ->
                NevtaChip(
                    text = when (f) {
                        "Pending" -> t("Pending Return", "वापसी बाकी")
                        "Settled" -> t("Settled", "बराबर")
                        "Advance" -> t("Advance Given", "अतिरिक्त दिया")
                        else -> t("All", "सभी")
                    },
                    selected = selectedFilter == f,
                    onClick = { selectedFilter = f }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredList.isEmpty()) {
            NevtaEmptyState(
                title = t("No records found", "कोई रिकॉर्ड नहीं मिला"),
                subtitle = t("No family member found in this category", "इस श्रेणी में कोई व्यक्ति नहीं है"),
                icon = Icons.Outlined.CompareArrows
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 2.dp, bottom = 16.dp)
            ) {
                items(filteredList, key = { it.person.id }) { item ->
                    PersonBalanceCard(
                        item = item,
                        onRecordReturn = onOpenAddGiven,
                        onCall = { IntentUtils.dialPhoneNumber(context, item.person.mobile) },
                        onWhatsApp = {
                            val msg = "नमस्ते ${item.person.name} जी, NevtaBook पारिवारिक खाते के अनुसार आपका नेवता विवरण:\nप्राप्त: ${CurrencyUtils.formatRupee(item.totalReceived)}\nदिया: ${CurrencyUtils.formatRupee(item.totalGiven)}\nसादर!"
                            IntentUtils.openWhatsApp(context, item.person.mobile, msg)
                        }
                    )
                }
            }
        }
    }
}

// Persistent Footer: "Showing X of Y contacts" | "Pending: Z"
NevtaSummaryFooter(
    leftText = "${t("Showing", "दिखा रहे हैं")} ${filteredList.size} ${t("of", "में से")} ${personBalances.size} ${t("contacts", "संपर्क")}",
    rightLabel = "${t("Pending Returns", "बाकी वापसी")}: ",
    rightValue = "$totalPendingCount",
    rightValueColor = NevtaDeepOrange,
    modifier = Modifier.align(Alignment.BottomCenter)
)
}
}

@Composable
private fun PersonBalanceCard(
    item: PersonBalanceSummary,
    onRecordReturn: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    val statusColor = when (item.returnStatus) {
        ReturnStatus.PENDING -> NevtaDeepOrange
        ReturnStatus.SETTLED -> NevtaSuccess
        ReturnStatus.ADVANCE -> NevtaBlue
    }

    val statusText = when (item.returnStatus) {
        ReturnStatus.PENDING -> t("Pending Return", "वापसी बाकी")
        ReturnStatus.SETTLED -> t("Settled", "बराबर")
        ReturnStatus.ADVANCE -> t("Advance Given", "अतिरिक्त दिया")
    }

    val statusBg = when (item.returnStatus) {
        ReturnStatus.PENDING -> NevtaPeach
        ReturnStatus.SETTLED -> NevtaMint
        ReturnStatus.ADVANCE -> NevtaSky
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NevtaPersonAvatar(name = item.person.name, size = 44.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.person.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val sub = listOf(item.person.relationship.split("/").first().trim(), item.person.city)
                            .filter { it.isNotBlank() }
                            .joinToString(" · ")
                        Text(
                            text = sub,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Received vs Given Stats
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(t("Received", "कुल प्राप्त"), style = MaterialTheme.typography.labelSmall, color = NevtaMuted)
                        Text(
                            CurrencyUtils.formatRupee(item.totalReceived),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NevtaSuccess)
                        )
                    }
                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(MaterialTheme.colorScheme.outline))
                    Column {
                        Text(t("Given", "कुल दिया"), style = MaterialTheme.typography.labelSmall, color = NevtaMuted)
                        Text(
                            CurrencyUtils.formatRupee(item.totalGiven),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NevtaDeepOrange)
                        )
                    }
                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(MaterialTheme.colorScheme.outline))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(t("Net Diff", "शुद्ध अंतर"), style = MaterialTheme.typography.labelSmall, color = NevtaMuted)
                        val net = item.netBalance
                        Text(
                            text = (if (net > 0) "+" else "") + CurrencyUtils.formatRupee(net),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, color = statusColor)
                        )
                    }
                }
            }

            // Return Recommendation (Section 24)
            if (item.returnStatus == ReturnStatus.PENDING) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NevtaSand
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = t(
                                "💡 Suggestion: Giving ₹${(item.totalReceived + 500).toInt()} at their next event is recommended.",
                                "💡 सुझाव: अगले समारोह में ₹${(item.totalReceived + 500).toInt()} का नेवता देना अनुशंसित है।"
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = NevtaDeepOrange
                        )
                    }
                }
            }

            // Action Buttons: Call, WhatsApp, Record Return
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (item.person.mobile.isNotBlank()) {
                        IconButton(
                            onClick = onCall,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(NevtaMint)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = NevtaSuccess, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = onWhatsApp,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE7F8E8))
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Button(
                    onClick = onRecordReturn,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NevtaPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(t("Record Return", "नेवता वापसी दर्ज करें"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
