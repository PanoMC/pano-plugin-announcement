package com.panomc.plugins.announcement.db.migration

import com.panomc.platform.annotation.Migration
import com.panomc.platform.db.DatabaseMigration
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.SqlClient

@Migration
class AnnouncementMigration4to5(
    private val announcementDao: AnnouncementDao
) : DatabaseMigration(4, 5, "Add alertStyle and textAlign columns to announcement table") {
    override val handlers: List<suspend (SqlClient) -> Unit> = listOf(
        addAlertStyleColumn(),
        addTextAlignColumn()
    )

    private fun addAlertStyleColumn(): suspend (sqlClient: SqlClient) -> Unit =
        { sqlClient: SqlClient ->
            val query = "ALTER TABLE `${announcementDao.getTablePrefix() + "announcement"}` ADD COLUMN `alertStyle` VARCHAR(255) NOT NULL DEFAULT 'INFO' AFTER `external`"
            sqlClient.query(query).execute().coAwait()
        }

    private fun addTextAlignColumn(): suspend (sqlClient: SqlClient) -> Unit =
        { sqlClient: SqlClient ->
            val query = "ALTER TABLE `${announcementDao.getTablePrefix() + "announcement"}` ADD COLUMN `textAlign` VARCHAR(255) NOT NULL DEFAULT 'CENTER' AFTER `alertStyle`"
            sqlClient.query(query).execute().coAwait()
        }
}
