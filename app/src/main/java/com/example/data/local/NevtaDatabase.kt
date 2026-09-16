package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.NevtaDao
import com.example.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PersonEntity::class,
        EventEntity::class,
        NevtaEntryEntity::class,
        NevtaGivenEntity::class,
        ReminderEntity::class,
        FamilyMemberEntity::class,
        AppNotificationEntity::class,
        UserProfileEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NevtaDatabase : RoomDatabase() {

    abstract fun nevtaDao(): NevtaDao

    companion object {
        @Volatile
        private var INSTANCE: NevtaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NevtaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NevtaDatabase::class.java,
                    "nevta_book_database"
                )
                    .addCallback(NevtaDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class NevtaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.nevtaDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    if (database.nevtaDao().getEntriesCount() == 0) {
                        populateInitialData(database.nevtaDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(dao: NevtaDao) {
            // Seed User Profile
            dao.insertOrUpdateProfile(
                UserProfileEntity(
                    id = 1,
                    name = "Rajendra Sharma",
                    mobile = "+91 98290 12345",
                    email = "rajendra.sharma@example.com",
                    city = "Jaipur",
                    isPremium = true,
                    language = "HINDI_ENGLISH"
                )
            )

            // Seed Family Members
            dao.insertFamilyMember(FamilyMemberEntity(name = "Rajendra Sharma", relation = "स्वयं (Self)", mobile = "+91 98290 12345", role = "Admin", status = "Active"))
            dao.insertFamilyMember(FamilyMemberEntity(name = "Sunita Sharma", relation = "पत्नी (Wife)", mobile = "+91 98290 54321", role = "Editor", status = "Active"))
            dao.insertFamilyMember(FamilyMemberEntity(name = "Aarav Sharma", relation = "पुत्र (Son)", mobile = "+91 94140 11223", role = "Editor", status = "Active"))
            dao.insertFamilyMember(FamilyMemberEntity(name = "Pooja Sharma", relation = "पुत्री (Daughter)", mobile = "+91 98291 99887", role = "Viewer", status = "Active"))

            // Seed Persons
            val sureshId = dao.insertPerson(
                PersonEntity(
                    name = "सुरेश शर्मा (Suresh Sharma)",
                    fatherName = "श्री नवाबों लाल शर्मा",
                    mobile = "+91 98291 33445",
                    city = "जयपुर (Jaipur)",
                    village = "चकपुर",
                    relationship = "परिवार",
                    isVip = false,
                    notes = "पारिवारिक सदस्य"
                )
            )
            val maheshId = dao.insertPerson(
                PersonEntity(
                    name = "महेश अग्रवाल (Mahesh Agrawal)",
                    fatherName = "श्री रामलाल अग्रवाल",
                    mobile = "+91 98290 11223",
                    city = "सीकर (Sikar)",
                    village = "काकोड़पुर",
                    relationship = "रिश्तेदार",
                    isVip = true,
                    notes = "पारिवारिक पुराने घनिष्ठ मित्र"
                )
            )
            val rajeshId = dao.insertPerson(
                PersonEntity(
                    name = "राजेश गुप्ता (Rajesh Gupta)",
                    fatherName = "श्री धनश्याम गुप्ता",
                    mobile = "+91 94140 55667",
                    city = "पटना, बिहार",
                    village = "",
                    relationship = "रिश्तेदार",
                    isVip = true,
                    notes = "पटना से विशेष अतिथि"
                )
            )
            val anitaId = dao.insertPerson(
                PersonEntity(
                    name = "अनीता देवी (Anita Devi)",
                    fatherName = "पति – राजेश मिश्रा",
                    mobile = "+91 98292 66778",
                    city = "छपरा, बिहार",
                    village = "",
                    relationship = "रिश्तेदार",
                    isVip = false,
                    notes = "छपरा से पधारे"
                )
            )
            val vinodId = dao.insertPerson(
                PersonEntity(
                    name = "विनोद कुमार (Vinod Kumar)",
                    fatherName = "ग्राम – बरौली",
                    mobile = "+91 98293 88990",
                    city = "सीवान, बिहार",
                    village = "बरौली",
                    relationship = "रिश्तेदार",
                    isVip = false,
                    notes = "सीवान से पधारे"
                )
            )

            // Seed Events
            val weddingEventId = dao.insertEvent(
                EventEntity(
                    title = "शादी – आरव शर्मा",
                    type = "शादी / Wedding",
                    personName = "आरव शर्मा & प्रिया",
                    date = "2026-08-10",
                    time = "07:00 PM",
                    venue = "रॉयल पैलेस रिसॉर्ट",
                    city = "जयपुर (Jaipur)",
                    address = "अजमेर रोड, जयपुर",
                    guestCount = 650,
                    plannedAmount = 350000.0,
                    budget = 1200000.0,
                    giftExpectation = "चांदी सिक्का / लिफाफा",
                    status = "Upcoming"
                )
            )
            val mundanEventId = dao.insertEvent(
                EventEntity(
                    title = "मुंडन समारोह",
                    type = "मुंडन / Mundan",
                    personName = "विवान शर्मा",
                    date = "2026-07-22",
                    time = "10:30 AM",
                    venue = "श्री गोविंद देव जी मंदिर धर्मशाला",
                    city = "जयपुर (Jaipur)",
                    address = "सिटी पैलेस के पास",
                    guestCount = 180,
                    plannedAmount = 85000.0,
                    budget = 250000.0,
                    status = "Upcoming"
                )
            )

            // Seed Received Entries (5 Entries matching Reference & Prompt)
            // 1. Suresh Sharma: NB-1037, 10 Aug 2026, 11:30 AM, ₹5,100 Cash, साड़ी सेट
            dao.insertEntry(
                NevtaEntryEntity(
                    entryId = "NB-1037",
                    personId = sureshId,
                    eventId = weddingEventId,
                    amount = 5100.0,
                    paymentMethod = "Cash",
                    gift = "साड़ी सेट",
                    date = "2026-08-10",
                    time = "11:30 AM",
                    notes = "चाचा जी की तरफ से"
                )
            )
            // 2. Mahesh Agrawal: NB-1042, 10 Aug 2026, 09:15 AM, ₹2,500 UPI, घड़ी का गिफ्ट, VIP
            dao.insertEntry(
                NevtaEntryEntity(
                    entryId = "NB-1042",
                    personId = maheshId,
                    eventId = weddingEventId,
                    amount = 2500.0,
                    paymentMethod = "UPI",
                    gift = "घड़ी का गिफ्ट",
                    date = "2026-08-10",
                    time = "09:15 AM",
                    notes = "सपरिवार पधारे थे"
                )
            )
            // 3. Rajesh Gupta: NB-1015, 22 Jul 2026, 06:40 PM, ₹2,100 Cash, मनोजी सेट, VIP
            dao.insertEntry(
                NevtaEntryEntity(
                    entryId = "NB-1015",
                    personId = rajeshId,
                    eventId = mundanEventId,
                    amount = 2100.0,
                    paymentMethod = "Cash",
                    gift = "मनोजी सेट",
                    date = "2026-07-22",
                    time = "06:40 PM",
                    notes = "पटना से विशेष आगमन"
                )
            )
            // 4. Anita Devi: NB-1008, 05 Jul 2026, 01:20 PM, ₹1,601 Cash, मिक्सर
            dao.insertEntry(
                NevtaEntryEntity(
                    entryId = "NB-1008",
                    personId = anitaId,
                    eventId = weddingEventId,
                    amount = 1601.0,
                    paymentMethod = "Cash",
                    gift = "मिक्सर",
                    date = "2026-07-05",
                    time = "01:20 PM",
                    notes = "शुभकामनाएं सहित"
                )
            )
            // 5. Vinod Kumar: NB-0989, 28 Jun 2026, 04:10 PM, ₹1,000 Cash, स्टील सेट
            dao.insertEntry(
                NevtaEntryEntity(
                    entryId = "NB-0989",
                    personId = vinodId,
                    eventId = weddingEventId,
                    amount = 1000.0,
                    paymentMethod = "Cash",
                    gift = "स्टील सेट",
                    date = "2026-06-28",
                    time = "04:10 PM",
                    notes = "सीवान से आगमन"
                )
            )

            // Seed Given Nevta
            dao.insertGiven(
                NevtaGivenEntity(
                    personId = maheshId,
                    eventId = null,
                    eventNameCustom = "शादी — महेश जी की सुपुत्री (प्रिया)",
                    amount = 2100.0,
                    paymentMethod = "Cash",
                    gift = "चांदी का सिक्का",
                    date = "2026-02-18",
                    notes = "सीकर विवाह समारोह में दिया"
                )
            )
            dao.insertGiven(
                NevtaGivenEntity(
                    personId = sureshId,
                    eventId = null,
                    eventNameCustom = "गृह प्रवेश — सुरेश जी का नया मकान",
                    amount = 5100.0,
                    paymentMethod = "UPI",
                    gift = "बर्तन सेट",
                    date = "2025-12-05",
                    notes = "जयपुर समारोह"
                )
            )
            dao.insertGiven(
                NevtaGivenEntity(
                    personId = rajeshId,
                    eventId = null,
                    eventNameCustom = "सालगिरह — राजेश जी",
                    amount = 1100.0,
                    paymentMethod = "Cash",
                    gift = "मिठाई",
                    date = "2026-03-20",
                    notes = "किशनगढ़ में भेंट"
                )
            )

            // Seed Reminders
            dao.insertReminder(
                ReminderEntity(
                    title = "शादी आरव शर्मा - व्यवस्था समीक्षा",
                    date = "2026-11-10",
                    time = "10:00 AM",
                    type = "Upcoming Event",
                    repeatInterval = "One-time",
                    notes = "कैटरिंग और स्टेज व्यवस्था फाइनल करना",
                    eventId = weddingEventId
                )
            )
            dao.insertReminder(
                ReminderEntity(
                    title = "महेश जी को नेवता वापसी ध्यान रखें",
                    date = "2026-10-01",
                    time = "05:00 PM",
                    type = "Return Nevta",
                    repeatInterval = "One-time",
                    notes = "उनका नेवता ₹2500 प्राप्त हुआ था, पिछली बार ₹2100 दिया गया था।",
                    personId = maheshId
                )
            )
            dao.insertReminder(
                ReminderEntity(
                    title = "सुरेश जी का जन्मदिन",
                    date = "2026-10-18",
                    time = "09:00 AM",
                    type = "Birthday",
                    repeatInterval = "Yearly",
                    notes = "शुभकामनाएं फोन करना",
                    personId = sureshId
                )
            )

            // Seed Notifications
            dao.insertNotification(
                AppNotificationEntity(
                    title = "आने वाला कार्यक्रम: शादी — आरव शर्मा",
                    description = "20 नवंबर को रॉयल पैलेस में शादी का कार्यक्रम निर्धारित है।",
                    type = "Upcoming wedding",
                    timestamp = System.currentTimeMillis() - 3600000L
                )
            )
            dao.insertNotification(
                AppNotificationEntity(
                    title = "वापसी नेवता शेष (Pending Return)",
                    description = "महेश जी (सीकर) का नेवता बैलेंस ₹400 आपके पास शेष है।",
                    type = "Pending return Nevta",
                    timestamp = System.currentTimeMillis() - 86400000L
                )
            )
            dao.insertNotification(
                AppNotificationEntity(
                    title = "डेटा सुरक्षित रूप से सुरक्षित (Backup Ready)",
                    description = "स्थानीय डेटाबेस एन्क्रिप्टेड और सुरक्षित है।",
                    type = "Backup complete",
                    timestamp = System.currentTimeMillis() - 172800000L
                )
            )
        }
    }
}
