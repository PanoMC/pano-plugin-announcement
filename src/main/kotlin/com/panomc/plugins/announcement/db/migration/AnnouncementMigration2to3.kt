package com.panomc.plugins.announcement.db.migration

import com.panomc.platform.annotation.Migration
import com.panomc.platform.db.DatabaseMigration
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.SqlClient

@Migration
class AnnouncementMigration2to3(
    private val announcementDao: AnnouncementDao
) : DatabaseMigration(2, 3, "Add location column to announcement table") {
    override val handlers: List<suspend (SqlClient) -> Unit> = listOf(
        addLocationColumn()
    )

    private fun addLocationColumn(): suspend (sqlClient: SqlClient) -> Unit =
        { sqlClient: SqlClient ->
            val query = "ALTER TABLE `${announcementDao.getTablePrefix() + "announcement"}` ADD COLUMN `location` VARCHAR(255) NOT NULL DEFAULT 'GLOBAL' AFTER `showFrom`"
            sqlClient.query(query).execute().coAwait()
        }
}
