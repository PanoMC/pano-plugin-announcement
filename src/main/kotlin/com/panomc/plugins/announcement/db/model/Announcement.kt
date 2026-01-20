package com.panomc.plugins.announcement.db.model

import com.panomc.platform.db.DBEntity
import com.panomc.plugins.announcement.util.*

open class Announcement(
    val id: Long = -1,
    val title: String = "",
    val status: Boolean = true,
    val link: String = "",
    val type: AnnouncementType = AnnouncementType.BANNER,
    val effectType: AnnouncementEffectType? = AnnouncementEffectType.NONE,
    val until: Long? = null,
    val contents: List<String>,
    val customCss: String = "",
    val size: Int? = null,
    val displayFrequency: AnnouncementDisplayFrequency? = null,
    val imageFileName: String? = null,
    val closeable: Boolean = true,
    val showFrom: Long? = null,
    val location: AnnouncementLocation = AnnouncementLocation.GLOBAL,
    val external: Boolean = false,
    val alertStyle: AnnouncementAlertStyle = AnnouncementAlertStyle.INFO,
    val textAlign: AnnouncementTextAlign = AnnouncementTextAlign.CENTER,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) : DBEntity()