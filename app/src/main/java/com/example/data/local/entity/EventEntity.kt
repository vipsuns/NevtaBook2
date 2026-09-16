package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "events",
    indices = [
        Index(value = ["date"]),
        Index(value = ["status"]),
        Index(value = ["type"])
    ]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: String, // Wedding, Mundan, Griha Pravesh, Tilak, Sagai, Birthday, Anniversary, Other
    val personName: String = "",
    val date: String, // YYYY-MM-DD or readable
    val time: String = "",
    val venue: String = "",
    val city: String = "",
    val address: String = "",
    val guestCount: Int = 0,
    val plannedAmount: Double = 0.0,
    val budget: Double = 0.0,
    val giftExpectation: String = "",
    val contactPerson: String = "",
    val notes: String = "",
    val status: String = "Upcoming", // Upcoming, Today, Completed
    val createdAt: Long = System.currentTimeMillis()
)
