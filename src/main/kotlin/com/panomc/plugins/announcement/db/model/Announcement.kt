package com.panomc.plugins.announcement.db.model

import com.panomc.platform.db.DBEntity
import com.panomc.plugins.announcement.util.AnnouncementEffectType
import com.panomc.plugins.announcement.util.AnnouncementType
import com.panomc.plugins.announcement.util.AnnouncementLocation
import com.panomc.plugins.announcement.util.ModalDisplayFrequency

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
    val displayFrequency: ModalDisplayFrequency? = null,
    val imageFileName: String? = null,
    val closeable: Boolean = true,
    val showFrom: Long? = null,
    val location: AnnouncementLocation = AnnouncementLocation.GLOBAL,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) : DBEntity()