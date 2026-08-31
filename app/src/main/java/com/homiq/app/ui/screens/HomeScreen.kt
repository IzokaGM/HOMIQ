package com.homiq.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.data.local.entity.BookingEntity
import com.homiq.app.ui.components.EmptyStateCard
import com.homiq.app.ui.components.MetricCard
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.util.formatEpochDay
import com.homiq.app.ui.util.formatPercent
import com.homiq.app.ui.util.formatSenAsRinggit
import com.homiq.app.ui.viewmodel.DashboardViewModel
import com.homiq.app.ui.viewmodel.OutstandingBooking

@Composable
fun HomeScreen(
    viewModel: DashboardViewModel,
    onBookingClick: (String) -> Unit,
    onReportsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state
        .collectAsStateWithLifecycle()
    val locale =
        LocalConfiguration.current.locales[0]
    val analytics = state.analytics

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 24.dp,
            end = 20.dp,
            bottom = 104.dp,
        ),
        verticalArrangement =
            Arrangement.spacedBy(18.dp),
    ) {
        item {
            ScreenHeader(
                eyebrow =
                    stringResource(R.string.home_eyebrow),
                title =
                    stringResource(R.string.home_title),
                subtitle =
                    stringResource(
                        R.string.home_live_subtitle,
                    ),
            )
        }

        item {
            Text(
                text =
                    stringResource(R.string.this_month),
                style =
                    MaterialTheme.typography.titleLarge,
            )
        }

        item {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),
                ) {
                    MetricCard(
                        label =
                            stringResource(
                                R.string.revenue,
                            ),
                        value =
                            formatSenAsRinggit(
                                analytics
                                    ?.revenueSen
                                    ?: 0L,
                                locale,
                            ),
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label =
                            stringResource(
                                R.string.expenses,
                            ),
                        value =
                            formatSenAsRinggit(
                                analytics
                                    ?.expensesSen
                                    ?: 0L,
                                locale,
                            ),
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),
                ) {
                    MetricCard(
                        label =
                            stringResource(
                                R.string.net_income,
                            ),
                        value =
                            formatSenAsRinggit(
                                analytics
                                    ?.netIncomeSen
                                    ?: 0L,
                                locale,
                            ),
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label =
                            stringResource(
                                R.string.occupancy,
                            ),
                        value =
                            formatPercent(
                                analytics
                                    ?.occupancyPercent
                                    ?: 0.0,
                                locale,
                            ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        item {
            Button(
                onClick = onReportsClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector =
                        Icons.Outlined.QueryStats,
                    contentDescription = null,
                )
                Text(
                    text =
                        stringResource(
                            R.string.view_reports,
                        ),
                    modifier =
                        Modifier.padding(start = 8.dp),
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.today),
                style =
                    MaterialTheme.typography.titleLarge,
            )
        }

        if (
            state.checkInsToday.isEmpty() &&
            state.checkOutsToday.isEmpty()
        ) {
            item {
                EmptyStateCard(
                    title =
                        stringResource(
                            R.string.today_empty_title,
                        ),
                    body =
                        stringResource(
                            R.string.today_empty_body,
                        ),
                    icon =
                        Icons.Outlined.EventAvailable,
                )
            }
        } else {
            items(
                items = state.checkInsToday,
                key = { "in-${it.id}" },
            ) { booking ->
                TodayBookingRow(
                    booking = booking,
                    propertyName =
                        state.propertyNames[
                            booking.propertyId
                        ].orEmpty(),
                    isCheckIn = true,
                    onClick = {
                        onBookingClick(booking.id)
                    },
                )
            }

            items(
                items = state.checkOutsToday,
                key = { "out-${it.id}" },
            ) { booking ->
                TodayBookingRow(
                    booking = booking,
                    propertyName =
                        state.propertyNames[
                            booking.propertyId
                        ].orEmpty(),
                    isCheckIn = false,
                    onClick = {
                        onBookingClick(booking.id)
                    },
                )
            }
        }

        item {
            Text(
                text =
                    stringResource(
                        R.string.needs_attention,
                    ),
                style =
                    MaterialTheme.typography.titleLarge,
            )
        }

        if (state.outstandingBookings.isEmpty()) {
            item {
                EmptyStateCard(
                    title =
                        stringResource(
                            R.string.no_outstanding_attention,
                        ),
                    body =
                        stringResource(
                            R.string.no_outstanding_attention_body,
                        ),
                    icon = Icons.Outlined.Payments,
                )
            }
        } else {
            items(
                items = state.outstandingBookings,
                key = { "balance-${it.booking.id}" },
            ) { item ->
                OutstandingRow(
                    item = item,
                    propertyName =
                        state.propertyNames[
                            item.booking.propertyId
                        ].orEmpty(),
                    locale = locale,
                    onClick = {
                        onBookingClick(
                            item.booking.id,
                        )
                    },
                )
            }
        }

        item {
            Text(
                text =
                    stringResource(
                        R.string.upcoming_bookings,
                    ),
                style =
                    MaterialTheme.typography.titleLarge,
            )
        }

        if (state.upcomingBookings.isEmpty()) {
            item {
                EmptyStateCard(
                    title =
                        stringResource(
                            R.string.upcoming_empty_title,
                        ),
                    body =
                        stringResource(
                            R.string.upcoming_empty_body,
                        ),
                    icon = Icons.Outlined.NightsStay,
                )
            }
        } else {
            items(
                items = state.upcomingBookings,
                key = { "upcoming-${it.id}" },
            ) { booking ->
                UpcomingBookingRow(
                    booking = booking,
                    propertyName =
                        state.propertyNames[
                            booking.propertyId
                        ].orEmpty(),
                    locale = locale,
                    onClick = {
                        onBookingClick(booking.id)
                    },
                )
            }
        }
    }
}

@Composable
private fun TodayBookingRow(
    booking: BookingEntity,
    propertyName: String,
    isCheckIn: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = if (isCheckIn) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = if (isCheckIn) {
                    Icons.Outlined.Login
                } else {
                    Icons.Outlined.Logout
                },
                contentDescription = null,
            )
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = stringResource(
                        if (isCheckIn) {
                            R.string.check_in_today
                        } else {
                            R.string.check_out_today
                        },
                    ),
                    style =
                        MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = booking.guestName,
                    style =
                        MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = propertyName,
                    style =
                        MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun OutstandingRow(
    item: OutstandingBooking,
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
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = item.booking.guestName,
                    style =
                        MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = propertyName,
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                )
            }

            Text(
                text = formatSenAsRinggit(
                    item.outstandingSen,
                    locale,
                ),
                style =
                    MaterialTheme.typography.titleMedium,
                color =
                    MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun UpcomingBookingRow(
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = booking.guestName,
                style =
                    MaterialTheme.typography.titleMedium,
            )
            Text(
                text = propertyName,
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant,
            )
            Text(
                text = formatEpochDay(
                    booking.checkInEpochDay,
                    locale,
                ),
                style =
                    MaterialTheme.typography.labelLarge,
                color =
                    MaterialTheme.colorScheme.primary,
            )
        }
    }
}
