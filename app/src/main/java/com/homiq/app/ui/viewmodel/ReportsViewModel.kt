package com.homiq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homiq.app.data.repository.BlockedDateRepository
import com.homiq.app.data.repository.BookingRepository
import com.homiq.app.data.repository.ExpenseRepository
import com.homiq.app.data.repository.PaymentRepository
import com.homiq.app.data.repository.PropertyRepository
import com.homiq.app.domain.ReportAnalytics
import com.homiq.app.domain.ReportPeriod
import java.time.Year
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ReportsViewModel(
    properties: PropertyRepository,
    bookings: BookingRepository,
    blockedDates: BlockedDateRepository,
    payments: PaymentRepository,
    expenses: ExpenseRepository,
) : ViewModel() {
    private val selectedMonth =
        MutableStateFlow(YearMonth.now())
    private val selectedYear =
        MutableStateFlow(Year.now().value)

    val month: StateFlow<YearMonth> =
        selectedMonth

    val year: StateFlow<Int> =
        selectedYear

    val monthlyReport: StateFlow<ReportAnalytics?> =
        reportAnalyticsFlow(
            periodFlow = selectedMonth.map { month ->
                ReportPeriod(
                    startEpochDay =
                        month.atDay(1).toEpochDay(),
                    endEpochDayExclusive =
                        month
                            .plusMonths(1)
                            .atDay(1)
                            .toEpochDay(),
                )
            },
            properties = properties,
            bookings = bookings,
            blockedDates = blockedDates,
            payments = payments,
            expenses = expenses,
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                5_000,
            ),
            initialValue = null,
        )

    val yearlyReport: StateFlow<ReportAnalytics?> =
        reportAnalyticsFlow(
            periodFlow = selectedYear.map { year ->
                ReportPeriod(
                    startEpochDay =
                        Year.of(year)
                            .atDay(1)
                            .toEpochDay(),
                    endEpochDayExclusive =
                        Year.of(year + 1)
                            .atDay(1)
                            .toEpochDay(),
                )
            },
            properties = properties,
            bookings = bookings,
            blockedDates = blockedDates,
            payments = payments,
            expenses = expenses,
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(
                5_000,
            ),
            initialValue = null,
        )

    fun previousMonth() {
        selectedMonth.value =
            selectedMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        selectedMonth.value =
            selectedMonth.value.plusMonths(1)
    }

    fun currentMonth() {
        selectedMonth.value =
            YearMonth.now()
    }

    fun previousYear() {
        selectedYear.value -= 1
    }

    fun nextYear() {
        selectedYear.value += 1
    }

    fun currentYear() {
        selectedYear.value =
            Year.now().value
    }
}
