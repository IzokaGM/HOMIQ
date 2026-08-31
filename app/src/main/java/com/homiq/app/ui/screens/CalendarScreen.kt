package com.homiq.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.data.local.entity.BlockedDateEntity
import com.homiq.app.data.local.entity.BookingEntity
import com.homiq.app.data.local.entity.PropertyEntity
import com.homiq.app.data.model.BookingStatus
import com.homiq.app.domain.CalendarRules
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
    val today = remember { LocalDate.now() }

    var selectedPropertyId by remember {
        mutableStateOf<String?>(null)
    }
    var selectedDay by remember {
        mutableLongStateOf(today.toEpochDay())
    }

    LaunchedEffect(state.month) {
        if (
            YearMonth.from(
                LocalDate.ofEpochDay(selectedDay),
            ) != state.month
        ) {
            selectedDay = state.month.atDay(1).toEpochDay()
        }
    }

    val propertyOptions = remember(
        state.properties,
        locale,
    ) {
        buildList {
            add(
                CalendarPropertyOption(
                    id = null,
                    name = "",
                ),
            )
            state.properties
                .filter { !it.isDeleted }
                .forEach {
                    add(
                        CalendarPropertyOption(
                            id = it.id,
                            name = it.name,
                        ),
                    )
                }
        }
    }

    val filteredBookings = remember(
        state.bookings,
        selectedPropertyId,
    ) {
        state.bookings.filter {
            selectedPropertyId == null ||
                it.propertyId == selectedPropertyId
        }
    }

    val filteredBlocks = remember(
        state.blockedDates,
        selectedPropertyId,
    ) {
        state.blockedDates.filter {
            selectedPropertyId == null ||
                it.propertyId == selectedPropertyId
        }
    }

    val selectedBookings = remember(
        filteredBookings,
        selectedDay,
    ) {
        filteredBookings.filter {
            CalendarRules.containsDay(
                startEpochDay = it.checkInEpochDay,
                endEpochDayExclusive = it.checkOutEpochDay,
                dayEpoch = selectedDay,
            )
        }
    }

    val selectedBlocks = remember(
        filteredBlocks,
        selectedDay,
    ) {
        filteredBlocks.filter {
            CalendarRules.containsDay(
                startEpochDay = it.startEpochDay,
                endEpochDayExclusive = it.endEpochDay,
                dayEpoch = selectedDay,
            )
        }
    }

    val propertyNames = remember(state.properties) {
        state.properties.associate {
            it.id to it.name
        }
    }

    val monthTitle = remember(state.month, locale) {
        state.month.format(
            DateTimeFormatter.ofPattern(
                "MMMM yyyy",
                locale,
            ),
        )
    }.replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(locale)
        } else {
            it.toString()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 18.dp,
            end = 16.dp,
            bottom = 116.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = stringResource(R.string.calendar_title),
                subtitle = stringResource(
                    R.string.calendar_live_subtitle,
                ),
            )
        }

        item {
            val selectedOption = propertyOptions
                .firstOrNull {
                    it.id == selectedPropertyId
                }
                ?: propertyOptions.first()

            SelectionField(
                label = stringResource(R.string.property_filter),
                selectedText = if (selectedOption.id == null) {
                    stringResource(R.string.all_properties)
                } else {
                    selectedOption.name
                },
                options = propertyOptions,
                optionText = {
                    if (it.id == null) {
                        stringResource(R.string.all_properties)
                    } else {
                        it.name
                    }
                },
                onSelected = {
                    selectedPropertyId = it.id
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
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = viewModel::previousMonth,
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Outlined.ChevronLeft,
                                contentDescription = stringResource(
                                    R.string.previous_month,
                                ),
                            )
                        }

                        Text(
                            text = monthTitle,
                            style =
                                MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f),
                        )

                        IconButton(
                            onClick = viewModel::nextMonth,
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Outlined.ChevronRight,
                                contentDescription = stringResource(
                                    R.string.next_month,
                                ),
                            )
                        }
                    }

                    MonthGrid(
                        month = state.month,
                        today = today,
                        selectedDay = selectedDay,
                        bookings = filteredBookings,
                        blocks = filteredBlocks,
                        onDayClick = {
                            selectedDay = it
                        },
                    )

                    OutlinedButton(
                        onClick = viewModel::goToToday,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.go_to_today))
                    }
                }
            }
        }

        item {
            CalendarLegend()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = {
                        onNewBooking(
                            selectedDay,
                            selectedPropertyId,
                        )
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(
                            R.string.book_from_date,
                        ),
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }

                OutlinedButton(
                    onClick = {
                        onBlockDate(
                            selectedDay,
                            selectedPropertyId,
                        )
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Block,
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(
                            R.string.block_from_date,
                        ),
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(
                    R.string.selected_date_title,
                    formatEpochDay(
                        selectedDay,
                        locale,
                    ),
                ),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if (
            selectedBookings.isEmpty() &&
            selectedBlocks.isEmpty()
        ) {
            item {
                EmptyStateCard(
                    title = stringResource(
                        R.string.date_available,
                    ),
                    body = stringResource(
                        R.string.date_available_body,
                    ),
                    icon = Icons.Outlined.Event,
                )
            }
        } else {
            items(
                items = selectedBookings,
                key = { "booking-${it.id}" },
            ) { booking ->
                CalendarBookingRow(
                    booking = booking,
                    propertyName = propertyNames[
                        booking.propertyId
                    ].orEmpty(),
                    onClick = {
                        onBookingClick(booking.id)
                    },
                )
            }

            items(
                items = selectedBlocks,
                key = { "block-${it.id}" },
            ) { block ->
                CalendarBlockRow(
                    block = block,
                    propertyName = propertyNames[
                        block.propertyId
                    ].orEmpty(),
                )
            }
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
        List(42) { index ->
            val day = index - firstDayOffset + 1
            if (day in 1..month.lengthOfMonth()) {
                month.atDay(day)
            } else {
                null
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant,
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
                                .aspectRatio(0.9f),
                        )
                    } else {
                        val epoch = date.toEpochDay()
                        val isToday = date == today
                        val isSelected = epoch == selectedDay
                        val hasBooking = bookings.any {
                            CalendarRules.containsDay(
                                it.checkInEpochDay,
                                it.checkOutEpochDay,
                                epoch,
                            )
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
                            isToday = isToday,
                            isSelected = isSelected,
                            hasBooking = hasBooking,
                            hasBlock = hasBlock,
                            onClick = {
                                onDayClick(epoch)
                            },
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
    hasBooking: Boolean,
    hasBlock: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = when {
        isSelected ->
            MaterialTheme.colorScheme.primaryContainer
        hasBlock ->
            MaterialTheme.colorScheme.surfaceVariant
        hasBooking ->
            MaterialTheme.colorScheme.secondaryContainer
        else ->
            MaterialTheme.colorScheme.surface
    }

    val border = when {
        isToday ->
            BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary,
            )
        else -> null
    }

    Surface(
        modifier = modifier
            .aspectRatio(0.9f)
            .padding(2.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = container,
        border = border,
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
            )

            if (hasBooking || hasBlock) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        3.dp,
                    ),
                ) {
                    if (hasBooking) {
                        EventDot(
                            booked = true,
                        )
                    }
                    if (hasBlock) {
                        EventDot(
                            booked = false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventDot(
    booked: Boolean,
) {
    Surface(
        modifier = Modifier.size(5.dp),
        shape = CircleShape,
        color = if (booked) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
    ) {}
}

@Composable
private fun CalendarLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        LegendItem(
            booked = true,
            label = stringResource(R.string.legend_booked),
        )
        LegendItem(
            booked = false,
            label = stringResource(R.string.legend_blocked),
        )
    }
}

@Composable
private fun LegendItem(
    booked: Boolean,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        EventDot(booked = booked)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CalendarBookingRow(
    booking: BookingEntity,
    propertyName: String,
    onClick: () -> Unit,
) {
    val statusColor = when (booking.status) {
        BookingStatus.PENDING ->
            MaterialTheme.colorScheme.tertiaryContainer
        BookingStatus.CONFIRMED ->
            MaterialTheme.colorScheme.primaryContainer
        BookingStatus.CHECKED_IN ->
            MaterialTheme.colorScheme.secondaryContainer
        BookingStatus.CHECKED_OUT ->
            MaterialTheme.colorScheme.surfaceVariant
        BookingStatus.CANCELLED ->
            MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = statusColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.HomeWork,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = booking.guestName,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = propertyName,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Text(
                text = stringResource(
                    booking.status.labelRes(),
                ),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun CalendarBlockRow(
    block: BlockedDateEntity,
    propertyName: String,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Block,
                contentDescription = null,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = stringResource(R.string.blocked),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = buildString {
                        append(propertyName)
                        block.reason?.let {
                            append(" · ")
                            append(it)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
