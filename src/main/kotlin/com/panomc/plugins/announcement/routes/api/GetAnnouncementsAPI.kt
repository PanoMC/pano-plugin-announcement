package com.panomc.plugins.announcement.routes.api

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.*
import com.panomc.plugins.announcement.AnnouncementPlugin
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository

@Endpoint
class GetAnnouncementsAPI(
    private val plugin: AnnouncementPlugin,
    private val announcementDao: AnnouncementDao
) : Api() {
    override val paths = listOf(Path("/api/announcements", RouteType.GET))

    private val databaseManager: DatabaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .build()

    override suspend fun handle(context: RoutingContext): Result {
        val sqlClient = databaseManager.getSqlClient()
        
        // Fetch all currently visible announcements
        val visibleAnnouncements = announcementDao.getAllVisible(null, sqlClient)

        return Successful(
            mapOf(
                "data" to visibleAnnouncements
            )
        )
    }
}
