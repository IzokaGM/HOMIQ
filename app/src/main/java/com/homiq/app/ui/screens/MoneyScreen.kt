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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.ui.components.EmptyStateCard
import com.homiq.app.ui.components.InfoCard
import com.homiq.app.ui.components.MetricCard
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.util.formatEpochDay
import com.homiq.app.ui.util.formatSenAsRinggit
import com.homiq.app.ui.util.labelRes
import com.homiq.app.ui.viewmodel.MoneyViewModel
import java.time.format.DateTimeFormatter

@Composable
fun MoneyScreen(
    viewModel: MoneyViewModel,
    onAddExpense: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]

    val monthTitle = state.month.format(
        DateTimeFormatter.ofPattern(
            "MMMM yyyy",
            locale,
        ),
    ).replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(locale)
        } else {
            it.toString()
        }
    }

    val propertyNames = viewModel.activeProperties
        .collectAsStateWithLifecycle()
        .value
        .associate {
            it.id to it.name
        }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 24.dp,
            end = 20.dp,
            bottom = 104.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            ScreenHeader(
                title = stringResource(R.string.money_title),
                subtitle = stringResource(
                    R.string.money_live_subtitle,
                ),
            )
        }

        item {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
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
                        fontWeight = FontWeight.Bold,
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
            }
        }

        item {
            OutlinedButton(
                onClick = viewModel::currentMonth,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    stringResource(
                        R.string.current_month,
                    ),
                )
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),
                ) {
                    MetricCard(
                        label = stringResource(R.string.revenue),
                        value = formatSenAsRinggit(
                            state.revenueSen,
                            locale,
                        ),
                        modifier = Modifier.weight(1f),
                    )

                    MetricCard(
                        label = stringResource(R.string.expenses),
                        value = formatSenAsRinggit(
                            state.expensesSen,
                            locale,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }

                MetricCard(
                    label = stringResource(R.string.net_income),
                    value = formatSenAsRinggit(
                        state.netIncomeSen,
                        locale,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        item {
            InfoCard(
                title = stringResource(
                    R.string.cash_basis_title,
                ),
                body = stringResource(
                    R.string.cash_basis_body,
                ),
            )
        }

        item {
            Button(
                onClick = onAddExpense,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(R.string.add_expense),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }

        item {
            Text(
                text = stringResource(
                    R.string.property_breakdown,
                ),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if (state.breakdown.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(
                        R.string.no_money_activity,
                    ),
                    body = stringResource(
                        R.string.no_money_activity_body,
                    ),
                    icon = Icons.Outlined.ReceiptLong,
                )
            }
        } else {
            items(
                items = state.breakdown,
                key = {
                    it.propertyId ?: "general"
                },
            ) { row ->
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = if (
                                row.propertyId == null
                            ) {
                                stringResource(
                                    R.string.general_expense,
                                )
                            } else {
                                row.propertyName.ifBlank {
                                    stringResource(
                                        R.string.unknown_property,
                                    )
                                }
                            },
                            style =
                                MaterialTheme.typography.titleMedium,
                        )

                        MoneyLine(
                            label = stringResource(
                                R.string.revenue,
                            ),
                            value = formatSenAsRinggit(
                                row.revenueSen,
                                locale,
                            ),
                        )
                        MoneyLine(
                            label = stringResource(
                                R.string.expenses,
                            ),
                            value = formatSenAsRinggit(
                                row.expensesSen,
                                locale,
                            ),
                        )
                        MoneyLine(
                            label = stringResource(
                                R.string.net_income,
                            ),
                            value = formatSenAsRinggit(
                                row.netIncomeSen,
                                locale,
                            ),
                            emphasized = true,
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(
                    R.string.expense_history,
                ),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if (state.expenses.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(
                        R.string.no_expenses_this_month,
                    ),
                    body = stringResource(
                        R.string.no_expenses_this_month_body,
                    ),
                    icon = Icons.Outlined.ReceiptLong,
                )
            }
        } else {
            items(
                items = state.expenses,
                key = { it.id },
            ) { expense ->
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(5.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = stringResource(
                                    expense.category.labelRes(),
                                ),
                                style =
                                    MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = formatSenAsRinggit(
                                    expense.amountSen,
                                    locale,
                                ),
                                style =
                                    MaterialTheme.typography.titleMedium,
                                color =
                                    MaterialTheme.colorScheme.error,
                            )
                        }

                        Text(
                            text = expense.propertyId
                                ?.let {
                                    propertyNames[it]
                                }
                                ?: stringResource(
                                    R.string.general_expense,
                                ),
                            style =
                                MaterialTheme.typography.bodyMedium,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Text(
                            text = formatEpochDay(
                                expense.expenseDateEpochDay,
                                locale,
                            ),
                            style =
                                MaterialTheme.typography.bodyMedium,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        expense.description?.let {
                            Text(
                                text = it,
                                style =
                                    MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoneyLine(
    label: String,
    value: String,
    emphasized: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = if (emphasized) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
            fontWeight = if (emphasized) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            },
        )
    }
}
