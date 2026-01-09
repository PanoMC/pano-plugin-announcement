package com.panomc.plugins.announcement.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.error.PageNotFound
import com.panomc.platform.model.*
import com.panomc.plugins.announcement.AnnouncementPlugin
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import com.panomc.plugins.announcement.permission.ManageAnnouncementsPermission
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Parameters.optionalParam
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.booleanSchema
import io.vertx.json.schema.common.dsl.Schemas.numberSchema
import kotlin.math.ceil

@Endpoint
class PanelGetAnnouncementsAPI(
    private val plugin: AnnouncementPlugin,
    private val announcementDao: AnnouncementDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/announcements", RouteType.GET))

    private val authProvider: AuthProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager: DatabaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .queryParameter(optionalParam("status", booleanSchema()))
            .queryParameter(optionalParam("page", numberSchema()))
            .build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageAnnouncementsPermission(), context)

        val parameters = getParameters(context)
        val status = parameters.queryParameter("status")?.boolean
        val page = parameters.queryParameter("page")?.long ?: 1L

        val sqlClient = databaseManager.getSqlClient()
        val announcements = announcementDao.getAllByStatus(page, status, sqlClient)
        val count = announcementDao.count(status, sqlClient)

        val totalPageNum = ceil(count.toDouble() / 10).toLong()

        if (totalPageNum in 1..<page) {
            throw PageNotFound()
        }

        return Successful(
            mapOf(
                "announcements" to announcements,
                "totalPage" to totalPageNum,
                "announcementCount" to count
            )
        )
    }
}