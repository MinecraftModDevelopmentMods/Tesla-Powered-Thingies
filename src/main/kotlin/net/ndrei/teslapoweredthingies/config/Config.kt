package net.ndrei.teslapoweredthingies.config

import net.ndrei.teslapoweredthingies.MOD_ID
import java.io.File

object Config {
    lateinit var configFolder: File private set

    fun init(modConfigurationDirectory: File) {
        this.configFolder = File(modConfigurationDirectory, MOD_ID)
        this.configFolder.mkdirs()
    }
}
