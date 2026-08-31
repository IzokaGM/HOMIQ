package com.homiq.app.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homiq.app.HomiqApplication
import com.homiq.app.R
import com.homiq.app.ui.screens.BlockDateFormScreen
import com.homiq.app.ui.screens.BookingDetailScreen
import com.homiq.app.ui.screens.BookingFormScreen
import com.homiq.app.ui.screens.BookingsScreen
import com.homiq.app.ui.screens.CalendarScreen
import com.homiq.app.ui.screens.HomeScreen
import com.homiq.app.ui.screens.PaymentFormScreen
import com.homiq.app.ui.screens.PaymentBookingPickerScreen
import com.homiq.app.ui.screens.DepositScreen
import com.homiq.app.ui.screens.MoneyScreen
import com.homiq.app.ui.screens.MoreScreen
import com.homiq.app.ui.screens.PropertiesScreen
import com.homiq.app.ui.screens.PropertyFormScreen
import com.homiq.app.ui.viewmodel.BlockedDateViewModel
import com.homiq.app.ui.viewmodel.BookingViewModel
import com.homiq.app.ui.viewmodel.CalendarViewModel
import com.homiq.app.ui.viewmodel.HomiqViewModelFactory
import com.homiq.app.ui.viewmodel.FinanceViewModel
import com.homiq.app.ui.viewmodel.PropertyViewModel
import kotlinx.coroutines.launch

private enum class HomiqDestination(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Home(R.string.nav_home, Icons.Outlined.Home),
    Calendar(R.string.nav_calendar, Icons.Outlined.CalendarMonth),
    Bookings(R.string.nav_bookings, Icons.Outlined.ListAlt),
    Money(R.string.nav_money, Icons.Outlined.AccountBalanceWallet),
    More(R.string.nav_more, Icons.Outlined.MoreHoriz),
}

private enum class HomiqRoute {
    MAIN,
    PROPERTIES,
    PROPERTY_FORM,
    BOOKING_FORM,
    BOOKING_DETAIL,
    BLOCK_DATE_FORM,
    PAYMENT_PICKER,
    PAYMENT_FORM,
    DEPOSIT,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomiqApp() {
    val application = LocalContext.current.applicationContext
        as HomiqApplication
    val factory = remember(application) {
        HomiqViewModelFactory(application.container)
    }

    val propertyViewModel: PropertyViewModel = viewModel(
        factory = factory,
    )
    val bookingViewModel: BookingViewModel = viewModel(
        factory = factory,
    )
    val calendarViewModel: CalendarViewModel = viewModel(
        factory = factory,
    )
    val blockedDateViewModel: BlockedDateViewModel = viewModel(
        factory = factory,
    )
    val financeViewModel: FinanceViewModel = viewModel(
        factory = factory,
    )

    var destinationName by rememberSaveable {
        mutableStateOf(HomiqDestination.Home.name)
    }
    var routeName by rememberSaveable {
        mutableStateOf(HomiqRoute.MAIN.name)
    }
    var routeId by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var routeEpochDay by rememberSaveable {
        mutableStateOf<Long?>(null)
    }
    var routePropertyId by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var showQuickAdd by rememberSaveable {
        mutableStateOf(false)
    }

    val selectedDestination = remember(destinationName) {
        HomiqDestination.entries.firstOrNull {
            it.name == destinationName
        } ?: HomiqDestination.Home
    }
    val route = remember(routeName) {
        HomiqRoute.entries.firstOrNull {
            it.name == routeName
        } ?: HomiqRoute.MAIN
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    val unavailableMessage = stringResource(
        R.string.feature_next_phase,
    )

    fun navigate(
        newRoute: HomiqRoute,
        id: String? = null,
        epochDay: Long? = null,
        propertyId: String? = null,
    ) {
        routeName = newRoute.name
        routeId = id
        routeEpochDay = epochDay
        routePropertyId = propertyId
    }

    fun goMain(
        destination: HomiqDestination? = null,
    ) {
        destination?.let {
            destinationName = it.name
        }
        navigate(HomiqRoute.MAIN)
    }

    BackHandler(enabled = route != HomiqRoute.MAIN) {
        when (route) {
            HomiqRoute.PROPERTY_FORM ->
                navigate(HomiqRoute.PROPERTIES)

            HomiqRoute.BOOKING_FORM -> {
                if (routeId != null) {
                    navigate(
                        HomiqRoute.BOOKING_DETAIL,
                        id = routeId,
                    )
                } else {
                    goMain(HomiqDestination.Calendar)
                }
            }

            HomiqRoute.BOOKING_DETAIL ->
                goMain(HomiqDestination.Bookings)

            HomiqRoute.BLOCK_DATE_FORM ->
                goMain(HomiqDestination.Calendar)

            HomiqRoute.PAYMENT_PICKER ->
                goMain(HomiqDestination.Bookings)

            HomiqRoute.PAYMENT_FORM ->
                navigate(
                    HomiqRoute.BOOKING_DETAIL,
                    id = routeId,
                )

            HomiqRoute.DEPOSIT ->
                navigate(
                    HomiqRoute.BOOKING_DETAIL,
                    id = routeId,
                )

            HomiqRoute.PROPERTIES ->
                goMain(HomiqDestination.More)

            HomiqRoute.MAIN -> Unit
        }
    }

    if (route == HomiqRoute.MAIN) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            bottomBar = {
                NavigationBar {
                    HomiqDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected =
                                selectedDestination == destination,
                            onClick = {
                                destinationName = destination.name
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = null,
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(
                                        destination.labelRes,
                                    ),
                                )
                            },
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showQuickAdd = true },
                    containerColor =
                        MaterialTheme.colorScheme.primary,
                    contentColor =
                        MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(
                            R.string.quick_add,
                        ),
                    )
                }
            },
        ) { innerPadding ->
            when (selectedDestination) {
                HomiqDestination.Home -> HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                )

                HomiqDestination.Calendar ->
                    CalendarScreen(
                        viewModel = calendarViewModel,
                        onBookingClick = {
                            navigate(
                                HomiqRoute.BOOKING_DETAIL,
                                id = it,
                            )
                        },
                        onNewBooking = { day, propertyId ->
                            navigate(
                                HomiqRoute.BOOKING_FORM,
                                epochDay = day,
                                propertyId = propertyId,
                            )
                        },
                        onBlockDate = { day, propertyId ->
                            navigate(
                                HomiqRoute.BLOCK_DATE_FORM,
                                epochDay = day,
                                propertyId = propertyId,
                            )
                        },
                        modifier = Modifier.padding(innerPadding),
                    )

                HomiqDestination.Bookings -> BookingsScreen(
                    viewModel = bookingViewModel,
                    onBookingClick = {
                        navigate(
                            HomiqRoute.BOOKING_DETAIL,
                            id = it,
                        )
                    },
                    modifier = Modifier.padding(innerPadding),
                )

                HomiqDestination.Money -> MoneyScreen(
                    modifier = Modifier.padding(innerPadding),
                )

                HomiqDestination.More -> MoreScreen(
                    onPropertiesClick = {
                        navigate(HomiqRoute.PROPERTIES)
                    },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    } else {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { innerPadding ->
            when (route) {
                HomiqRoute.PROPERTIES -> PropertiesScreen(
                    viewModel = propertyViewModel,
                    onAddProperty = {
                        navigate(HomiqRoute.PROPERTY_FORM)
                    },
                    onPropertyClick = {
                        navigate(
                            HomiqRoute.PROPERTY_FORM,
                            id = it,
                        )
                    },
                    modifier = Modifier.padding(innerPadding),
                )

                HomiqRoute.PROPERTY_FORM -> PropertyFormScreen(
                    propertyId = routeId,
                    viewModel = propertyViewModel,
                    onSaved = {
                        navigate(HomiqRoute.PROPERTIES)
                    },
                    modifier = Modifier.padding(innerPadding),
                )

                HomiqRoute.BOOKING_FORM -> BookingFormScreen(
                    bookingId = routeId,
                    viewModel = bookingViewModel,
                    onSaved = {
                        goMain(HomiqDestination.Calendar)
                    },
                    onNeedProperty = {
                        navigate(HomiqRoute.PROPERTY_FORM)
                    },
                    modifier = Modifier.padding(innerPadding),
                    initialCheckInEpochDay = routeEpochDay,
                    initialPropertyId = routePropertyId,
                )

                HomiqRoute.BOOKING_DETAIL -> {
                    val id = routeId
                    if (id != null) {
                        BookingDetailScreen(
                            bookingId = id,
                            viewModel = bookingViewModel,
                            financeViewModel = financeViewModel,
                            onEdit = {
                                navigate(
                                    HomiqRoute.BOOKING_FORM,
                                    id = id,
                                )
                            },
                            onCancelled = {
                                goMain(
                                    HomiqDestination.Calendar,
                                )
                            },
                            onRecordPayment = {
                                navigate(
                                    HomiqRoute.PAYMENT_FORM,
                                    id = id,
                                )
                            },
                            onManageDeposit = {
                                navigate(
                                    HomiqRoute.DEPOSIT,
                                    id = id,
                                )
                            },
                            modifier = Modifier.padding(
                                innerPadding,
                            ),
                        )
                    }
                }

                HomiqRoute.BLOCK_DATE_FORM ->
                    BlockDateFormScreen(
                        viewModel = blockedDateViewModel,
                        onSaved = {
                            goMain(HomiqDestination.Calendar)
                        },
                        onNeedProperty = {
                            navigate(HomiqRoute.PROPERTY_FORM)
                        },
                        modifier = Modifier.padding(innerPadding),
                        initialStartEpochDay = routeEpochDay,
                        initialPropertyId = routePropertyId,
                    )


                HomiqRoute.PAYMENT_PICKER ->
                    PaymentBookingPickerScreen(
                        bookingViewModel = bookingViewModel,
                        financeViewModel = financeViewModel,
                        onBookingSelected = {
                            navigate(
                                HomiqRoute.PAYMENT_FORM,
                                id = it,
                            )
                        },
                        modifier = Modifier.padding(innerPadding),
                    )

                HomiqRoute.PAYMENT_FORM -> {
                    val id = routeId
                    if (id != null) {
                        PaymentFormScreen(
                            bookingId = id,
                            bookingViewModel = bookingViewModel,
                            financeViewModel = financeViewModel,
                            onSaved = {
                                navigate(
                                    HomiqRoute.BOOKING_DETAIL,
                                    id = id,
                                )
                            },
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }

                HomiqRoute.DEPOSIT -> {
                    val id = routeId
                    if (id != null) {
                        DepositScreen(
                            bookingId = id,
                            bookingViewModel = bookingViewModel,
                            financeViewModel = financeViewModel,
                            onDone = {
                                navigate(
                                    HomiqRoute.BOOKING_DETAIL,
                                    id = id,
                                )
                            },
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }

                HomiqRoute.MAIN -> Unit
            }
        }
    }

    if (showQuickAdd) {
        ModalBottomSheet(
            onDismissRequest = {
                showQuickAdd = false
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(R.string.quick_add),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 8.dp,
                    ),
                )

                QuickActionRow(
                    icon = Icons.Outlined.EventNote,
                    title = stringResource(R.string.new_booking),
                    onClick = {
                        showQuickAdd = false
                        navigate(HomiqRoute.BOOKING_FORM)
                    },
                )

                HorizontalDivider()

                QuickActionRow(
                    icon = Icons.Outlined.Payments,
                    title = stringResource(
                        R.string.record_payment,
                    ),
                    onClick = {
                        showQuickAdd = false
                        navigate(HomiqRoute.PAYMENT_PICKER)
                    },
                )

                HorizontalDivider()

                QuickActionRow(
                    icon = Icons.Outlined.ReceiptLong,
                    title = stringResource(R.string.add_expense),
                    onClick = {
                        showQuickAdd = false
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                unavailableMessage,
                            )
                        }
                    },
                )

                HorizontalDivider()

                QuickActionRow(
                    icon = Icons.Outlined.Block,
                    title = stringResource(R.string.block_date),
                    onClick = {
                        showQuickAdd = false
                        navigate(HomiqRoute.BLOCK_DATE_FORM)
                    },
                )
            }
        }
    }
}

@Composable
private fun QuickActionRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
    )
}
