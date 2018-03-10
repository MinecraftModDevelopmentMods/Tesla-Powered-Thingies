package net.ndrei.teslapoweredthingies.machines.treefarm

import com.google.common.collect.Lists
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslacorelib.utils.BlockCube

/**
 * Created by CF on 2017-07-07.
 */
class TreeScanner /*implements INBTSerializable<NBTTagCompound>*/ {
    private var scanned: MutableList<BlockPos>? = null
    private var toScan: MutableList<BlockPos>? = null

    init {
        this.scanned = Lists.newArrayList<BlockPos>()
    }

    fun scan(world: World, base: BlockCube, perBlockValue: Float, maxValue: Float): Float {
        var result = 0f

        if (this.scanned == null) {
            this.scanned = Lists.newArrayList<BlockPos>()
        }
        if (this.toScan == null) {
            this.toScan = Lists.newArrayList<BlockPos>()
        }

        // always scan first level
        for (pos in base) {
            if (!this.scanned!!.contains(pos) && !this.toScan!!.contains(pos)
                    && TreeWrapperFactory.isHarvestable(world, pos, null)) {
                this.toScan!!.add(0, pos)

                result += perBlockValue
                if (result >= maxValue) {
                    return result
                }
            }
        }

        // scan nearby blocks
        while (toScan!!.size > 0) {
            val pos = this.toScan!![0]

            for (ox in -1..1) {
                for (oy in -1..1) {
                    for (oz in -1..1) {
                        if ((ox == 0) && (oy == 0) && (oz == 0)) {
                            continue
                        }

                        val nb = pos.add(ox, oy, oz)
                        if (!this.scanned!!.contains(nb) && !this.toScan!!.contains(nb)
                            && TreeWrapperFactory.isHarvestable(world, nb, null)) {
                            this.toScan!!.add(nb)

                            result += perBlockValue
                            if (result >= maxValue) {
                                return result
                            }
                        }
                    }
                }
            }
//            for (facing in EnumFacing.VALUES) {
//                val nb = pos.offset(facing)
//                if (!this.scanned!!.contains(nb) && !this.toScan!!.contains(nb)
//                        && TreeWrapperFactory.isHarvestable(world, nb, null)) {
//                    this.toScan!!.add(nb)
//
//                    result += perBlockValue
//                    if (result >= maxValue) {
//                        return result
//                    }
//                }
//            }

            this.toScan!!.removeAt(0)
            this.scanned!!.add(pos)
        }

        return result
    }

    fun blockCount(): Int {
        return if (this.scanned == null) 0 else this.scanned!!.size
    }

    fun pendingCount(): Int {
        return if (this.toScan == null) 0 else this.toScan!!.size
    }

    fun popScannedPos(): BlockPos? {
        if (this.scanned == null || this.scanned!!.size == 0) {
            return null
        }

        val index = this.scanned!!.size - 1
        val pos = this.scanned!![index]
        this.scanned!!.removeAt(index)
        return pos
    }

    //    @Override
    //    public NBTTagCompound serializeNBT() {
    //        NBTTagCompound compound = new NBTTagCompound();
    //
    //        return compound;
    //    }
    //
    //    @Override
    //    public void deserializeNBT(NBTTagCompound nbt) {
    //
    //    }
}
