package com.homiq.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.data.local.entity.BookingEntity
import com.homiq.app.data.model.BookingStatus
import com.homiq.app.ui.components.EmptyStateCard
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.util.formatEpochDay
import com.homiq.app.ui.util.formatSenAsRinggit
import com.homiq.app.ui.util.labelRes
import com.homiq.app.ui.viewmodel.BookingViewModel
import java.time.LocalDate

@Composable
fun BookingsScreen(
    viewModel: BookingViewModel,
    onBookingClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bookings by viewModel.bookingList.collectAsStateWithLifecycle()
    val properties by viewModel.propertyList.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]
    val today = LocalDate.now().toEpochDay()

    var selectedFilter by remember { mutableIntStateOf(0) }
    val filters = listOf(
        stringResource(R.string.filter_all),
        stringResource(R.string.filter_upcoming),
        stringResource(R.string.filter_completed),
        stringResource(R.string.filter_cancelled),
    )

    val filtered = remember(
        bookings,
        selectedFilter,
        today,
    ) {
        when (selectedFilter) {
            1 -> bookings.filter {
                it.status != BookingStatus.CANCELLED &&
                    it.checkOutEpochDay > today
            }
            2 -> bookings.filter {
                it.status == BookingStatus.CHECKED_OUT ||
                    (
                        it.status != BookingStatus.CANCELLED &&
                            it.checkOutEpochDay <= today
                    )
            }
            3 -> bookings.filter {
                it.status == BookingStatus.CANCELLED
            }
            else -> bookings
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 24.dp,
            end = 20.dp,
            bottom = 104.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            ScreenHeader(
                title = stringResource(R.string.bookings_title),
                subtitle = stringResource(R.string.bookings_live_subtitle),
            )
        }

        item {
            Row(
                modifier = Modifier.horizontalScroll(
                    rememberScrollState(),
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                filters.forEachIndexed { index, label ->
                    FilterChip(
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                        label = { Text(label) },
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(
                        if (bookings.isEmpty()) {
                            R.string.bookings_empty_title
                        } else {
                            R.string.no_matching_bookings
                        },
                    ),
                    body = stringResource(
                        if (bookings.isEmpty()) {
                            R.string.upcoming_empty_body
                        } else {
                            R.string.change_booking_filter
                        },
                    ),
                    icon = Icons.Outlined.EventNote,
                )
            }
        } else {
            items(
                items = filtered,
                key = { it.id },
            ) { booking ->
                val propertyName = properties
                    .firstOrNull { it.id == booking.propertyId }
                    ?.name
                    ?: stringResource(R.string.unknown_property)

                BookingCard(
                    booking = booking,
                    propertyName = propertyName,
                    locale = locale,
                    onClick = { onBookingClick(booking.id) },
                )
            }
        }
    }
}

@Composable
private fun BookingCard(
    booking: BookingEntity,
    propertyName: String,
    locale: java.util.Locale,
    onClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = booking.guestName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = formatSenAsRinggit(
                        booking.totalAmountSen,
                        locale,
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Text(
                text = propertyName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = stringResource(
                    R.string.booking_date_range,
                    formatEpochDay(
                        booking.checkInEpochDay,
                        locale,
                    ),
                    formatEpochDay(
                        booking.checkOutEpochDay,
                        locale,
                    ),
                ),
                style = MaterialTheme.typography.bodyMedium,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(stringResource(booking.source.labelRes()))
                    },
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text(stringResource(booking.status.labelRes()))
                    },
                )
            }
        }
    }
}
