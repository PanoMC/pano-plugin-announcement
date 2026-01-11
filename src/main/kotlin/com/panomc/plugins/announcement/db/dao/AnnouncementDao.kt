package com.panomc.plugins.announcement.db.dao

import com.panomc.platform.db.Dao
import com.panomc.plugins.announcement.db.model.Announcement
import io.vertx.sqlclient.SqlClient

abstract class AnnouncementDao : Dao<Announcement>(Announcement::class.java) {
    abstract suspend fun add(announcement: Announcement, sqlClient: SqlClient): Long

    abstract suspend fun update(announcement: Announcement, sqlClient: SqlClient)

    abstract suspend fun getAllByStatus(page: Long, status: Boolean?, sqlClient: SqlClient): List<Announcement>

    abstract suspend fun count(status: Boolean?, sqlClient: SqlClient): Long

    abstract suspend fun deleteById(id: Long, sqlClient: SqlClient)

    abstract suspend fun getById(id: Long, sqlClient: SqlClient): Announcement?

    abstract suspend fun getByImageFileName(imageFileName: String, sqlClient: SqlClient): Announcement?

    abstract suspend fun getAllActive(sqlClient: SqlClient): List<Announcement>

    abstract suspend fun getAll(sqlClient: SqlClient): List<Announcement>
}