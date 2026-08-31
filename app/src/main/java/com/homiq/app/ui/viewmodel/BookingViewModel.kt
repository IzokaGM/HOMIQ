package com.homiq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homiq.app.data.local.entity.BookingEntity
import com.homiq.app.data.local.entity.PropertyEntity
import com.homiq.app.data.repository.BookingRepository
import com.homiq.app.data.repository.PropertyRepository
import com.homiq.app.domain.BookingDraft
import com.homiq.app.domain.BookingManager
import com.homiq.app.domain.BookingSaveResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BookingViewModel(
    properties: PropertyRepository,
    private val bookings: BookingRepository,
    private val bookingManager: BookingManager,
) : ViewModel() {
    val bookingList: StateFlow<List<BookingEntity>> =
        bookings.observeAll().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val propertyList: StateFlow<List<PropertyEntity>> =
        properties.observeAll().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    suspend fun save(
        draft: BookingDraft,
    ): BookingSaveResult = bookingManager.save(draft)

    suspend fun cancel(bookingId: String): Boolean =
        bookingManager.cancel(bookingId)
}
