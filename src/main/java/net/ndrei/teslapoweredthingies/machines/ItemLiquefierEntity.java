package net.ndrei.teslapoweredthingies.machines;

import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.*;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.inventory.FluidStorage;
import net.ndrei.teslacorelib.tileentities.ElectricMachine;
import net.ndrei.teslapoweredthingies.TeslaThingiesMod;
import net.ndrei.teslapoweredthingies.gui.IWorkItemProvider;
import net.ndrei.teslapoweredthingies.gui.ItemStackPiece;
import net.ndrei.teslapoweredthingies.machines.itemliquefier.LiquefierRecipe;
import net.ndrei.teslapoweredthingies.machines.itemliquefier.LiquefierRecipes;

import java.util.List;

/**
 * Created by CF on 2017-04-09.
 */
public class ItemLiquefierEntity extends ElectricMachine implements IWorkItemProvider {
    private IFluidTank lavaTank;
    private ItemStackHandler inputs;
    private ItemStackHandler fluidOutputs;

    private LiquefierRecipe currentRecipe = null;

    public ItemLiquefierEntity() {
        super(ItemLiquefierEntity.class.getName().hashCode());
    }

    //region Inventory and GUI stuff

    @Override
    protected void initializeInventories() {
        super.initializeInventories();

        super.ensureFluidItems();
        this.lavaTank = super.addFluidTank(FluidRegistry.LAVA, 5000, EnumDyeColor.RED, "Lava Tank",
                new BoundingRectangle(133, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT));

        this.inputs = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                ItemLiquefierEntity.this.markDirty();
            }
        };
        super.addInventory(new ColoredItemHandler(this.inputs, EnumDyeColor.GREEN, "Input Items", new BoundingRectangle(61, 25, 18, 54)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return !ItemStackUtil.isEmpty(stack) && (LiquefierRecipes.getRecipe(stack.getItem()) != null);
            }

            @Override
            public boolean canExtractItem(int slot) { return false; }

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
        super.addInventoryToStorage(this.inputs, "inv_inputs");

        BoundingRectangle box = new BoundingRectangle(151, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT);
        this.fluidOutputs = new ItemStackHandler(2) {
            @Override
            public int getSlotLimit(int slot) {
                return (slot == 0) ? 1 : super.getSlotLimit(slot);
            }

            @Override
            protected void onContentsChanged(int slot) {
                ItemLiquefierEntity.this.markDirty();
            }
        };
        super.addInventory(new ColoredItemHandler(this.fluidOutputs, EnumDyeColor.SILVER, "Fluid Output", box) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return (slot == 0) && ItemLiquefierEntity.this.isValidFluidContainer(stack);
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
                    pieces.add(new BasicRenderedGuiPiece(box.getLeft(), box.getTop(), box.getWidth(), box.getHeight(),
                            TeslaThingiesMod.MACHINES_TEXTURES, 98, 36));
                }

                return pieces;
            }
        });
        super.addInventoryToStorage(this.fluidOutputs, "inv_fluid_outputs");
    }

    private boolean isValidFluidContainer(ItemStack stack) {
        if (!ItemStackUtil.isEmpty(stack)) {
            Item item = stack.getItem();
            if ((item == Items.GLASS_BOTTLE) || (item == Items.BUCKET)) {
                return true;
            }

            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if (handler != null) {
                    IFluidTankProperties[] tanks = handler.getTankProperties();
                    if ((tanks != null) && (tanks.length > 0)) {
                        for(IFluidTankProperties tank: tanks) {
                            if (tank.canFill()) {
                                FluidStack content = tank.getContents();
                                if ((content == null) || ((content.amount < tank.getCapacity()) && (content.getFluid() == FluidRegistry.LAVA))) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected boolean shouldAddFluidItemsInventory() {
        return false;
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

        pieces.add(new BasicRenderedGuiPiece(79, 41, 54, 22,
                TeslaThingiesMod.MACHINES_TEXTURES, 24, 4));

        pieces.add(new BasicRenderedGuiPiece(99, 64, 14, 14,
                TeslaThingiesMod.MACHINES_TEXTURES, 44, 27));

        pieces.add(new ItemStackPiece(96, 42, 20, 20, this));

        return pieces;
    }

    //endregion

    //region serialization

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("currentRecipe")) {
            this.currentRecipe = LiquefierRecipe.deserializeNBT(compound.getCompoundTag("currentRecipe"));
        }
        else {
            this.currentRecipe = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        if (this.currentRecipe != null) {
            compound.setTag("currentRecipe", this.currentRecipe.serializeNBT());
        }

        return compound;
    }

    //endregion

    @Override
    public ItemStack getWorkItem() {
        if (this.currentRecipe != null) {
            return new ItemStack(this.currentRecipe.input, this.currentRecipe.inputStackSize);
        }
        return ItemStackUtil.getEmptyStack();
    }

    @Override
    protected int getEnergyForWork() {
        return 6000;
    }

    @Override
    protected float performWork() {
        float result = 0.0f;
        if (this.currentRecipe != null) {
            FluidStack fluid = new FluidStack(this.currentRecipe.output, this.currentRecipe.outputQuantity);
            if (this.lavaTank.fill(fluid, false) == this.currentRecipe.outputQuantity) {
                this.lavaTank.fill(fluid, true);
                this.currentRecipe = null;
                result = 1.0f;
            }
        }

        return result;
    }

    @Override
    protected void processImmediateInventories() {
        super.processImmediateInventories();

        ItemStack stack = this.fluidOutputs.getStackInSlot(0);
        int maxDrain = this.lavaTank.getFluidAmount();
        if (!ItemStackUtil.isEmpty(stack) && (maxDrain > 0)) {
            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                int toFill = Math.max(maxDrain, Fluid.BUCKET_VOLUME);
                FluidStorage dummy = new FluidStorage();
                dummy.addTank(this.lavaTank);
                FluidActionResult result = FluidUtil.tryFillContainer(stack, dummy, toFill, null, true);
                if (result.isSuccess()) {
                    this.fluidOutputs.setStackInSlot(0, stack = result.getResult());
                }

                if (!ItemStackUtil.isEmpty(stack) && !this.isEmptyFluidContainer(stack)) {
                    this.fluidOutputs.setStackInSlot(0, this.fluidOutputs.insertItem(1, stack, false));
                }
            }
        }

        if (this.currentRecipe == null) {
            for(ItemStack input : ItemStackUtil.getCombinedInventory(this.inputs)) {
                LiquefierRecipe recipe = LiquefierRecipes.getRecipe(input.getItem());
                if ((recipe != null) && (recipe.inputStackSize <= ItemStackUtil.getSize(input))) {
                    FluidStack fluid = new FluidStack(recipe.output, recipe.outputQuantity);
                    if (this.lavaTank.fill(fluid, false) == recipe.outputQuantity) {
                        ItemStackUtil.extractFromCombinedInventory(this.inputs, input, recipe.inputStackSize);
                        this.currentRecipe = recipe;
                        break;
                    }
                }
            }
        }
    }

    private boolean isEmptyFluidContainer(ItemStack stack) {
        FluidStack fluid = FluidUtil.getFluidContained(stack);
        return (fluid == null) || (fluid.amount == 0);
    }
}
