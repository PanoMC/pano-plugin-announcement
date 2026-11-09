package com.panomc.plugins.announcement.util

import com.panomc.platform.error.BadRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.FileUpload
import java.io.File
import java.util.UUID

/**
 * Announcement images: only real png, jpeg, gif or webp images are stored, under a name and extension chosen here, and
 * they are served with a fixed image type so a browser never renders one as a page.
 */
object AnnouncementImageFiles {
    const val MAX_SIZE = 2L * 1024 * 1024

    private val IMAGE_TYPES = mapOf(
        "png" to "image/png",
        "jpg" to "image/jpeg",
        "jpeg" to "image/jpeg",
        "gif" to "image/gif",
        "webp" to "image/webp"
    )

    /** The image format of [file] read from its first bytes (png, jpg, gif or webp), or null when it is no such image. */
    fun detectImageExtension(file: File): String? {
        val head = ByteArray(12)
        val read = file.inputStream().use { input ->
            var total = 0
            while (total < head.size) {
                val n = input.read(head, total, head.size - total)
                if (n < 0) break
                total += n
            }
            total
        }

        fun at(offset: Int, vararg bytes: Int) =
            read >= offset + bytes.size && bytes.indices.all { head[offset + it] == bytes[it].toByte() }

        return when {
            at(0, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A) -> "png"
            at(0, 0xFF, 0xD8, 0xFF) -> "jpg"
            at(0, 0x47, 0x49, 0x46, 0x38) -> "gif"
            at(0, 0x52, 0x49, 0x46, 0x46) && at(8, 0x57, 0x45, 0x42, 0x50) -> "webp"
            else -> null
        }
    }

    /** [BadRequest] unless [fileUpload] is a png, jpeg, gif or webp image of at most [MAX_SIZE] bytes. */
    fun check(fileUpload: FileUpload): String {
        if (fileUpload.size() > MAX_SIZE) throw BadRequest()
        val source = File(fileUpload.uploadedFileName())
        return (if (source.isFile) detectImageExtension(source) else null) ?: throw BadRequest()
    }

    fun store(fileUpload: FileUpload, uploadsDir: File): String {
        val extension = check(fileUpload)
        val fileName = "${System.currentTimeMillis()}-${UUID.randomUUID()}.$extension"
        val destFile = File(uploadsDir, fileName)

        File(fileUpload.uploadedFileName()).copyTo(destFile, true)

        ImageUtil.generateThumbnail(destFile, File(uploadsDir, "thumbnails"))

        return fileName
    }

    /** Content type and safety headers for a stored image file. */
    fun putHeaders(response: HttpServerResponse, file: File) {
        val contentType = IMAGE_TYPES[file.extension.lowercase()]
        if (contentType != null) {
            response.putHeader("Content-Type", contentType)
        } else {
            // Files stored by older versions with a non-image extension: never let the browser render them.
            response.putHeader("Content-Type", "application/octet-stream")
            response.putHeader("Content-Disposition", "attachment")
        }
        response.putHeader("X-Content-Type-Options", "nosniff")
        response.putHeader("Content-Security-Policy", "default-src 'none'; sandbox")
    }
}
