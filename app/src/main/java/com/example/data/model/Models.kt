package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.local.entity.EventEntity
import com.example.data.local.entity.NevtaEntryEntity
import com.example.data.local.entity.NevtaGivenEntity
import com.example.data.local.entity.PersonEntity

data class EntryWithPersonAndEvent(
    @Embedded val entry: NevtaEntryEntity,
    @Relation(
        parentColumn = "personId",
        entityColumn = "id"
    )
    val person: PersonEntity,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "id"
    )
    val event: EventEntity?
)

data class GivenWithPersonAndEvent(
    @Embedded val given: NevtaGivenEntity,
    @Relation(
        parentColumn = "personId",
        entityColumn = "id"
    )
    val person: PersonEntity,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "id"
    )
    val event: EventEntity?
)

data class PersonBalanceSummary(
    val person: PersonEntity,
    val totalReceived: Double,
    val totalGiven: Double,
    val netBalance: Double, // received - given
    val returnStatus: ReturnStatus
)

enum class ReturnStatus {
    SETTLED,  // Received == Given or 0 (बराबर)
    PENDING,  // Received > Given (वापसी बाकी)
    ADVANCE   // Given > Received (अतिरिक्त दिया)
}

data class DashboardKpi(
    val totalReceivedAmount: Double = 0.0,
    val totalGivenAmount: Double = 0.0,
    val totalReceivedCount: Int = 0,
    val totalGivenCount: Int = 0,
    val upcomingEventsCount: Int = 0,
    val pendingReturnCount: Int = 0,
    val totalGiftsCount: Int = 0,
    val avgGivenAmount: Double = 0.0,
    val averageEntryAmount: Double = 0.0
)

data class SmartSuggestion(
    val personName: String,
    val lastGivenAmount: Double,
    val suggestedAmount: Double,
    val note: String
)
