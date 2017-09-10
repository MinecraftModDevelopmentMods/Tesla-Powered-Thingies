package net.ndrei.teslapoweredthingies.machines.incinerator

import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

/**
 * Created by CF on 2017-06-30.
 */
@AutoRegisterBlock
object IncineratorBlock : BaseThingyBlock<IncineratorEntity>("incinerator", IncineratorEntity::class.java)