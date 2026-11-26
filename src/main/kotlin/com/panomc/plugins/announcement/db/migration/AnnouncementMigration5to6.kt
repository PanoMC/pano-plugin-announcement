package com.panomc.plugins.announcement.db.migration

import com.panomc.platform.annotation.Migration
import com.panomc.platform.db.DatabaseMigration
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.SqlClient

@Migration
class AnnouncementMigration5to6(
    private val announcementDao: AnnouncementDao
) : DatabaseMigration(5, 6, "Add displayOptions column to announcement table") {
    override val handlers: List<suspend (SqlClient) -> Unit> = listOf(
        addDisplayOptionsColumn()
    )

    private fun addDisplayOptionsColumn(): suspend (sqlClient: SqlClient) -> Unit =
        { sqlClient: SqlClient ->
            val query = "ALTER TABLE `${announcementDao.getTablePrefix() + "announcement"}` ADD COLUMN `displayOptions` MEDIUMTEXT AFTER `textAlign`"
            sqlClient.query(query).execute().coAwait()
        }
}
