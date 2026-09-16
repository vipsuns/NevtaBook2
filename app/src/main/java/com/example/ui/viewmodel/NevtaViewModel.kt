package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NevtaDatabase
import com.example.data.local.entity.*
import com.example.data.model.*
import com.example.data.repository.NevtaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NevtaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NevtaRepository

    init {
        val database = NevtaDatabase.getDatabase(application, viewModelScope)
        repository = NevtaRepository(database.nevtaDao())
    }

    // Language Preference
    private val prefs = application.getSharedPreferences("nevta_prefs", android.content.Context.MODE_PRIVATE)
    private val _appLanguage = MutableStateFlow(
        when (prefs.getString("app_language", "en")) {
            "hi" -> com.example.utils.AppLanguage.HINDI
            else -> com.example.utils.AppLanguage.ENGLISH
        }
    )
    val appLanguage = _appLanguage.asStateFlow()

    fun setAppLanguage(language: com.example.utils.AppLanguage) {
        _appLanguage.value = language
        prefs.edit().putString("app_language", language.code).apply()
    }

    // Theme (Light / Dark Mode) Preference
    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("dark_theme", false))
    val isDarkTheme = _isDarkTheme.asStateFlow()

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        prefs.edit().putBoolean("dark_theme", enabled).apply()
    }

    fun toggleDarkTheme() {
        setDarkTheme(!_isDarkTheme.value)
    }

    // Repository Flows
    val allPersons = repository.allPersons.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allEvents = repository.allEvents.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val upcomingEvents = repository.upcomingEvents.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allEntries = repository.allEntries.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allGiven = repository.allGiven.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val dashboardKpi = repository.dashboardKpi.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardKpi())
    val allPersonBalances = repository.allPersonBalances.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allReminders = repository.allReminders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allFamilyMembers = repository.allFamilyMembers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allNotifications = repository.allNotifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val userProfile = repository.userProfile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Global Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Quick Search By Entry ID (Section 20)
    private val _quickSearchIdResult = MutableStateFlow<EntryWithPersonAndEvent?>(null)
    val quickSearchIdResult = _quickSearchIdResult.asStateFlow()
    private val _quickSearchStatus = MutableStateFlow<String?>(null)
    val quickSearchStatus = _quickSearchStatus.asStateFlow()

    fun searchByEntryId(id: String) {
        viewModelScope.launch {
            val cleanId = id.trim().uppercase()
            val result = repository.getEntryByEntryId(cleanId)
            _quickSearchIdResult.value = result
            if (result != null) {
                _quickSearchStatus.value = "FOUND"
            } else {
                _quickSearchStatus.value = "NOT_FOUND"
            }
        }
    }

    fun clearQuickSearchResult() {
        _quickSearchIdResult.value = null
        _quickSearchStatus.value = null
    }

    // Smart Suggestion
    private val _currentSmartSuggestion = MutableStateFlow<SmartSuggestion?>(null)
    val currentSmartSuggestion = _currentSmartSuggestion.asStateFlow()

    fun fetchSmartSuggestion(personId: Long) {
        viewModelScope.launch {
            _currentSmartSuggestion.value = repository.getSmartSuggestion(personId)
        }
    }

    fun clearSmartSuggestion() {
        _currentSmartSuggestion.value = null
    }

    // Filter states for Entries
    private val _entriesFilter = MutableStateFlow("All") // All, Cash, UPI, Gift, Wedding, Mundan, etc.
    val entriesFilter = _entriesFilter.asStateFlow()
    private val _entriesSort = MutableStateFlow("Newest") // Newest, Oldest, Highest, Lowest
    val entriesSort = _entriesSort.asStateFlow()

    fun setEntriesFilter(filter: String) { _entriesFilter.value = filter }
    fun setEntriesSort(sort: String) { _entriesSort.value = sort }

    val filteredEntries = combine(allEntries, entriesFilter, entriesSort, searchQuery) { entries, filter, sort, query ->
        var list = entries
        // Search filter
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.entry.entryId.lowercase().contains(q) ||
                it.person.name.lowercase().contains(q) ||
                it.person.fatherName.lowercase().contains(q) ||
                it.person.city.lowercase().contains(q) ||
                it.person.village.lowercase().contains(q) ||
                it.person.mobile.contains(q) ||
                (it.event?.title?.lowercase()?.contains(q) ?: false) ||
                it.entry.gift.lowercase().contains(q) ||
                it.entry.amount.toInt().toString().contains(q)
            }
        }
        // Type filter
        if (filter != "All") {
            list = when (filter) {
                "Cash" -> list.filter { it.entry.paymentMethod.equals("Cash", true) }
                "UPI" -> list.filter { it.entry.paymentMethod.equals("UPI", true) }
                "Gift" -> list.filter { it.entry.gift.isNotBlank() }
                else -> list.filter { it.event?.type?.contains(filter, true) == true || it.event?.title?.contains(filter, true) == true }
            }
        }
        // Sort
        when (sort) {
            "Oldest" -> list.sortedBy { it.entry.date }
            "Highest Amount" -> list.sortedByDescending { it.entry.amount }
            "Lowest Amount" -> list.sortedBy { it.entry.amount }
            else -> list.sortedByDescending { it.entry.date } // Newest
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter for Given Nevta
    private val _givenFilter = MutableStateFlow("All")
    val givenFilter = _givenFilter.asStateFlow()

    fun setGivenFilter(filter: String) { _givenFilter.value = filter }

    val filteredGiven = combine(allGiven, givenFilter, searchQuery) { givenList, filter, query ->
        var list = givenList
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.person.name.lowercase().contains(q) ||
                it.person.city.lowercase().contains(q) ||
                it.person.mobile.contains(q) ||
                it.given.eventNameCustom.lowercase().contains(q) ||
                (it.event?.title?.lowercase()?.contains(q) ?: false) ||
                it.given.gift.lowercase().contains(q) ||
                it.given.amount.toInt().toString().contains(q)
            }
        }
        if (filter != "All") {
            list = list.filter {
                it.given.eventNameCustom.contains(filter, true) ||
                (it.event?.type?.contains(filter, true) == true)
            }
        }
        list.sortedByDescending { it.given.date }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter for Events
    private val _eventsFilter = MutableStateFlow("All")
    val eventsFilter = _eventsFilter.asStateFlow()

    fun setEventsFilter(filter: String) { _eventsFilter.value = filter }

    val filteredEvents = combine(allEvents, eventsFilter, searchQuery) { events, filter, query ->
        var list = events
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.personName.lowercase().contains(q) ||
                it.city.lowercase().contains(q) ||
                it.venue.lowercase().contains(q)
            }
        }
        when (filter) {
            "Upcoming" -> list.filter { it.status == "Upcoming" }
            "Today" -> list.filter { it.status == "Today" }
            "Completed" -> list.filter { it.status == "Completed" }
            "All" -> list
            else -> list.filter { it.type.contains(filter, true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- ACTIONS: ADD / EDIT / DELETE ---

    fun addNevtaEntry(
        personName: String,
        fatherName: String,
        mobile: String,
        city: String,
        village: String,
        relationship: String,
        eventId: Long?,
        amount: Double,
        paymentMethod: String,
        gift: String,
        date: String,
        notes: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val personId = repository.insertOrFindPerson(
                name = personName,
                fatherName = fatherName,
                mobile = mobile,
                city = city,
                village = village,
                relationship = relationship
            )
            val entryId = repository.generateNextEntryId()
            val entry = NevtaEntryEntity(
                entryId = entryId,
                personId = personId,
                eventId = eventId,
                amount = amount,
                paymentMethod = paymentMethod,
                gift = gift,
                date = date,
                notes = notes
            )
            repository.insertEntry(entry)
            onSuccess(entryId)
        }
    }

    fun addGivenNevta(
        personName: String,
        fatherName: String,
        mobile: String,
        city: String,
        relationship: String,
        eventId: Long?,
        customEventName: String,
        amount: Double,
        paymentMethod: String,
        gift: String,
        date: String,
        notes: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val personId = repository.insertOrFindPerson(
                name = personName,
                fatherName = fatherName,
                mobile = mobile,
                city = city,
                relationship = relationship
            )
            val given = NevtaGivenEntity(
                personId = personId,
                eventId = eventId,
                eventNameCustom = customEventName,
                amount = amount,
                paymentMethod = paymentMethod,
                gift = gift,
                date = date,
                notes = notes
            )
            repository.insertGiven(given)
            onSuccess()
        }
    }

    fun addEvent(
        title: String,
        type: String,
        personName: String,
        date: String,
        time: String,
        venue: String,
        city: String,
        address: String,
        guestCount: Int,
        plannedAmount: Double,
        budget: Double,
        notes: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val event = EventEntity(
                title = title,
                type = type,
                personName = personName,
                date = date,
                time = time,
                venue = venue,
                city = city,
                address = address,
                guestCount = guestCount,
                plannedAmount = plannedAmount,
                budget = budget,
                notes = notes
            )
            repository.insertEvent(event)
            onSuccess()
        }
    }

    fun addReminder(
        title: String,
        date: String,
        time: String,
        type: String,
        repeatInterval: String,
        notes: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                title = title,
                date = date,
                time = time,
                type = type,
                repeatInterval = repeatInterval,
                notes = notes
            )
            repository.insertReminder(reminder)
            onSuccess()
        }
    }

    fun addFamilyMember(
        name: String,
        relation: String,
        mobile: String,
        role: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val member = FamilyMemberEntity(
                name = name,
                relation = relation,
                mobile = mobile,
                role = role
            )
            repository.insertFamilyMember(member)
            onSuccess()
        }
    }

    fun toggleReminderCompleted(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.updateReminder(reminder.copy(isCompleted = !reminder.isCompleted))
        }
    }

    fun deleteEntry(entry: NevtaEntryEntity) {
        viewModelScope.launch { repository.deleteEntry(entry) }
    }

    fun deleteGiven(given: NevtaGivenEntity) {
        viewModelScope.launch { repository.deleteGiven(given) }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch { repository.deleteEvent(event) }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch { repository.deleteReminder(reminder) }
    }

    fun deleteFamilyMember(member: FamilyMemberEntity) {
        viewModelScope.launch { repository.deleteFamilyMember(member) }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch { repository.markNotificationAsRead(id) }
    }

    fun updateProfile(profile: UserProfileEntity) {
        viewModelScope.launch { repository.updateProfile(profile) }
    }

    // CSV Export
    fun exportCsv(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val csv = repository.generateEntriesCsv()
            onResult(csv)
        }
    }
}
