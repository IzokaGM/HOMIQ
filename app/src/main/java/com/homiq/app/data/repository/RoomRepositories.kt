package com.homiq.app.data.repository

import com.homiq.app.data.local.dao.BlockedDateDao
import com.homiq.app.data.local.dao.BookingDao
import com.homiq.app.data.local.dao.DepositDao
import com.homiq.app.data.local.dao.ExpenseDao
import com.homiq.app.data.local.dao.PaymentDao
import com.homiq.app.data.local.dao.PropertyDao
import com.homiq.app.data.local.entity.BlockedDateEntity
import com.homiq.app.data.local.entity.BookingEntity
import com.homiq.app.data.local.entity.DepositEntity
import com.homiq.app.data.local.entity.ExpenseEntity
import com.homiq.app.data.local.entity.PaymentEntity
import com.homiq.app.data.local.entity.PropertyEntity
import com.homiq.app.data.local.model.BookingBalanceRow
import kotlinx.coroutines.flow.Flow

private fun now(): Long = System.currentTimeMillis()

private fun PropertyEntity.nextRevision(timestamp: Long) = copy(
    updatedAtEpochMillis = timestamp,
    revision = revision + 1,
)

private fun BookingEntity.nextRevision(timestamp: Long) = copy(
    updatedAtEpochMillis = timestamp,
    revision = revision + 1,
)

private fun PaymentEntity.nextRevision(timestamp: Long) = copy(
    updatedAtEpochMillis = timestamp,
    revision = revision + 1,
)

private fun DepositEntity.nextRevision(timestamp: Long) = copy(
    updatedAtEpochMillis = timestamp,
    revision = revision + 1,
)

private fun ExpenseEntity.nextRevision(timestamp: Long) = copy(
    updatedAtEpochMillis = timestamp,
    revision = revision + 1,
)

private fun BlockedDateEntity.nextRevision(timestamp: Long) = copy(
    updatedAtEpochMillis = timestamp,
    revision = revision + 1,
)

class RoomPropertyRepository(
    private val dao: PropertyDao,
) : PropertyRepository {
    override fun observeAll(): Flow<List<PropertyEntity>> = dao.observeAll()

    override fun observeActive(): Flow<List<PropertyEntity>> = dao.observeActive()

    override suspend fun getById(id: String): PropertyEntity? = dao.getById(id)

    override suspend fun save(entity: PropertyEntity) {
        dao.upsert(entity.nextRevision(now()))
    }

    override suspend fun delete(id: String) {
        dao.softDelete(id, now())
    }
}

class RoomBookingRepository(
    private val dao: BookingDao,
) : BookingRepository {
    override fun observeAll(): Flow<List<BookingEntity>> = dao.observeAll()

    override fun observeByProperty(propertyId: String): Flow<List<BookingEntity>> =
        dao.observeByProperty(propertyId)

    override fun observeInRange(
        rangeStart: Long,
        rangeEndExclusive: Long,
    ): Flow<List<BookingEntity>> =
        dao.observeInRange(rangeStart, rangeEndExclusive)

    override suspend fun getById(id: String): BookingEntity? = dao.getById(id)

    override suspend fun findOverlaps(
        propertyId: String,
        checkIn: Long,
        checkOutExclusive: Long,
        excludeBookingId: String,
    ): List<BookingEntity> =
        dao.findOverlaps(
            propertyId = propertyId,
            checkIn = checkIn,
            checkOutExclusive = checkOutExclusive,
            excludeBookingId = excludeBookingId,
        )

    override suspend fun save(entity: BookingEntity) {
        dao.upsert(entity.nextRevision(now()))
    }

    override suspend fun delete(id: String) {
        dao.softDelete(id, now())
    }
}

class RoomPaymentRepository(
    private val dao: PaymentDao,
) : PaymentRepository {
    override fun observeBookingBalances(): Flow<List<BookingBalanceRow>> =
        dao.observeBookingBalances()

    override fun observeForBooking(bookingId: String): Flow<List<PaymentEntity>> =
        dao.observeForBooking(bookingId)

    override fun observeTotalPaidSen(bookingId: String): Flow<Long> =
        dao.observeTotalPaidSen(bookingId)

    override suspend fun getById(id: String): PaymentEntity? = dao.getById(id)

    override suspend fun save(entity: PaymentEntity) {
        dao.upsert(entity.nextRevision(now()))
    }

    override suspend fun delete(id: String) {
        dao.softDelete(id, now())
    }
}

class RoomDepositRepository(
    private val dao: DepositDao,
) : DepositRepository {
    override fun observeForBooking(bookingId: String): Flow<DepositEntity?> =
        dao.observeForBooking(bookingId)

    override suspend fun getById(id: String): DepositEntity? = dao.getById(id)

    override suspend fun save(entity: DepositEntity) {
        dao.upsert(entity.nextRevision(now()))
    }

    override suspend fun delete(id: String) {
        dao.softDelete(id, now())
    }
}

class RoomExpenseRepository(
    private val dao: ExpenseDao,
) : ExpenseRepository {
    override fun observeAll(): Flow<List<ExpenseEntity>> = dao.observeAll()

    override fun observeInRange(
        startEpochDay: Long,
        endEpochDayExclusive: Long,
    ): Flow<List<ExpenseEntity>> =
        dao.observeInRange(startEpochDay, endEpochDayExclusive)

    override fun observeTotalInRangeSen(
        startEpochDay: Long,
        endEpochDayExclusive: Long,
    ): Flow<Long> =
        dao.observeTotalInRangeSen(startEpochDay, endEpochDayExclusive)

    override suspend fun getById(id: String): ExpenseEntity? = dao.getById(id)

    override suspend fun save(entity: ExpenseEntity) {
        dao.upsert(entity.nextRevision(now()))
    }

    override suspend fun delete(id: String) {
        dao.softDelete(id, now())
    }
}

class RoomBlockedDateRepository(
    private val dao: BlockedDateDao,
) : BlockedDateRepository {
    override fun observeInRange(
        rangeStart: Long,
        rangeEndExclusive: Long,
    ): Flow<List<BlockedDateEntity>> =
        dao.observeInRange(rangeStart, rangeEndExclusive)

    override suspend fun findOverlaps(
        propertyId: String,
        start: Long,
        endExclusive: Long,
        excludeBlockId: String,
    ): List<BlockedDateEntity> =
        dao.findOverlaps(
            propertyId = propertyId,
            start = start,
            endExclusive = endExclusive,
            excludeBlockId = excludeBlockId,
        )

    override suspend fun getById(id: String): BlockedDateEntity? = dao.getById(id)

    override suspend fun save(entity: BlockedDateEntity) {
        dao.upsert(entity.nextRevision(now()))
    }

    override suspend fun delete(id: String) {
        dao.softDelete(id, now())
    }
}
