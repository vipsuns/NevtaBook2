package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "persons",
    indices = [
        Index(value = ["name"]),
        Index(value = ["mobile"]),
        Index(value = ["city"])
    ]
)
data class PersonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val fatherName: String = "",
    val mobile: String = "",
    val city: String = "",
    val village: String = "",
    val relationship: String = "रिश्तेदार", // Relative, Friend, Business, Neighbor, Family, Other
    val isVip: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
