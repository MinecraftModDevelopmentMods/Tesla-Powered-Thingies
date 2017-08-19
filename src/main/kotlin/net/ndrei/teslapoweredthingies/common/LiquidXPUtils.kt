package net.ndrei.teslapoweredthingies.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.play.server.SPacketSetExperience
import net.ndrei.teslapoweredthingies.TeslaThingiesMod

object LiquidXPUtils {
    // trying to be consistent with enderIO
    const val XP_PER_BOTTLE = 8
    const val LiquidXP_PER_XP = 20

    fun getXPForLevel(level: Int) =
        when (level) {
            in Int.MIN_VALUE..0 -> 0
            in 1..15 -> level * 17
            in 16..30 -> (1.5 * Math.pow(level.toDouble(), 2.0) - 29.5 * level + 360).toInt()
            else -> (3.5 * Math.pow(level.toDouble(), 2.0) - 151.5 * level + 2220).toInt()
        }

    fun getXPBarCapacity(level: Int) =
        when (level) {
            in 30..Int.MAX_VALUE -> 62 + (level - 30) * 7
            in 15..29 -> 17 + (level - 15) * 3
            else -> 17
        }

    fun getXPToNextLevel(level: Int, xpBar: Float) =
        (getXPBarCapacity(level).toFloat() * (1.0f - xpBar)).toInt()

    fun getLevelForXP(xp: Int): Int {
        var i = 1
        while (getXPForLevel(i) <= xp) { i++ }
        return i
    }
}

fun EntityPlayer.changeExperience(diff: Int) {
    this.setExperience(this.experienceTotal + diff)
}

fun EntityPlayer.setExperience(xp: Int) {
//    TeslaThingiesMod.logger.info("BEFORE: ${this.experienceTotal} = [${this.experienceLevel}] :: ${this.experience}")

    this.experienceTotal = xp
    if (this.experienceTotal <= 0) {
        this.experienceTotal = 0
        this.experienceLevel = 0
        this.experience = 0.0f
    }
    else {
        this.experienceLevel = LiquidXPUtils.getLevelForXP(this.experienceTotal)
        this.experience = (this.experienceTotal - LiquidXPUtils.getXPForLevel(this.experienceLevel - 1)).toFloat() /
            LiquidXPUtils.getXPBarCapacity(this.experienceLevel).toFloat()
    }

    if (this is EntityPlayerMP) {
        // TODO: might create double packages
        // TODO: find a way to set lastExperience field
        this.connection.sendPacket(SPacketSetExperience(this.experience, this.experienceTotal, this.experienceLevel))
    }

//    TeslaThingiesMod.logger.info("AFTER: ${this.experienceTotal} = [${this.experienceLevel}] :: ${this.experience}")
}
