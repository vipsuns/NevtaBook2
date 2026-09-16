package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "nevta_entries",
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["entryId"], unique = true),
        Index(value = ["personId"]),
        Index(value = ["eventId"]),
        Index(value = ["date"]),
        Index(value = ["paymentMethod"])
    ]
)
data class NevtaEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryId: String, // e.g. NB-1042
    val personId: Long,
    val eventId: Long?,
    val amount: Double,
    val paymentMethod: String = "Cash", // Cash, UPI, Bank, Other
    val gift: String = "", // चांदी सिक्का, साड़ी, कपड़े, बर्तन, etc.
    val date: String,
    val time: String = "", // e.g. "11:30 AM"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
