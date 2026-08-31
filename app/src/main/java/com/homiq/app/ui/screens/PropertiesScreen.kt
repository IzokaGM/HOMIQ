package com.homiq.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddHome
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.ui.components.EmptyStateCard
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.util.formatSenAsRinggit
import com.homiq.app.ui.viewmodel.PropertyViewModel

@Composable
fun PropertiesScreen(
    viewModel: PropertyViewModel,
    onAddProperty: () -> Unit,
    onPropertyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val properties by viewModel.propertyList.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]

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
                title = stringResource(R.string.properties),
                subtitle = stringResource(
                    R.string.properties_manage_subtitle,
                ),
            )
        }

        item {
            Button(
                onClick = onAddProperty,
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddHome,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(R.string.add_property),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }

        if (properties.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(
                        R.string.properties_empty_title,
                    ),
                    body = stringResource(
                        R.string.properties_empty_body,
                    ),
                    icon = Icons.Outlined.HomeWork,
                )
            }
        } else {
            items(
                items = properties,
                key = { it.id },
            ) { property ->
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    modifier = Modifier.clickable {
                        onPropertyClick(property.id)
                    },
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = property.name,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                        supportingContent = {
                            Text(
                                text = buildString {
                                    append(
                                        formatSenAsRinggit(
                                            property.defaultNightlyRateSen,
                                            locale,
                                        ),
                                    )
                                    append(" / ")
                                    append(
                                        stringResource(
                                            R.string.per_night,
                                        ),
                                    )
                                    property.address?.let {
                                        append("\n")
                                        append(it)
                                    }
                                },
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.HomeWork,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        },
                        trailingContent = {
                            Text(
                                text = if (property.isActive) {
                                    stringResource(R.string.active)
                                } else {
                                    stringResource(R.string.inactive)
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = if (property.isActive) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        },
                    )
                }
            }
        }
    }
}
