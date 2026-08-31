package com.homiq.app.ui.screens

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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.homiq.app.ui.components.EmptyStateCard
import com.homiq.app.ui.components.MetricCard
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.components.SectionHeader
import com.homiq.app.ui.util.formatEpochDay
import com.homiq.app.ui.util.formatSenAsRinggit
import com.homiq.app.ui.util.labelRes
import com.homiq.app.ui.viewmodel.MoneyViewModel
import java.time.format.DateTimeFormatter

@Composable
fun MoneyScreen(
    viewModel: MoneyViewModel,
    onAddExpense: () -> Unit,
    onReportsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]
    val propertyNames = viewModel.activeProperties.collectAsStateWithLifecycle().value.associate { it.id to it.name }
    val monthTitle = state.month.format(DateTimeFormatter.ofPattern("MMMM yyyy", locale)).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(locale) else it.toString()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            ScreenHeader(
                title = stringResource(R.string.money_title),
                subtitle = stringResource(R.string.money_live_subtitle),
            )
        }
        item {
            MonthSelector(
                title = monthTitle,
                onPrevious = viewModel::previousMonth,
                onNext = viewModel::nextMonth,
                onCurrent = viewModel::currentMonth,
            )
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        stringResource(R.string.money_month_summary),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        formatSenAsRinggit(state.netIncomeSen, locale),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        stringResource(R.string.money_net_support),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        MetricCard(
                            label = stringResource(R.string.revenue),
                            value = formatSenAsRinggit(state.revenueSen, locale),
                            modifier = Modifier.weight(1f),
                        )
                        MetricCard(
                            label = stringResource(R.string.expenses),
                            value = formatSenAsRinggit(state.expensesSen, locale),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(onClick = onAddExpense, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Text(stringResource(R.string.add_expense), modifier = Modifier.padding(start = 6.dp), maxLines = 1)
                }
                OutlinedButton(onClick = onReportsClick, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.QueryStats, contentDescription = null)
                    Text(stringResource(R.string.view_reports), modifier = Modifier.padding(start = 6.dp), maxLines = 1)
                }
            }
        }
        item { SectionHeader(title = stringResource(R.string.property_breakdown)) }
        if (state.breakdown.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.no_money_activity),
                    body = stringResource(R.string.no_money_activity_body),
                    icon = Icons.Outlined.ReceiptLong,
                )
            }
        } else {
            items(state.breakdown, key = { it.propertyId ?: "general" }) { row ->
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Column(
                        modifier = Modifier.padding(15.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = if (row.propertyId == null) stringResource(R.string.general_expense) else row.propertyName.ifBlank { stringResource(R.string.unknown_property) },
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        MoneyLine(stringResource(R.string.revenue), formatSenAsRinggit(row.revenueSen, locale))
                        MoneyLine(stringResource(R.string.expenses), formatSenAsRinggit(row.expensesSen, locale))
                        MoneyLine(stringResource(R.string.net_income), formatSenAsRinggit(row.netIncomeSen, locale), true)
                    }
                }
            }
        }

        item { SectionHeader(title = stringResource(R.string.expense_history)) }
        if (state.expenses.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.no_expenses_this_month),
                    body = stringResource(R.string.no_expenses_this_month_body),
                    icon = Icons.Outlined.ReceiptLong,
                )
            }
        } else {
            items(state.expenses, key = { it.id }) { expense ->
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(stringResource(expense.category.labelRes()), style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                expense.propertyId?.let { propertyNames[it] } ?: stringResource(R.string.general_expense),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                formatEpochDay(expense.expenseDateEpochDay, locale),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            formatSenAsRinggit(expense.amountSen, locale),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    title: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onCurrent: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPrevious) { Icon(Icons.Outlined.ChevronLeft, contentDescription = null) }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            androidx.compose.material3.TextButton(onClick = onCurrent) {
                Text(stringResource(R.string.current_month), maxLines = 1)
            }
            IconButton(onClick = onNext) { Icon(Icons.Outlined.ChevronRight, contentDescription = null) }
        }
    }
}

@Composable
private fun MoneyLine(label: String, value: String, emphasized: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
        )
    }
}
