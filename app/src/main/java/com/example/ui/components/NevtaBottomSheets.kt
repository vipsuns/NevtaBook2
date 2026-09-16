package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.EventEntity
import com.example.data.local.entity.PersonEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.CurrencyUtils
import com.example.utils.DateUtils
import com.example.utils.t

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryBottomSheet(
    viewModel: NevtaViewModel,
    onDismiss: () -> Unit,
    onEntryAdded: (String) -> Unit
) {
    val events by viewModel.allEvents.collectAsState()
    val persons by viewModel.allPersons.collectAsState()
    val quickSearchResult by viewModel.quickSearchIdResult.collectAsState()
    val quickSearchStatus by viewModel.quickSearchStatus.collectAsState()
    val smartSuggestion by viewModel.currentSmartSuggestion.collectAsState()

    var lookupId by remember { mutableStateOf("") }
    var selectedEventId by remember { mutableStateOf<Long?>(events.firstOrNull()?.id) }
    var personName by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("जयपुर") }
    var village by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("रिश्तेदार") }
    var amountText by remember { mutableStateOf("2100") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var giftText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(DateUtils.todayString()) }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // When quick search result arrives
    LaunchedEffect(quickSearchResult) {
        quickSearchResult?.let { result ->
            personName = result.person.name
            fatherName = result.person.fatherName
            mobile = result.person.mobile
            city = result.person.city
            village = result.person.village
            relationship = result.person.relationship
            amountText = result.entry.amount.toInt().toString()
            paymentMethod = result.entry.paymentMethod
            giftText = result.entry.gift
            if (result.entry.eventId != null) {
                selectedEventId = result.entry.eventId
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = t("Add New Nevta Entry", "नया नेवता जोड़ें"),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = t("Who gave, when and how much", "किससे मिला, कब मिला और कितना मिला"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Search by ID (Section 20)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NevtaSand,
                border = androidx.compose.foundation.BorderStroke(1.dp, NevtaGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = t("Quick Search by ID", "त्वरित आईडी खोज (Quick Search by ID)"),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = NevtaDeepOrange
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = lookupId,
                            onValueChange = { lookupId = it },
                            placeholder = { Text(t("e.g. NB-1042", "उदा. NB-1042"), fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (lookupId.isNotBlank()) {
                                    viewModel.searchByEntryId(lookupId)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NevtaPrimary),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(t("Search", "खोजें"))
                        }
                    }
                    if (quickSearchStatus == "FOUND") {
                        Text(
                            text = t("✓ Details auto-filled", "✓ विवरण भर दिए गए"),
                            style = MaterialTheme.typography.labelSmall.copy(color = NevtaSuccess, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else if (quickSearchStatus == "NOT_FOUND") {
                        Text(
                            text = t("ID not found", "यह ID नहीं मिली"),
                            style = MaterialTheme.typography.labelSmall.copy(color = NevtaDanger, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Event Selector
            Text(t("Select Event", "कार्यक्रम चुनें (Select Event)"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(events) { evt ->
                    NevtaChip(
                        text = evt.title,
                        selected = selectedEventId == evt.id,
                        onClick = { selectedEventId = evt.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Person Name + quick person selector
            Text(t("Person Name *", "व्यक्ति का नाम (Person Name) *"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = personName,
                onValueChange = {
                    personName = it
                    // Check if existing person matches
                    val match = persons.find { p -> p.name.equals(it.trim(), ignoreCase = true) }
                    if (match != null) {
                        fatherName = match.fatherName
                        mobile = match.mobile
                        city = match.city
                        village = match.village
                        viewModel.fetchSmartSuggestion(match.id)
                    }
                },
                placeholder = { Text(t("e.g. Mahesh Agarwal", "जैसे: महेश अग्रवाल")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_person_name_input"),
                shape = RoundedCornerShape(12.dp)
            )

            // Smart Nevta Suggestion (Section 19)
            if (smartSuggestion != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NevtaPeach,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NevtaPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = t("💡 Smart Suggestion", "💡 स्मार्ट सुझाव"),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NevtaDeepOrange
                            )
                            Text(
                                text = smartSuggestion!!.note,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${t("Suggested: ₹", "सुझाव: ₹")}${smartSuggestion!!.suggestedAmount.toInt()}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = NevtaPrimary
                            )
                        }
                        Button(
                            onClick = {
                                amountText = smartSuggestion!!.suggestedAmount.toInt().toString()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NevtaPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(t("Apply Suggestion", "सुझाव अपनाएं"), fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Father Name & Mobile
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("Father / Husband Name", "पिता का नाम"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = fatherName,
                        onValueChange = { fatherName = it },
                        placeholder = { Text(t("Father/Husband", "पिता/पति")) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("Mobile Number", "मोबाइल नंबर"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        placeholder = { Text("98290...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // City / Village & Relationship
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("City / Village", "शहर / गांव"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        placeholder = { Text(t("Jaipur / Sikar", "जयपुर / सीकर")) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("Relationship", "रिश्ता (Relation)"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = relationship,
                        onValueChange = { relationship = it },
                        placeholder = { Text(t("Relative/Friend", "रिश्तेदार/मित्र")) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount + Presets
            Text(t("Nevta Amount *", "नेवता राशि (Amount) *"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                prefix = { Text("₹ ", fontWeight = FontWeight.Bold, color = NevtaSuccess) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("entry_amount_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CurrencyUtils.AMOUNT_PRESETS) { amt ->
                    FilterChip(
                        selected = amountText == amt.toString(),
                        onClick = { amountText = amt.toString() },
                        label = { Text("₹$amt") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NevtaPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment method
            Text(t("Payment Method", "भुगतान माध्यम (Payment Method)"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Cash", "UPI", "Bank", "Other").forEach { method ->
                    NevtaChip(
                        text = method,
                        selected = paymentMethod == method,
                        onClick = { paymentMethod = method }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Gift chips & input
            Text(t("Gift / Presents", "उपहार (Gift / Presents)"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CurrencyUtils.GIFT_PRESETS) { gift ->
                    FilterChip(
                        selected = giftText == gift,
                        onClick = { giftText = if (giftText == gift) "" else gift },
                        label = { Text(gift, fontSize = 12.sp) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = giftText,
                onValueChange = { giftText = it },
                placeholder = { Text(t("Any other gift...", "अन्य कोई उपहार...")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Date & Notes
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("Date (YYYY-MM-DD)", "तारीख (YYYY-MM-DD)"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("Notes", "टिप्पणी (Notes)"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text(t("Came with family", "सपरिवार आए थे")) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = NevtaDanger, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(20.dp))

            val personNameRequiredMsg = t("Please enter person name", "कृपया व्यक्ति का नाम दर्ज करें")
            val amountRequiredMsg = t("Please enter valid amount", "कृपया सही राशि दर्ज करें")

            // Save Button
            NevtaButton(
                text = t("Save Nevta Entry", "नेवता प्रविष्टि सेव करें (Save Entry)"),
                onClick = {
                    if (personName.isBlank()) {
                        errorMessage = personNameRequiredMsg
                        return@NevtaButton
                    }
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        errorMessage = amountRequiredMsg
                        return@NevtaButton
                    }
                    viewModel.addNevtaEntry(
                        personName = personName,
                        fatherName = fatherName,
                        mobile = mobile,
                        city = city,
                        village = village,
                        relationship = relationship,
                        eventId = selectedEventId,
                        amount = amount,
                        paymentMethod = paymentMethod,
                        gift = giftText,
                        date = dateText,
                        notes = notes,
                        onSuccess = { newId ->
                            viewModel.clearQuickSearchResult()
                            viewModel.clearSmartSuggestion()
                            onEntryAdded(newId)
                            onDismiss()
                        }
                    )
                },
                testTag = "save_entry_button"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGivenBottomSheet(
    viewModel: NevtaViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val events by viewModel.allEvents.collectAsState()
    var personName by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("जयपुर") }
    var relationship by remember { mutableStateOf("रिश्तेदार") }
    var customEventName by remember { mutableStateOf("शादी — ") }
    var selectedEventId by remember { mutableStateOf<Long?>(null) }
    var amountText by remember { mutableStateOf("2100") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var giftText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(DateUtils.todayString()) }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = t("Record Given Nevta", "नया दिया हुआ नेवता जोड़ें"),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = t("Track Nevta given at ceremonies", "किसके कार्यक्रम में कितना नेवता दिया"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(t("Person / Family Name *", "व्यक्ति / परिवार का नाम *"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = personName,
                onValueChange = { personName = it },
                placeholder = { Text(t("e.g. Suresh Sharma", "जैसे: सुरेश शर्मा")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("City", "शहर"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        placeholder = { Text(t("Sikar / Ajmer", "सीकर / अजमेर")) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("Mobile", "मोबाइल"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        placeholder = { Text("98290...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(t("Event Name *", "कार्यक्रम का नाम (Event Name) *"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = customEventName,
                onValueChange = { customEventName = it },
                placeholder = { Text(t("e.g. Wedding — Suresh ji's daughter", "उदा. शादी — सुरेश जी की पुत्री")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Event type presets
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("शादी", "मुंडन", "गृह प्रवेश", "सगाई", "सालगिरह", "जन्मदिन")) { type ->
                    AssistChip(
                        onClick = { customEventName = "$type — $personName" },
                        label = { Text(type, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount
            Text(t("Amount Given *", "दी गई राशि (Amount) *"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                prefix = { Text("₹ ", fontWeight = FontWeight.Bold, color = NevtaDeepOrange) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CurrencyUtils.AMOUNT_PRESETS) { amt ->
                    FilterChip(
                        selected = amountText == amt.toString(),
                        onClick = { amountText = amt.toString() },
                        label = { Text("₹$amt") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NevtaDeepOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Payment Method
            Text(t("Payment Method", "भुगतान माध्यम (Payment Method)"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Cash", "UPI", "Bank", "Other").forEach { method ->
                    NevtaChip(
                        text = method,
                        selected = paymentMethod == method,
                        onClick = { paymentMethod = method }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(t("Gift / Presents", "उपहार (Gift / Presents)"), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = giftText,
                onValueChange = { giftText = it },
                placeholder = { Text(t("Silver coin / Utensil / Sweets", "चांदी का सिक्का / बर्तन / मिठाई")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("Date", "तारीख"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("Notes", "टिप्पणी"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text(t("Best wishes", "शुभकामनाएं")) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = NevtaDanger, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(20.dp))

            val givenNameRequiredMsg = t("Please enter person name", "कृपया व्यक्ति का नाम दर्ज करें")
            val givenAmountRequiredMsg = t("Please enter valid amount", "कृपया सही राशि दर्ज करें")

            NevtaButton(
                text = t("Save Given Nevta", "दिया हुआ नेवता सेव करें (Save Given)"),
                onClick = {
                    if (personName.isBlank()) {
                        errorMessage = givenNameRequiredMsg
                        return@NevtaButton
                    }
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        errorMessage = givenAmountRequiredMsg
                        return@NevtaButton
                    }
                    viewModel.addGivenNevta(
                        personName = personName,
                        fatherName = fatherName,
                        mobile = mobile,
                        city = city,
                        relationship = relationship,
                        eventId = selectedEventId,
                        customEventName = customEventName,
                        amount = amount,
                        paymentMethod = paymentMethod,
                        gift = giftText,
                        date = dateText,
                        notes = notes,
                        onSuccess = {
                            onSuccess()
                            onDismiss()
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventBottomSheet(
    viewModel: NevtaViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Type, 2: Details, 3: Guest & Nevta
    var selectedType by remember { mutableStateOf("शादी / Wedding") }
    var eventName by remember { mutableStateOf("") }
    var personName by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(DateUtils.todayString()) }
    var timeText by remember { mutableStateOf("07:00 PM") }
    var venue by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("जयपुर") }
    var address by remember { mutableStateOf("") }
    var guestCountText by remember { mutableStateOf("250") }
    var plannedAmountText by remember { mutableStateOf("150000") }
    var budgetText by remember { mutableStateOf("500000") }
    var notes by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = t("Add New Event", "नया कार्यक्रम जोड़ें (Add Event)"),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${t("Step", "चरण")} $step of 3",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = NevtaPrimary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (step) {
                1 -> {
                    Text(t("Select Event Type", "कार्यक्रम का प्रकार चुनें (Select Event Type)"), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CurrencyUtils.EVENT_TYPES.forEach { evtType ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        selectedType = evtType
                                        eventName = evtType.split("/").first().trim()
                                    },
                                shape = RoundedCornerShape(14.dp),
                                color = if (selectedType == evtType) NevtaPeach else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (selectedType == evtType) NevtaPrimary else MaterialTheme.colorScheme.outline
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = evtType,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = if (selectedType == evtType) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (selectedType == evtType) NevtaDeepOrange else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (selectedType == evtType) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NevtaPrimary)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    NevtaButton(text = t("Next", "आगे बढ़ें (Next)"), onClick = { step = 2 })
                }
                2 -> {
                    Text(t("Event Details", "कार्यक्रम विवरण (Event Details)"), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(t("Event Title *", "शीर्षक (Event Title) *"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        placeholder = { Text(t("e.g. Wedding — Aarav Sharma", "जैसे: शादी — आरव शर्मा")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(t("Host / Honoree Name", "किसका कार्यक्रम है (Host/Person Name)"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = personName,
                        onValueChange = { personName = it },
                        placeholder = { Text(t("Aarav & Priya", "आरव & प्रिया")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(t("Date", "दिनांक (Date)"), style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(value = dateText, onValueChange = { dateText = it }, shape = RoundedCornerShape(12.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(t("Time", "समय (Time)"), style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(value = timeText, onValueChange = { timeText = it }, shape = RoundedCornerShape(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(t("Venue", "स्थान / वेन्यू (Venue)"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        placeholder = { Text(t("Royal Palace Resort", "रॉयल पैलेस रिसॉर्ट")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(t("City", "शहर (City)"), style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(value = city, onValueChange = { city = it }, shape = RoundedCornerShape(12.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(t("Address", "पता (Address)"), style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(value = address, onValueChange = { address = it }, placeholder = { Text(t("Ajmer Road", "अजमेर रोड")) }, shape = RoundedCornerShape(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NevtaOutlinedButton(text = t("Back", "वापस"), onClick = { step = 1 }, modifier = Modifier.weight(1f))
                        NevtaButton(text = t("Next", "आगे बढ़ें"), onClick = { step = 3 }, modifier = Modifier.weight(1f))
                    }
                }
                3 -> {
                    Text(t("Guest & Budget Planning", "मेहमान एवं नेवता योजना (Guest & Budget Planning)"), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(t("Expected Guests", "अनुमानित मेहमान संख्या (Expected Guests)"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = guestCountText,
                        onValueChange = { guestCountText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(t("Expected Nevta Collection", "अनुमानित प्राप्त नेवता राशि (Planned Nevta Amount)"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = plannedAmountText,
                        onValueChange = { plannedAmountText = it },
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold, color = NevtaSuccess) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(t("Event Budget", "कार्यक्रम बजट (Event Budget)"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = budgetText,
                        onValueChange = { budgetText = it },
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold, color = NevtaPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(t("Additional Notes", "अतिरिक्त विवरण (Notes)"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text(t("Catering, decoration etc.", "कैटरिंग, डेकोरेशन आदि")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NevtaOutlinedButton(text = t("Back", "वापस"), onClick = { step = 2 }, modifier = Modifier.weight(1f))
                        NevtaButton(
                            text = t("Save Event", "कार्यक्रम सेव करें"),
                            onClick = {
                                viewModel.addEvent(
                                    title = if (eventName.isNotBlank()) eventName else selectedType,
                                    type = selectedType,
                                    personName = personName,
                                    date = dateText,
                                    time = timeText,
                                    venue = venue,
                                    city = city,
                                    address = address,
                                    guestCount = guestCountText.toIntOrNull() ?: 0,
                                    plannedAmount = plannedAmountText.toDoubleOrNull() ?: 0.0,
                                    budget = budgetText.toDoubleOrNull() ?: 0.0,
                                    notes = notes,
                                    onSuccess = {
                                        onSuccess()
                                        onDismiss()
                                    }
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
