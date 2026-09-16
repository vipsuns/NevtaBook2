package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import com.example.data.model.EntryWithPersonAndEvent
import com.example.data.model.GivenWithPersonAndEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface NevtaDao {

    // --- PERSONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity): Long

    @Update
    suspend fun updatePerson(person: PersonEntity)

    @Delete
    suspend fun deletePerson(person: PersonEntity)

    @Query("SELECT * FROM persons ORDER BY name ASC")
    fun getAllPersons(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getPersonById(id: Long): PersonEntity?

    @Query("SELECT * FROM persons WHERE name LIKE '%' || :query || '%' OR mobile LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%' LIMIT 20")
    suspend fun searchPersons(query: String): List<PersonEntity>

    @Query("SELECT * FROM persons WHERE mobile = :mobile LIMIT 1")
    suspend fun findPersonByMobile(mobile: String): PersonEntity?

    @Query("SELECT * FROM persons WHERE name = :name LIMIT 1")
    suspend fun findPersonByName(name: String): PersonEntity?

    // --- EVENTS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE status != 'Completed' ORDER BY date ASC")
    fun getUpcomingEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): EventEntity?

    // --- NEVTA ENTRIES (RECEIVED) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: NevtaEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: NevtaEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: NevtaEntryEntity)

    @Transaction
    @Query("SELECT * FROM nevta_entries ORDER BY date DESC, id DESC")
    fun getAllEntries(): Flow<List<EntryWithPersonAndEvent>>

    @Transaction
    @Query("SELECT * FROM nevta_entries WHERE entryId = :entryId LIMIT 1")
    suspend fun getEntryByEntryId(entryId: String): EntryWithPersonAndEvent?

    @Transaction
    @Query("SELECT * FROM nevta_entries WHERE personId = :personId ORDER BY date DESC")
    fun getEntriesForPerson(personId: Long): Flow<List<EntryWithPersonAndEvent>>

    @Transaction
    @Query("SELECT * FROM nevta_entries WHERE eventId = :eventId ORDER BY date DESC")
    fun getEntriesForEvent(eventId: Long): Flow<List<EntryWithPersonAndEvent>>

    @Query("SELECT COUNT(*) FROM nevta_entries")
    suspend fun getEntriesCount(): Int

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM nevta_entries")
    fun getTotalReceivedSum(): Flow<Double>

    // --- NEVTA GIVEN ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGiven(given: NevtaGivenEntity): Long

    @Update
    suspend fun updateGiven(given: NevtaGivenEntity)

    @Delete
    suspend fun deleteGiven(given: NevtaGivenEntity)

    @Transaction
    @Query("SELECT * FROM nevta_given ORDER BY date DESC, id DESC")
    fun getAllGiven(): Flow<List<GivenWithPersonAndEvent>>

    @Transaction
    @Query("SELECT * FROM nevta_given WHERE personId = :personId ORDER BY date DESC")
    fun getGivenForPerson(personId: Long): Flow<List<GivenWithPersonAndEvent>>

    @Query("SELECT COUNT(*) FROM nevta_given")
    suspend fun getGivenCount(): Int

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM nevta_given")
    fun getTotalGivenSum(): Flow<Double>

    // --- REMINDERS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders ORDER BY isCompleted ASC, date ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    // --- FAMILY MEMBERS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(member: FamilyMemberEntity): Long

    @Update
    suspend fun updateFamilyMember(member: FamilyMemberEntity)

    @Delete
    suspend fun deleteFamilyMember(member: FamilyMemberEntity)

    @Query("SELECT * FROM family_members ORDER BY role ASC, name ASC")
    fun getAllFamilyMembers(): Flow<List<FamilyMemberEntity>>

    // --- NOTIFICATIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity): Long

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    // --- USER PROFILE ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?
}
