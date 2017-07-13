package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.BlockFarmland
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemHoe
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.GuiPieceSide
import net.ndrei.teslapoweredthingies.common.IAdditionalProcessingAddon
import net.ndrei.teslapoweredthingies.machines.CROP_FARM_WORK_AREA_COLOR
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine

/**
 * Created by CF on 2017-07-07.
 */
class CropFarmEntity : ElectricFarmMachine(CropFarmEntity::class.java.name.hashCode()) {
    private lateinit var waterTank: IFluidTank

    override fun initializeInventories() {
        super.initializeInventories()

        this.waterTank = super.addFluidTank(FluidRegistry.WATER, 5000, EnumDyeColor.BLUE, "Water Tank",
                BoundingRectangle(43, 25, 18, 54))
    }

    override fun getWorkAreaColor(): Int = CROP_FARM_WORK_AREA_COLOR

    override fun acceptsInputStack(slot: Int, stack: ItemStack): Boolean {
        if (ItemStackUtil.isEmpty(stack))
            return true

        // test for hoe
        if (stack.item is ItemHoe) {
            return true
        }

        return PlantWrapperFactory.isFertilizer(stack) || PlantWrapperFactory.isSeed(stack)
    }

    override val lockableInputLockPosition: GuiPieceSide
        get() = GuiPieceSide.LEFT

    override fun performWork(): Float {
        var result = 0.0f
        val facing = super.facing
        val cube = this.getWorkArea(facing.opposite, 1)

        val blockers = mutableListOf<IPlantWrapper>()

        //region harvest plants

        cube.forEach  {
            val plant = PlantWrapperFactory.getPlantWrapper(this.getWorld(), it)

            if (plant != null) {
                if (plant.canBeHarvested() && (1.0f - result >= .45f)) {
                    val loot = plant.harvest(0)
                    super.outputItems(loot)
                    result += 0.45f
                } else if (plant.canBlockNeighbours()) {
                    blockers.add(plant)
                }
            }
        }

        //endregion

        val inputs = this.inStackHandler!!

        //region water land & plant things

        val seeds = ItemStackUtil.getCombinedInventory(inputs)
                .mapNotNull { PlantWrapperFactory.getSeedWrapper(it) }

        for (pos in cube) {
            if (blockers.any { it.blocksNeighbour(pos) }) {
                continue
            }

            val landPos = pos.offset(EnumFacing.DOWN)
            if (this.getWorld().isAirBlock(pos) && result <= 0.8f) {
                //region plant thing

                var plant: IBlockState? = null
                var plantedSeed: ItemStack = ItemStack.EMPTY
                for (seed in seeds) {
                    if (seed.canPlantHere(this.getWorld(), pos)) {
                        plant = seed.plant(this.getWorld(), pos)
                        plantedSeed = seed.seeds
                        break
                    }
                }

                if ((plant != null) && !plantedSeed.isEmpty) {
                    if (1 == ItemStackUtil.extractFromCombinedInventory(inputs, plantedSeed, 1)) {
                        this.getWorld().setBlockState(pos, plant)
                        ItemStackUtil.shrink(plantedSeed, 1)
                        result += 0.2f

                        val newPlant = PlantWrapperFactory.getPlantWrapper(this.getWorld(), pos)
                        if ((newPlant != null) && newPlant.canBlockNeighbours()) {
                            blockers.add(newPlant)
                        }
                    }
                }

                //endregion
            }

            val state = this.getWorld().getBlockState(landPos)
            if (state.block === Blocks.FARMLAND) {
                //region moisturize land

                if (result <= 0.95f) {
                    var moisture = state.getValue(BlockFarmland.MOISTURE)
                    val fluidNeeded = Math.min(2, 7 - moisture) * 15
                    if (fluidNeeded > 0 && this.waterTank!!.fluidAmount >= fluidNeeded) {
                        moisture = Math.min(7, moisture + 2)
                        this.getWorld().setBlockState(landPos, state.withProperty(BlockFarmland.MOISTURE, moisture))
                        this.waterTank!!.drain(fluidNeeded, true)
                        result += 0.05f
                    }
                }

                //endregion
            } else if (state.block === Blocks.GRASS || state.block === Blocks.DIRT || state.block === Blocks.GRASS_PATH) {
                //region hoe land

                if (this.getWorld().isAirBlock(pos) && result <= 0.7f) {
                    // find hoe
                    var hoeSlot = -1
                    for (i in 0..inputs.slots - 1) {
                        val stack = inputs.getStackInSlot(i)
                        if (!ItemStackUtil.isEmpty(stack) && stack.item is ItemHoe) {
                            hoeSlot = i
                            break
                        }
                    }

                    if (hoeSlot >= 0) {
                        // hoe land
                        this.getWorld().setBlockState(landPos, Blocks.FARMLAND.defaultState)
                        if (inputs.getStackInSlot(hoeSlot).attemptDamageItem(1, this.getWorld().rand, TeslaThingiesMod.getFakePlayer(this.getWorld()))) {
                            inputs.setStackInSlot(hoeSlot, ItemStackUtil.emptyStack)
                        }
                        result += 0.3f
                    }
                }

                //endregion
            }

            if (result > 0.95f) {
                // no more power for anything
                break
            }
        }

        //endregion

        //#region fertilize all the thingies

        if (result <= .9f) {
            var fertilizer = ItemStackUtil.emptyStack
            for (stack in ItemStackUtil.getCombinedInventory(inputs)) {
                if (PlantWrapperFactory.isFertilizer(stack)) {
                    fertilizer = stack.copy()
                    break
                }
            }
            if (!ItemStackUtil.isEmpty(fertilizer)) {
                var tries = 10
                while (tries >= 0 && result <= .9f && !ItemStackUtil.isEmpty(fertilizer)) {
                    val pos = cube.getRandomInside(this.getWorld().rand)
                    val plant = PlantWrapperFactory.getPlantWrapper(this.getWorld(), pos)
                    if (plant != null && plant!!.canUseFertilizer()) {
                        val used = plant!!.useFertilizer(fertilizer)
                        ItemStackUtil.shrink(fertilizer, ItemStackUtil.extractFromCombinedInventory(inputs, fertilizer, used))
                        result += .1f
                    }
                    tries--
                }
            }
        }

        //#endregion

        for (addon in this.addons) {
            if (addon is IAdditionalProcessingAddon) {
                val available = 1.0f - result
                result += Math.min(addon.processAddon(this, available), available)
            }
        }

        return result
    }
}
