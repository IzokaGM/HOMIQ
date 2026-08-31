package com.homiq.app.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homiq.app.data.backup.BackupFailureReason
import com.homiq.app.data.backup.BackupHistory
import com.homiq.app.data.backup.BackupPreview
import com.homiq.app.data.backup.BackupReadResult
import com.homiq.app.data.backup.BackupRestoreResult
import com.homiq.app.data.backup.BackupWriteResult
import com.homiq.app.data.backup.HomiqBackupService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BackupUiState(
    val isBusy: Boolean = false,
    val history: BackupHistory =
        BackupHistory(
            lastBackupEpochMillis = null,
            lastRestoreEpochMillis = null,
        ),
    val pendingRestoreUri: Uri? = null,
    val pendingRestorePreview: BackupPreview? = null,
    val message: BackupUiMessage? = null,
)

sealed interface BackupUiMessage {
    data class BackupCreated(
        val preview: BackupPreview,
    ) : BackupUiMessage

    data class RestoreCompleted(
        val preview: BackupPreview,
    ) : BackupUiMessage

    data class Failure(
        val reason: BackupFailureReason,
    ) : BackupUiMessage
}

class BackupViewModel(
    private val service: HomiqBackupService,
) : ViewModel() {
    private val mutableState =
        MutableStateFlow(
            BackupUiState(
                history = service.history(),
            ),
        )

    val state: StateFlow<BackupUiState> =
        mutableState.asStateFlow()

    fun backupFileName(): String {
        val stamp =
            LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd-HHmm",
                ),
            )
        return "HOMIQU-backup-$stamp.homiqu.json"
    }

    fun createBackup(
        uri: Uri,
    ) {
        viewModelScope.launch {
            updateBusy(true)

            when (
                val result =
                    service.writeBackup(uri)
            ) {
                is BackupWriteResult.Success -> {
                    mutableState.value =
                        mutableState.value.copy(
                            isBusy = false,
                            history =
                                service.history(),
                            message =
                                BackupUiMessage
                                    .BackupCreated(
                                        result.preview,
                                    ),
                        )
                }

                is BackupWriteResult.Failure -> {
                    fail(result.reason)
                }
            }
        }
    }

    fun inspectRestore(
        uri: Uri,
    ) {
        viewModelScope.launch {
            updateBusy(true)

            when (
                val result =
                    service.inspectBackup(uri)
            ) {
                is BackupReadResult.Success -> {
                    mutableState.value =
                        mutableState.value.copy(
                            isBusy = false,
                            pendingRestoreUri = uri,
                            pendingRestorePreview =
                                result.preview,
                            message = null,
                        )
                }

                is BackupReadResult.Failure -> {
                    fail(result.reason)
                }
            }
        }
    }

    fun confirmRestore() {
        val uri =
            mutableState.value.pendingRestoreUri
                ?: return

        viewModelScope.launch {
            updateBusy(true)

            when (
                val result =
                    service.restoreBackup(uri)
            ) {
                is BackupRestoreResult.Success -> {
                    mutableState.value =
                        mutableState.value.copy(
                            isBusy = false,
                            history =
                                service.history(),
                            pendingRestoreUri = null,
                            pendingRestorePreview = null,
                            message =
                                BackupUiMessage
                                    .RestoreCompleted(
                                        result.preview,
                                    ),
                        )
                }

                is BackupRestoreResult.Failure -> {
                    mutableState.value =
                        mutableState.value.copy(
                            pendingRestoreUri = null,
                            pendingRestorePreview = null,
                        )
                    fail(result.reason)
                }
            }
        }
    }

    fun cancelRestore() {
        mutableState.value =
            mutableState.value.copy(
                pendingRestoreUri = null,
                pendingRestorePreview = null,
            )
    }

    fun clearMessage() {
        mutableState.value =
            mutableState.value.copy(
                message = null,
            )
    }

    private fun updateBusy(
        busy: Boolean,
    ) {
        mutableState.value =
            mutableState.value.copy(
                isBusy = busy,
                message = null,
            )
    }

    private fun fail(
        reason: BackupFailureReason,
    ) {
        mutableState.value =
            mutableState.value.copy(
                isBusy = false,
                message =
                    BackupUiMessage.Failure(
                        reason,
                    ),
            )
    }
}
