package com.homiq.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.data.model.BookingSource
import com.homiq.app.data.model.BookingStatus
import com.homiq.app.domain.BookingDraft
import com.homiq.app.domain.BookingSaveResult
import com.homiq.app.ui.components.DateField
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.components.SelectionField
import com.homiq.app.ui.util.formatSenForInput
import com.homiq.app.ui.util.labelRes
import com.homiq.app.ui.util.messageRes
import com.homiq.app.ui.util.parseRinggitToSen
import com.homiq.app.ui.viewmodel.BookingViewModel
import java.time.LocalDate
import kotlinx.coroutines.launch

@Composable
fun BookingFormScreen(
    bookingId: String?,
    viewModel: BookingViewModel,
    onSaved: (String) -> Unit,
    onNeedProperty: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bookings by viewModel.bookingList.collectAsStateWithLifecycle()
    val properties by viewModel.propertyList.collectAsStateWithLifecycle()
    val existing = bookings.firstOrNull { it.id == bookingId }

    val selectableProperties = remember(properties, existing) {
        properties.filter {
            it.isActive || it.id == existing?.propertyId
        }
    }

    var propertyId by remember(bookingId) { mutableStateOf("") }
    var guestName by remember(bookingId) { mutableStateOf("") }
    var guestPhone by remember(bookingId) { mutableStateOf("") }
    var checkIn by remember(bookingId) {
        mutableLongStateOf(LocalDate.now().toEpochDay())
    }
    var checkOut by remember(bookingId) {
        mutableLongStateOf(
            LocalDate.now().plusDays(1).toEpochDay(),
        )
    }
    var source by remember(bookingId) {
        mutableStateOf(BookingSource.WHATSAPP)
    }
    var totalAmount by remember(bookingId) { mutableStateOf("") }
    var notes by remember(bookingId) { mutableStateOf("") }
    var initialized by remember(bookingId) { mutableStateOf(false) }
    var errorMessage by remember(bookingId) {
        mutableStateOf<Int?>(null)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(existing, selectableProperties) {
        if (!initialized && existing != null) {
            propertyId = existing.propertyId
            guestName = existing.guestName
            guestPhone = existing.guestPhone.orEmpty()
            checkIn = existing.checkInEpochDay
            checkOut = existing.checkOutEpochDay
            source = existing.source
            totalAmount = formatSenForInput(
                existing.totalAmountSen,
            )
            notes = existing.notes.orEmpty()
            initialized = true
        } else if (
            !initialized &&
            bookingId == null &&
            propertyId.isBlank() &&
            selectableProperties.size == 1
        ) {
            propertyId = selectableProperties.first().id
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 24.dp,
            end = 20.dp,
            bottom = 40.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = stringResource(
                    if (bookingId == null) {
                        R.string.new_booking
                    } else {
                        R.string.edit_booking
                    },
                ),
                subtitle = stringResource(
                    R.string.booking_form_subtitle,
                ),
            )
        }

        if (selectableProperties.isEmpty()) {
            item {
                Text(
                    text = stringResource(
                        R.string.booking_needs_property,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            item {
                Button(
                    onClick = onNeedProperty,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.add_property))
                }
            }
        } else {
            item {
                val selectedName = selectableProperties
                    .firstOrNull { it.id == propertyId }
                    ?.name
                    .orEmpty()

                SelectionField(
                    label = stringResource(R.string.property),
                    selectedText = selectedName,
                    options = selectableProperties,
                    optionText = { it.name },
                    onSelected = {
                        propertyId = it.id
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    value = guestName,
                    onValueChange = {
                        guestName = it
                        errorMessage = null
                    },
                    label = {
                        Text(stringResource(R.string.guest_name))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    value = guestPhone,
                    onValueChange = { guestPhone = it },
                    label = {
                        Text(stringResource(R.string.guest_phone))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                DateField(
                    label = stringResource(R.string.check_in),
                    epochDay = checkIn,
                    onDateSelected = {
                        checkIn = it
                        if (checkOut <= checkIn) {
                            checkOut = checkIn + 1
                        }
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                DateField(
                    label = stringResource(R.string.check_out),
                    epochDay = checkOut,
                    onDateSelected = {
                        checkOut = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                SelectionField(
                    label = stringResource(R.string.booking_source),
                    selectedText = stringResource(source.labelRes()),
                    options = BookingSource.entries,
                    optionText = {
                        stringResource(it.labelRes())
                    },
                    onSelected = {
                        source = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    value = totalAmount,
                    onValueChange = {
                        totalAmount = it.filter { char ->
                            char.isDigit() || char == '.'
                        }
                        errorMessage = null
                    },
                    label = {
                        Text(stringResource(R.string.total_booking))
                    },
                    prefix = { Text("RM ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.notes)) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            errorMessage?.let { message ->
                item {
                    Text(
                        text = stringResource(message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        val amountSen =
                            parseRinggitToSen(totalAmount)

                        if (amountSen == null) {
                            errorMessage =
                                R.string.error_invalid_amount
                            return@Button
                        }

                        scope.launch {
                            when (
                                val result = viewModel.save(
                                    BookingDraft(
                                        id = bookingId,
                                        propertyId = propertyId,
                                        guestName = guestName,
                                        guestPhone = guestPhone,
                                        checkInEpochDay = checkIn,
                                        checkOutEpochDay = checkOut,
                                        source = source,
                                        totalAmountSen = amountSen,
                                        status = existing?.status
                                            ?: BookingStatus.CONFIRMED,
                                        notes = notes,
                                    ),
                                )
                            ) {
                                is BookingSaveResult.Success ->
                                    onSaved(result.bookingId)

                                is BookingSaveResult.Failure ->
                                    errorMessage =
                                        result.issue.messageRes()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.save_booking))
                }
            }
        }
    }
}
