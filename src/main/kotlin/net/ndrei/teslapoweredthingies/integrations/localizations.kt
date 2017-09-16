package net.ndrei.teslapoweredthingies.integrations

import net.ndrei.teslacorelib.localization.LocalizedModText
import net.ndrei.teslacorelib.localization.localizeModString
import net.ndrei.teslapoweredthingies.MOD_ID

const val GUI_BUTTONS = "buttons"
const val GUI_GENERATOR_BURN = "generator_burn"
const val GUI_PLAYER_LIQUID_XP = "player_liquid_xp"
const val GUI_FLUID_SOLIDIFIER = "fluid_solidifier"
const val GUI_ANIMAL_GYM = "animal_gym"
const val GUI_TREE_FARM = "tree_farm"

fun localize(guiType: String, key: String, init: (LocalizedModText.() -> Unit)? = null) =
    localizeModString(MOD_ID, guiType, key, init).formattedText
