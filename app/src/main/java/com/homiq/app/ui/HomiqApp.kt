package com.homiq.app.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homiq.app.HomiqApplication
import com.homiq.app.R
import com.homiq.app.data.preferences.OnboardingPreferences
import com.homiq.app.ui.screens.AppLockScreen
import com.homiq.app.ui.screens.BackupScreen
import com.homiq.app.ui.screens.BlockDateFormScreen
import com.homiq.app.ui.screens.BookingDetailScreen
import com.homiq.app.ui.screens.BookingFormScreen
import com.homiq.app.ui.screens.BookingsScreen
import com.homiq.app.ui.screens.CalendarScreen
import com.homiq.app.ui.screens.DepositScreen
import com.homiq.app.ui.screens.ExpenseFormScreen
import com.homiq.app.ui.screens.HomeScreen
import com.homiq.app.ui.screens.MoneyScreen
import com.homiq.app.ui.screens.MoreScreen
import com.homiq.app.ui.screens.OnboardingScreen
import com.homiq.app.ui.screens.PaymentBookingPickerScreen
import com.homiq.app.ui.screens.PaymentFormScreen
import com.homiq.app.ui.screens.PropertiesScreen
import com.homiq.app.ui.screens.PropertyFormScreen
import com.homiq.app.ui.screens.ReportsScreen
import com.homiq.app.ui.screens.SecurityScreen
import com.homiq.app.ui.screens.SyncScreen
import com.homiq.app.ui.viewmodel.AppLockViewModel
import com.homiq.app.ui.viewmodel.BackupViewModel
import com.homiq.app.ui.viewmodel.BlockedDateViewModel
import com.homiq.app.ui.viewmodel.BookingViewModel
import com.homiq.app.ui.viewmodel.CalendarViewModel
import com.homiq.app.ui.viewmodel.DashboardViewModel
import com.homiq.app.ui.viewmodel.FinanceViewModel
import com.homiq.app.ui.viewmodel.HomiqViewModelFactory
import com.homiq.app.ui.viewmodel.MoneyViewModel
import com.homiq.app.ui.viewmodel.PropertyViewModel
import com.homiq.app.ui.viewmodel.ReportsViewModel
import com.homiq.app.ui.viewmodel.SyncViewModel

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
    EXPENSE_FORM,
    REPORTS,
    BACKUP,
    SYNC,
    SECURITY,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomiqApp() {
    val application = LocalContext.current.applicationContext as HomiqApplication
    val factory = remember(application) { HomiqViewModelFactory(application.container) }

    val appLockViewModel: AppLockViewModel = viewModel(factory = factory)
    val appLockState by appLockViewModel.state.collectAsStateWithLifecycle()
    if (appLockState.locked) {
        AppLockScreen(viewModel = appLockViewModel)
        return
    }

    val propertyViewModel: PropertyViewModel = viewModel(factory = factory)
    val bookingViewModel: BookingViewModel = viewModel(factory = factory)
    val calendarViewModel: CalendarViewModel = viewModel(factory = factory)
    val blockedDateViewModel: BlockedDateViewModel = viewModel(factory = factory)
    val financeViewModel: FinanceViewModel = viewModel(factory = factory)
    val moneyViewModel: MoneyViewModel = viewModel(factory = factory)
    val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)
    val reportsViewModel: ReportsViewModel = viewModel(factory = factory)
    val backupViewModel: BackupViewModel = viewModel(factory = factory)
    val syncViewModel: SyncViewModel = viewModel(factory = factory)
    val syncUiState by syncViewModel.state.collectAsStateWithLifecycle()

    val onboardingPreferences = remember(application) { OnboardingPreferences(application) }
    var onboardingComplete by rememberSaveable { mutableStateOf(onboardingPreferences.isComplete) }

    var destinationName by rememberSaveable { mutableStateOf(HomiqDestination.Home.name) }
    var routeName by rememberSaveable { mutableStateOf(HomiqRoute.MAIN.name) }
    var routeId by rememberSaveable { mutableStateOf<String?>(null) }
    var routeEpochDay by rememberSaveable { mutableStateOf<Long?>(null) }
    var routePropertyId by rememberSaveable { mutableStateOf<String?>(null) }
    var showQuickAdd by rememberSaveable { mutableStateOf(false) }

    if (!onboardingComplete) {
        OnboardingScreen(
            syncViewModel = syncViewModel,
            appLockViewModel = appLockViewModel,
            onFinished = { createFirstProperty ->
                onboardingPreferences.complete()
                onboardingComplete = true
                if (createFirstProperty) {
                    routeName = HomiqRoute.PROPERTY_FORM.name
                    routeId = null
                }
            },
        )
        return
    }

    val selectedDestination = remember(destinationName) {
        HomiqDestination.entries.firstOrNull { it.name == destinationName } ?: HomiqDestination.Home
    }
    val route = remember(routeName) {
        HomiqRoute.entries.firstOrNull { it.name == routeName } ?: HomiqRoute.MAIN
    }
    val snackbarHostState = remember { SnackbarHostState() }

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

    fun goMain(destination: HomiqDestination? = null) {
        if (destination != null) destinationName = destination.name
        navigate(HomiqRoute.MAIN)
    }

    BackHandler(enabled = route != HomiqRoute.MAIN) {
        when (route) {
            HomiqRoute.PROPERTY_FORM -> navigate(HomiqRoute.PROPERTIES)
            HomiqRoute.BOOKING_FORM -> {
                if (routeId != null) navigate(HomiqRoute.BOOKING_DETAIL, id = routeId)
                else goMain(HomiqDestination.Calendar)
            }
            HomiqRoute.BOOKING_DETAIL -> goMain(HomiqDestination.Bookings)
            HomiqRoute.BLOCK_DATE_FORM -> goMain(HomiqDestination.Calendar)
            HomiqRoute.PAYMENT_PICKER -> goMain(HomiqDestination.Bookings)
            HomiqRoute.PAYMENT_FORM -> navigate(HomiqRoute.BOOKING_DETAIL, id = routeId)
            HomiqRoute.DEPOSIT -> navigate(HomiqRoute.BOOKING_DETAIL, id = routeId)
            HomiqRoute.EXPENSE_FORM -> goMain(HomiqDestination.Money)
            HomiqRoute.REPORTS -> goMain(HomiqDestination.Home)
            HomiqRoute.BACKUP, HomiqRoute.SYNC, HomiqRoute.SECURITY, HomiqRoute.PROPERTIES -> goMain(HomiqDestination.More)
            HomiqRoute.MAIN -> Unit
        }
    }

    if (route == HomiqRoute.MAIN) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                HomikaBottomBar(
                    selected = selectedDestination,
                    onSelected = { destinationName = it.name },
                )
            },
            floatingActionButton = {
                SmallFloatingActionButton(
                    onClick = { showQuickAdd = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.large,
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.quick_add))
                }
            },
        ) { innerPadding ->
            when (selectedDestination) {
                HomiqDestination.Home -> HomeScreen(
                    viewModel = dashboardViewModel,
                    onBookingClick = { navigate(HomiqRoute.BOOKING_DETAIL, id = it) },
                    onReportsClick = { navigate(HomiqRoute.REPORTS) },
                    modifier = Modifier.padding(innerPadding),
                )
                HomiqDestination.Calendar -> CalendarScreen(
                    viewModel = calendarViewModel,
                    onBookingClick = { navigate(HomiqRoute.BOOKING_DETAIL, id = it) },
                    onNewBooking = { day, propertyId -> navigate(HomiqRoute.BOOKING_FORM, epochDay = day, propertyId = propertyId) },
                    onBlockDate = { day, propertyId -> navigate(HomiqRoute.BLOCK_DATE_FORM, epochDay = day, propertyId = propertyId) },
                    modifier = Modifier.padding(innerPadding),
                )
                HomiqDestination.Bookings -> BookingsScreen(
                    viewModel = bookingViewModel,
                    onBookingClick = { navigate(HomiqRoute.BOOKING_DETAIL, id = it) },
                    modifier = Modifier.padding(innerPadding),
                )
                HomiqDestination.Money -> MoneyScreen(
                    viewModel = moneyViewModel,
                    onAddExpense = { navigate(HomiqRoute.EXPENSE_FORM) },
                    onReportsClick = { navigate(HomiqRoute.REPORTS) },
                    modifier = Modifier.padding(innerPadding),
                )
                HomiqDestination.More -> MoreScreen(
                    onPropertiesClick = { navigate(HomiqRoute.PROPERTIES) },
                    onBackupClick = { navigate(HomiqRoute.BACKUP) },
                    syncEnabled = syncUiState.runtime.enabled,
                    onSyncClick = { navigate(HomiqRoute.SYNC) },
                    appLockEnabled = appLockState.hasPin,
                    onSecurityClick = { navigate(HomiqRoute.SECURITY) },
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    } else {
        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
            when (route) {
                HomiqRoute.PROPERTIES -> PropertiesScreen(
                    viewModel = propertyViewModel,
                    onAddProperty = { navigate(HomiqRoute.PROPERTY_FORM) },
                    onPropertyClick = { navigate(HomiqRoute.PROPERTY_FORM, id = it) },
                    modifier = Modifier.padding(innerPadding),
                )
                HomiqRoute.PROPERTY_FORM -> PropertyFormScreen(
                    propertyId = routeId,
                    viewModel = propertyViewModel,
                    onSaved = { navigate(HomiqRoute.PROPERTIES) },
                    modifier = Modifier.padding(innerPadding),
                )
                HomiqRoute.BOOKING_FORM -> BookingFormScreen(
                    bookingId = routeId,
                    viewModel = bookingViewModel,
                    onSaved = { goMain(HomiqDestination.Calendar) },
                    onNeedProperty = { navigate(HomiqRoute.PROPERTY_FORM) },
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
                            onEdit = { navigate(HomiqRoute.BOOKING_FORM, id = id) },
                            onCancelled = { goMain(HomiqDestination.Calendar) },
                            onRecordPayment = { navigate(HomiqRoute.PAYMENT_FORM, id = id) },
                            onManageDeposit = { navigate(HomiqRoute.DEPOSIT, id = id) },
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
                HomiqRoute.BLOCK_DATE_FORM -> BlockDateFormScreen(
                    viewModel = blockedDateViewModel,
                    onSaved = { goMain(HomiqDestination.Calendar) },
                    onNeedProperty = { navigate(HomiqRoute.PROPERTY_FORM) },
                    modifier = Modifier.padding(innerPadding),
                    initialStartEpochDay = routeEpochDay,
                    initialPropertyId = routePropertyId,
                )
                HomiqRoute.PAYMENT_PICKER -> PaymentBookingPickerScreen(
                    bookingViewModel = bookingViewModel,
                    financeViewModel = financeViewModel,
                    onBookingSelected = { navigate(HomiqRoute.PAYMENT_FORM, id = it) },
                    modifier = Modifier.padding(innerPadding),
                )
                HomiqRoute.PAYMENT_FORM -> {
                    val id = routeId
                    if (id != null) {
                        PaymentFormScreen(
                            bookingId = id,
                            bookingViewModel = bookingViewModel,
                            financeViewModel = financeViewModel,
                            onSaved = { navigate(HomiqRoute.BOOKING_DETAIL, id = id) },
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
                            onDone = { navigate(HomiqRoute.BOOKING_DETAIL, id = id) },
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
                HomiqRoute.EXPENSE_FORM -> ExpenseFormScreen(
                    viewModel = moneyViewModel,
                    onSaved = { goMain(HomiqDestination.Money) },
                    modifier = Modifier.padding(innerPadding),
                )
                HomiqRoute.REPORTS -> ReportsScreen(viewModel = reportsViewModel, modifier = Modifier.padding(innerPadding))
                HomiqRoute.BACKUP -> BackupScreen(viewModel = backupViewModel, modifier = Modifier.padding(innerPadding))
                HomiqRoute.SYNC -> SyncScreen(viewModel = syncViewModel, modifier = Modifier.padding(innerPadding))
                HomiqRoute.SECURITY -> SecurityScreen(viewModel = appLockViewModel, modifier = Modifier.padding(innerPadding))
                HomiqRoute.MAIN -> Unit
            }
        }
    }

    if (showQuickAdd) {
        ModalBottomSheet(onDismissRequest = { showQuickAdd = false }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                    Text(stringResource(R.string.quick_add), style = MaterialTheme.typography.titleLarge)
                    Text(
                        stringResource(R.string.quick_add_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                QuickActionRow(Icons.Outlined.EventNote, stringResource(R.string.new_booking)) {
                    showQuickAdd = false
                    navigate(HomiqRoute.BOOKING_FORM)
                }
                QuickActionRow(Icons.Outlined.Payments, stringResource(R.string.record_payment)) {
                    showQuickAdd = false
                    navigate(HomiqRoute.PAYMENT_PICKER)
                }
                QuickActionRow(Icons.Outlined.ReceiptLong, stringResource(R.string.add_expense)) {
                    showQuickAdd = false
                    navigate(HomiqRoute.EXPENSE_FORM)
                }
                QuickActionRow(Icons.Outlined.Block, stringResource(R.string.block_date)) {
                    showQuickAdd = false
                    navigate(HomiqRoute.BLOCK_DATE_FORM)
                }
            }
        }
    }
}

@Composable
private fun HomikaBottomBar(
    selected: HomiqDestination,
    onSelected: (HomiqDestination) -> Unit,
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomiqDestination.entries.forEach { destination ->
                val active = destination == selected
                Column(
                    modifier = Modifier.weight(1f).clickable { onSelected(destination) }.padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                destination.icon,
                                contentDescription = null,
                                tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(21.dp),
                            )
                        }
                    }
                    Text(
                        text = stringResource(destination.labelRes),
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
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
        headlineContent = { Text(title, style = MaterialTheme.typography.titleMedium) },
        leadingContent = {
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(9.dp),
                )
            }
        },
    )
}
