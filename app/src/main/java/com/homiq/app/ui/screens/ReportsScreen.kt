package com.homiq.app.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.domain.ReportAnalytics
import com.homiq.app.ui.components.InfoCard
import com.homiq.app.ui.components.MetricCard
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.util.formatPercent
import com.homiq.app.ui.util.formatSenAsRinggit
import com.homiq.app.ui.util.labelRes
import com.homiq.app.ui.viewmodel.ReportsViewModel
import java.time.format.DateTimeFormatter

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    modifier: Modifier = Modifier,
) {
    var mode by remember {
        mutableIntStateOf(0)
    }
    val month by viewModel.month
        .collectAsStateWithLifecycle()
    val year by viewModel.year
        .collectAsStateWithLifecycle()
    val monthly by viewModel.monthlyReport
        .collectAsStateWithLifecycle()
    val yearly by viewModel.yearlyReport
        .collectAsStateWithLifecycle()

    val locale =
        LocalConfiguration.current.locales[0]
    val context = LocalContext.current

    val isMonthly = mode == 0
    val report = if (isMonthly) {
        monthly
    } else {
        yearly
    }

    val periodLabel = if (isMonthly) {
        month.format(
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
    } else {
        year.toString()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 18.dp,
            end = 16.dp,
            bottom = 32.dp,
        ),
        verticalArrangement =
            Arrangement.spacedBy(18.dp),
    ) {
        item {
            ScreenHeader(
                title =
                    stringResource(R.string.reports),
                subtitle =
                    stringResource(
                        R.string.reports_live_subtitle,
                    ),
            )
        }

        item {
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = mode == 0,
                    onClick = { mode = 0 },
                    label = {
                        Text(
                            stringResource(
                                R.string.monthly_report,
                            ),
                        )
                    },
                )
                FilterChip(
                    selected = mode == 1,
                    onClick = { mode = 1 },
                    label = {
                        Text(
                            stringResource(
                                R.string.yearly_report,
                            ),
                        )
                    },
                )
            }
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
                    verticalAlignment =
                        Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            if (isMonthly) {
                                viewModel.previousMonth()
                            } else {
                                viewModel.previousYear()
                            }
                        },
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.ChevronLeft,
                            contentDescription = null,
                        )
                    }

                    Text(
                        text = periodLabel,
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )

                    IconButton(
                        onClick = {
                            if (isMonthly) {
                                viewModel.nextMonth()
                            } else {
                                viewModel.nextYear()
                            }
                        },
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.ChevronRight,
                            contentDescription = null,
                        )
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = {
                    if (isMonthly) {
                        viewModel.currentMonth()
                    } else {
                        viewModel.currentYear()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    stringResource(
                        if (isMonthly) {
                            R.string.current_month
                        } else {
                            R.string.current_year
                        },
                    ),
                )
            }
        }

        item {
            ReportMetrics(
                report = report,
                locale = locale,
            )
        }

        item {
            InfoCard(
                title = stringResource(
                    R.string.report_formula_title,
                ),
                body = stringResource(
                    R.string.report_formula_body,
                ),
            )
        }

        item {
            Text(
                text = stringResource(
                    R.string.booking_performance,
                ),
                style =
                    MaterialTheme.typography.titleLarge,
            )
        }

        item {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(10.dp),
                ) {
                    ReportLine(
                        label =
                            stringResource(
                                R.string.booking_count,
                            ),
                        value =
                            (report?.bookingCount ?: 0)
                                .toString(),
                    )
                    ReportLine(
                        label =
                            stringResource(
                                R.string.booked_value,
                            ),
                        value =
                            formatSenAsRinggit(
                                report?.bookedValueSen
                                    ?: 0L,
                                locale,
                            ),
                    )
                    ReportLine(
                        label =
                            stringResource(
                                R.string.average_booking_value,
                            ),
                        value =
                            formatSenAsRinggit(
                                report
                                    ?.averageBookingValueSen
                                    ?: 0L,
                                locale,
                            ),
                    )
                    ReportLine(
                        label =
                            stringResource(
                                R.string.booked_nights,
                            ),
                        value =
                            (report?.bookedNights ?: 0L)
                                .toString(),
                    )
                    ReportLine(
                        label =
                            stringResource(
                                R.string.available_nights,
                            ),
                        value =
                            (report?.availableNights ?: 0L)
                                .toString(),
                    )
                }
            }
        }

        item {
            Text(
                text =
                    stringResource(
                        R.string.source_analytics,
                    ),
                style =
                    MaterialTheme.typography.titleLarge,
            )
        }

        val sources =
            report?.sourceAnalytics.orEmpty()

        if (sources.isEmpty()) {
            item {
                InfoCard(
                    title =
                        stringResource(
                            R.string.no_source_data,
                        ),
                    body =
                        stringResource(
                            R.string.no_source_data_body,
                        ),
                )
            }
        } else {
            items(
                items = sources,
                key = { it.source.name },
            ) { source ->
                Surface(
                    shape =
                        MaterialTheme.shapes.extraLarge,
                    color =
                        MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                ) {
                    Column(
                        modifier =
                            Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text =
                                stringResource(
                                    source.source.labelRes(),
                                ),
                            style =
                                MaterialTheme.typography
                                    .titleMedium,
                        )
                        Text(
                            text =
                                stringResource(
                                    R.string.source_booking_count,
                                    source.bookingCount,
                                ),
                            style =
                                MaterialTheme.typography
                                    .bodyMedium,
                        )
                        Text(
                            text =
                                formatSenAsRinggit(
                                    source.bookedValueSen,
                                    locale,
                                ),
                            style =
                                MaterialTheme.typography
                                    .labelLarge,
                            color =
                                MaterialTheme.colorScheme
                                    .primary,
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    val currentReport =
                        report ?: return@Button
                    val shareText =
                        buildShareText(
                            periodLabel = periodLabel,
                            report = currentReport,
                            locale = locale,
                            revenueLabel =
                                context.getString(
                                    R.string.revenue,
                                ),
                            expenseLabel =
                                context.getString(
                                    R.string.expenses,
                                ),
                            netLabel =
                                context.getString(
                                    R.string.net_income,
                                ),
                            occupancyLabel =
                                context.getString(
                                    R.string.occupancy,
                                ),
                            bookingLabel =
                                context.getString(
                                    R.string.booking_count,
                                ),
                        )
                    val intent = Intent(
                        Intent.ACTION_SEND,
                    ).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_SUBJECT,
                            context.getString(
                                R.string.report_share_subject,
                                periodLabel,
                            ),
                        )
                        putExtra(
                            Intent.EXTRA_TEXT,
                            shareText,
                        )
                    }
                    context.startActivity(
                        Intent.createChooser(
                            intent,
                            context.getString(
                                R.string.share_report,
                            ),
                        ),
                    )
                },
                enabled = report != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector =
                        Icons.Outlined.Share,
                    contentDescription = null,
                )
                Text(
                    text =
                        stringResource(
                            R.string.share_report,
                        ),
                    modifier =
                        Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun ReportMetrics(
    report: ReportAnalytics?,
    locale: java.util.Locale,
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                label =
                    stringResource(R.string.revenue),
                value =
                    formatSenAsRinggit(
                        report?.revenueSen ?: 0L,
                        locale,
                    ),
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                label =
                    stringResource(R.string.expenses),
                value =
                    formatSenAsRinggit(
                        report?.expensesSen ?: 0L,
                        locale,
                    ),
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp),
        ) {
            MetricCard(
                label =
                    stringResource(
                        R.string.net_income,
                    ),
                value =
                    formatSenAsRinggit(
                        report?.netIncomeSen ?: 0L,
                        locale,
                    ),
                modifier = Modifier.weight(1f),
            )
            MetricCard(
                label =
                    stringResource(R.string.occupancy),
                value =
                    formatPercent(
                        report?.occupancyPercent ?: 0.0,
                        locale,
                    ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ReportLine(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style =
                MaterialTheme.typography.bodyMedium,
            color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant,
        )
        Text(
            text = value,
            style =
                MaterialTheme.typography.titleMedium,
        )
    }
}

private fun buildShareText(
    periodLabel: String,
    report: ReportAnalytics,
    locale: java.util.Locale,
    revenueLabel: String,
    expenseLabel: String,
    netLabel: String,
    occupancyLabel: String,
    bookingLabel: String,
): String = buildString {
    append("Homika · ")
    append(periodLabel)
    append('\n')
    append(revenueLabel)
    append(": ")
    append(
        formatSenAsRinggit(
            report.revenueSen,
            locale,
        ),
    )
    append('\n')
    append(expenseLabel)
    append(": ")
    append(
        formatSenAsRinggit(
            report.expensesSen,
            locale,
        ),
    )
    append('\n')
    append(netLabel)
    append(": ")
    append(
        formatSenAsRinggit(
            report.netIncomeSen,
            locale,
        ),
    )
    append('\n')
    append(occupancyLabel)
    append(": ")
    append(
        formatPercent(
            report.occupancyPercent,
            locale,
        ),
    )
    append('\n')
    append(bookingLabel)
    append(": ")
    append(report.bookingCount)
}
