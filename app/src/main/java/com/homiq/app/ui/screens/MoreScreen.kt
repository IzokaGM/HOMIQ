package com.homiq.app.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.homiq.app.R
import com.homiq.app.ui.components.ScreenHeader

@Composable
fun MoreScreen(
    onPropertiesClick: () -> Unit,
    onBackupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val explicitLocales = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    val currentLanguage = if (explicitLocales.isBlank()) {
        configuration.locales[0].language
    } else {
        explicitLocales.substringBefore(",").substringBefore("-")
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
                title = stringResource(R.string.more_title),
                subtitle = stringResource(R.string.more_subtitle),
            )
        }

        item {
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Outlined.Person,
                    title = stringResource(R.string.account),
                    body = stringResource(R.string.not_signed_in),
                    trailing = stringResource(R.string.local_only),
                )
            }
        }

        item {
            SettingsGroup {
                SettingsRow(
                    icon = Icons.Outlined.Home,
                    title = stringResource(R.string.properties),
                    body = stringResource(R.string.properties_body),
                    onClick = onPropertiesClick,
                )
                HorizontalDivider()
                SettingsRow(
                    icon = Icons.Outlined.Backup,
                    title = stringResource(R.string.backup_restore),
                    body = stringResource(R.string.backup_restore_body),
                    onClick = onBackupClick,
                )
                HorizontalDivider()
                SettingsRow(
                    icon = Icons.Outlined.Lock,
                    title = stringResource(R.string.security),
                    body = stringResource(R.string.security_body),
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.language),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = stringResource(R.string.language_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                SettingsGroup {
                    LanguageRow(
                        title = stringResource(R.string.language_english),
                        selected = currentLanguage != "ms",
                        onClick = { setHomiqLanguage("en") },
                    )
                    HorizontalDivider()
                    LanguageRow(
                        title = stringResource(R.string.language_malay),
                        selected = currentLanguage == "ms",
                        onClick = { setHomiqLanguage("ms") },
                    )
                }
            }
        }
    }
}

private fun setHomiqLanguage(languageTag: String) {
    AppCompatDelegate.setApplicationLocales(
        LocaleListCompat.forLanguageTags(languageTag),
    )
}

@Composable
private fun SettingsGroup(
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        content = content,
    )
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
    trailing: String? = null,
    onClick: (() -> Unit)? = null,
) {
    ListItem(
        modifier = if (onClick != null) {
            Modifier.clickable(onClick = onClick)
        } else {
            Modifier
        },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        supportingContent = {
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        trailingContent = trailing?.let { text ->
            {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}

@Composable
private fun LanguageRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.selectable(
            selected = selected,
            onClick = onClick,
        ),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = onClick,
            )
        },
    )
}
