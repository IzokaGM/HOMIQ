package com.homiq.app.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.homiq.app.BuildConfig
import com.homiq.app.R
import com.homiq.app.ui.components.HomikaBrandMark
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.components.SettingRow

@Composable
fun MoreScreen(
    onPropertiesClick: () -> Unit,
    onBackupClick: () -> Unit,
    syncEnabled: Boolean,
    onSyncClick: () -> Unit,
    appLockEnabled: Boolean,
    onSecurityClick: () -> Unit,
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
        contentPadding = PaddingValues(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HomikaBrandMark()
                ScreenHeader(
                    title = stringResource(R.string.more_title),
                    subtitle = stringResource(R.string.more_brand_subtitle),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            SettingsSection(title = stringResource(R.string.settings_workspace)) {
                SettingRow(
                    icon = Icons.Outlined.HomeWork,
                    title = stringResource(R.string.properties),
                    body = stringResource(R.string.properties_body),
                    modifier = Modifier.clickable(onClick = onPropertiesClick),
                )
            }
        }

        item {
            SettingsSection(title = stringResource(R.string.settings_data)) {
                SettingRow(
                    icon = Icons.Outlined.Person,
                    title = stringResource(R.string.account),
                    body = stringResource(if (syncEnabled) R.string.sync_connected_body_short else R.string.sync_not_connected_body_short),
                    trailing = stringResource(if (syncEnabled) R.string.sync_connected else R.string.optional),
                    modifier = Modifier.clickable(onClick = onSyncClick),
                )
                HorizontalDivider(modifier = Modifier.padding(start = 62.dp))
                SettingRow(
                    icon = Icons.Outlined.CloudSync,
                    title = stringResource(R.string.sync_title),
                    body = stringResource(R.string.sync_more_body),
                    modifier = Modifier.clickable(onClick = onSyncClick),
                )
                HorizontalDivider(modifier = Modifier.padding(start = 62.dp))
                SettingRow(
                    icon = Icons.Outlined.Backup,
                    title = stringResource(R.string.backup_restore),
                    body = stringResource(R.string.backup_restore_body),
                    modifier = Modifier.clickable(onClick = onBackupClick),
                )
                HorizontalDivider(modifier = Modifier.padding(start = 62.dp))
                SettingRow(
                    icon = Icons.Outlined.Lock,
                    title = stringResource(R.string.security),
                    body = stringResource(if (appLockEnabled) R.string.security_enabled_body else R.string.security_body),
                    trailing = if (appLockEnabled) stringResource(R.string.on) else null,
                    modifier = Modifier.clickable(onClick = onSecurityClick),
                )
            }
        }

        item {
            SettingsSection(title = stringResource(R.string.settings_preferences)) {
                LanguageChoiceRow(
                    title = stringResource(R.string.language_malay),
                    selected = currentLanguage == "ms",
                    onClick = { setHomiqLanguage("ms") },
                )
                HorizontalDivider(modifier = Modifier.padding(start = 62.dp))
                LanguageChoiceRow(
                    title = stringResource(R.string.language_english),
                    selected = currentLanguage != "ms",
                    onClick = { setHomiqLanguage("en") },
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.settings_about),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                        Text(
                            stringResource(R.string.about_version_value, BuildConfig.VERSION_NAME),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            stringResource(R.string.about_private_body),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            content = content,
        )
    }
}

@Composable
private fun LanguageChoiceRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            androidx.compose.material3.Icon(
                Icons.Outlined.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(9.dp),
            )
        }
        Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        RadioButton(selected = selected, onClick = onClick)
    }
}

private fun setHomiqLanguage(languageTag: String) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
}
