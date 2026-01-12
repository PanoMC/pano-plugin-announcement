package com.panomc.plugins.announcement.db.migration

import com.panomc.platform.annotation.Migration
import com.panomc.platform.db.DatabaseMigration
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.SqlClient

@Migration
class AnnouncementMigration3to4(
    private val announcementDao: AnnouncementDao
) : DatabaseMigration(3, 4, "Add external column to announcement table") {
    override val handlers: List<suspend (SqlClient) -> Unit> = listOf(
        addExternalColumn()
    )

    private fun addExternalColumn(): suspend (sqlClient: SqlClient) -> Unit =
        { sqlClient: SqlClient ->
            val query = "ALTER TABLE `${announcementDao.getTablePrefix() + "announcement"}` ADD COLUMN `external` TINYINT(1) NOT NULL DEFAULT 0 AFTER `location`"
            sqlClient.query(query).execute().coAwait()
        }
}
