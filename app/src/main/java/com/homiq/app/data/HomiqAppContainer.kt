package com.homiq.app.data

import android.content.Context
import com.homiq.app.data.backup.HomiqBackupService
import com.homiq.app.data.security.AppLockPreferences
import com.homiq.app.data.security.AppLockService
import com.homiq.app.data.sync.GoogleDriveAuthorization
import com.homiq.app.data.sync.GoogleDriveRestClient
import com.homiq.app.data.sync.HomiqSyncEngine
import com.homiq.app.data.sync.HomiqSyncService
import com.homiq.app.data.sync.SyncChangeSignal
import com.homiq.app.data.sync.SyncPreferences
import com.homiq.app.data.local.HomiqDatabase
import com.homiq.app.data.repository.BlockedDateRepository
import com.homiq.app.data.repository.BookingRepository
import com.homiq.app.data.repository.DepositRepository
import com.homiq.app.data.repository.ExpenseRepository
import com.homiq.app.data.repository.PaymentRepository
import com.homiq.app.data.repository.PropertyRepository
import com.homiq.app.data.repository.RoomBlockedDateRepository
import com.homiq.app.data.repository.RoomBookingRepository
import com.homiq.app.data.repository.RoomDepositRepository
import com.homiq.app.data.repository.RoomExpenseRepository
import com.homiq.app.data.repository.RoomPaymentRepository
import com.homiq.app.data.repository.RoomPropertyRepository

class HomiqAppContainer(
    context: Context,
) {
    val database: HomiqDatabase by lazy {
        HomiqDatabase.create(context)
    }

    val appLockPreferences: AppLockPreferences by lazy {
        AppLockPreferences(context)
    }

    val appLockService: AppLockService by lazy {
        AppLockService(appLockPreferences)
    }

    val syncPreferences: SyncPreferences by lazy {
        SyncPreferences(context)
    }

    val syncChanges: SyncChangeSignal by lazy {
        SyncChangeSignal()
    }

    val properties: PropertyRepository by lazy {
        RoomPropertyRepository(
            dao = database.propertyDao(),
            onChanged = syncChanges::notifyChanged,
        )
    }

    val bookings: BookingRepository by lazy {
        RoomBookingRepository(
            dao = database.bookingDao(),
            onChanged = syncChanges::notifyChanged,
        )
    }

    val payments: PaymentRepository by lazy {
        RoomPaymentRepository(
            dao = database.paymentDao(),
            onChanged = syncChanges::notifyChanged,
        )
    }

    val deposits: DepositRepository by lazy {
        RoomDepositRepository(
            dao = database.depositDao(),
            onChanged = syncChanges::notifyChanged,
        )
    }

    val expenses: ExpenseRepository by lazy {
        RoomExpenseRepository(
            dao = database.expenseDao(),
            onChanged = syncChanges::notifyChanged,
        )
    }

    val blockedDates: BlockedDateRepository by lazy {
        RoomBlockedDateRepository(
            dao = database.blockedDateDao(),
            onChanged = syncChanges::notifyChanged,
        )
    }

    val backupService: HomiqBackupService by lazy {
        HomiqBackupService(
            context = context,
            database = database,
        )
    }

    val syncService: HomiqSyncService by lazy {
        HomiqSyncService(
            authorization =
                GoogleDriveAuthorization(context),
            engine =
                HomiqSyncEngine(
                    database = database,
                    drive =
                        GoogleDriveRestClient(),
                    preferences =
                        syncPreferences,
                ),
            preferences = syncPreferences,
            changes = syncChanges,
        )
    }
}
