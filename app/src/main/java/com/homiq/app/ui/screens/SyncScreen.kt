package com.homiq.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.LinkOff
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
import com.homiq.app.ui.components.InfoCard
import com.homiq.app.ui.components.ScreenHeader
import com.homiq.app.ui.util.formatBackupTime
import com.homiq.app.ui.util.messageRes
import com.homiq.app.ui.viewmodel.SyncUiMessage
import com.homiq.app.ui.viewmodel.SyncViewModel

@Composable
fun SyncScreen(
    viewModel: SyncViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state
        .collectAsStateWithLifecycle()
    val locale =
        LocalConfiguration.current.locales[0]

    val authorizationLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .StartIntentSenderForResult(),
        ) { result ->
            viewModel.completeAuthorization(
                result.data,
            )
        }

    state.pendingResolution?.let { pending ->
        LaunchedEffect(pending) {
            viewModel.resolutionLaunched()
            authorizationLauncher.launch(
                IntentSenderRequest.Builder(
                    pending.intentSender,
                ).build(),
            )
        }
    }

    val lastSync =
        formatBackupTime(
            state.runtime
                .lastSyncEpochMillis,
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
                        R.string.sync_title,
                    ),
                subtitle =
                    stringResource(
                        R.string.sync_subtitle,
                    ),
            )
        }

        item {
            InfoCard(
                title =
                    stringResource(
                        if (state.runtime.enabled) {
                            R.string.sync_connected
                        } else {
                            R.string.sync_not_connected
                        },
                    ),
                body =
                    stringResource(
                        if (state.runtime.enabled) {
                            R.string.sync_connected_body
                        } else {
                            R.string.sync_not_connected_body
                        },
                    ),
            )
        }

        item {
            InfoCard(
                title =
                    stringResource(
                        R.string.sync_same_account_title,
                    ),
                body =
                    stringResource(
                        R.string.sync_same_account_body,
                    ),
            )
        }

        item {
            InfoCard(
                title =
                    stringResource(
                        R.string.sync_last_sync,
                    ),
                body = lastSync,
            )
        }

        if (state.runtime.enabled) {
            item {
                InfoCard(
                    title =
                        stringResource(
                            R.string.sync_devices_seen,
                        ),
                    body =
                        stringResource(
                            R.string.sync_devices_seen_body,
                            state.runtime
                                .remoteDeviceCount,
                        ),
                )
            }

            item {
                InfoCard(
                    title =
                        stringResource(
                            R.string.sync_conflicts,
                        ),
                    body =
                        stringResource(
                            R.string.sync_conflicts_body,
                            state.runtime
                                .lastConflictCount,
                        ),
                )
            }
        }

        if (state.runtime.authorizationRequired) {
            item {
                InfoCard(
                    title =
                        stringResource(
                            R.string.sync_reauthorize_title,
                        ),
                    body =
                        stringResource(
                            R.string.sync_reauthorize_body,
                        ),
                )
            }
        }

        if (state.runtime.isSyncing) {
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

        if (!state.runtime.enabled) {
            item {
                Button(
                    onClick = viewModel::connect,
                    enabled =
                        !state.runtime.isSyncing,
                    modifier =
                        Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.CloudDone,
                        contentDescription = null,
                    )
                    Text(
                        text =
                            stringResource(
                                R.string.sync_connect,
                            ),
                        modifier =
                            Modifier.padding(
                                start = 8.dp,
                            ),
                    )
                }
            }
        } else {
            item {
                Button(
                    onClick = viewModel::syncNow,
                    enabled =
                        !state.runtime.isSyncing,
                    modifier =
                        Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.CloudSync,
                        contentDescription = null,
                    )
                    Text(
                        text =
                            stringResource(
                                R.string.sync_now,
                            ),
                        modifier =
                            Modifier.padding(
                                start = 8.dp,
                            ),
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick =
                        viewModel::disconnect,
                    enabled =
                        !state.runtime.isSyncing,
                    modifier =
                        Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.LinkOff,
                        contentDescription = null,
                    )
                    Text(
                        text =
                            stringResource(
                                R.string.sync_disconnect,
                            ),
                        modifier =
                            Modifier.padding(
                                start = 8.dp,
                            ),
                    )
                }
            }
        }

        item {
            InfoCard(
                title =
                    stringResource(
                        R.string.sync_architecture_title,
                    ),
                body =
                    stringResource(
                        R.string.sync_architecture_body,
                    ),
            )
        }

        item {
            Text(
                text =
                    stringResource(
                        R.string.sync_device_id,
                        state.deviceId.take(8),
                    ),
                style =
                    MaterialTheme.typography
                        .labelMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant,
            )
        }
    }

    state.message?.let { message ->
        SyncMessageDialog(
            message = message,
            onDismiss =
                viewModel::clearMessage,
        )
    }
}

@Composable
private fun SyncMessageDialog(
    message: SyncUiMessage,
    onDismiss: () -> Unit,
) {
    val title: String
    val body: String

    when (message) {
        SyncUiMessage.SyncCompleted -> {
            title =
                stringResource(
                    R.string.sync_success_title,
                )
            body =
                stringResource(
                    R.string.sync_success_body,
                )
        }

        SyncUiMessage.Disconnected -> {
            title =
                stringResource(
                    R.string.sync_disconnected_title,
                )
            body =
                stringResource(
                    R.string.sync_disconnected_body,
                )
        }

        is SyncUiMessage.Failure -> {
            title =
                stringResource(
                    R.string.sync_error_title,
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
