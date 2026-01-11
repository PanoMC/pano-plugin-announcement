package com.panomc.plugins.announcement.db.impl

import com.panomc.platform.annotation.Dao
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import com.panomc.plugins.announcement.db.model.Announcement
import io.vertx.core.json.JsonArray
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.mysqlclient.MySQLClient
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.Tuple
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope

@Dao
@Lazy
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class AnnouncementDaoImpl : AnnouncementDao() {

    override suspend fun init(sqlClient: SqlClient) {
        sqlClient
            .query(
                """
                            CREATE TABLE IF NOT EXISTS `${getTablePrefix() + tableName}` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `title` MEDIUMTEXT NOT NULL,
                              `status` TINYINT(1) NOT NULL DEFAULT 1,
                              `link` MEDIUMTEXT,
                              `type` VARCHAR(255) NOT NULL,
                              `effectType` VARCHAR(255),
                              `until` BIGINT,
                              `contents` MEDIUMTEXT,
                              `customCss` MEDIUMTEXT,
                              `size` TINYINT(1),
                              `displayFrequency` VARCHAR(255),
                              `imageFileName` VARCHAR(255),
                              `closeable` TINYINT(1) NOT NULL DEFAULT 1,
                              `showFrom` BIGINT,
                              `createdAt` BIGINT(20) NOT NULL,
                              `updatedAt` BIGINT(20) NOT NULL,
                              PRIMARY KEY (`id`)
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Announcement table.';
                        """
            )
            .execute()
            .coAwait()
    }

    override suspend fun add(announcement: Announcement, sqlClient: SqlClient): Long {
        val query =
            "INSERT INTO `${getTablePrefix() + tableName}` (`title`, `status`, `link`, `type`, `effectType`, `until`, `contents`, `customCss`, `size`, `displayFrequency`, `imageFileName`, `closeable`, `showFrom`, `createdAt`, `updatedAt`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query)
            .execute(
                Tuple.of(
                    announcement.title,
                    announcement.status,
                    announcement.link,
                    announcement.type.name,
                    announcement.effectType?.name,
                    announcement.until,
                    JsonArray(announcement.contents).encode(),
                    announcement.customCss,
                    announcement.size,
                    announcement.displayFrequency?.name,
                    announcement.imageFileName,
                    announcement.closeable,
                    announcement.showFrom,
                    announcement.createdAt,
                    announcement.updatedAt
                )
            )
            .coAwait()

        return rows.property(MySQLClient.LAST_INSERTED_ID)
    }

    override suspend fun update(announcement: Announcement, sqlClient: SqlClient) {
        val query =
            "UPDATE `${getTablePrefix() + tableName}` SET `title` = ?, `status` = ?, `link` = ?, `type` = ?, `effectType` = ?, `until` = ?, `contents` = ?, `customCss` = ?, `size` = ?, `displayFrequency` = ?, `imageFileName` = ?, `closeable` = ?, `showFrom` = ?, `updatedAt` = ? WHERE `id` = ?"

        sqlClient
            .preparedQuery(query)
            .execute(
                Tuple.of(
                    announcement.title,
                    announcement.status,
                    announcement.link,
                    announcement.type.name,
                    announcement.effectType?.name,
                    announcement.until,
                    JsonArray(announcement.contents).encode(),
                    announcement.customCss,
                    announcement.size,
                    announcement.displayFrequency?.name,
                    announcement.imageFileName,
                    announcement.closeable,
                    announcement.showFrom,
                    announcement.updatedAt,
                    announcement.id
                )
            )
            .coAwait()
    }

    override suspend fun getAllByStatus(page: Long, status: Boolean?, sqlClient: SqlClient): List<Announcement> {
        val offset = (page - 1) * 10
        val query =
            "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE (`status` = COALESCE(?, `status`)) ORDER BY `id` DESC LIMIT 10 OFFSET ?"

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(status, offset))
            .coAwait()

        return rows.toEntities()
    }

    override suspend fun count(status: Boolean?, sqlClient: SqlClient): Long {
        val query =
            "SELECT COUNT(`id`) FROM `${getTablePrefix() + tableName}` WHERE (`status` = COALESCE(?, `status`))"

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(status))
            .coAwait()

        return rows.toList()[0].getLong(0)
    }

    override suspend fun deleteById(id: Long, sqlClient: SqlClient) {
        val query = "DELETE FROM `${getTablePrefix() + tableName}` WHERE `id` = ?"

        sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(id))
            .coAwait()
    }

    override suspend fun getById(id: Long, sqlClient: SqlClient): Announcement? {
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `id` = ?"

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(id))
            .coAwait()

        return rows.toEntities().getOrNull(0)
    }

    override suspend fun getByImageFileName(imageFileName: String, sqlClient: SqlClient): Announcement? {
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `imageFileName` = ?"

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(imageFileName))
            .coAwait()

        return rows.toEntities().getOrNull(0)
    }

    override suspend fun getAllActive(sqlClient: SqlClient): List<Announcement> {
        val query =
            "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}` WHERE `status` = 1 AND (`until` IS NULL OR `until` > ?) AND (`showFrom` IS NULL OR `showFrom` <= ?)"

        val rows: RowSet<Row> = sqlClient
            .preparedQuery(query)
            .execute(Tuple.of(System.currentTimeMillis(), System.currentTimeMillis()))
            .coAwait()

        return rows.toEntities()
    }

    override suspend fun getAll(sqlClient: SqlClient): List<Announcement> {
        val query = "SELECT ${fields.toTableQuery()} FROM `${getTablePrefix() + tableName}`"

        val rows: RowSet<Row> = sqlClient
            .query(query)
            .execute()
            .coAwait()

        return rows.toEntities()
    }

    override suspend fun uninstall(sqlClient: SqlClient) {
        sqlClient
            .query("DROP TABLE IF EXISTS `${getTablePrefix() + tableName}`")
            .execute()
            .coAwait()
    }
}