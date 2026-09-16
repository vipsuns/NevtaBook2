package com.example.ui.screens.given

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.NevtaGivenEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.CurrencyUtils
import com.example.utils.t

@Composable
fun GivenScreen(
    viewModel: NevtaViewModel,
    onOpenAddGiven: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val givenList by viewModel.filteredGiven.collectAsState()
    val allGiven by viewModel.allGiven.collectAsState()
    val currentFilter by viewModel.givenFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var givenToDelete by remember { mutableStateOf<NevtaGivenEntity?>(null) }

    val totalGivenAmount = allGiven.sumOf { it.given.amount }
    val totalGivenCount = allGiven.size
    val giftsCount = allGiven.count { it.given.gift.isNotBlank() }

    val filterChips = listOf("All", "शादी", "मुंडन", "गृह प्रवेश", "सगाई", "सालगिरह")

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
                            text = t("Given Nevta", "दिया हुआ नेवता"),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                letterSpacing = (-0.3).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = t("Gifts & cash presented at others' ceremonies", "समारोहों में दिया गया नेवता"),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onOpenAddGiven,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NevtaDeepOrange,
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("add_given_header_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("Record Given", "नेवता दें"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Summary Card
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
                            Text(t("Total Given Nevta", "कुल दिया नेवता"), style = MaterialTheme.typography.labelSmall, color = NevtaMuted)
                            Text(
                                CurrencyUtils.formatRupee(totalGivenAmount),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = NevtaDeepOrange)
                            )
                        }
                        Box(modifier = Modifier.height(28.dp).width(1.dp).background(MaterialTheme.colorScheme.outline))
                        Column {
                            Text(t("Total Events", "कुल अवसर"), style = MaterialTheme.typography.labelSmall, color = NevtaMuted)
                            Text("$totalGivenCount ${t("events", "कार्यक्रम")}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Box(modifier = Modifier.height(28.dp).width(1.dp).background(MaterialTheme.colorScheme.outline))
                        Column {
                            Text(t("Gifts Given", "दिए गए उपहार"), style = MaterialTheme.typography.labelSmall, color = NevtaMuted)
                            Text("$giftsCount ${t("gifts", "भेंट")}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NevtaSuccess))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                NevtaSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    placeholderText = t("Search name, city, or occasion...", "नाम, शहर, या अवसर खोजें...")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterChips) { filter ->
                        val filterLabel = when (filter) {
                            "All" -> t("All", "सभी")
                            "शादी" -> t("Wedding", "शादी")
                            "मुंडन" -> t("Mundan", "मुंडन")
                            "गृह प्रवेश" -> t("Griha Pravesh", "गृह प्रवेश")
                            "सगाई" -> t("Engagement", "सगाई")
                            "सालगिरह" -> t("Anniversary", "सालगिरह")
                            else -> filter
                        }
                        NevtaChip(
                            text = filterLabel,
                            selected = currentFilter == filter,
                            onClick = { viewModel.setGivenFilter(filter) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // List
                if (givenList.isEmpty()) {
                    NevtaEmptyState(
                        title = t("No given Nevta records found", "कोई दिया हुआ नेवता रिकॉर्ड नहीं है"),
                        subtitle = t("Tap below to record Nevta given at an event", "किसी समारोह में दिया गया नेवता दर्ज करने के लिए नीचे बटन दबाएं"),
                        buttonText = t("Add Given Nevta", "दिया हुआ नेवता जोड़ें"),
                        onButtonClick = onOpenAddGiven,
                        icon = Icons.Outlined.Payments
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 2.dp, bottom = 16.dp)
                    ) {
                        items(givenList, key = { it.given.id }) { item ->
                            NevtaGivenCard(
                                item = item,
                                onViewDetail = {},
                                onDelete = { givenToDelete = item.given }
                            )
                        }
                    }
                }
            }
        }

        // Persistent Footer: "Showing X of Y records" | "Total Given: ₹..."
        NevtaSummaryFooter(
            leftText = "${t("Showing", "दिखा रहे हैं")} ${givenList.size} ${t("of", "में से")} ${allGiven.size} ${t("records", "रिकॉर्ड")}",
            rightLabel = "${t("Total Given", "कुल दिया")}: ",
            rightValue = CurrencyUtils.formatRupee(givenList.sumOf { it.given.amount }),
            rightValueColor = NevtaDeepOrange,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (givenToDelete != null) {
        DeleteConfirmationDialog(
            title = t("Delete Record?", "रिकॉर्ड हटाएं?"),
            message = t("Are you sure you want to delete this given Nevta record?", "क्या आप यह दिया हुआ नेवता रिकॉर्ड हटाना चाहते हैं?"),
            onConfirm = { viewModel.deleteGiven(givenToDelete!!) },
            onDismiss = { givenToDelete = null }
        )
    }
}
