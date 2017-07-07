package net.ndrei.teslapoweredthingies.machines.treefarm

import com.google.common.collect.Lists
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.machines.ElectricFarmMachine
import java.util.*

/**
 * Created by CF on 2017-07-07.
 */
class TreeFarmEntity : ElectricFarmMachine(TreeFarmEntity::class.java.name.hashCode()) {
    private val scanner = TreeScanner()

    override fun acceptsInputStack(slot: Int, stack: ItemStack): Boolean {
        if (ItemStackUtil.isEmpty(stack))
            return true

        return TreeFarmEntity.acceptedItems.contains(stack.item)
    }

    override fun performWork(): Float {
        val facing = super.facing
        var result = 0f
        val cube = this.getWorkArea(facing.getOpposite(), 1)

        //#region scan trees

        result += this.scanner.scan(this.getWorld(), cube, SCAN_PERCENT, 1.0f)

        //#endregion

        val inputs = this.inStackHandler!!

        //#region plant saplings

        val saplings = TreeWrapperFactory.getSaplingWrappers(ItemStackUtil.getCombinedInventory(inputs))
                .toMutableList()
        if (saplings.isNotEmpty()) {
            for (pos in cube) {
                if (result > 1 - PLANT_PERCENT) {
                    break
                }

                if (this.getWorld().isAirBlock(pos)) {
                    for (sapling in saplings!!) {
                        if (sapling.canPlant(this.getWorld(), pos)) {
                            val planted = sapling.plant(this.getWorld(), pos)
                            if (planted > 0) {
                                val original = sapling.stack
                                val extracted = ItemStackUtil.extractFromCombinedInventory(inputs, original, planted)
                                if (ItemStackUtil.getSize(original) <= extracted) {
                                    saplings.remove(sapling)
                                } else {
                                    ItemStackUtil.shrink(original, extracted)
                                }

                                result += PLANT_PERCENT
                                break
                            }
                        }
                    }
                }
            }
        }

        //#endregion

        //#region cut trees

        var hasShears = false
        var shearsSlot = 0
        for (index in 0..inputs.getSlots() - 1) {
            val stack = inputs.getStackInSlot(index)
            if (!ItemStackUtil.isEmpty(stack) && stack.item === Items.SHEARS) {
                hasShears = true
                shearsSlot = index
                break
            }
        }

        val items = Lists.newArrayList<ItemStack>()
        while (result <= 1 - BREAK_PERCENT && this.scanner.blockCount() > 0) {
            val pos = this.scanner.popScannedPos()
            if (pos != null) {
                val wrapper = TreeWrapperFactory.getBlockWrapper(this.getWorld(), pos, null)
                if (wrapper is ITreeLeafWrapper) {
                    if (hasShears) {
                        items.addAll(wrapper.shearBlock())
                        if (shearsSlot >= 0) {
                            if (inputs.getStackInSlot(shearsSlot).attemptDamageItem(1, this.getWorld().rand, TeslaThingiesMod.getFakePlayer(this.getWorld()))) {
                                inputs.setStackInSlot(shearsSlot, ItemStackUtil.emptyStack)
                                shearsSlot = -1
                                hasShears = false
                            }
                        }
                    } else {
                        items.addAll(wrapper!!.breakBlock(1))
                    }
                } else if (wrapper != null) {
                    items.addAll(wrapper!!.breakBlock(1))
                }

                result += BREAK_PERCENT
            }
        }

        if (items.size > 0) {
            // TODO: find a way to not lose the items
            super.outputItems(items)
        }

        //#endregion

        return result
    }

    companion object {
        private val acceptedItems = ArrayList<Item>()

        init {
            TreeFarmEntity.acceptedItems.add(Items.SHEARS)
            TreeFarmEntity.acceptedItems.add(Item.getItemFromBlock(Blocks.SAPLING))
        }

        private const val SCAN_PERCENT = 0.025f
        private const val BREAK_PERCENT = 0.05f
        private const val PLANT_PERCENT = 0.10f
    }
}
