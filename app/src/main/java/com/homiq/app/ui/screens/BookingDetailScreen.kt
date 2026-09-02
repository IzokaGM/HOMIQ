package com.homiq.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.data.local.entity.PaymentEntity
import com.homiq.app.data.model.BookingStatus
import com.homiq.app.data.model.DepositStatus
import com.homiq.app.domain.DepositRules
import com.homiq.app.domain.PaymentRules
import com.homiq.app.ui.components.InfoCard
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.util.formatEpochDay
import com.homiq.app.ui.util.formatSenAsRinggit
import com.homiq.app.ui.util.labelRes
import com.homiq.app.ui.util.nightsBetween
import com.homiq.app.ui.viewmodel.BookingViewModel
import com.homiq.app.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bookingId: String,
    viewModel: BookingViewModel,
    financeViewModel: FinanceViewModel,
    onEdit: () -> Unit,
    onCancelled: () -> Unit,
    onRecordPayment: () -> Unit,
    onManageDeposit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bookings by viewModel.bookingList.collectAsStateWithLifecycle()
    val properties by viewModel.propertyList.collectAsStateWithLifecycle()
    val booking = bookings.firstOrNull { it.id == bookingId }

    val paymentFlow = remember(bookingId) {
        financeViewModel.paymentsFor(bookingId)
    }
    val payments by paymentFlow.collectAsStateWithLifecycle(
        initialValue = emptyList(),
    )

    val totalPaidFlow = remember(bookingId) {
        financeViewModel.totalPaidFor(bookingId)
    }
    val totalPaid by totalPaidFlow.collectAsStateWithLifecycle(
        initialValue = 0L,
    )

    val depositFlow = remember(bookingId) {
        financeViewModel.depositFor(bookingId)
    }
    val deposit by depositFlow.collectAsStateWithLifecycle(
        initialValue = null,
    )

    val locale = LocalConfiguration.current.locales[0]
    val scope = rememberCoroutineScope()
    var showCancelDialog by remember { mutableStateOf(false) }
    var actionPayment by remember { mutableStateOf<PaymentEntity?>(null) }
    var deletePayment by remember { mutableStateOf<PaymentEntity?>(null) }

    if (booking == null) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
        ) {
            item {
                ScreenHeader(
                    title = stringResource(R.string.booking_not_found),
                    subtitle = stringResource(
                        R.string.booking_not_found_body,
                    ),
                )
            }
        }
        return
    }

    val propertyName = properties
        .firstOrNull { it.id == booking.propertyId }
        ?.name
        ?: stringResource(R.string.unknown_property)

    val outstanding = PaymentRules.outstandingSen(
        bookingTotalSen = booking.totalAmountSen,
        totalPaidSen = totalPaid,
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 18.dp,
            end = 16.dp,
            bottom = 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            ScreenHeader(
                eyebrow = stringResource(booking.status.labelRes()),
                title = booking.guestName,
                subtitle = propertyName,
            )
        }

        item {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
            ) {
                androidx.compose.foundation.layout.Column {
                    DetailRow(
                        label = stringResource(R.string.check_in),
                        value = formatEpochDay(
                            booking.checkInEpochDay,
                            locale,
                        ),
                    )
                    HorizontalDivider()
                    DetailRow(
                        label = stringResource(R.string.check_out),
                        value = formatEpochDay(
                            booking.checkOutEpochDay,
                            locale,
                        ),
                    )
                    HorizontalDivider()
                    DetailRow(
                        label = stringResource(R.string.nights),
                        value = nightsBetween(
                            booking.checkInEpochDay,
                            booking.checkOutEpochDay,
                        ).toString(),
                    )
                    HorizontalDivider()
                    DetailRow(
                        label = stringResource(R.string.total_booking),
                        value = formatSenAsRinggit(
                            booking.totalAmountSen,
                            locale,
                        ),
                    )
                    HorizontalDivider()
                    DetailRow(
                        label = stringResource(R.string.booking_source),
                        value = stringResource(
                            booking.source.labelRes(),
                        ),
                    )
                    booking.guestPhone?.let { phone ->
                        HorizontalDivider()
                        DetailRow(
                            label = stringResource(
                                R.string.guest_phone,
                            ),
                            value = phone,
                        )
                    }
                    booking.notes?.let { notes ->
                        HorizontalDivider()
                        DetailRow(
                            label = stringResource(R.string.notes),
                            value = notes,
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.payment_summary),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        item {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
            ) {
                androidx.compose.foundation.layout.Column {
                    DetailRow(
                        label = stringResource(R.string.total_booking),
                        value = formatSenAsRinggit(
                            booking.totalAmountSen,
                            locale,
                        ),
                    )
                    HorizontalDivider()
                    DetailRow(
                        label = stringResource(R.string.total_paid),
                        value = formatSenAsRinggit(
                            totalPaid,
                            locale,
                        ),
                    )
                    HorizontalDivider()
                    DetailRow(
                        label = stringResource(R.string.outstanding),
                        value = formatSenAsRinggit(
                            outstanding,
                            locale,
                        ),
                    )
                }
            }
        }

        if (
            outstanding > 0L &&
            booking.status != BookingStatus.CANCELLED
        ) {
            item {
                Button(
                    onClick = onRecordPayment,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Payments,
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(R.string.record_payment),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }

        if (payments.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.payment_history),
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            items(
                items = payments.reversed(),
                key = { it.id },
            ) { payment ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { actionPayment = payment },
                        ),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text = formatSenAsRinggit(
                                payment.amountSen,
                                locale,
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(
                                payment.method.labelRes(),
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = formatEpochDay(
                                payment.paymentDateEpochDay,
                                locale,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        payment.notes?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.security_deposit),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        item {
            val currentDeposit = deposit
            if (
                currentDeposit == null ||
                currentDeposit.status == DepositStatus.NOT_REQUIRED
            ) {
                InfoCard(
                    title = stringResource(
                        R.string.deposit_not_required,
                    ),
                    body = stringResource(
                        R.string.deposit_not_required_body,
                    ),
                )
            } else {
                val remaining = DepositRules.remainingSen(
                    depositAmountSen = currentDeposit.amountSen,
                    returnedAmountSen =
                        currentDeposit.returnedAmountSen,
                )
                InfoCard(
                    title = stringResource(
                        currentDeposit.status.labelRes(),
                    ),
                    body = stringResource(
                        R.string.deposit_summary_body,
                        formatSenAsRinggit(
                            currentDeposit.amountSen,
                            locale,
                        ),
                        formatSenAsRinggit(
                            remaining,
                            locale,
                        ),
                    ),
                )
            }
        }

        if (booking.status != BookingStatus.CANCELLED) {
            item {
                OutlinedButton(
                    onClick = onManageDeposit,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(
                            R.string.manage_deposit,
                        ),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }

        if (booking.status != BookingStatus.CANCELLED) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                        )
                        Text(
                            text = stringResource(R.string.edit_booking),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }

                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = null,
                        )
                        Text(
                            text = stringResource(
                                R.string.cancel_booking,
                            ),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        }
    }

    val selectedActionPayment = actionPayment
    if (selectedActionPayment != null) {
        ModalBottomSheet(onDismissRequest = { actionPayment = null }) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = formatSenAsRinggit(selectedActionPayment.amountSen, locale),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                )
                androidx.compose.material3.ListItem(
                    modifier = Modifier.clickable {
                        actionPayment = null
                        deletePayment = selectedActionPayment
                    },
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.list_payment_action_delete),
                            color = MaterialTheme.colorScheme.error,
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    },
                )
            }
        }
    }

    val selectedDeletePayment = deletePayment
    if (selectedDeletePayment != null) {
        AlertDialog(
            onDismissRequest = { deletePayment = null },
            title = { Text(stringResource(R.string.list_payment_delete_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.list_payment_delete_message,
                        formatSenAsRinggit(selectedDeletePayment.amountSen, locale),
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        financeViewModel.deletePayment(selectedDeletePayment.id)
                        deletePayment = null
                    },
                ) {
                    Text(
                        text = stringResource(R.string.list_payment_action_delete),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { deletePayment = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(stringResource(R.string.cancel_booking))
            },
            text = {
                Text(
                    stringResource(
                        R.string.cancel_booking_confirmation,
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        scope.launch {
                            if (viewModel.cancel(bookingId)) {
                                onCancelled()
                            }
                        }
                    },
                ) {
                    Text(stringResource(R.string.confirm_cancel))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelDialog = false },
                ) {
                    Text(stringResource(R.string.keep_booking))
                }
            },
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
