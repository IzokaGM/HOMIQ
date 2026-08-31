package com.homiq.app.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.AddHome
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
        contentPadding = PaddingValues(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ScreenHeader(
                title = stringResource(R.string.properties),
                subtitle = stringResource(R.string.properties_manage_subtitle),
            )
        }
        item {
            Button(onClick = onAddProperty, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.AddHome, contentDescription = null)
                Text(stringResource(R.string.add_property), modifier = Modifier.padding(start = 8.dp))
            }
        }
        if (properties.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.properties_empty_title),
                    body = stringResource(R.string.properties_empty_body),
                    icon = Icons.Outlined.HomeWork,
                )
            }
        } else {
            items(properties, key = { it.id }) { property ->
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { onPropertyClick(property.id) },
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) {
                            Icon(
                                Icons.Outlined.HomeWork,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(9.dp),
                            )
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(property.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                text = "${formatSenAsRinggit(property.defaultNightlyRateSen, locale)} / ${stringResource(R.string.per_night)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                            )
                            if (!property.address.isNullOrBlank()) {
                                Text(
                                    text = property.address.orEmpty(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = if (property.isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Text(
                                text = stringResource(if (property.isActive) R.string.active else R.string.inactive),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (property.isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
