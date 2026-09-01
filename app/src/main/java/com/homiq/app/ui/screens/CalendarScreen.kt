package com.homiq.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.data.local.entity.BlockedDateEntity
import com.homiq.app.data.local.entity.BookingEntity
import com.homiq.app.data.preferences.CalendarPreferences
import com.homiq.app.data.model.BookingStatus
import com.homiq.app.domain.CalendarRules
import com.homiq.app.ui.components.AvailabilityLegend
import com.homiq.app.ui.components.CalendarCheckInBlue
import com.homiq.app.ui.components.CalendarCheckOutRed
import com.homiq.app.ui.components.CalendarMarkerDot
import com.homiq.app.ui.components.EmptyStateCard
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.components.SelectionField
import com.homiq.app.ui.util.formatEpochDay
import com.homiq.app.ui.util.labelRes
import com.homiq.app.ui.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private data class CalendarPropertyOption(
    val id: String?,
    val name: String,
)

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onBookingClick: (String) -> Unit,
    onNewBooking: (
        checkInEpochDay: Long,
        propertyId: String?,
    ) -> Unit,
    onBlockDate: (
        startEpochDay: Long,
        propertyId: String?,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    val context = LocalContext.current.applicationContext
    val calendarPreferences = remember(context) {
        CalendarPreferences(context)
    }
    val today = remember { LocalDate.now() }
    var selectedPropertyId by remember {
        mutableStateOf(calendarPreferences.selectedPropertyId)
    }
    var selectedDay by remember { mutableLongStateOf(today.toEpochDay()) }

    LaunchedEffect(state.month) {
        if (YearMonth.from(LocalDate.ofEpochDay(selectedDay)) != state.month) {
            selectedDay = state.month.atDay(1).toEpochDay()
        }
    }

    LaunchedEffect(state.properties, selectedPropertyId) {
        val savedPropertyId = selectedPropertyId
        if (
            savedPropertyId != null &&
            state.properties.isNotEmpty() &&
            state.properties.none { !it.isDeleted && it.id == savedPropertyId }
        ) {
            selectedPropertyId = null
            calendarPreferences.setSelectedProperty(null)
        }
    }

    val propertyOptions = remember(state.properties, locale) {
        buildList {
            add(CalendarPropertyOption(id = null, name = ""))
            state.properties
                .filter { !it.isDeleted }
                .forEach {
                    add(CalendarPropertyOption(id = it.id, name = it.name))
                }
        }
    }
    val filteredBookings = remember(state.bookings, selectedPropertyId) {
        state.bookings.filter {
            !it.isDeleted &&
                it.status != BookingStatus.CANCELLED &&
                (selectedPropertyId == null || it.propertyId == selectedPropertyId)
        }
    }
    val filteredBlocks = remember(state.blockedDates, selectedPropertyId) {
        state.blockedDates.filter {
            !it.isDeleted &&
                (selectedPropertyId == null || it.propertyId == selectedPropertyId)
        }
    }
    val selectedBookings = remember(filteredBookings, selectedDay) {
        filteredBookings.filter {
            selectedDay >= it.checkInEpochDay &&
                selectedDay <= it.checkOutEpochDay
        }
    }
    val selectedBlocks = remember(filteredBlocks, selectedDay) {
        filteredBlocks.filter {
            CalendarRules.containsDay(
                startEpochDay = it.startEpochDay,
                endEpochDayExclusive = it.endEpochDay,
                dayEpoch = selectedDay,
            )
        }
    }
    val selectedDayUnavailable = remember(
        filteredBookings,
        filteredBlocks,
        selectedDay,
        selectedPropertyId,
    ) {
        selectedPropertyId != null && (
            filteredBookings.any {
                selectedDay >= it.checkInEpochDay && selectedDay < it.checkOutEpochDay
            } || filteredBlocks.any {
                CalendarRules.containsDay(
                    startEpochDay = it.startEpochDay,
                    endEpochDayExclusive = it.endEpochDay,
                    dayEpoch = selectedDay,
                )
            }
        )
    }
    val propertyNames = remember(state.properties) {
        state.properties.associate { it.id to it.name }
    }
    val monthTitle = remember(state.month, locale) {
        state.month.format(
            DateTimeFormatter.ofPattern("MMMM yyyy", locale),
        )
    }.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(locale) else it.toString()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 10.dp,
            end = 16.dp,
            bottom = 80.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            ScreenHeader(
                title = stringResource(R.string.calendar_title),
                subtitle = stringResource(R.string.calendar_live_subtitle),
                compact = true,
            )
        }
        item {
            val selectedOption = propertyOptions.firstOrNull {
                it.id == selectedPropertyId
            } ?: propertyOptions.first()
            SelectionField(
                label = stringResource(R.string.property_filter),
                selectedText = if (selectedOption.id == null) {
                    stringResource(R.string.all_properties)
                } else {
                    selectedOption.name
                },
                options = propertyOptions,
                optionText = {
                    if (it.id == null) stringResource(R.string.all_properties) else it.name
                },
                onSelected = {
                    selectedPropertyId = it.id
                    calendarPreferences.setSelectedProperty(it.id)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = viewModel::previousMonth,
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChevronLeft,
                                contentDescription = stringResource(R.string.previous_month),
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Text(
                            text = monthTitle,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(
                            onClick = viewModel::nextMonth,
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = stringResource(R.string.next_month),
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    MonthGrid(
                        month = state.month,
                        today = today,
                        selectedDay = selectedDay,
                        bookings = filteredBookings,
                        blocks = filteredBlocks,
                        onDayClick = { selectedDay = it },
                    )
                    TextButton(
                        onClick = {
                            selectedDay = today.toEpochDay()
                            viewModel.goToToday()
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    ) {
                        Text(
                            stringResource(R.string.go_to_today),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
        item { AvailabilityLegend() }

        item {
            Text(
                text = stringResource(
                    R.string.selected_date_title,
                    formatEpochDay(selectedDay, locale),
                ),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        if (selectedBookings.isEmpty() && selectedBlocks.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.date_available),
                    body = stringResource(R.string.date_available_body),
                    icon = Icons.Outlined.Event,
                    compact = true,
                )
            }
        } else {
            items(
                items = selectedBookings,
                key = { "booking-${it.id}" },
            ) { booking ->
                CalendarBookingRow(
                    booking = booking,
                    propertyName = propertyNames[booking.propertyId].orEmpty(),
                    onClick = { onBookingClick(booking.id) },
                )
            }

            items(
                items = selectedBlocks,
                key = { "block-${it.id}" },
            ) { block ->
                CalendarBlockRow(
                    block = block,
                    propertyName = propertyNames[block.propertyId].orEmpty(),
                    onUnblock = { viewModel.deleteBlock(block.id) },
                )
            }
        }

        item {
            CalendarSelectedDateActions(
                canCreate = !selectedDayUnavailable,
                onBook = {
                    onNewBooking(selectedDay, selectedPropertyId)
                },
                onBlock = {
                    onBlockDate(selectedDay, selectedPropertyId)
                },
            )
        }
    }
}

@Composable
private fun CalendarSelectedDateActions(
    canCreate: Boolean,
    onBook: () -> Unit,
    onBlock: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onBook,
            modifier = Modifier.weight(1f),
            enabled = canCreate,
            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 8.dp,
            ),
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(R.string.book_from_date),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 5.dp),
            )
        }

        OutlinedButton(
            onClick = onBlock,
            modifier = Modifier.weight(1f),
            enabled = canCreate,
            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 8.dp,
            ),
        ) {
            Icon(
                imageVector = Icons.Outlined.Block,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(R.string.block_from_date),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 5.dp),
            )
        }
    }
}

@Composable
private fun MonthGrid(
    month: YearMonth,
    today: LocalDate,
    selectedDay: Long,
    bookings: List<BookingEntity>,
    blocks: List<BlockedDateEntity>,
    onDayClick: (Long) -> Unit,
) {
    val weekdays = stringArrayResource(R.array.weekdays_short)
    val firstDayOffset = month.atDay(1).dayOfWeek.value % 7
    val cells = remember(month) {
        val usedCells = firstDayOffset + month.lengthOfMonth()
        val weekCount = (usedCells + 6) / 7
        List(weekCount * 7) { index ->
            val day = index - firstDayOffset + 1
            if (day in 1..month.lengthOfMonth()) month.atDay(day) else null
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    if (date == null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                        )
                    } else {
                        val epoch = date.toEpochDay()
                        val hasCheckIn = bookings.any {
                            it.checkInEpochDay == epoch
                        }
                        val hasCheckOut = bookings.any {
                            it.checkOutEpochDay == epoch
                        }
                        val hasStay = bookings.any {
                            epoch >= it.checkInEpochDay && epoch < it.checkOutEpochDay
                        }
                        val hasBlock = blocks.any {
                            CalendarRules.containsDay(
                                it.startEpochDay,
                                it.endEpochDay,
                                epoch,
                            )
                        }

                        DayCell(
                            day = date.dayOfMonth,
                            isToday = date == today,
                            isSelected = epoch == selectedDay,
                            hasCheckIn = hasCheckIn,
                            hasCheckOut = hasCheckOut,
                            hasStay = hasStay,
                            hasBlock = hasBlock,
                            onClick = { onDayClick(epoch) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    hasCheckIn: Boolean,
    hasCheckOut: Boolean,
    hasStay: Boolean,
    hasBlock: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        hasBlock -> MaterialTheme.colorScheme.surfaceVariant
        hasStay -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.58f)
        else -> MaterialTheme.colorScheme.surface
    }
    val border = if (isToday) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    } else {
        null
    }

    Surface(
        modifier = modifier
            .height(36.dp)
            .padding(1.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = container,
        border = border,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isToday || isSelected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
            )

            if (hasCheckIn || hasCheckOut) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    if (hasCheckOut) CalendarMarkerDot(CalendarCheckOutRed)
                    if (hasCheckIn) CalendarMarkerDot(CalendarCheckInBlue)
                }
            }
        }
    }
}

@Composable
private fun CalendarBookingRow(
    booking: BookingEntity,
    propertyName: String,
    onClick: () -> Unit,
) {
    val statusColor = when (booking.status) {
        BookingStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
        BookingStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer
        BookingStatus.CHECKED_IN -> MaterialTheme.colorScheme.secondaryContainer
        BookingStatus.CHECKED_OUT -> MaterialTheme.colorScheme.surfaceVariant
        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(
        shape = MaterialTheme.shapes.large,
        color = statusColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.HomeWork,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                Text(
                    text = booking.guestName,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = propertyName,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                text = stringResource(booking.status.labelRes()),
                style = MaterialTheme.typography.labelSmall,
            )
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CalendarBlockRow(
    block: BlockedDateEntity,
    propertyName: String,
    onUnblock: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Block,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                Text(
                    text = stringResource(R.string.blocked),
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = buildString {
                        append(propertyName)
                        block.reason?.let {
                            append(" · ")
                            append(it)
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            TextButton(
                onClick = onUnblock,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = stringResource(R.string.unblock),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

