package com.homiq.app.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.homiq.app.BuildConfig
import com.homiq.app.R
import com.homiq.app.data.preferences.AppearanceMode
import com.homiq.app.data.preferences.AppearancePreferences
import com.homiq.app.ui.components.HomikaBrandMark

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
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val explicitLocales = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    val currentLanguage = if (explicitLocales.isBlank()) {
        configuration.locales[0].language
    } else {
        explicitLocales.substringBefore(",").substringBefore("-")
    }
    val appearancePreferences = remember(context) { AppearancePreferences(context.applicationContext) }
    var appearanceMode by remember { mutableStateOf(appearancePreferences.mode) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CompactHeader()

        CompactSection(stringResource(R.string.settings_account)) {
            CompactSettingRow(
                icon = Icons.Outlined.Person,
                title = stringResource(R.string.account),
                supporting = stringResource(
                    if (syncEnabled) R.string.account_connected_supporting
                    else R.string.account_local_supporting,
                ),
                trailing = stringResource(
                    if (syncEnabled) R.string.sync_connected else R.string.optional,
                ),
                onClick = onSyncClick,
            )
        }

        CompactSection(stringResource(R.string.settings_workspace)) {
            CompactSettingRow(
                icon = Icons.Outlined.HomeWork,
                title = stringResource(R.string.properties),
                supporting = stringResource(R.string.properties_compact_supporting),
                onClick = onPropertiesClick,
            )
        }

        CompactSection(stringResource(R.string.settings_data)) {
            CompactSettingRow(
                icon = Icons.Outlined.CloudSync,
                title = stringResource(R.string.sync_title),
                trailing = stringResource(
                    if (syncEnabled) R.string.sync_connected else R.string.sync_not_connected,
                ),
                onClick = onSyncClick,
            )
            CompactDivider()
            CompactSettingRow(
                icon = Icons.Outlined.Backup,
                title = stringResource(R.string.backup_restore),
                onClick = onBackupClick,
            )
            CompactDivider()
            CompactSettingRow(
                icon = Icons.Outlined.Lock,
                title = stringResource(R.string.security),
                trailing = stringResource(
                    if (appLockEnabled) R.string.on else R.string.off,
                ),
                onClick = onSecurityClick,
            )
        }

        CompactSection(stringResource(R.string.settings_preferences)) {
            SelectorSettingRow(
                icon = Icons.Outlined.Language,
                title = stringResource(R.string.language),
            ) {
                TinyChoice(
                    label = "MY",
                    selected = currentLanguage == "ms",
                    onClick = { setHomikaLanguage("ms") },
                )
                TinyChoice(
                    label = "EN",
                    selected = currentLanguage != "ms",
                    onClick = { setHomikaLanguage("en") },
                )
            }
            CompactDivider()
            SelectorSettingRow(
                icon = Icons.Outlined.Palette,
                title = stringResource(R.string.appearance),
            ) {
                TinyChoice(
                    label = stringResource(R.string.theme_system_short),
                    selected = appearanceMode == AppearanceMode.SYSTEM,
                    onClick = {
                        appearanceMode = AppearanceMode.SYSTEM
                        appearancePreferences.set(AppearanceMode.SYSTEM)
                    },
                )
                TinyChoice(
                    label = stringResource(R.string.theme_light_short),
                    selected = appearanceMode == AppearanceMode.LIGHT,
                    onClick = {
                        appearanceMode = AppearanceMode.LIGHT
                        appearancePreferences.set(AppearanceMode.LIGHT)
                    },
                )
                TinyChoice(
                    label = stringResource(R.string.theme_dark_short),
                    selected = appearanceMode == AppearanceMode.DARK,
                    onClick = {
                        appearanceMode = AppearanceMode.DARK
                        appearancePreferences.set(AppearanceMode.DARK)
                    },
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.about_version_compact, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(2.dp))
    }
}

@Composable
private fun CompactHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HomikaBrandMark(modifier = Modifier.size(38.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.more_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.more_compact_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CompactSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
        ) {
            // Surface lays siblings in a Box by default. A Column is mandatory here;
            // otherwise multiple settings are drawn on top of one another.
            Column(content = { content() })
        }
    }
}

@Composable
private fun CompactSettingRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    supporting: String? = null,
    trailing: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = if (supporting == null) 9.dp else 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(7.dp).size(18.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!supporting.isNullOrBlank()) {
                Text(
                    text = supporting,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (!trailing.isNullOrBlank()) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
            )
        } else {
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun SelectorSettingRow(
    icon: ImageVector,
    title: String,
    choices: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(7.dp).size(18.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            choices()
        }
    }
}

@Composable
private fun TinyChoice(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)) else null,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun CompactDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 52.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
    )
}

private fun setHomikaLanguage(languageTag: String) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
}
