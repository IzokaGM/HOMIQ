package com.homiq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homiq.app.data.local.entity.BookingEntity
import com.homiq.app.data.local.model.BookingBalanceRow
import com.homiq.app.data.model.BookingStatus
import com.homiq.app.data.repository.BlockedDateRepository
import com.homiq.app.data.repository.BookingRepository
import com.homiq.app.data.repository.ExpenseRepository
import com.homiq.app.data.repository.PaymentRepository
import com.homiq.app.data.repository.PropertyRepository
import com.homiq.app.domain.ReportAnalytics
import com.homiq.app.domain.ReportPeriod
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class OutstandingBooking(
    val booking: BookingEntity,
    val outstandingSen: Long,
)

data class DashboardUiState(
    val analytics: ReportAnalytics? = null,
    val propertyNames: Map<String, String> = emptyMap(),
    val checkInsToday: List<BookingEntity> = emptyList(),
    val checkOutsToday: List<BookingEntity> = emptyList(),
    val upcomingBookings: List<BookingEntity> = emptyList(),
    val outstandingBookings: List<OutstandingBooking> =
        emptyList(),
)

class DashboardViewModel(
    properties: PropertyRepository,
    bookings: BookingRepository,
    blockedDates: BlockedDateRepository,
    payments: PaymentRepository,
    expenses: ExpenseRepository,
) : ViewModel() {
    private val today =
        LocalDate.now().toEpochDay()
    private val currentMonth =
        YearMonth.now()
    private val monthPeriod = ReportPeriod(
        startEpochDay =
            currentMonth.atDay(1).toEpochDay(),
        endEpochDayExclusive =
            currentMonth
                .plusMonths(1)
                .atDay(1)
                .toEpochDay(),
    )

    private val analytics =
        reportAnalyticsFlow(
            periodFlow = flowOf(monthPeriod),
            properties = properties,
            bookings = bookings,
            blockedDates = blockedDates,
            payments = payments,
            expenses = expenses,
        )

    val state: StateFlow<DashboardUiState> =
        combine(
            analytics,
            properties.observeAll(),
            bookings.observeAll(),
            payments.observeBookingBalances(),
        ) {
            monthAnalytics,
            propertyList,
            bookingList,
            balances,
        ->
            buildDashboard(
                analytics = monthAnalytics,
                propertyNames = propertyList
                    .associate {
                        it.id to it.name
                    },
                bookings = bookingList,
                balances = balances,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                5_000,
            ),
            initialValue = DashboardUiState(),
        )

    private fun buildDashboard(
        analytics: ReportAnalytics,
        propertyNames: Map<String, String>,
        bookings: List<BookingEntity>,
        balances: List<BookingBalanceRow>,
    ): DashboardUiState {
        val activeBookings = bookings.filter {
            !it.isDeleted &&
                it.status !=
                BookingStatus.CANCELLED
        }

        val checkIns = activeBookings
            .filter {
                it.checkInEpochDay == today
            }
            .sortedBy { it.guestName.lowercase() }

        val checkOuts = activeBookings
            .filter {
                it.checkOutEpochDay == today
            }
            .sortedBy { it.guestName.lowercase() }

        val upcoming = activeBookings
            .filter {
                it.checkInEpochDay >= today
            }
            .sortedBy { it.checkInEpochDay }
            .take(5)

        val bookingById =
            activeBookings.associateBy { it.id }

        val outstanding = balances
            .mapNotNull { balance ->
                val booking =
                    bookingById[balance.bookingId]
                        ?: return@mapNotNull null
                val amount =
                    balance.outstandingSen()
                if (amount <= 0L) {
                    return@mapNotNull null
                }
                OutstandingBooking(
                    booking = booking,
                    outstandingSen = amount,
                )
            }
            .sortedWith(
                compareBy<OutstandingBooking> {
                    it.booking.checkInEpochDay
                }.thenByDescending {
                    it.outstandingSen
                },
            )
            .take(5)

        return DashboardUiState(
            analytics = analytics,
            propertyNames = propertyNames,
            checkInsToday = checkIns,
            checkOutsToday = checkOuts,
            upcomingBookings = upcoming,
            outstandingBookings = outstanding,
        )
    }
}
