package com.homiq.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.data.local.entity.BookingEntity
import com.homiq.app.ui.components.EmptyStateCard
import com.homiq.app.ui.components.HomikaBrandMark
import com.homiq.app.ui.components.SectionHeader
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
    val state by viewModel.state.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]
    val analytics = state.analytics

    val revenue = formatSenAsRinggit(analytics?.revenueSen ?: 0L, locale)
    val expenses = formatSenAsRinggit(analytics?.expensesSen ?: 0L, locale)
    val net = formatSenAsRinggit(analytics?.netIncomeSen ?: 0L, locale)
    val occupancy = formatPercent(analytics?.occupancyPercent ?: 0.0, locale)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 18.dp,
            end = 16.dp,
            bottom = 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            HomeTopHeader()
        }

        item {
            PerformanceCard(
                netIncome = net,
                revenue = revenue,
                expenses = expenses,
                occupancy = occupancy,
                onReportsClick = onReportsClick,
            )
        }

        item {
            SectionHeader(title = stringResource(R.string.dashboard_today_operations))
        }

        if (state.checkInsToday.isEmpty() && state.checkOutsToday.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.today_empty_title),
                    body = stringResource(R.string.today_empty_body),
                    icon = Icons.Outlined.EventAvailable,
                )
            }
        } else {
            items(state.checkInsToday, key = { "in-${it.id}" }) { booking ->
                OperationRow(
                    booking = booking,
                    propertyName = state.propertyNames[booking.propertyId].orEmpty(),
                    checkIn = true,
                    onClick = { onBookingClick(booking.id) },
                )
            }
            items(state.checkOutsToday, key = { "out-${it.id}" }) { booking ->
                OperationRow(
                    booking = booking,
                    propertyName = state.propertyNames[booking.propertyId].orEmpty(),
                    checkIn = false,
                    onClick = { onBookingClick(booking.id) },
                )
            }
        }

        item {
            SectionHeader(
                title = stringResource(R.string.needs_attention),
                action = if (state.outstandingBookings.isNotEmpty()) {
                    stringResource(R.string.dashboard_attention_count, state.outstandingBookings.size)
                } else null,
            )
        }

        if (state.outstandingBookings.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.no_outstanding_attention),
                    body = stringResource(R.string.no_outstanding_attention_body),
                    icon = Icons.Outlined.Payments,
                )
            }
        } else {
            items(state.outstandingBookings, key = { "balance-${it.booking.id}" }) { item ->
                OutstandingRow(
                    item = item,
                    propertyName = state.propertyNames[item.booking.propertyId].orEmpty(),
                    locale = locale,
                    onClick = { onBookingClick(item.booking.id) },
                )
            }
        }

        item {
            SectionHeader(
                title = stringResource(R.string.upcoming_bookings),
                action = if (state.upcomingBookings.isNotEmpty()) {
                    stringResource(R.string.dashboard_upcoming_count, state.upcomingBookings.size)
                } else null,
            )
        }

        if (state.upcomingBookings.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.upcoming_empty_title),
                    body = stringResource(R.string.upcoming_empty_body),
                    icon = Icons.Outlined.NightsStay,
                )
            }
        } else {
            items(state.upcomingBookings, key = { "upcoming-${it.id}" }) { booking ->
                UpcomingBookingRow(
                    booking = booking,
                    propertyName = state.propertyNames[booking.propertyId].orEmpty(),
                    locale = locale,
                    onClick = { onBookingClick(booking.id) },
                )
            }
        }
    }
}

@Composable
private fun HomeTopHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HomikaBrandMark()
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.home_live_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PerformanceCard(
    netIncome: String,
    revenue: String,
    expenses: String,
    occupancy: String,
    onReportsClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.dashboard_month_overview),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_occupancy_short, occupancy),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = stringResource(R.string.dashboard_net_income),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                )
                Text(
                    text = netIncome,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.dashboard_cash_flow, revenue, expenses),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            TextButton(onClick = onReportsClick, contentPadding = PaddingValues(0.dp)) {
                Icon(Icons.Outlined.QueryStats, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(stringResource(R.string.view_reports), modifier = Modifier.padding(start = 7.dp))
                Icon(Icons.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 5.dp).size(17.dp))
            }
        }
    }
}

@Composable
private fun OperationRow(
    booking: BookingEntity,
    propertyName: String,
    checkIn: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceVariant) {
                Icon(
                    imageVector = if (checkIn) Icons.Outlined.Login else Icons.Outlined.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(9.dp).size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = booking.guestName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = propertyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = stringResource(if (checkIn) R.string.check_in_today else R.string.check_out_today),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
            )
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
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(item.booking.guestName, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(propertyName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(
                text = formatSenAsRinggit(item.outstandingSen, locale),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                maxLines = 1,
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
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceVariant) {
                Icon(
                    Icons.Outlined.NightsStay,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(9.dp).size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(booking.guestName, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(propertyName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(
                text = formatEpochDay(booking.checkInEpochDay, locale),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
            )
        }
    }
}
