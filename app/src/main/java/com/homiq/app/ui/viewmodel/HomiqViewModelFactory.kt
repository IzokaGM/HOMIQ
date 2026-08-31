package com.homiq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.homiq.app.data.HomiqAppContainer
import com.homiq.app.domain.BookingManager

class HomiqViewModelFactory(
    private val container: HomiqAppContainer,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PropertyViewModel::class.java) ->
                PropertyViewModel(
                    properties = container.properties,
                ) as T

            modelClass.isAssignableFrom(BookingViewModel::class.java) ->
                BookingViewModel(
                    properties = container.properties,
                    bookings = container.bookings,
                    bookingManager = BookingManager(
                        properties = container.properties,
                        bookings = container.bookings,
                        blockedDates = container.blockedDates,
                    ),
                ) as T

            else -> error(
                "Unknown HOMIQ ViewModel: ${modelClass.name}",
            )
        }
    }
}
