package com.example.data.repository

import com.example.data.local.dao.NevtaDao
import com.example.data.local.entity.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NevtaRepository(private val dao: NevtaDao) {

    // --- PERSONS ---
    val allPersons: Flow<List<PersonEntity>> = dao.getAllPersons()

    suspend fun getPersonById(id: Long): PersonEntity? = dao.getPersonById(id)

    suspend fun insertOrFindPerson(
        name: String,
        fatherName: String = "",
        mobile: String = "",
        city: String = "",
        village: String = "",
        relationship: String = "रिश्तेदार",
        notes: String = ""
    ): Long {
        if (mobile.isNotBlank()) {
            val existing = dao.findPersonByMobile(mobile)
            if (existing != null) return existing.id
        }
        val existingByName = dao.findPersonByName(name.trim())
        if (existingByName != null) return existingByName.id

        return dao.insertPerson(
            PersonEntity(
                name = name.trim(),
                fatherName = fatherName.trim(),
                mobile = mobile.trim(),
                city = city.trim(),
                village = village.trim(),
                relationship = relationship,
                notes = notes.trim()
            )
        )
    }

    suspend fun updatePerson(person: PersonEntity) = dao.updatePerson(person)
    suspend fun deletePerson(person: PersonEntity) = dao.deletePerson(person)
    suspend fun searchPersons(query: String) = dao.searchPersons(query)

    // --- EVENTS ---
    val allEvents: Flow<List<EventEntity>> = dao.getAllEvents()
    val upcomingEvents: Flow<List<EventEntity>> = dao.getUpcomingEvents()

    suspend fun getEventById(id: Long): EventEntity? = dao.getEventById(id)
    suspend fun insertEvent(event: EventEntity): Long = dao.insertEvent(event)
    suspend fun updateEvent(event: EventEntity) = dao.updateEvent(event)
    suspend fun deleteEvent(event: EventEntity) = dao.deleteEvent(event)

    // --- ENTRIES (RECEIVED) ---
    val allEntries: Flow<List<EntryWithPersonAndEvent>> = dao.getAllEntries()

    suspend fun getEntryByEntryId(entryId: String): EntryWithPersonAndEvent? = dao.getEntryByEntryId(entryId)
    fun getEntriesForPerson(personId: Long) = dao.getEntriesForPerson(personId)
    fun getEntriesForEvent(eventId: Long) = dao.getEntriesForEvent(eventId)

    suspend fun generateNextEntryId(): String {
        val count = dao.getEntriesCount()
        return "NB-${1001 + count}"
    }

    suspend fun insertEntry(entry: NevtaEntryEntity): Long = dao.insertEntry(entry)
    suspend fun updateEntry(entry: NevtaEntryEntity) = dao.updateEntry(entry)
    suspend fun deleteEntry(entry: NevtaEntryEntity) = dao.deleteEntry(entry)

    // --- GIVEN NEVTA ---
    val allGiven: Flow<List<GivenWithPersonAndEvent>> = dao.getAllGiven()
    fun getGivenForPerson(personId: Long) = dao.getGivenForPerson(personId)

    suspend fun insertGiven(given: NevtaGivenEntity): Long = dao.insertGiven(given)
    suspend fun updateGiven(given: NevtaGivenEntity) = dao.updateGiven(given)
    suspend fun deleteGiven(given: NevtaGivenEntity) = dao.deleteGiven(given)

    // --- DASHBOARD KPI ---
    val dashboardKpi: Flow<DashboardKpi> = combine(
        dao.getAllEntries(),
        dao.getAllGiven(),
        dao.getUpcomingEvents()
    ) { entries, givenList, events ->
        val receivedSum = entries.sumOf { it.entry.amount }
        val givenSum = givenList.sumOf { it.given.amount }
        val giftsCount = entries.count { it.entry.gift.isNotBlank() } + givenList.count { it.given.gift.isNotBlank() }
        val avgGiven = if (givenList.isNotEmpty()) givenSum / givenList.size else 0.0
        val avgEntry = if (entries.isNotEmpty()) receivedSum / entries.size else 0.0

        // Calculate pending return count (persons where received != given)
        val personReceivedMap = entries.groupBy { it.person.id }.mapValues { (_, list) -> list.sumOf { it.entry.amount } }
        val personGivenMap = givenList.groupBy { it.person.id }.mapValues { (_, list) -> list.sumOf { it.given.amount } }
        val allPersonIds = (personReceivedMap.keys + personGivenMap.keys).toSet()
        val pendingCount = allPersonIds.count { pId ->
            val rec = personReceivedMap[pId] ?: 0.0
            val giv = personGivenMap[pId] ?: 0.0
            rec > giv // they gave us more, we owe a return
        }

        DashboardKpi(
            totalReceivedAmount = receivedSum,
            totalGivenAmount = givenSum,
            totalReceivedCount = entries.size,
            totalGivenCount = givenList.size,
            upcomingEventsCount = events.size,
            pendingReturnCount = pendingCount,
            totalGiftsCount = giftsCount,
            avgGivenAmount = avgGiven,
            averageEntryAmount = avgEntry
        )
    }

    // --- PERSON BALANCE / RETURN NEVTA ---
    val allPersonBalances: Flow<List<PersonBalanceSummary>> = combine(
        dao.getAllPersons(),
        dao.getAllEntries(),
        dao.getAllGiven()
    ) { persons, entries, givenList ->
        val receivedMap = entries.groupBy { it.person.id }.mapValues { (_, l) -> l.sumOf { it.entry.amount } }
        val givenMap = givenList.groupBy { it.person.id }.mapValues { (_, l) -> l.sumOf { it.given.amount } }

        persons.map { person ->
            val received = receivedMap[person.id] ?: 0.0
            val given = givenMap[person.id] ?: 0.0
            val net = received - given
            val status = when {
                received == 0.0 && given == 0.0 -> ReturnStatus.SETTLED
                received == given -> ReturnStatus.SETTLED
                received > given -> ReturnStatus.PENDING
                else -> ReturnStatus.ADVANCE
            }
            PersonBalanceSummary(
                person = person,
                totalReceived = received,
                totalGiven = given,
                netBalance = net,
                returnStatus = status
            )
        }
    }

    // --- SMART SUGGESTIONS ---
    suspend fun getSmartSuggestion(personId: Long): SmartSuggestion? {
        val person = dao.getPersonById(personId) ?: return null
        val givenList = dao.getGivenForPerson(personId).first()
        val entries = dao.getEntriesForPerson(personId).first()

        val lastReceived = entries.maxByOrNull { it.entry.createdAt }?.entry?.amount ?: 0.0
        val lastGiven = givenList.maxByOrNull { it.given.createdAt }?.given?.amount ?: 0.0

        if (lastReceived > 0.0) {
            // Suggest slightly higher or matching auspicious amount
            val suggested = auspiciousNextAmount(lastReceived)
            return SmartSuggestion(
                personName = person.name,
                lastGivenAmount = lastReceived,
                suggestedAmount = suggested,
                note = "इस व्यक्ति ने पिछली बार ₹${lastReceived.toInt()} दिया था।"
            )
        } else if (lastGiven > 0.0) {
            return SmartSuggestion(
                personName = person.name,
                lastGivenAmount = lastGiven,
                suggestedAmount = lastGiven,
                note = "आपने पिछली बार ₹${lastGiven.toInt()} दिया था।"
            )
        }
        return null
    }

    private fun auspiciousNextAmount(amount: Double): Double {
        return when {
            amount <= 501.0 -> 1100.0
            amount <= 1100.0 -> 2100.0
            amount <= 2100.0 -> 2500.0
            amount <= 2500.0 -> 3100.0
            amount <= 5100.0 -> 7100.0
            amount <= 11000.0 -> 15100.0
            else -> amount + 1000.0
        }
    }

    // --- REMINDERS ---
    val allReminders: Flow<List<ReminderEntity>> = dao.getAllReminders()
    suspend fun insertReminder(reminder: ReminderEntity) = dao.insertReminder(reminder)
    suspend fun updateReminder(reminder: ReminderEntity) = dao.updateReminder(reminder)
    suspend fun deleteReminder(reminder: ReminderEntity) = dao.deleteReminder(reminder)

    // --- FAMILY MEMBERS ---
    val allFamilyMembers: Flow<List<FamilyMemberEntity>> = dao.getAllFamilyMembers()
    suspend fun insertFamilyMember(member: FamilyMemberEntity) = dao.insertFamilyMember(member)
    suspend fun updateFamilyMember(member: FamilyMemberEntity) = dao.updateFamilyMember(member)
    suspend fun deleteFamilyMember(member: FamilyMemberEntity) = dao.deleteFamilyMember(member)

    // --- NOTIFICATIONS ---
    val allNotifications: Flow<List<AppNotificationEntity>> = dao.getAllNotifications()
    suspend fun markNotificationAsRead(id: Long) = dao.markNotificationAsRead(id)
    suspend fun clearAllNotifications() = dao.clearAllNotifications()

    // --- PROFILE ---
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    suspend fun updateProfile(profile: UserProfileEntity) = dao.insertOrUpdateProfile(profile)

    // --- EXPORT CSV ---
    suspend fun generateEntriesCsv(): String {
        val entries = dao.getAllEntries().first()
        val sb = StringBuilder()
        sb.append("Entry ID,Name,Father Name,Mobile,City,Event,Amount,Payment Method,Gift,Date,Notes\n")
        entries.forEach { item ->
            sb.append("\"${item.entry.entryId}\",")
            sb.append("\"${item.person.name}\",")
            sb.append("\"${item.person.fatherName}\",")
            sb.append("\"${item.person.mobile}\",")
            sb.append("\"${item.person.city}\",")
            sb.append("\"${item.event?.title ?: ""}\",")
            sb.append("${item.entry.amount},")
            sb.append("\"${item.entry.paymentMethod}\",")
            sb.append("\"${item.entry.gift}\",")
            sb.append("\"${item.entry.date}\",")
            sb.append("\"${item.entry.notes}\"\n")
        }
        return sb.toString()
    }
}
