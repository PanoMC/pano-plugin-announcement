package com.panomc.plugins.announcement.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.error.BadRequest
import com.panomc.platform.model.*
import com.panomc.plugins.announcement.AnnouncementPlugin
import com.panomc.plugins.announcement.db.dao.AnnouncementDao
import com.panomc.plugins.announcement.db.model.Announcement
import com.panomc.plugins.announcement.permission.ManageAnnouncementsPermission
import com.panomc.plugins.announcement.util.*
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.validation.RequestPredicate
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Parameters.param
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.*
import java.io.File

@Endpoint
class PanelUpdateAnnouncementAPI(
    private val plugin: AnnouncementPlugin,
    private val announcementDao: AnnouncementDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/announcements/:id", RouteType.PUT))

    private val authProvider: AuthProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager: DatabaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun bodyHandler(): Handler<RoutingContext> =
        BodyHandler.create()
            .setDeleteUploadedFilesOnEnd(true)
            .setBodyLimit(2 * 1024 * 1024) // 2MB

    override suspend fun getFailureHandler(context: RoutingContext) {
        if (context.failure() == null) {
            throw BadRequest()
        }
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .pathParameter(param("id", numberSchema()))
            .body(
                io.vertx.ext.web.validation.builder.Bodies.multipartFormData(
                    objectSchema()
                        .requiredProperty("title", stringSchema())
                        .requiredProperty("status", booleanSchema())
                        .requiredProperty("type", enumSchema(*AnnouncementType.entries.map { it.name }.toTypedArray()))
                        .requiredProperty("contents", stringSchema()) // JSON string likely
                        .optionalProperty("link", stringSchema())
                        .optionalProperty("effectType", enumSchema(*AnnouncementEffectType.entries.map { it.name }.toTypedArray()))
                        .optionalProperty("until", numberSchema())
                        .optionalProperty("showFrom", numberSchema())
                        .optionalProperty("customCss", stringSchema())
                        .optionalProperty("size", numberSchema())
                        .optionalProperty("removeImage", booleanSchema())
                        .optionalProperty("closeable", booleanSchema())
                        .optionalProperty("displayFrequency", enumSchema(*AnnouncementDisplayFrequency.entries.map { it.name }.toTypedArray()))
                        .optionalProperty("location", enumSchema(*AnnouncementLocation.entries.map { it.name }.toTypedArray()))
                        .optionalProperty("external", booleanSchema())
                )
            )
            .predicate(RequestPredicate.BODY_REQUIRED)
            .build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageAnnouncementsPermission(), context)

        val parameters = getParameters(context)
        val id = parameters.pathParameter("id").long
        val data = parameters.body().jsonObject
        val fileUploads = context.fileUploads()

        if (fileUploads.size > 1) {
            throw BadRequest()
        }

        val fileUpload = fileUploads.firstOrNull { it.name() == "image" }

        val title = data.getString("title")
        val status = data.getBoolean("status")
        val type = AnnouncementType.valueOf(data.getString("type"))
        val contentsJson = data.getString("contents")
        val contents = JsonArray(contentsJson).map { it.toString() }

        val displayFrequency = data.getString("displayFrequency")?.let { AnnouncementDisplayFrequency.valueOf(it) }
        val size = data.getInteger("size")

        validateForm(title, contents, type, fileUpload, false, displayFrequency, size)

        val sqlClient = databaseManager.getSqlClient()
        val existingAnnouncement = announcementDao.getById(id, sqlClient)

        var imageFileName: String? = existingAnnouncement?.imageFileName
        val removeImage = data.getBoolean("removeImage") ?: false

        if (removeImage || fileUpload != null) {
            existingAnnouncement?.imageFileName?.let { oldPath ->
                deleteFile(oldPath)
            }
            imageFileName = null
        }

        if (fileUpload != null) {
            imageFileName = saveUploadedFile(fileUpload)
        }

        val announcement = Announcement(
            id = id,
            title = title,
            status = status,
            link = data.getString("link") ?: "",
            type = type,
            effectType = data.getString("effectType")?.let { AnnouncementEffectType.valueOf(it) },
            until = data.getLong("until"),
            contents = contents,
            customCss = data.getString("customCss") ?: "",
            size = size,
            displayFrequency = displayFrequency,
            imageFileName = imageFileName,
            closeable = data.getBoolean("closeable") ?: true,
            showFrom = data.getLong("showFrom"),
            location = data.getString("location")?.let { AnnouncementLocation.valueOf(it) } ?: AnnouncementLocation.GLOBAL,
            external = data.getBoolean("external") ?: false,
            updatedAt = System.currentTimeMillis()
        )

        announcementDao.update(announcement, sqlClient)

        val userId = authProvider.getUserIdFromRoutingContext(context)
        val username = databaseManager.userDao.getUsernameFromUserId(userId, sqlClient)!!

        val changes = JsonObject()
        if (existingAnnouncement != null) {
            if (existingAnnouncement.title != title) changes.put("title", title)
            if (existingAnnouncement.status != status) changes.put("status", status)
            if (existingAnnouncement.link != (data.getString("link") ?: "")) changes.put("link", data.getString("link") ?: "")
            if (existingAnnouncement.type != type) changes.put("type", type.name)
            if (existingAnnouncement.effectType != (data.getString("effectType")?.let { AnnouncementEffectType.valueOf(it) })) changes.put("effectType", data.getString("effectType"))
            if (existingAnnouncement.until != data.getLong("until")) changes.put("until", data.getLong("until"))
            if (existingAnnouncement.contents != contents) changes.put("contents", JsonArray(contents))
            if (existingAnnouncement.customCss != (data.getString("customCss") ?: "")) changes.put("customCss", data.getString("customCss") ?: "")
            if (existingAnnouncement.size != size) changes.put("size", size)
            if (existingAnnouncement.displayFrequency != displayFrequency) changes.put("displayFrequency", displayFrequency?.name)
            if (existingAnnouncement.closeable != (data.getBoolean("closeable") ?: true)) changes.put("closeable", data.getBoolean("closeable") ?: true)
            if (existingAnnouncement.showFrom != data.getLong("showFrom")) changes.put("showFrom", data.getLong("showFrom"))
            if (existingAnnouncement.location != (data.getString("location")?.let { AnnouncementLocation.valueOf(it) } ?: AnnouncementLocation.GLOBAL)) changes.put("location", data.getString("location") ?: "GLOBAL")
            if (existingAnnouncement.external != (data.getBoolean("external") ?: false)) changes.put("external", data.getBoolean("external") ?: false)
            if (imageFileName != existingAnnouncement.imageFileName) changes.put("image", imageFileName != null)
        }

        databaseManager.panelActivityLogDao.add(
            com.panomc.plugins.announcement.log.UpdatedAnnouncementLog(userId, username, plugin.pluginId, title, if (changes.isEmpty) null else changes),
            sqlClient
        )

        return Successful()
    }

    private fun validateForm(
        title: String?,
        contents: List<String>,
        type: AnnouncementType,
        fileUpload: io.vertx.ext.web.FileUpload?,
        isCreate: Boolean,
        displayFrequency: AnnouncementDisplayFrequency?,
        size: Int?
    ) {
        if (title.isNullOrBlank()) {
            throw BadRequest()
        }

        if (contents.isEmpty()) {
            throw BadRequest()
        }

        if (type == AnnouncementType.MODAL) {
            if (contents.size > 1) {
                throw BadRequest()
            }

            if (displayFrequency == null) {
                throw BadRequest()
            }

            if (size == null) {
                throw BadRequest()
            }

            if (isCreate && fileUpload == null) {
                throw BadRequest()
            }

            if (fileUpload != null) {
                if (fileUpload.size() > 2 * 1024 * 1024) {
                    throw BadRequest()
                }

                val allowedTypes = listOf("image/webp", "image/jpeg", "image/png", "image/gif")
                if (!allowedTypes.contains(fileUpload.contentType())) {
                    throw BadRequest()
                }
            }
        }
    }

    private fun saveUploadedFile(fileUpload: io.vertx.ext.web.FileUpload): String {
        val extension = fileUpload.fileName().split(".").last()
        val fileName = "${System.currentTimeMillis()}-${fileUpload.uploadedFileName().split(File.separator).last()}.$extension"
        val destFile = File(plugin.uploadsDir, fileName)

        File(fileUpload.uploadedFileName()).copyTo(destFile, true)

        ImageUtil.generateThumbnail(destFile, File(plugin.uploadsDir, "thumbnails"))

        return fileName
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
