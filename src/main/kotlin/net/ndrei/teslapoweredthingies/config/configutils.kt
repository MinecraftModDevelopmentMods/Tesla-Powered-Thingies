package net.ndrei.teslapoweredthingies.config

import com.google.gson.JsonObject
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

fun readExtraRecipesFile(fileName: String, callback: (json: JsonObject) -> Unit) {
    TeslaThingiesMod.config.readExtraRecipesFile(fileName, callback)
}
