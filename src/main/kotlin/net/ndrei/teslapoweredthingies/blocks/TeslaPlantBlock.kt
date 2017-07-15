package net.ndrei.teslapoweredthingies.blocks

import net.minecraft.block.BlockBush
import net.minecraft.block.IGrowable
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.entities.TeslaLightningBolt
import net.ndrei.teslacorelib.entities.TeslaLightningStruckEvent
import net.ndrei.teslacorelib.utils.BlockPosUtils
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.items.TeslaPlantSeeds
import java.util.*

/**
 * Created by CF on 2017-07-10.
 */
@AutoRegisterBlock
object TeslaPlantBlock
    : BlockBush(Material.PLANTS, MapColor.CYAN), IGrowable {

//    fun registerRenderer() {
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
//                ModelResourceLocation(this.registryName!!, "inventory")
//        )
//    }

    private var _age: PropertyInteger? = null

    private const val MAX_AGE = 2

    val AGE: PropertyInteger
        get() {
            if (this._age == null)
                this._age = PropertyInteger.create("age", 0, MAX_AGE)
            return this._age!!
        }

    private val TESLA_PLANT_AABB = arrayOf(
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.6875, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.875, 1.0))

    init {
        this.setRegistryName(TeslaThingiesMod.MODID, "tesla_plant")
        this.unlocalizedName = "${TeslaThingiesMod.MODID}.tesla_plant"
        this.setCreativeTab(null) // TeslaThingiesMod.creativeTab)

        this.defaultState = this.blockState.baseState.withProperty(AGE, Integer.valueOf(0)!!)
        this.tickRandomly = true

        MinecraftForge.EVENT_BUS.register(this)
    }

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?): AxisAlignedBB {
        return TESLA_PLANT_AABB[(state!!.getValue(AGE) as Int).toInt()]
    }

    /**
     * Return true if the block can sustain a Bush
     */
    override fun canSustainBush(state: IBlockState): Boolean {
        return state.block === Blocks.FARMLAND
    }

    //#region grow & lightning event

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
//        val i = (state.getValue(AGE) as Int).toInt()

//        val probability = (i + 1) * if (worldIn.worldInfo.isThundering) 3
//        else if (worldIn.worldInfo.isRaining) 20
//        else 0
//
//        this.doGrow(worldIn, pos, state, rand, probability)
        this.grow(worldIn, rand, pos, state)

        if (worldIn.worldInfo.isThundering && (rand.nextInt(2) == 1)) {
            worldIn.addWeatherEffect(TeslaLightningBolt(worldIn, pos))
        }

        super.updateTick(worldIn, pos, state, rand)
    }

    private fun doGrow(world: World, pos: BlockPos, state: IBlockState, rand: Random, probability: Int) {
        val i = (state.getValue(AGE) as Int).toInt()
        if (i < 2 && (probability > 0) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(world, pos, state, rand.nextInt(probability) == 0)) {
            val updatedState = state.withProperty(AGE, Integer.valueOf(i + 1)!!)
            world.setBlockState(pos, updatedState, 2)
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos))
        }
    }

    override fun canUseBonemeal(worldIn: World?, rand: Random?, pos: BlockPos?, state: IBlockState?) = false

    override fun grow(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState) {
        val i = (state.getValue(AGE) as Int).toInt()

        val probability = (i + 1) * if (worldIn.worldInfo.isThundering) 3
        else if (worldIn.worldInfo.isRaining) 20
        else 0

        this.doGrow(worldIn, pos, state, rand, probability)
    }

    override fun canGrow(worldIn: World, pos: BlockPos, state: IBlockState, isClient: Boolean)
            = (!isClient && (state.getValue(AGE) as Int).toInt() < MAX_AGE)

    @SubscribeEvent
    fun onEntityEvent(ev: TeslaLightningStruckEvent) {
        if ((ev.entity is EntityLightningBolt) && (ev.entity.position != null) && (ev.entity.entityWorld != null)) {
            val world = ev.entity.entityWorld
            val pos = ev.entity.position
            val centerState = world.getBlockState(pos)
            if (centerState.block == TeslaPlantBlock) {
                this.doGrow(world, pos, centerState, world.rand, 1)
            }

            val cube = BlockPosUtils.getCube(ev.entity.position, null, 1,1)
            cube.forEach {
                val state = world.getBlockState(it)
                if (state.block == TeslaPlantBlock) {
                    // this.updateTick(world, it, state, world.rand)
                    this.doGrow(world, pos, state, world.rand, 3)
                }
            }
        }
    }

    //#endregion

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
//    override fun dropBlockAsItemWithChance(worldIn: World, pos: BlockPos, state: IBlockState, chance: Float, fortune: Int) {
//        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune)
//        if (!worldIn.isRemote) {
//            var i = 1
//
//            if ((state.getValue(AGE) as Int).toInt() >= 2) {
//                i = 2 + worldIn.rand.nextInt(3)
//
//                if (fortune > 0) {
//                    i += worldIn.rand.nextInt(fortune + 1)
//                }
//            }
//
//            for (j in 0..i - 1) {
//                Block.spawnAsEntity(worldIn, pos, ItemStack(TeslaPlantSeeds))
//            }
//        }
//    }

//    override fun getPickBlock(state: IBlockState?, target: RayTraceResult?, world: World?, pos: BlockPos?, player: EntityPlayer?): ItemStack {
//        return super.getPickBlock(state, target, world, pos, player)
//    }


    //#region meta <-> state

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(AGE, Integer.valueOf(meta)!!)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(AGE) as Int).toInt()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(AGE))
    }

    //#endregion

    override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int)
            = TeslaPlantSeeds

    override fun getItem(worldIn: World?, pos: BlockPos?, state: IBlockState)
            = ItemStack(TeslaPlantSeeds)

    override fun quantityDropped(random: Random?) = 1

    override fun quantityDroppedWithBonus(fortune: Int, random: Random?)
            = this.quantityDropped(random) + if (fortune > 0) (random?.nextInt(fortune) ?: 0) else 0

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        val rand = if (world is World) world.rand else Random()
        var count = 1

        if (state.getValue(AGE) as Int >= MAX_AGE) {
            count = 2 + if (fortune > 0) rand.nextInt(fortune + 1) else 0
        }

        for (i in 0 until count) {
            drops.add(ItemStack(TeslaPlantSeeds))
        }
    }
}