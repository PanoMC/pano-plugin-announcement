package com.panomc.plugins.announcement

import com.panomc.platform.api.PanoPlugin
import com.panomc.platform.api.PluginDatabaseManager
import com.panomc.platform.setup.SetupManager
import java.io.File

class AnnouncementPlugin : PanoPlugin() {
    private val pluginDatabaseManager by lazy {
        applicationContext.getBean(PluginDatabaseManager::class.java)
    }

    private val setupManager by lazy {
        applicationContext.getBean(SetupManager::class.java)
    }

    val uploadsDir: File by lazy {
        File(pluginDataFolder, "uploads")
    }

    private var isInitialized = false

    override suspend fun onStart() {
        logger.info("Starting...")

        startPlugin()
    }

    internal suspend fun startPlugin() {
        if (isInitialized) return

        if (!setupManager.isSetupDone()) {
            logger.info("Setup is not finished, waiting for setup completion...")
            return
        }

        isInitialized = true

        pluginDatabaseManager.initialize(this)

        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs()
        }
        
        logger.info("Started!")
    }

    override suspend fun onEnable() {
        logger.info("Enabled!")
    }

    override suspend fun onDisable() {
        isInitialized = false
    }

    override suspend fun onUninstall() {
        pluginDatabaseManager.uninstall(this)

        if (uploadsDir.exists()) {
            uploadsDir.deleteRecursively()
        }
    }
}
