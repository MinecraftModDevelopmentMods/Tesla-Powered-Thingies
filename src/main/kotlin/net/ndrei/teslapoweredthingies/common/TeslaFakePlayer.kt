package net.ndrei.teslapoweredthingies.common

import com.mojang.authlib.GameProfile
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.FakePlayer
import java.lang.ref.WeakReference
import java.util.*

class TeslaFakePlayer(world: WorldServer, name: GameProfile) : FakePlayer(world, name) {
    fun setItemInUse(stack: ItemStack) {
        this.setHeldItem(EnumHand.MAIN_HAND, stack)
        this.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY)
        this.activeHand = EnumHand.MAIN_HAND
    }

    fun resetTicksSinceLastSwing() {
        this.ticksSinceLastSwing = Int.MAX_VALUE
    }

    override fun attackTargetEntityWithCurrentItem(targetEntity: Entity?) {
        this.resetTicksSinceLastSwing()
        super.attackTargetEntityWithCurrentItem(targetEntity)
    }

    override fun getDistanceSq(x: Double, y: Double, z: Double) = 0.0
    override fun getDistance(x: Double, y: Double, z: Double) = 0.0

    companion object {
        private val PROFILE = GameProfile(UUID.fromString("225F6E4B-5BAE-4BDA-9B88-2397DEFD95EB"), "[TESLA_THINGIES]")
        private var PLAYER: WeakReference<TeslaFakePlayer>? = null

        fun getPlayer(world: WorldServer): TeslaFakePlayer {
            var ret: TeslaFakePlayer? = if (PLAYER != null) PLAYER!!.get() else null
            if (ret == null) {
                ret = TeslaFakePlayer(world, PROFILE)
                PLAYER = WeakReference(ret)
            }
            return ret
        }
    }
}
