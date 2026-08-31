package com.homiq.app.data.local

import androidx.room.migration.Migration

object HomiqMigrations {
    /*
     * Database version 1 is the initial HOMIQ schema, so there is no migration
     * path yet. Future schema changes must add explicit Migration instances
     * here. Destructive migration is intentionally not enabled.
     */
    val ALL: Array<Migration> = emptyArray()
}
