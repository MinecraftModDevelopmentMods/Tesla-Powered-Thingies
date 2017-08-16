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
import net.minecraftforge.oredict.OreDictionary
import net.ndrei.teslacorelib.utils.copyWithSize
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

fun JsonObject.readItemStacks(memberName: String): List<ItemStack> {
    val json = JsonUtils.getJsonObject(this, memberName) ?: return listOf()
    return json.readItemStacks()
}

fun JsonObject.readItemStacks(): List<ItemStack> {
    val item = JsonUtils.getString(this, "name", "")
            .let {
                val registryName = if (it.isNullOrEmpty()) null else ResourceLocation(it)
                if ((registryName != null) && Item.REGISTRY.containsKey(registryName))
                    Item.REGISTRY.getObject(registryName)
                else null
            }
    if (item != null) {
        val meta = JsonUtils.getInt(this, "meta", 0)
        val amount = JsonUtils.getInt(this, "quantity", 1)
        return listOf(ItemStack(item, amount, meta))
    }
    else {
        val ore = JsonUtils.getString(this, "ore", "")
        if (!ore.isNullOrEmpty()) {
            val amount = JsonUtils.getInt(this, "quantity", 1)
            return OreDictionary.getOres(ore)
                    .map { it.copyWithSize(amount) }
        }
    }

    return listOf()
}

fun JsonObject.readItemStack(memberName: String)
    = this.readItemStacks(memberName).firstOrNull()

fun JsonObject.readItemStack()
        = this.readItemStacks().firstOrNull()
