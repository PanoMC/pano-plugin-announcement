package com.panomc.plugins.announcement.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.PanelApi
import com.panomc.platform.model.Path
import com.panomc.platform.model.Result
import com.panomc.platform.model.RouteType
import com.panomc.platform.util.MimeTypeUtil
import com.panomc.plugins.announcement.AnnouncementPlugin
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import com.panomc.plugins.announcement.permission.ManageAnnouncementsPermission
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Parameters.param
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.stringSchema
import java.io.File

@Endpoint
class PanelGetAnnouncementImageAPI(
    private val plugin: AnnouncementPlugin,
    private val announcementDao: AnnouncementDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/announcements/image/:fileName", RouteType.GET))

    private val authProvider: AuthProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager: DatabaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    companion object {
        private const val CACHE_TTL_SECONDS = 7 * 24 * 60 * 60 // 1 week
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .pathParameter(param("fileName", stringSchema()))
            .build()

    override suspend fun handle(context: RoutingContext): Result? {
        authProvider.requirePermission(ManageAnnouncementsPermission(), context)

        val parameters = getParameters(context)
        val fileName = parameters.pathParameter("fileName").string

        val sqlClient = databaseManager.getSqlClient()
        val announcement = announcementDao.getByImageFileName(fileName, sqlClient)

        if (announcement == null) {
            context.response().setStatusCode(404).end()
            return null
        }

        val file = File(plugin.uploadsDir, announcement.imageFileName)

        if (!file.exists()) {
            context.response().setStatusCode(404).end()
            return null
        }

        val etag = "\"${announcement.imageFileName}\""
        val mimeType = MimeTypeUtil.getMimeTypeFromFileName(file.absolutePath)

        val response = context.response()
        response.putHeader("Content-Type", mimeType)
        response.putHeader("ETag", etag)
        response.putHeader("Cache-Control", "public, max-age=$CACHE_TTL_SECONDS, immutable")

        response.sendFile(file.absolutePath)

        return null
    }
}
