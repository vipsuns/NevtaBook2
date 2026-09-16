package com.example.ui.screens.family

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FamilyMemberEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NevtaViewModel
import com.example.utils.IntentUtils
import com.example.utils.t

@Composable
fun FamilyScreen(
    viewModel: NevtaViewModel,
    onOpenAddMember: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val familyMembers by viewModel.allFamilyMembers.collectAsState()
    var memberToDelete by remember { mutableStateOf<FamilyMemberEntity?>(null) }

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
                            text = t("Family Members", "परिवार के सदस्य"),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                letterSpacing = (-0.3).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = t("Manage Nevta records together as a family", "परिवार के साथ मिलकर खाता प्रबंधित करें"),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onOpenAddMember,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NevtaPrimary,
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("add_family_member_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("Add Member", "सदस्य जोड़ें"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Info Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NevtaSand,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NevtaGold.copy(alpha = 0.35f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Group, contentDescription = null, tint = NevtaDeepOrange)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = t(
                                "All family members can view and enter Nevta records from their phones.",
                                "परिवार के सभी सदस्य अपने-अपने फोन से नेवता रिकॉर्ड देख और दर्ज कर सकते हैं।"
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (familyMembers.isEmpty()) {
                    NevtaEmptyState(
                        title = t("No family members added", "कोई परिवार सदस्य नहीं जोड़ा गया"),
                        subtitle = t("Add family members to manage your Nevta accounts collectively", "परिवार के अन्य सदस्यों को जोड़ने के लिए नीचे बटन दबाएं"),
                        buttonText = t("Add Member", "सदस्य जोड़ें"),
                        onButtonClick = onOpenAddMember,
                        icon = Icons.Outlined.Group
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 2.dp, bottom = 16.dp)
                    ) {
                        items(familyMembers, key = { it.id }) { member ->
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
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        NevtaPersonAvatar(name = member.name, size = 44.dp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = member.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (member.role == "Admin") NevtaPeach else NevtaSky
                                                ) {
                                                    Text(
                                                        text = member.role,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                                        color = if (member.role == "Admin") NevtaDeepOrange else NevtaBlue,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(text = member.relation, style = MaterialTheme.typography.bodySmall, color = NevtaMuted)
                                            if (member.mobile.isNotBlank()) {
                                                Text(text = member.mobile, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = NevtaPrimary)
                                            }
                                        }
                                    }

                                    Row {
                                        if (member.mobile.isNotBlank()) {
                                            IconButton(onClick = { IntentUtils.dialPhoneNumber(context, member.mobile) }) {
                                                Icon(Icons.Default.Call, contentDescription = "Call", tint = NevtaSuccess, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                        IconButton(onClick = { memberToDelete = member }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NevtaMuted, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Persistent Footer: "Total Family Members: X" | "Admins: Y"
        NevtaSummaryFooter(
            leftText = "${t("Total Family Members", "कुल परिवार सदस्य")}: ${familyMembers.size}",
            rightLabel = "${t("Admins", "व्यवस्थापक")}: ",
            rightValue = "${familyMembers.count { it.role == "Admin" }}",
            rightValueColor = NevtaPrimary,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (memberToDelete != null) {
        DeleteConfirmationDialog(
            title = t("Remove Member?", "सदस्य हटाएं?"),
            message = t("Are you sure you want to remove ${memberToDelete!!.name} from family members?", "क्या आप ${memberToDelete!!.name} को परिवार सूची से हटाना चाहते हैं?"),
            onConfirm = { viewModel.deleteFamilyMember(memberToDelete!!) },
            onDismiss = { memberToDelete = null }
        )
    }
}
