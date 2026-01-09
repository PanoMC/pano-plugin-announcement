package com.panomc.plugins.announcement

import com.panomc.platform.api.PanoPlugin
import com.panomc.platform.api.PluginDatabaseManager
import java.io.File

class AnnouncementPlugin : PanoPlugin() {
    private val pluginDatabaseManager by lazy {
        applicationContext.getBean(PluginDatabaseManager::class.java)
    }

    val uploadsDir: File by lazy {
        File(pluginDataFolder, "uploads")
    }

    override suspend fun onStart() {
        logger.info("Starting...")
        try {
            pluginDatabaseManager.initialize(this)

            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        logger.info("Started!")
    }

    override suspend fun onEnable() {
        logger.info("Enabled!")
    }

    override suspend fun onUninstall() {
        pluginDatabaseManager.uninstall(this)

        if (uploadsDir.exists()) {
            uploadsDir.deleteRecursively()
        }
    }
}

