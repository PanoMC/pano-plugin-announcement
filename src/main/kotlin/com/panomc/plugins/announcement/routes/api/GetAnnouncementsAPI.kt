package com.panomc.plugins.announcement.routes.api

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.*
import com.panomc.plugins.announcement.AnnouncementPlugin
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import com.panomc.platform.schema.dsl.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import com.panomc.platform.schema.EndpointDoc
import io.vertx.json.schema.common.dsl.Schemas.*

@Endpoint
class GetAnnouncementsAPI(
    private val plugin: AnnouncementPlugin,
    private val announcementDao: AnnouncementDao
) : Api() {
    override val paths = listOf(Path("/announcements", RouteType.GET))

    override val doc = EndpointDoc(
        summary = "The announcements that are visible right now.",
        tag = "announcements",
        response = objectSchema()
            .requiredProperty(
                "items",
                arraySchema().items(
                    objectSchema()
                    .requiredProperty("id", intSchema())
                    .requiredProperty("title", stringSchema())
                    .requiredProperty("status", booleanSchema())
                    .requiredProperty("type", stringSchema())
                    .requiredProperty("contents", arraySchema().items(stringSchema()))
                    .optionalProperty("link", stringSchema())
                    .optionalProperty("effectType", stringSchema().nullable())
                    .optionalProperty("until", intSchema().nullable())
                    .optionalProperty("customCss", stringSchema())
                    .optionalProperty("size", intSchema().nullable())
                    .optionalProperty("displayFrequency", stringSchema().nullable())
                    .optionalProperty("imageFileName", stringSchema().nullable())
                    .optionalProperty("closeable", booleanSchema())
                    .optionalProperty("showFrom", intSchema().nullable())
                    .optionalProperty("location", stringSchema())
                    .optionalProperty("external", booleanSchema())
                    .optionalProperty("alertStyle", stringSchema())
                    .optionalProperty("textAlign", stringSchema())
                    .optionalProperty("createdAt", intSchema())
                    .optionalProperty("updatedAt", intSchema())
                )
            )
    )

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
                "items" to visibleAnnouncements
            )
        )
    }
}
