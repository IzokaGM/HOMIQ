package com.homiq.app.data

import android.content.Context
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

    val properties: PropertyRepository by lazy {
        RoomPropertyRepository(database.propertyDao())
    }

    val bookings: BookingRepository by lazy {
        RoomBookingRepository(database.bookingDao())
    }

    val payments: PaymentRepository by lazy {
        RoomPaymentRepository(database.paymentDao())
    }

    val deposits: DepositRepository by lazy {
        RoomDepositRepository(database.depositDao())
    }

    val expenses: ExpenseRepository by lazy {
        RoomExpenseRepository(database.expenseDao())
    }

    val blockedDates: BlockedDateRepository by lazy {
        RoomBlockedDateRepository(database.blockedDateDao())
    }
}
