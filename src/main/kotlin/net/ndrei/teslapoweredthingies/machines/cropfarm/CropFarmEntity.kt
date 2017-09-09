package net.ndrei.teslapoweredthingies.machines.cropfarm

import net.minecraft.block.state.IBlockState
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemHoe
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
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
        this.sideConfig.setColorIndex(EnumDyeColor.BLUE, COLOR_INDEX_INPUTS - 2 )
    }

    override val fluidItemsColorIndex: Int?
        get() = COLOR_INDEX_INPUTS - 1

    override fun getWorkAreaColor(): Int = CROP_FARM_WORK_AREA_COLOR

    override fun acceptsInputStack(slot: Int, stack: ItemStack): Boolean {
        if (stack.isEmpty)
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
        val hoes = (0 until inputs.slots)
                .map { it.to(HoeFactory.getHoe(inputs.getStackInSlot(it))) }
                .filter { it.second != null }
        val player = TeslaThingiesMod.getFakePlayer(this.getWorld())

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

            val farmLand = if (result <= 0.95f) FarmlandFactory.getFarmland(this.getWorld(), landPos) else null
            if (farmLand != null) {
                //#region moisturize land

                if (farmLand.moisturize(this.waterTank!!, this.getWorld(), landPos)) {
                    result += 0.05f
                }

                //#endregion
            } else if (hoes.isNotEmpty() && (player != null)
                    && this.getWorld().isAirBlock(pos)
                    && !this.getWorld().isAirBlock(landPos)
                    && (result <= 0.7f)) {
                //#region hoe land

                for (hoe in hoes) {
                    val hoeStack = inputs.getStackInSlot(hoe.first)
                    if (!hoeStack.isEmpty && (hoe.second != null)) {
                        player.setHeldItem(EnumHand.MAIN_HAND, hoeStack)
                        if (hoe.second!!.hoe(player, hoeStack, this.getWorld(), landPos)) {
                            result += 0.3f
                            break
                        }
                    }
                }

                //#endregion
            }

            if (result > 0.95f) {
                // no more power for anything
                break
            }
        }

        //endregion

        //#region fertilize all the thingies

        if (result <= .9f) {
            var fertilizer = ItemStack.EMPTY
            for (stack in ItemStackUtil.getCombinedInventory(inputs)) {
                if (PlantWrapperFactory.isFertilizer(stack)) {
                    fertilizer = stack.copy()
                    break
                }
            }
            if (!fertilizer.isEmpty) {
                var tries = 10
                while (tries >= 0 && result <= .9f && !fertilizer.isEmpty) {
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
