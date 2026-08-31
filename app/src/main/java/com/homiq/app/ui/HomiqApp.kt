package com.homiq.app.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.homiq.app.R
import com.homiq.app.ui.screens.BookingsScreen
import com.homiq.app.ui.screens.CalendarScreen
import com.homiq.app.ui.screens.HomeScreen
import com.homiq.app.ui.screens.MoneyScreen
import com.homiq.app.ui.screens.MoreScreen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomiqApp() {
    var destinationName by rememberSaveable {
        mutableStateOf(HomiqDestination.Home.name)
    }
    var showQuickAdd by rememberSaveable { mutableStateOf(false) }

    val selectedDestination = remember(destinationName) {
        HomiqDestination.entries.firstOrNull { it.name == destinationName }
            ?: HomiqDestination.Home
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val unavailableMessage = stringResource(R.string.feature_next_phase)

    fun onQuickAction() {
        showQuickAdd = false
        scope.launch {
            snackbarHostState.showSnackbar(unavailableMessage)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            NavigationBar {
                HomiqDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = selectedDestination == destination,
                        onClick = { destinationName = destination.name },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(text = stringResource(destination.labelRes))
                        },
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showQuickAdd = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.quick_add),
                )
            }
        },
    ) { innerPadding ->
        when (selectedDestination) {
            HomiqDestination.Home -> HomeScreen(
                modifier = Modifier.padding(innerPadding),
            )
            HomiqDestination.Calendar -> CalendarScreen(
                modifier = Modifier.padding(innerPadding),
            )
            HomiqDestination.Bookings -> BookingsScreen(
                modifier = Modifier.padding(innerPadding),
            )
            HomiqDestination.Money -> MoneyScreen(
                modifier = Modifier.padding(innerPadding),
            )
            HomiqDestination.More -> MoreScreen(
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    if (showQuickAdd) {
        ModalBottomSheet(
            onDismissRequest = { showQuickAdd = false },
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
                    onClick = ::onQuickAction,
                )
                HorizontalDivider()
                QuickActionRow(
                    icon = Icons.Outlined.Payments,
                    title = stringResource(R.string.record_payment),
                    onClick = ::onQuickAction,
                )
                HorizontalDivider()
                QuickActionRow(
                    icon = Icons.Outlined.ReceiptLong,
                    title = stringResource(R.string.add_expense),
                    onClick = ::onQuickAction,
                )
                HorizontalDivider()
                QuickActionRow(
                    icon = Icons.Outlined.Block,
                    title = stringResource(R.string.block_date),
                    onClick = ::onQuickAction,
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
