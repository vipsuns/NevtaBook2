package com.example.ui.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.EventEntity
import com.example.ui.components.*
import com.example.ui.theme.NevtaPrimary
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.t

@Composable
fun EventsScreen(
    viewModel: NevtaViewModel,
    onOpenAddEvent: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val events by viewModel.filteredEvents.collectAsState()
    val currentFilter by viewModel.eventsFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var eventToDelete by remember { mutableStateOf<EventEntity?>(null) }

    val filterOptions = listOf(
        "All", "Upcoming", "Today", "Completed",
        "शादी", "मुंडन", "गृह प्रवेश", "जन्मदिन"
    )

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
                            text = t("Events & Celebrations", "मांगलिक कार्यक्रम"),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                letterSpacing = (-0.3).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = t("All family and community celebrations", "सभी पारिवारिक एवं मांगलिक उत्सव"),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onOpenAddEvent,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NevtaPrimary,
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("add_event_header_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("New Event", "नया उत्सव"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                NevtaSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    placeholderText = t("Search event, name, or city...", "कार्यक्रम, नाम, या शहर खोजें...")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterOptions) { opt ->
                        val optLabel = when(opt) {
                            "All" -> t("All", "सभी")
                            "Upcoming" -> t("Upcoming", "आगामी")
                            "Today" -> t("Today", "आज")
                            "Completed" -> t("Completed", "पूर्ण")
                            "शादी" -> t("Wedding", "शादी")
                            "मुंडन" -> t("Mundan", "मुंडन")
                            "गृह प्रवेश" -> t("Griha Pravesh", "गृह प्रवेश")
                            "जन्मदिन" -> t("Birthday", "जन्मदिन")
                            else -> opt
                        }
                        NevtaChip(
                            text = optLabel,
                            selected = currentFilter == opt,
                            onClick = { viewModel.setEventsFilter(opt) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (events.isEmpty()) {
                    NevtaEmptyState(
                        title = t("No events found", "कोई कार्यक्रम नहीं मिला"),
                        subtitle = t("Tap below to schedule a new family event", "नया कार्यक्रम दर्ज करने के लिए नीचे बटन दबाएं"),
                        buttonText = t("Add Event", "कार्यक्रम जोड़ें"),
                        onButtonClick = onOpenAddEvent,
                        icon = Icons.Outlined.Celebration
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 2.dp, bottom = 16.dp)
                    ) {
                        items(events, key = { it.id }) { event ->
                            NevtaEventCard(
                                event = event,
                                onClick = {},
                                onDelete = { eventToDelete = event }
                            )
                        }
                    }
                }
            }
        }

        // Persistent Footer: "Total X events" | "Upcoming: Y"
        NevtaSummaryFooter(
            leftText = "${t("Total", "कुल")} ${events.size} ${t("events listed", "कार्यक्रम सूची")}",
            rightLabel = "${t("Upcoming", "आगामी")}: ",
            rightValue = "${events.count { it.status == "Upcoming" }} ${t("active", "सक्रिय")}",
            rightValueColor = NevtaPrimary,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (eventToDelete != null) {
        DeleteConfirmationDialog(
            title = t("Delete Event?", "कार्यक्रम हटाएं?"),
            message = t("Are you sure you want to delete '${eventToDelete!!.title}'?", "क्या आप '${eventToDelete!!.title}' कार्यक्रम हटाना चाहते हैं?"),
            onConfirm = { viewModel.deleteEvent(eventToDelete!!) },
            onDismiss = { eventToDelete = null }
        )
    }
}
