package com.example.ui.screens.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ReminderEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.DateUtils
import com.example.utils.t

@Composable
fun RemindersScreen(
    viewModel: NevtaViewModel,
    onOpenAddReminder: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val reminders by viewModel.allReminders.collectAsState()
    var reminderToDelete by remember { mutableStateOf<ReminderEntity?>(null) }
    var filterTab by remember { mutableStateOf("All") } // All, Active, Completed

    val filteredReminders = when (filterTab) {
        "Active" -> reminders.filter { !it.isCompleted }
        "Completed" -> reminders.filter { it.isCompleted }
        else -> reminders
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
                            text = t("Reminders & Alerts", "रिमाइंडर एवं अलर्ट"),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                letterSpacing = (-0.3).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = t("Timely alerts for events and Nevta returns", "मांगलिक उत्सव व नेवता का स्मरण"),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onOpenAddReminder,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NevtaPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("add_reminder_header_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("New Alert", "नया अलर्ट"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "All" to t("All", "सभी"),
                        "Active" to t("Active", "सक्रिय"),
                        "Completed" to t("Completed", "पूर्ण")
                    ).forEach { (key, label) ->
                        NevtaChip(
                            text = label,
                            selected = filterTab == key,
                            onClick = { filterTab = key }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (filteredReminders.isEmpty()) {
                    NevtaEmptyState(
                        title = t("No reminders set", "कोई रिमाइंडर नहीं है"),
                        subtitle = t("Set a reminder for upcoming events or Nevta return dates", "किसी भी कार्यक्रम या नेवता वापसी का स्मरण सेट करने के लिए नीचे बटन दबाएं"),
                        buttonText = t("Add Reminder", "रिमाइंडर जोड़ें"),
                        onButtonClick = onOpenAddReminder,
                        icon = Icons.Outlined.Alarm
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 2.dp, bottom = 16.dp)
                    ) {
                        items(filteredReminders, key = { it.id }) { reminder ->
                            ReminderCard(
                                reminder = reminder,
                                onToggle = { viewModel.toggleReminderCompleted(reminder) },
                                onDelete = { reminderToDelete = reminder }
                            )
                        }
                    }
                }
            }
        }

        // Persistent Footer: "Total X reminders" | "Active: Y"
        NevtaSummaryFooter(
            leftText = "${t("Total", "कुल")} ${filteredReminders.size} ${t("reminders", "रिमाइंडर")}",
            rightLabel = "${t("Active", "सक्रिय")}: ",
            rightValue = "${reminders.count { !it.isCompleted }}",
            rightValueColor = NevtaPrimary,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (reminderToDelete != null) {
        DeleteConfirmationDialog(
            title = t("Delete Reminder?", "रिमाइंडर हटाएं?"),
            message = t("Are you sure you want to delete '${reminderToDelete!!.title}'?", "क्या आप '${reminderToDelete!!.title}' रिमाइंडर हटाना चाहते हैं?"),
            onConfirm = { viewModel.deleteReminder(reminderToDelete!!) },
            onDismiss = { reminderToDelete = null }
        )
    }
}

@Composable
private fun ReminderCard(
    reminder: ReminderEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = reminder.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = NevtaSuccess)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (reminder.isCompleted) NevtaMuted else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${DateUtils.formatDisplayDate(reminder.date)} · ${reminder.time}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NevtaMuted
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NevtaSand
                    ) {
                        Text(
                            text = reminder.type,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = NevtaDeepOrange,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                if (reminder.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reminder.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NevtaMuted, modifier = Modifier.size(20.dp))
            }
        }
    }
}
