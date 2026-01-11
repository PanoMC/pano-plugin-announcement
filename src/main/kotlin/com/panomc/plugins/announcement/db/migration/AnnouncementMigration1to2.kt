package com.panomc.plugins.announcement.db.migration

import com.panomc.platform.annotation.Migration
import com.panomc.platform.db.DatabaseMigration
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.SqlClient

@Migration
class AnnouncementMigration1to2(
    private val announcementDao: AnnouncementDao
) : DatabaseMigration(1, 2, "Add closeable column to announcement table") {
    override val handlers: List<suspend (SqlClient) -> Unit> = listOf(
        addCloseableColumn()
    )

    private fun addCloseableColumn(): suspend (sqlClient: SqlClient) -> Unit =
        { sqlClient: SqlClient ->
            val query1 = "ALTER TABLE `${announcementDao.getTablePrefix() + "announcement"}` ADD COLUMN `closeable` TINYINT(1) NOT NULL DEFAULT 1 AFTER `imageFileName`"
            val query2 = "ALTER TABLE `${announcementDao.getTablePrefix() + "announcement"}` ADD COLUMN `showFrom` BIGINT NULL AFTER `closeable`"
            sqlClient.query(query1).execute().coAwait()
            sqlClient.query(query2).execute().coAwait()
        }
}
