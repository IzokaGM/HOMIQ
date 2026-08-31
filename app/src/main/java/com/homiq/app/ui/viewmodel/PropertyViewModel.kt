package com.homiq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homiq.app.data.local.entity.PropertyEntity
import com.homiq.app.data.repository.PropertyRepository
import com.homiq.app.domain.PropertyDraft
import com.homiq.app.domain.PropertySaveIssue
import com.homiq.app.domain.PropertySaveResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PropertyViewModel(
    private val properties: PropertyRepository,
) : ViewModel() {
    val propertyList: StateFlow<List<PropertyEntity>> =
        properties.observeAll().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    suspend fun save(
        draft: PropertyDraft,
    ): PropertySaveResult {
        if (draft.name.isBlank()) {
            return PropertySaveResult.Failure(
                PropertySaveIssue.NAME_REQUIRED,
            )
        }
        if (draft.defaultNightlyRateSen < 0L) {
            return PropertySaveResult.Failure(
                PropertySaveIssue.INVALID_RATE,
            )
        }

        val existing = if (draft.id != null) {
            properties.getById(draft.id)
        } else {
            null
        }

        val entity = PropertyEntity(
            id = existing?.id ?: draft.id
                ?: java.util.UUID.randomUUID().toString(),
            name = draft.name.trim(),
            address = draft.address.trim().ifBlank { null },
            notes = draft.notes.trim().ifBlank { null },
            defaultNightlyRateSen = draft.defaultNightlyRateSen,
            isActive = draft.isActive,
            createdAtEpochMillis = existing?.createdAtEpochMillis
                ?: System.currentTimeMillis(),
            updatedAtEpochMillis = existing?.updatedAtEpochMillis
                ?: System.currentTimeMillis(),
            revision = existing?.revision ?: 0L,
            isDeleted = existing?.isDeleted ?: false,
        )

        properties.save(entity)
        return PropertySaveResult.Success(entity.id)
    }
}
