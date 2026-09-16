package com.example.ui.screens.entries

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EntryWithPersonAndEvent
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.CurrencyUtils
import com.example.utils.DateUtils
import com.example.utils.IntentUtils
import com.example.utils.t

@Composable
fun EntryDetailScreen(
    entryId: String,
    viewModel: NevtaViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val allEntries by viewModel.allEntries.collectAsState()
    val entryItem = allEntries.find { it.entry.entryId.equals(entryId, ignoreCase = true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (entryItem == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(t("Entry not found", "प्रविष्टि नहीं मिली (Entry not found)"))
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onBack) { Text(t("Go Back", "वापस जाएं")) }
        }
        return
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
                            text = t("Nevta Slip Details", "नेवता रसीद विवरण"),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                letterSpacing = (-0.3).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${entryItem.entry.entryId} · ${entryItem.person.name}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NevtaDanger.copy(alpha = 0.12f))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NevtaDanger, modifier = Modifier.size(18.dp))
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

                // Receipt Card (Section 21)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header of Slip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NevtaSand,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NevtaGold.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = entryItem.entry.entryId,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = NevtaDeepOrange,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = DateUtils.formatDisplayDate(entryItem.entry.date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = NevtaMuted
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Amount
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(NevtaMint)
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = t("Received Nevta Amount", "प्राप्त नेवता राशि"),
                            style = MaterialTheme.typography.labelSmall,
                            color = NevtaSuccess
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyUtils.formatRupee(entryItem.entry.amount),
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = NevtaSuccess
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White
                        ) {
                            Text(
                                text = "${t("Payment:", "भुगतान:")} ${entryItem.entry.paymentMethod}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NevtaDeepOrange,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (entryItem.entry.gift.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NevtaPeach,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.CardGiftcard, contentDescription = null, tint = NevtaDeepOrange)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(t("Accompanying Gift:", "साथ में भेंट / उपहार:"), style = MaterialTheme.typography.labelSmall, color = NevtaMuted)
                                Text(entryItem.entry.gift, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Person Info
                Text(t("Person Details", "व्यक्ति का विवरण (Person Info)"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    NevtaPersonAvatar(name = entryItem.person.name, size = 52.dp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = entryItem.person.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        if (entryItem.person.fatherName.isNotBlank()) {
                            Text(
                                text = "${t("Father / Guardian:", "पिता:")} ${entryItem.person.fatherName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NevtaMuted
                            )
                        }
                        val loc = listOf(entryItem.person.village, entryItem.person.city).filter { it.isNotBlank() }.joinToString(", ")
                        if (loc.isNotBlank()) {
                            Text(text = "${t("Location:", "स्थान:")} $loc", style = MaterialTheme.typography.bodySmall, color = NevtaMuted)
                        }
                        if (entryItem.person.mobile.isNotBlank()) {
                            Text(text = "${t("Mobile:", "मोबाइल:")} ${entryItem.person.mobile}", style = MaterialTheme.typography.bodySmall, color = NevtaPrimary)
                        }
                    }
                }

                if (entryItem.event != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(t("Event Details", "कार्यक्रम विवरण (Event Info)"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = entryItem.event.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = "${t("Venue:", "स्थान:")} ${entryItem.event.venue}, ${entryItem.event.city}", style = MaterialTheme.typography.bodySmall, color = NevtaMuted)
                    Text(text = "${t("Date:", "तारीख:")} ${DateUtils.formatDisplayDate(entryItem.event.date)}", style = MaterialTheme.typography.bodySmall, color = NevtaMuted)
                }

                if (entryItem.entry.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(t("Notes:", "टिप्पणी (Notes):"), style = MaterialTheme.typography.labelSmall, color = NevtaMuted)
                    Text(entryItem.entry.notes, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons: Call, WhatsApp, Share Slip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (entryItem.person.mobile.isNotBlank()) {
                Button(
                    onClick = { IntentUtils.dialPhoneNumber(context, entryItem.person.mobile) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = NevtaSuccess),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(t("Call", "Call"))
                }

                Button(
                    onClick = {
                        val message = "नमस्ते ${entryItem.person.name} जी, NevtaBook में आपका नेवता रिकॉर्ड दर्ज है:\nआईडी: ${entryItem.entry.entryId}\nराशि: ${CurrencyUtils.formatRupee(entryItem.entry.amount)}\nकार्यक्रम: ${entryItem.event?.title ?: "समारोह"}\nतारीख: ${DateUtils.formatDisplayDate(entryItem.entry.date)}\nहार्दिक धन्यवाद!"
                        IntentUtils.openWhatsApp(context, entryItem.person.mobile, message)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(t("WhatsApp", "WhatsApp"))
                }
            }

            Button(
                onClick = {
                    val shareText = "--- NevtaBook Slip ---\nID: ${entryItem.entry.entryId}\nName: ${entryItem.person.name}\nAmount: ${CurrencyUtils.formatRupee(entryItem.entry.amount)}\nDate: ${DateUtils.formatDisplayDate(entryItem.entry.date)}\nEvent: ${entryItem.event?.title ?: "Event"}\nPayment: ${entryItem.entry.paymentMethod}\nनेवता का हिसाब, अब मोबाइल पर।"
                    IntentUtils.shareText(context, "NevtaBook Entry ${entryItem.entry.entryId}", shareText)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = NevtaPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(t("Share Slip", "रसीद साझा करें"))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// Persistent Footer: Amount and Status
NevtaSummaryFooter(
    leftText = "${t("Slip ID", "रसीद क्रमांक")}: ${entryItem.entry.entryId}",
    rightLabel = "${t("Amount", "राशि")}: ",
    rightValue = CurrencyUtils.formatRupee(entryItem.entry.amount),
    rightValueColor = NevtaPrimary,
    modifier = Modifier.align(Alignment.BottomCenter)
)
}

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            title = t("Delete Entry?", "प्रविष्टि हटाएं?"),
            message = t("Are you sure you want to delete entry ${entryItem.entry.entryId}?", "क्या आप ${entryItem.entry.entryId} का रिकॉर्ड हटाना चाहते हैं?"),
            onConfirm = {
                viewModel.deleteEntry(entryItem.entry)
                onBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}
