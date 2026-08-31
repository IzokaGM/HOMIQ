package com.homiq.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.homiq.app.R
import com.homiq.app.data.backup.BackupPreview
import com.homiq.app.ui.components.InfoCard
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.util.formatBackupTime
import com.homiq.app.ui.util.messageRes
import com.homiq.app.ui.viewmodel.BackupUiMessage
import com.homiq.app.ui.viewmodel.BackupViewModel

@Composable
fun BackupScreen(
    viewModel: BackupViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state
        .collectAsStateWithLifecycle()
    val locale =
        LocalConfiguration.current.locales[0]

    val createLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .CreateDocument(
                        "application/json",
                    ),
        ) { uri ->
            if (uri != null) {
                viewModel.createBackup(uri)
            }
        }

    val openLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .OpenDocument(),
        ) { uri ->
            if (uri != null) {
                viewModel.inspectRestore(uri)
            }
        }

    val lastBackup =
        formatBackupTime(
            state.history
                .lastBackupEpochMillis,
            locale,
        ) ?: stringResource(R.string.never)

    val lastRestore =
        formatBackupTime(
            state.history
                .lastRestoreEpochMillis,
            locale,
        ) ?: stringResource(R.string.never)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 24.dp,
            end = 20.dp,
            bottom = 40.dp,
        ),
        verticalArrangement =
            Arrangement.spacedBy(18.dp),
    ) {
        item {
            ScreenHeader(
                title =
                    stringResource(
                        R.string.backup_restore_title,
                    ),
                subtitle =
                    stringResource(
                        R.string.backup_restore_subtitle,
                    ),
            )
        }

        item {
            InfoCard(
                title =
                    stringResource(
                        R.string.backup_zero_cost_title,
                    ),
                body =
                    stringResource(
                        R.string.backup_zero_cost_body,
                    ),
            )
        }

        item {
            InfoCard(
                title =
                    stringResource(
                        R.string.backup_last_backup,
                    ),
                body = lastBackup,
            )
        }

        item {
            InfoCard(
                title =
                    stringResource(
                        R.string.backup_last_restore,
                    ),
                body = lastRestore,
            )
        }

        if (state.isBusy) {
            item {
                androidx.compose.foundation.layout.Box(
                    modifier =
                        Modifier.fillMaxWidth(),
                    contentAlignment =
                        Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        item {
            Button(
                onClick = {
                    createLauncher.launch(
                        viewModel.backupFileName(),
                    )
                },
                enabled = !state.isBusy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector =
                        Icons.Outlined.CloudUpload,
                    contentDescription = null,
                )
                Text(
                    text =
                        stringResource(
                            R.string.create_backup,
                        ),
                    modifier =
                        Modifier.padding(start = 8.dp),
                )
            }
        }

        item {
            Text(
                text =
                    stringResource(
                        R.string.backup_picker_hint,
                    ),
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant,
            )
        }

        item {
            OutlinedButton(
                onClick = {
                    openLauncher.launch(
                        arrayOf(
                            "application/json",
                            "application/octet-stream",
                            "text/plain",
                        ),
                    )
                },
                enabled = !state.isBusy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector =
                        Icons.Outlined.Restore,
                    contentDescription = null,
                )
                Text(
                    text =
                        stringResource(
                            R.string.restore_backup,
                        ),
                    modifier =
                        Modifier.padding(start = 8.dp),
                )
            }
        }

        item {
            InfoCard(
                title =
                    stringResource(
                        R.string.restore_warning_title,
                    ),
                body =
                    stringResource(
                        R.string.restore_warning_body,
                    ),
            )
        }
    }

    state.pendingRestorePreview?.let {
        RestoreConfirmationDialog(
            preview = it,
            locale = locale,
            onConfirm =
                viewModel::confirmRestore,
            onDismiss =
                viewModel::cancelRestore,
        )
    }

    state.message?.let { message ->
        BackupResultDialog(
            message = message,
            locale = locale,
            onDismiss =
                viewModel::clearMessage,
        )
    }
}

@Composable
private fun RestoreConfirmationDialog(
    preview: BackupPreview,
    locale: java.util.Locale,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    R.string.restore_confirm_title,
                ),
            )
        },
        text = {
            Text(
                stringResource(
                    R.string.restore_confirm_body,
                    formatBackupTime(
                        preview
                            .createdAtEpochMillis,
                        locale,
                    ).orEmpty(),
                    preview.propertyCount,
                    preview.bookingCount,
                    preview.paymentCount,
                    preview.expenseCount,
                    preview.totalRecordCount,
                ),
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
            ) {
                Text(
                    stringResource(
                        R.string.restore_now,
                    ),
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(
                    stringResource(
                        R.string.cancel,
                    ),
                )
            }
        },
    )
}

@Composable
private fun BackupResultDialog(
    message: BackupUiMessage,
    locale: java.util.Locale,
    onDismiss: () -> Unit,
) {
    val title: String
    val body: String

    when (message) {
        is BackupUiMessage.BackupCreated -> {
            title =
                stringResource(
                    R.string.backup_success_title,
                )
            body =
                stringResource(
                    R.string.backup_success_body,
                    message.preview
                        .totalRecordCount,
                )
        }

        is BackupUiMessage.RestoreCompleted -> {
            title =
                stringResource(
                    R.string.restore_success_title,
                )
            body =
                stringResource(
                    R.string.restore_success_body,
                    message.preview
                        .totalRecordCount,
                    formatBackupTime(
                        message.preview
                            .createdAtEpochMillis,
                        locale,
                    ).orEmpty(),
                )
        }

        is BackupUiMessage.Failure -> {
            title =
                stringResource(
                    R.string.backup_error_title,
                )
            body =
                stringResource(
                    message.reason.messageRes(),
                )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(
                    stringResource(R.string.ok),
                )
            }
        },
    )
}
