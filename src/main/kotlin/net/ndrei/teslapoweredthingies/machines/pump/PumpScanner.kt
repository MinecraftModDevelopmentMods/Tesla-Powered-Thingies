package net.ndrei.teslapoweredthingies.machines.pump

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.IFluidBlock

open class PumpScanner(private val entity: PumpEntity) {
//    private val blocks = mutableMapOf<BlockPos, Float>()
    private val visited = mutableListOf<BlockPos>()
    private var scanIndex = -1
    private var sourceFluid: Fluid? = null

    constructor(entity: PumpEntity, initial: BlockPos)
        : this(entity) {
//        this.blocks.put(initial, initial.getFluidLevel())
        this.visited.add(initial)
        this.scanIndex = 0
        this.onChanged()
    }

    private fun getSourceFluid(): Fluid? {
        if ((this.sourceFluid == null) && (this.visited.size > 0)) {
            this.sourceFluid = this.entity.world.getBlockState(this.visited[0]).getFluidWrapper()?.fluid
        }
        return this.sourceFluid
    }

    private fun BlockPos.getFluidLevel() =
        this@PumpScanner.entity.world.getBlockState(this).getFluidWrapper()?.let {
            if (it.fluid == this@PumpScanner.sourceFluid)
                it.getFilledPercentage(this@PumpScanner.entity.world, this)
            else 0.0f
        } ?: 0f

    fun serializeNBT() = NBTTagCompound().also {
        it.setInteger("scan_index", this.scanIndex)
        it.setTag("blocks", this.visited.fold(NBTTagList()) { list, item ->
            val nbt = NBTTagCompound()
            nbt.setInteger("x", item.x)
            nbt.setInteger("y", item.y)
            nbt.setInteger("z", item.z)
//            nbt.setFloat("level", this.blocks.getOrPut(item, { item.getFluidLevel() }))
            list.appendTag(nbt)
            return@fold list
        })
    }

    fun serializeInfoNBT() = NBTTagCompound().also {
        it.setInteger("scanned", this.visited.size)
    }

    protected open fun onChanged() {
        this.entity.scannedChanged()
    }

    fun scan(): Int {
        if (this.scanIndex < 0) {
            this.scanIndex = 0
        }

        if (this.scanIndex >= this.visited.size) {
            return 0 // scan ended
        }

        val original = this.visited.size
        (0 until SCAN_PER_TICK).forEach { _ ->
            if (this.scanIndex >= this.visited.size) return@forEach
            val current = this.visited[this.scanIndex]
            this.scanIndex ++
            this.onChanged()

            EnumFacing.values().forEach fe@ {
                val other = current.offset(it)
                if (this.visited.contains(other)) return@fe

                val fluid = if (this.entity.world.isBlockLoaded(other)) this.entity.world.getBlockState(other).getFluidWrapper() else null
                if ((fluid?.fluid == this.getSourceFluid())/* && (fluid?.canDrain(this.world, other) == true)*/) {
                    this.visited.add(other)
                    this.onChanged()

                    if (this.visited.size >= MAX_BLOCKS) {
                        this.scanIndex = this.visited.size
                        this.onChanged()
                    }
                }
            }
        }

        return this.visited.size - original
    }

    fun getBlocks(max: Int = 1, onlyDrainable: Boolean = true): Map<BlockPos, IFluidBlock> {
        var changed = false
        val picked = mutableMapOf<BlockPos, IFluidBlock>()
        while ((picked.size < max) && (this.visited.size > 0)) {
            changed = true
            val current = this.visited.removeAt(this.visited.size - 1)
            val fluid = this.entity.world.getBlockState(current).getFluidWrapper()
//            TeslaCoreLib.logger.info("Fluid at: $current with level: ${fluid?.getFilledPercentage(this.world, current) ?: 0f} :: ${this.world.getBlockState(current)} :: ${this.world.getBlockState(current).block.javaClass.name}")
            if ((fluid != null) && (!onlyDrainable || fluid.canDrain(this.entity.world, current))) {
                picked.put(current, fluid)
            }
        }

        if (changed) this.onChanged()
        return picked
    }

    val buffer get() = this.visited.size

    companion object {
        private const val SCAN_PER_TICK = 24 // TODO: make this into a config option
        private const val MAX_BLOCKS = 4096 // TODO: make this into a config option

        fun deserializeNBT(entity: PumpEntity, nbt: NBTTagCompound): PumpScanner? {
            if (nbt.hasKey("scan_index", Constants.NBT.TAG_INT)) {
                val scanner = PumpScanner(entity)
                scanner.scanIndex = nbt.getInteger("scan_index")

                val list = nbt.getTagList("blocks", Constants.NBT.TAG_COMPOUND)
                scanner.visited.clear()
                list.filterIsInstance<NBTTagCompound>().mapTo(scanner.visited) {
                    BlockPos(it.getInteger("x"), it.getInteger("y"), it.getInteger("z"))
                }

                entity.scannedChanged()
                return scanner
            }
            return null
        }

        fun deserializeInfoNBT(nbt: NBTTagCompound): PumpScanner.Info? {
            if (nbt.hasKey("scanned", Constants.NBT.TAG_INT)) {
                return PumpScanner.Info(
                    nbt.getInteger("scanned")
                )
            }
            return null
        }
    }

    class Info(val scanned: Int)
}
