package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "nevta_given",
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
        Index(value = ["personId"]),
        Index(value = ["eventId"]),
        Index(value = ["date"])
    ]
)
data class NevtaGivenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personId: Long,
    val eventId: Long?,
    val eventNameCustom: String = "",
    val amount: Double,
    val paymentMethod: String = "Cash", // Cash, UPI, Bank, Other
    val gift: String = "",
    val date: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
