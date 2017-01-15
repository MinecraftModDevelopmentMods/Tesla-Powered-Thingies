package net.ndrei.teslapoweredthingies.machines;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.tileentities.ElectricGenerator;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;
import net.ndrei.teslapoweredthingies.common.SecondaryOutput;
import net.ndrei.teslapoweredthingies.gui.GeneratorBurnPiece;
import net.ndrei.teslapoweredthingies.gui.IWorkItemProvider;
import net.ndrei.teslapoweredthingies.gui.ItemStackPiece;
import net.ndrei.teslapoweredthingies.machines.incinerator.IncineratorRecipes;

import java.util.List;

/**
 * Created by CF on 2017-01-06.
 */
public class IncineratorEntity extends ElectricGenerator implements IWorkItemProvider {
    private ItemStackHandler inputs;
    private ItemStackHandler outputs;
    private ItemStackHandler currentItem;

    public IncineratorEntity() {
        super(IncineratorEntity.class.hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();

        this.inputs = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                IncineratorEntity.this.markDirty();
            }
        };
        super.addInventory(new ColoredItemHandler(this.inputs, EnumDyeColor.GREEN, "Input Items", new BoundingRectangle(61, 43, 18, 18)) {
            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                if ((stack == null) || stack.isEmpty()) {
                    return false;
                }

                return IncineratorRecipes.isFuel(stack);
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);

                BoundingRectangle box = this.getBoundingBox();
                slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 0, box.getLeft() + 1, box.getTop() + 1));

                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        1, 1,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.GREEN));

                return pieces;
            }
        });
        super.addInventoryToStorage(this.inputs, "inv_inputs");

        this.outputs = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                IncineratorEntity.this.markDirty();
            }
        };
        super.addInventory(new ColoredItemHandler(this.outputs, EnumDyeColor.PURPLE, "Output Items", new BoundingRectangle(133, 25, 18, 54)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);

                BoundingRectangle box = this.getBoundingBox();
                for(int y = 0; y < 3; y++) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), y,
                            box.getLeft() + 1, box.getTop() + 1 + y * 18));
                }

                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        1, 3,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.PURPLE));

                return pieces;
            }
        });
        super.addInventoryToStorage(this.outputs, "inv_outputs");

        this.currentItem = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                IncineratorEntity.this.markDirty();
            }
        };
        super.addInventoryToStorage(this.currentItem, "inv_current");
    }

    @Override
    protected long consumeFuel() {
        if (this.currentItem.getStackInSlot(0).isEmpty()) {
            ItemStack stack = this.inputs.extractItem(0, 1, true);
            if (!stack.isEmpty()) {
                long power = IncineratorRecipes.getPower(stack);
                if (power > 0) {
                    stack = this.inputs.extractItem(0, 1, false);
                    if (!stack.isEmpty()) {
                        this.currentItem.setStackInSlot(0, stack);
                        return power;
                    }
                }
            }
        }
        return 0;
    }

    @Override
    protected void fuelConsumed() {
        ItemStack stack = this.currentItem.getStackInSlot(0);
        if (!ItemStackUtil.isEmpty(stack)) {
            SecondaryOutput[] secondary = IncineratorRecipes.getSecondaryOutputs(stack.getItem());
            if ((secondary != null) && (secondary.length > 0)) {
                for(SecondaryOutput so : secondary) {
                    float chance = this.getWorld().rand.nextFloat();
                    // TeslaThingiesMod.logger.info("Change: " + chance + " vs " + so.chance);
                    if (chance <= so.chance) {
                        ItemStack thing = so.stack;
                        if (!ItemStackUtil.isEmpty(thing)) {
                            thing = ItemHandlerHelper.insertItem(this.outputs, thing.copy(), false);
                            if (!ItemStackUtil.isEmpty(thing)) {
                                BlockPos spawnPos = this.pos.offset(super.getFacing());
                                this.getWorld().spawnEntity(
                                        new EntityItem(this.getWorld(), spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()
                                                , thing));
                            }
                            super.forceSync();
                        }
                    }
                }
            }
        }
        this.currentItem.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Override
    protected long getEnergyOutputRate() {
        return 40;
    }

    @Override
    protected long getEnergyFillRate() {
        return 40;
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

        pieces.add(new BasicRenderedGuiPiece(79, 41, 54, 22,
                TeslaThingiesMod.MACHINES_TEXTURES, 24, 4));

        pieces.add(new GeneratorBurnPiece(99, 64, this));

        pieces.add(new ItemStackPiece(94, 41, 22, 22, this) {
            @Override
            public void drawForegroundTopLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
                if (!this.isInside(container, mouseX, mouseY)) {
                    return;
                }

                List<String> lines = GeneratorBurnPiece.getTooltipLines(IncineratorEntity.this);
                if ((lines != null) && (lines.size() > 0)) {
                    container.drawTooltip(lines, mouseX - guiX, mouseY - guiY);
                }
            }
        });

        return pieces;
    }

    @Override
    public ItemStack getWorkItem() {
        return (this.currentItem == null) ? ItemStack.EMPTY : this.currentItem.getStackInSlot(0);
    }
}
