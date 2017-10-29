package net.ndrei.teslapoweredthingies.machines.pump

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslapoweredthingies.integrations.GUI_PUMP
import net.ndrei.teslapoweredthingies.integrations.localize

enum class PumpMode(val icon: PumpIcon) {
    AUTO(PumpIcon.MODE_AUTO),
    FLUID(PumpIcon.MODE_FLUID),
    BLOCKS(PumpIcon.MODE_BLOCKS);

    @SideOnly(Side.CLIENT)
    fun getLocalizedLabel(): String =
        localize(GUI_PUMP, "mode_${this.name.toLowerCase()}")
}
