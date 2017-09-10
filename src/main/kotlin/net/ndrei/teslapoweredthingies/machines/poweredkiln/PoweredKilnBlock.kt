package net.ndrei.teslapoweredthingies.machines.poweredkiln

import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

/**
 * Created by CF on 2017-07-06.
 */
@AutoRegisterBlock
object PoweredKilnBlock : BaseThingyBlock<PoweredKilnEntity>("powered_kiln", PoweredKilnEntity::class.java)
