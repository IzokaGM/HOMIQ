package com.homiq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.homiq.app.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query(
        """
        SELECT * FROM payments
        WHERE isDeleted = 0 AND bookingId = :bookingId
        ORDER BY paymentDateEpochDay ASC, createdAtEpochMillis ASC
        """,
    )
    fun observeForBooking(bookingId: String): Flow<List<PaymentEntity>>

    @Query(
        """
        SELECT COALESCE(SUM(amountSen), 0) FROM payments
        WHERE isDeleted = 0 AND bookingId = :bookingId
        """,
    )
    fun observeTotalPaidSen(bookingId: String): Flow<Long>

    @Query("SELECT * FROM payments WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): PaymentEntity?

    @Upsert
    suspend fun upsert(entity: PaymentEntity)

    @Query(
        """
        UPDATE payments
        SET isDeleted = 1,
            updatedAtEpochMillis = :updatedAt,
            revision = revision + 1
        WHERE id = :id
        """,
    )
    suspend fun softDelete(id: String, updatedAt: Long)
}
