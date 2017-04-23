package net.ndrei.teslapoweredthingies.machines;

import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.FluidTankPiece;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredFluidHandler;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.inventory.FluidTank;
import net.ndrei.teslacorelib.tileentities.ElectricGenerator;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;
import net.ndrei.teslapoweredthingies.common.FluidUtils;
import net.ndrei.teslapoweredthingies.gui.FluidBurnerTankPiece;
import net.ndrei.teslapoweredthingies.gui.GeneratorBurnPiece;
import net.ndrei.teslapoweredthingies.gui.IDualTankMachine;
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerCoolant;
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerFuel;
import net.ndrei.teslapoweredthingies.machines.fluidburner.FluidBurnerRecipes;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by CF on 2017-01-09.
 */
public class FluidBurnerEntity extends ElectricGenerator implements IDualTankMachine {
    private FluidTank coolantTank;
    private FluidTank fuelTank;
    private ItemStackHandler coolantItems;
    private ItemStackHandler fuelItems;

    private Fluid coolantInUse = null;
    private Fluid fuelInUse = null;

    public FluidBurnerEntity() {
        super(FluidBurnerEntity.class.getName().hashCode());
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();

        this.coolantItems = new ItemStackHandler(2);
        super.addInventory(new ColoredItemHandler(this.coolantItems, EnumDyeColor.MAGENTA, "Coolant Containers", new BoundingRectangle(61, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return (slot == 0) && FluidUtils.canFillFrom(FluidBurnerEntity.this.coolantTank, stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return (slot != 0);
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);

                BoundingRectangle box = this.getBoundingBox();
                if (box != null) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 0, box.getLeft() + 1, box.getTop() + 1));
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 1, box.getLeft() + 1, box.getTop() + 1 + 36));
                }

                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                if (box != null) {
                    pieces.add(new BasicRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 54,
                            TeslaThingiesMod.MACHINES_TEXTURES, 6, 44));
                }

                return pieces;
            }
        });
        super.addInventoryToStorage(this.coolantItems, "inv_coolant");

        super.addFluidTank(
                new ColoredFluidHandler(this.coolantTank = new FluidTank(5000),
                        EnumDyeColor.BLUE,
                        "Coolant Tank",
                        new BoundingRectangle(79, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
                    @Override
                    public boolean acceptsFluid(@Nonnull FluidStack fluid) {
                        return FluidBurnerRecipes.isCoolant(fluid);
                    }

                    @Override
                    public boolean canDrain() {
                        return false;
                    }
                },
                null
        );

        super.addFluidTank(
                new ColoredFluidHandler(this.fuelTank = new FluidTank(5000),
                        EnumDyeColor.RED,
                        "Fuel Tank",
                        new BoundingRectangle(97, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
                    @Override
                    public boolean acceptsFluid(@Nonnull FluidStack fluid) {
                        return FluidBurnerRecipes.isFuel(fluid);
                    }

                    @Override
                    public boolean canDrain() {
                        return false;
                    }
                },
                null
        );

        this.fuelItems = new ItemStackHandler(2);
        super.addInventory(new ColoredItemHandler(this.fuelItems, EnumDyeColor.PURPLE, "Fuel Containers", new BoundingRectangle(115, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return (slot == 0) && FluidUtils.canFillFrom(FluidBurnerEntity.this.fuelTank, stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return (slot != 0);
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);

                BoundingRectangle box = this.getBoundingBox();
                if (box != null) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 0, box.getLeft() + 1, box.getTop() + 1));
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 1, box.getLeft() + 1, box.getTop() + 1 + 36));
                }

                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                if (box != null) {
                    pieces.add(new BasicRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 54,
                            BasicTeslaGuiContainer.MACHINE_BACKGROUND, 78, 189));
                }

                return pieces;
            }
        });
        super.addInventoryToStorage(this.fuelItems, "inv_fuel");
    }

    @Override
    protected void ensureFluidItems() {
        // do nothing, we don't need that here
    }

    @Override
    protected void processImmediateInventories() {
        super.processImmediateInventories();

        this.processFluidItems(this.coolantItems, this.coolantTank);
        this.processFluidItems(this.fuelItems, this.fuelTank);
    }

    private void processFluidItems(ItemStackHandler handler, IFluidTank tank) {
        ItemStack stack = handler.getStackInSlot(0);
        if (!ItemStackUtil.isEmpty(stack) && FluidUtils.canFillFrom(tank, stack)) {
            ItemStack result = FluidUtils.fillFluidFrom(tank, stack);
            if (!ItemStack.areItemStacksEqual(stack, result)) {
                handler.setStackInSlot(0, result);
                this.discardUsedFluidItem(handler);
            }
        } else if (!ItemStackUtil.isEmpty(stack)) {
            this.discardUsedFluidItem(handler);
        }
    }

    private void discardUsedFluidItem(ItemStackHandler handler) {
        ItemStack source = handler.getStackInSlot(0);
        ItemStack result = handler.insertItem(1, source, false);
        handler.setStackInSlot(0, result);
    }

    @Override
    protected long consumeFuel() {
        FluidBurnerFuel fuel = FluidBurnerRecipes.drainFuel(this.fuelTank, true);
        if (fuel != null) {
            long power = fuel.recipe.baseTicks;
            this.fuelInUse = fuel.fuel.getFluid();

            FluidBurnerCoolant coolant = FluidBurnerRecipes.drainCoolant(this.coolantTank, true);
            if (coolant != null) {
                power *= coolant.recipe.timeMultiplier;
                this.coolantInUse = coolant.coolant.getFluid();
            }

            power *= this.getEnergyFillRate();
            return power;
        }

        return 0;
    }

    @Override
    protected long getEnergyOutputRate() {
        return 80;
    }

    @Override
    protected long getEnergyFillRate() {
        return 80;
    }

    public Fluid getCoolantInUse() { return this.coolantInUse; }
    public Fluid getFuelInUse() { return this.fuelInUse; }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

        pieces.add(new GeneratorBurnPiece(144, 63, this));

        pieces.add(new FluidBurnerTankPiece(142, 27, this));

        return pieces;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        if (this.fuelInUse != null) {
            compound.setString("fuelInUse", FluidRegistry.getFluidName(this.fuelInUse));
        }

        if (this.coolantInUse != null) {
            compound.setString("coolantInUse", FluidRegistry.getFluidName(this.coolantInUse));
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("fuelInUse")) {
            this.fuelInUse = FluidRegistry.getFluid(compound.getString("fuelInUse"));
        }

        if (compound.hasKey("coolantInUse")) {
            this.coolantInUse = FluidRegistry.getFluid(compound.getString("coolantInUse"));
        }
    }

    public float getLeftTankPercent() {
        return Math.min(1, Math.max(0, (float)this.coolantTank.getFluidAmount() / (float)this.coolantTank.getCapacity()));
    }

    public float getRightTankPercent() {
        return Math.min(1, Math.max(0, (float)this.fuelTank.getFluidAmount() / (float)this.fuelTank.getCapacity()));
    }

    public Fluid getLeftTankFluid() {
        FluidStack stack = this.coolantTank.getFluid();
        return (stack == null) ? null : stack.getFluid();
    }

    public Fluid getRightTankFluid() {
        FluidStack stack = this.fuelTank.getFluid();
        return (stack == null) ? null : stack.getFluid();
    }
}