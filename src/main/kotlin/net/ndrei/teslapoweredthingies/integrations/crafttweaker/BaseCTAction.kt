package net.ndrei.teslapoweredthingies.integrations.crafttweaker

import crafttweaker.IAction
import net.minecraftforge.fml.common.Optional

@Optional.Interface(iface = "crafttweaker.IAction", modid =  "crafttweaker", striprefs = true)
abstract class BaseCTAction : IAction {
    override fun describe() = "Not just an action. The best action in the world!"
}
