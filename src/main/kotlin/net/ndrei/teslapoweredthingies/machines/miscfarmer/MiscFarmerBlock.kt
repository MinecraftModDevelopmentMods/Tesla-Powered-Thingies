package net.ndrei.teslapoweredthingies.machines.miscfarmer

import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslapoweredthingies.machines.BaseThingyBlock

@AutoRegisterBlock
object MiscFarmerBlock : BaseThingyBlock<MiscFarmerEntity>("misc_farmer", MiscFarmerEntity::class.java)