package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: String,
    val time: String,
    val type: String = "Upcoming Event", // Upcoming Event, Birthday, Anniversary, Follow-up, Return Nevta, Custom
    val repeatInterval: String = "One-time", // One-time, Daily, Weekly, Monthly, Yearly
    val notes: String = "",
    val isCompleted: Boolean = false,
    val personId: Long? = null,
    val eventId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val relation: String,
    val mobile: String,
    val role: String = "Editor", // Admin, Editor, Viewer
    val status: String = "Active", // Active, Pending
    val avatarColor: Long = 0xFFFF7A1A
)

@Entity(tableName = "notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: String, // Upcoming wedding, Reminder, Pending return Nevta, New family member, Backup complete, Sync complete, Important event
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Rajendra Sharma",
    val mobile: String = "+91 98290 12345",
    val email: String = "rajendra.sharma@example.com",
    val city: String = "Jaipur",
    val isPremium: Boolean = true,
    val language: String = "HINDI_ENGLISH", // HINDI, ENGLISH, HINDI_ENGLISH
    val isAppLockEnabled: Boolean = false,
    val pinCode: String = "1234"
)
