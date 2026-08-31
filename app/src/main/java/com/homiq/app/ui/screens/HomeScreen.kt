package com.homiq.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.homiq.app.R
import com.homiq.app.ui.components.EmptyStateCard
import com.homiq.app.ui.components.MetricCard
import com.homiq.app.ui.components.ScreenHeader

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            top = 24.dp,
            end = 20.dp,
            bottom = 104.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            ScreenHeader(
                eyebrow = stringResource(R.string.home_eyebrow),
                title = stringResource(R.string.home_title),
                subtitle = stringResource(R.string.home_subtitle),
            )
        }

        item {
            Text(
                text = stringResource(R.string.this_month),
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MetricCard(
                        label = stringResource(R.string.net_income),
                        value = stringResource(R.string.currency_zero),
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label = stringResource(R.string.occupancy),
                        value = stringResource(R.string.percent_zero),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.today),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        item {
            EmptyStateCard(
                title = stringResource(R.string.today_empty_title),
                body = stringResource(R.string.today_empty_body),
                icon = Icons.Outlined.EventAvailable,
            )
        }

        item {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.upcoming_bookings),
                style = MaterialTheme.typography.titleLarge,
            )
        }

        item {
            EmptyStateCard(
                title = stringResource(R.string.upcoming_empty_title),
                body = stringResource(R.string.upcoming_empty_body),
                icon = Icons.Outlined.NightsStay,
            )
        }
    }
}
