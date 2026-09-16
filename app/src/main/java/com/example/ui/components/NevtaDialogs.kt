package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.DateUtils
import com.example.utils.t

@Composable
fun DeleteConfirmationDialog(
    title: String = t("Delete Record?", "रिकॉर्ड हटाएं?"),
    message: String = t("Are you sure you want to delete this record? This action cannot be undone.", "क्या आप यह रिकॉर्ड हटाना चाहते हैं? यह क्रिया पूर्ववत नहीं की जा सकती।"),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(); onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = NevtaDanger),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(t("Delete", "हटाएं"), color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text(t("Cancel", "रद्द करें"))
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderBottomSheet(
    viewModel: NevtaViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(DateUtils.todayString()) }
    var timeText by remember { mutableStateOf("10:00 AM") }
    var reminderType by remember { mutableStateOf("Upcoming Event") }
    var repeatInterval by remember { mutableStateOf("One-time") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val types = listOf(
        "Upcoming Event" to t("Upcoming Event", "आगामी कार्यक्रम"),
        "Birthday" to t("Birthday", "जन्मदिन"),
        "Anniversary" to t("Anniversary", "सालगिरह"),
        "Follow-up" to t("Follow-up", "फॉलो-अप"),
        "Return Nevta" to t("Return Nevta", "नेवता वापसी"),
        "Custom" to t("Custom", "कस्टम")
    )
    val intervals = listOf(
        "One-time" to t("One-time", "एक बार"),
        "Daily" to t("Daily", "प्रतिदिन"),
        "Weekly" to t("Weekly", "साप्ताहिक"),
        "Monthly" to t("Monthly", "मासिक"),
        "Yearly" to t("Yearly", "वार्षिक")
    )

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
                        text = t("Add Reminder", "नया रिमाइंडर जोड़ें"),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = t("Alert for event or Nevta return", "कार्यक्रम या नेवता वापसी का स्मरण"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(t("Title *", "शीर्षक *"), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text(t("e.g. Return Nevta to Mahesh ji", "उदा. महेश जी को नेवता वापसी")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("Date", "तारीख"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = dateText, onValueChange = { dateText = it }, shape = RoundedCornerShape(12.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(t("Time", "समय"), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = timeText, onValueChange = { timeText = it }, shape = RoundedCornerShape(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(t("Reminder Type", "रिमाइंडर प्रकार"), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                types.take(3).forEach { (k, label) ->
                    NevtaChip(text = label, selected = reminderType == k, onClick = { reminderType = k })
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                types.drop(3).forEach { (k, label) ->
                    NevtaChip(text = label, selected = reminderType == k, onClick = { reminderType = k })
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(t("Repeat", "पुनरावृत्ति"), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                intervals.forEach { (k, label) ->
                    NevtaChip(text = label, selected = repeatInterval == k, onClick = { repeatInterval = k })
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(t("Notes", "टिप्पणी"), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text(t("Additional instructions", "अतिरिक्त निर्देश")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(error!!, color = NevtaDanger, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(20.dp))

            val titleRequiredMsg = t("Please enter a title", "कृपया शीर्षक दर्ज करें")
            NevtaButton(
                text = t("Set Reminder", "रिमाइंडर सेट करें"),
                onClick = {
                    if (title.isBlank()) {
                        error = titleRequiredMsg
                        return@NevtaButton
                    }
                    viewModel.addReminder(
                        title = title,
                        date = dateText,
                        time = timeText,
                        type = reminderType,
                        repeatInterval = repeatInterval,
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
fun AddFamilyBottomSheet(
    viewModel: NevtaViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("Son") }
    var mobile by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Editor") }
    var error by remember { mutableStateOf<String?>(null) }

    val roles = listOf("Admin", "Editor", "Viewer")

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
                        text = t("Add Family Member", "परिवार का सदस्य जोड़ें"),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = t("Manage Nevta records together as a family", "परिवार के साथ मिलकर नेवता खाता प्रबंधित करें"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(t("Member Name *", "सदस्य का नाम *"), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(t("e.g. Sunita Sharma", "उदा. सुनीता शर्मा")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(t("Relation", "रिश्ता"), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = relation,
                onValueChange = { relation = it },
                placeholder = { Text(t("Wife / Brother / Son", "पत्नी / भाई / बेटा")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(t("Mobile Number", "मोबाइल नंबर"), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                placeholder = { Text("+91 98290...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(t("Role / Permissions", "अनुमति स्तर (Role / Permissions)"), style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                roles.forEach { r ->
                    NevtaChip(text = r, selected = role == r, onClick = { role = r })
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(error!!, color = NevtaDanger, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(20.dp))

            val nameRequiredMsg = t("Please enter a name", "कृपया नाम दर्ज करें")
            NevtaButton(
                text = t("Save Member", "सदस्य जोड़ें"),
                onClick = {
                    if (name.isBlank()) {
                        error = nameRequiredMsg
                        return@NevtaButton
                    }
                    viewModel.addFamilyMember(
                        name = name,
                        relation = relation,
                        mobile = mobile,
                        role = role,
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
