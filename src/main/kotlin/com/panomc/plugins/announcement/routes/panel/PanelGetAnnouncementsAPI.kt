package com.panomc.plugins.announcement.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.*
import com.panomc.plugins.announcement.AnnouncementPlugin
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import com.panomc.plugins.announcement.permission.ManageAnnouncementsPermission
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import com.panomc.platform.schema.dsl.Parameters.optionalParam
import com.panomc.platform.schema.dsl.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.booleanSchema

@Endpoint
class PanelGetAnnouncementsAPI(
    private val plugin: AnnouncementPlugin,
    private val announcementDao: AnnouncementDao
) : PanelApi() {
    override val paths = listOf(Path("/announcements", RouteType.GET))

    private val authProvider: AuthProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager: DatabaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        Paging.params(
            ValidationHandlerBuilder.create(schemaRepository)
                .queryParameter(optionalParam("status", booleanSchema()))
                .queryParameter(optionalParam("visible", booleanSchema()))
        ).build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageAnnouncementsPermission(), context)

        val parameters = getParameters(context)
        val status = parameters.queryParameter("status")?.boolean
        val visible = parameters.queryParameter("visible")?.boolean
        val page = Paging.request(context)

        val sqlClient = databaseManager.getSqlClient()
        val count = announcementDao.count(status, visible, sqlClient)

        Paging.requireInRange(page, count)

        val announcements = announcementDao.getAllByStatus(page, status, visible, sqlClient)

        return Successful(Paging.response(announcements, count, page))
    }
}