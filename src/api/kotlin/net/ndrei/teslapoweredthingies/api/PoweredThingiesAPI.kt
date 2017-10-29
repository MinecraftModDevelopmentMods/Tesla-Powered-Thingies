package net.ndrei.teslapoweredthingies.api

import net.ndrei.teslapoweredthingies.api.compoundmaker.ICompoundMakerRegistry

object PoweredThingiesAPI {
    lateinit var compoundMakerRegistry: ICompoundMakerRegistry<*>
}
