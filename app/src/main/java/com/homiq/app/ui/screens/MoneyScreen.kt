package com.homiq.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.homiq.app.R
import com.homiq.app.ui.components.EmptyStateCard
import com.homiq.app.ui.components.InfoCard
import com.homiq.app.ui.components.MetricCard
import com.homiq.app.ui.components.ScreenHeader

@Composable
fun MoneyScreen(
    modifier: Modifier = Modifier,
) {
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
                subtitle = stringResource(R.string.money_subtitle),
            )
        }

        item {
            Text(
                text = stringResource(R.string.financial_overview),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MetricCard(
                        label = stringResource(R.string.revenue),
                        value = stringResource(R.string.currency_zero),
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label = stringResource(R.string.expenses),
                        value = stringResource(R.string.currency_zero),
                        modifier = Modifier.weight(1f),
                    )
                }

                MetricCard(
                    label = stringResource(R.string.net_income),
                    value = stringResource(R.string.currency_zero),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        item {
            InfoCard(
                title = stringResource(R.string.deposit_notice_title),
                body = stringResource(R.string.deposit_notice_body),
            )
        }

        item {
            Text(
                text = stringResource(R.string.reports),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        item {
            EmptyStateCard(
                title = stringResource(R.string.reports),
                body = stringResource(R.string.reports_body),
                icon = Icons.Outlined.BarChart,
            )
        }
    }
}
