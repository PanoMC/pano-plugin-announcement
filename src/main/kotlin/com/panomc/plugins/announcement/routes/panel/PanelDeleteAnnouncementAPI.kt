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
import io.vertx.ext.web.validation.builder.Parameters.param
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.numberSchema
import java.io.File

@Endpoint
class PanelDeleteAnnouncementAPI(
    private val plugin: AnnouncementPlugin,
    private val announcementDao: AnnouncementDao,
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/announcements/:id", RouteType.DELETE))

    private val authProvider: AuthProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager: DatabaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .pathParameter(param("id", numberSchema()))
            .build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageAnnouncementsPermission(), context)

        val parameters = getParameters(context)
        val id = parameters.pathParameter("id").long

        val sqlClient = databaseManager.getSqlClient()
        val announcement = announcementDao.getById(id, sqlClient)

        if (announcement?.imageFileName != null) {
            deleteFile(announcement.imageFileName)
        }

        announcementDao.deleteById(id, sqlClient)

        val userId = authProvider.getUserIdFromRoutingContext(context)
        val username = databaseManager.userDao.getUsernameFromUserId(userId, sqlClient)!!

        databaseManager.panelActivityLogDao.add(
            com.panomc.plugins.announcement.log.DeletedAnnouncementLog(userId, username, plugin.pluginId, announcement?.title ?: id.toString()),
            sqlClient
        )

        return Successful()
    }

    private fun deleteFile(fileName: String) {
        val file = File(plugin.uploadsDir, fileName)
        if (file.exists()) {
            file.delete()
        }

        val thumbnailFile = File(File(plugin.uploadsDir, "thumbnails"), fileName)
        if (thumbnailFile.exists()) {
            thumbnailFile.delete()
        }
    }
}
