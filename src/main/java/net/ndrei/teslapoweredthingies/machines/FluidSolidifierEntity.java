package net.ndrei.teslapoweredthingies.machines;

import com.google.common.collect.Lists;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.*;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;
import net.ndrei.teslacorelib.tileentities.ElectricMachine;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;
import net.ndrei.teslapoweredthingies.gui.IDualTankMachine;
import net.ndrei.teslapoweredthingies.machines.fluidsolidifier.FluidSolidifierResult;

import java.util.List;

/**
 * Created by CF on 2017-01-11.
 */
public class FluidSolidifierEntity extends ElectricMachine implements IDualTankMachine {
    private IFluidTank waterTank;
    private IFluidTank lavaTank;
    private ItemStackHandler outputs;

    private FluidSolidifierResult resultType = FluidSolidifierResult.COBBLESTONE;
    private FluidSolidifierResult lastWorkResult = null;

    public FluidSolidifierEntity() {
        super(FluidSolidifierEntity.class.hashCode());
    }

    //region Inventory and GUI stuff

    @Override
    protected void initializeInventories() {
        super.initializeInventories();

        super.ensureFluidItems();
        this.waterTank = super.addFluidTank(FluidRegistry.WATER, 5000, EnumDyeColor.BLUE, "Water Tank",
                new BoundingRectangle(79, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT));
        this.lavaTank = super.addFluidTank(FluidRegistry.LAVA, 5000, EnumDyeColor.RED, "Lava Tank",
                new BoundingRectangle(97, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT));

        this.outputs = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                FluidSolidifierEntity.this.markDirty();
            }
        };
        super.addInventory(new ColoredItemHandler(this.outputs, EnumDyeColor.PURPLE, "Output Items", new BoundingRectangle(151, 25, 18, 54)) {
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
    }

    protected boolean shouldAddFluidItemsInventory() {
        return true;
    }

    protected BoundingRectangle getFluidItemsBoundingBox() {
        return new BoundingRectangle(61, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT);
    }

    protected void addFluidItemsBackground(List<IGuiContainerPiece> pieces, BoundingRectangle box) {
        pieces.add(new BasicRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 54,
                TeslaThingiesMod.MACHINES_TEXTURES, 6, 44));
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

        pieces.add(new BasicRenderedGuiPiece(115, 32, 36, 40,
                TeslaThingiesMod.MACHINES_TEXTURES, 61, 43));

        pieces.add(new ToggleButtonPiece(125, 44, 16, 16) {
            @Override
            protected int getCurrentState() {
                return 0;
            }

            @Override
            protected void renderState(BasicTeslaGuiContainer container, int state, BoundingRectangle box) {
                super.renderItemStack(container, FluidSolidifierEntity.this.resultType.resultStack, box);
            }

            @Override
            protected void clicked() {
                FluidSolidifierEntity.this.resultType = FluidSolidifierResult.fromStateIndex(
                        (FluidSolidifierEntity.this.resultType.stateIndex + 1) % FluidSolidifierResult.VALUES.length);

                NBTTagCompound nbt = FluidSolidifierEntity.this.setupSpecialNBTMessage("SET_RESULT_TYPE");
                nbt.setInteger("result_type", FluidSolidifierEntity.this.resultType.stateIndex);
                TeslaCoreLib.network.sendToServer(new SimpleNBTMessage(FluidSolidifierEntity.this, nbt));
            }

            @Override
            public void drawForegroundTopLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
                if (!BasicContainerGuiPiece.isInside(container, this, mouseX, mouseY)) {
                    return;
                }

                List<String> lines = Lists.newArrayList();

                FluidSolidifierResult result = FluidSolidifierEntity.this.resultType;

                lines.add("Result: " + result.resultStack.getDisplayName());
                lines.add("Water: " + result.waterMbConsumed + " mb (min: " + result.waterMbMin + " mb)");
                lines.add("Lava: " + result.lavaMbConsumed + " mb (min: " + result.lavaMbMin + " mb)");
                lines.add("Time: " + ((float)result.ticksRequired / 20.0f) + " s (~ " + (result.ticksRequired * FluidSolidifierEntity.this.getWorkEnergyTick()) + " T)");

                container.drawTooltip(lines, mouseX - guiX, mouseY - guiY);
            }
        });

        return pieces;
    }

    //endregion

    //#region IDualTankMachine

    public float getLeftTankPercent() {
        return Math.min(1, Math.max(0, (float)this.waterTank.getFluidAmount() / (float)this.waterTank.getCapacity()));
    }

    public float getRightTankPercent() {
        return Math.min(1, Math.max(0, (float)this.lavaTank.getFluidAmount() / (float)this.lavaTank.getCapacity()));
    }

    public Fluid getLeftTankFluid() {
        FluidStack stack = this.waterTank.getFluid();
        return (stack == null) ? null : stack.getFluid();
    }

    public Fluid getRightTankFluid() {
        FluidStack stack = this.lavaTank.getFluid();
        return (stack == null) ? null : stack.getFluid();
    }
    //#endregion

    //region serialization

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("result_type")) {
            this.resultType = FluidSolidifierResult.fromStateIndex(
                    compound.getInteger("result_type"));
        }

        if (compound.hasKey("work_result")) {
            this.lastWorkResult = FluidSolidifierResult.fromStateIndex(
                    compound.getInteger("work_result"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        compound.setInteger("result_type", this.resultType.stateIndex);

        if (this.lastWorkResult != null) {
            compound.setInteger("work_result", this.lastWorkResult.stateIndex);
        }

        return compound;
    }

    @Override
    protected SimpleNBTMessage processClientMessage(String messageType, NBTTagCompound compound) {
        if ((messageType != null) && messageType.equals("SET_RESULT_TYPE")) {
            this.resultType = FluidSolidifierResult.fromStateIndex(compound.getInteger("result_type"));
            this.markDirty();
        }

        return super.processClientMessage(messageType, compound);
    }

    //endregion

    @Override
    protected int getEnergyForWork() {
        return (this.lastWorkResult = this.resultType)
                .ticksRequired * this.getEnergyForWorkRate();
    }

    @Override
    protected float performWork() {
        if (this.lastWorkResult == null) {
            return 0.0f;
        }

        boolean hasWater = (this.waterTank.getFluidAmount() >= this.lastWorkResult.waterMbMin);
        if (hasWater) {
            boolean hasLava = (this.lavaTank.getFluidAmount() >= this.lastWorkResult.lavaMbMin);
            if (hasLava) {
                boolean waterRequired = (this.lastWorkResult.waterMbConsumed > 0);
                FluidStack water = waterRequired ? this.waterTank.drain(this.lastWorkResult.waterMbConsumed, false) : null;
                if (!waterRequired || ((water != null) && (water.amount == this.lastWorkResult.waterMbConsumed))) {
                    boolean lavaRequired = (this.lastWorkResult.lavaMbConsumed > 0);
                    FluidStack lava = lavaRequired ? this.lavaTank.drain(this.lastWorkResult.lavaMbConsumed, false) : null;
                    if (!lavaRequired || ((lava != null) && (lava.amount == this.lastWorkResult.lavaMbConsumed))) {
                        ItemStack remaining = ItemHandlerHelper.insertItem(this.outputs, this.lastWorkResult.resultStack.copy(), false);
                        if (ItemStackUtil.isEmpty(remaining)) {
                            // actually drain liquids
                            if (waterRequired) {
                                this.waterTank.drain(this.lastWorkResult.waterMbConsumed, true);
                            }
                            if (lavaRequired) {
                                this.lavaTank.drain(this.lastWorkResult.lavaMbConsumed, true);
                            }

                            // work performed
                            this.lastWorkResult = null;
                            return 1.0f;
                        }
                    }
                }
            }
        }
        return 0.0f;
    }
}
