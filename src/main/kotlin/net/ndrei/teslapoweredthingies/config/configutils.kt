package net.ndrei.teslapoweredthingies.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslapoweredthingies.MOD_ID
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

fun readExtraRecipesFile(fileName: String, callback: (json: JsonObject) -> Unit) {
    val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    val config = File(Config.configFolder, "$fileName-base.json")

    if (!config.exists()) {
        val stream = Config.javaClass.getResourceAsStream("/assets/$MOD_ID/extra-recipes/$fileName.json")
        if (stream == null) {
            TeslaThingiesMod.logger.error("Could not locate extra recipes base resource file: '$fileName.json'.")
        }
        else {
            stream.use { s ->
                if (config.createNewFile()) {
                    val writer = BufferedWriter(FileWriter(config, false))
                    writer.use { outs ->
                        s.bufferedReader().use { ins ->
                            var line = ins.readLine()
                            while (line != null) {
                                outs.write(line + "\n")
                                line = ins.readLine()
                            }
                        }
                    }
                }
                else {
                    TeslaThingiesMod.logger.error("Could not create extra recipes file: '${config.path}'.")
                }
            }
        }
    }

    fun readFile(file: File) {
        if (file.exists()) {
            file.bufferedReader().use {
                val json = JsonUtils.fromJson(GSON, it, JsonElement::class.java)
                if (json != null) {
                    if (json.isJsonArray) {
                        json.asJsonArray.forEach {
                            if (it.isJsonObject) {
                                callback(it.asJsonObject)
                            }
                        }
                    }
                    else if (json.isJsonObject) {
                        callback(json.asJsonObject)
                    }
                }
            }
        }
    }

    readFile(config)
    readFile(File(Config.configFolder, "$fileName-extra.json"))
}

fun JsonObject.readFluidStack(memberName: String): FluidStack? {
    val json = JsonUtils.getJsonObject(this, memberName) ?: return null

    val fluid = JsonUtils.getString(json, "name", "")
            .let { FluidRegistry.getFluid(it) } ?: return null
    val amount = JsonUtils.getInt(json, "quantity", 0)

    return if (amount <= 0) null else FluidStack(fluid, amount)
}

fun JsonObject.readItemStack(memberName: String): ItemStack? { // there is a good reason for not using ItemStack.EMPTY :)
    val json = JsonUtils.getJsonObject(this, memberName) ?: return null

    val item = JsonUtils.getString(json, "name", "")
            .let {
                val registryName = if (it.isNullOrEmpty()) null else ResourceLocation(it)
                if ((registryName != null) && Item.REGISTRY.containsKey(registryName))
                    Item.REGISTRY.getObject(registryName)
                else null
            } ?: return null

    val meta = JsonUtils.getInt(json, "meta", 0)
    val amount = JsonUtils.getInt(json, "quantity", 1)

    return ItemStack(item, amount, meta)
}
