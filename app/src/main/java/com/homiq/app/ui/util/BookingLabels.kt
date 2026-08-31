package com.homiq.app.ui.util

import androidx.annotation.StringRes
import com.homiq.app.R
import com.homiq.app.data.model.BookingSource
import com.homiq.app.data.model.BookingStatus
import com.homiq.app.domain.BookingSaveIssue
import com.homiq.app.domain.BlockedDateSaveIssue
import com.homiq.app.domain.PropertySaveIssue

@StringRes
fun BookingSource.labelRes(): Int = when (this) {
    BookingSource.WHATSAPP -> R.string.source_whatsapp
    BookingSource.AIRBNB -> R.string.source_airbnb
    BookingSource.BOOKING_COM -> R.string.source_booking_com
    BookingSource.FACEBOOK -> R.string.source_facebook
    BookingSource.TIKTOK -> R.string.source_tiktok
    BookingSource.REPEAT_GUEST -> R.string.source_repeat_guest
    BookingSource.WALK_IN -> R.string.source_walk_in
    BookingSource.OTHER -> R.string.source_other
}

@StringRes
fun BookingStatus.labelRes(): Int = when (this) {
    BookingStatus.PENDING -> R.string.status_pending
    BookingStatus.CONFIRMED -> R.string.status_confirmed
    BookingStatus.CHECKED_IN -> R.string.status_checked_in
    BookingStatus.CHECKED_OUT -> R.string.status_checked_out
    BookingStatus.CANCELLED -> R.string.status_cancelled
}

@StringRes
fun BookingSaveIssue.messageRes(): Int = when (this) {
    BookingSaveIssue.PROPERTY_REQUIRED -> R.string.error_property_required
    BookingSaveIssue.PROPERTY_NOT_FOUND -> R.string.error_property_not_found
    BookingSaveIssue.GUEST_REQUIRED -> R.string.error_guest_required
    BookingSaveIssue.INVALID_DATES -> R.string.error_invalid_dates
    BookingSaveIssue.INVALID_AMOUNT -> R.string.error_invalid_amount
    BookingSaveIssue.BOOKING_OVERLAP -> R.string.error_booking_overlap
    BookingSaveIssue.BLOCKED_DATE_OVERLAP -> R.string.error_blocked_overlap
}

@StringRes
fun PropertySaveIssue.messageRes(): Int = when (this) {
    PropertySaveIssue.NAME_REQUIRED -> R.string.error_property_name_required
    PropertySaveIssue.INVALID_RATE -> R.string.error_invalid_rate
}


@StringRes
fun BlockedDateSaveIssue.messageRes(): Int = when (this) {
    BlockedDateSaveIssue.PROPERTY_REQUIRED ->
        R.string.error_property_required
    BlockedDateSaveIssue.PROPERTY_NOT_FOUND ->
        R.string.error_property_not_found
    BlockedDateSaveIssue.INVALID_DATES ->
        R.string.error_invalid_block_dates
    BlockedDateSaveIssue.BOOKING_OVERLAP ->
        R.string.error_block_booking_overlap
    BlockedDateSaveIssue.BLOCKED_DATE_OVERLAP ->
        R.string.error_block_overlap
}
