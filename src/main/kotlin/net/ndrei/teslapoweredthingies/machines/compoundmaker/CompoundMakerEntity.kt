package net.ndrei.teslapoweredthingies.machines.compoundmaker

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.EnumDyeColor
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagString
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.IItemHandlerModifiable
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.FluidTankType
import net.ndrei.teslacorelib.inventory.SyncProviderLevel
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage
import net.ndrei.teslapoweredthingies.TeslaThingiesMod
import net.ndrei.teslapoweredthingies.common.gui.IMultiTankMachine
import net.ndrei.teslapoweredthingies.common.gui.TankInfo
import net.ndrei.teslapoweredthingies.machines.BaseThingyMachine
import net.ndrei.teslapoweredthingies.render.DualTankEntityRenderer
import java.util.function.Consumer
import java.util.function.Supplier

class CompoundMakerEntity
    : BaseThingyMachine(CompoundMakerEntity::class.java.name.hashCode()), IMultiTankMachine {

    private lateinit var leftFluid: IFluidTank
    private lateinit var rightFluid: IFluidTank
    private lateinit var topInventory: IItemHandlerModifiable
    private lateinit var bottomInventory: IItemHandlerModifiable
    private lateinit var outputInventory: IItemHandlerModifiable

    //#region inventory & gui

    override fun initializeInventories() {
        this.leftFluid = this.addSimpleFluidTank(5000, "Left Tank", EnumDyeColor.BLUE, 52, 25,
            FluidTankType.INPUT,
            { fluid -> CompoundMakerRegistry.acceptsLeft(fluid) })

        this.topInventory = this.addSimpleInventory(3, "input_top", EnumDyeColor.GREEN, "Top Inputs",
            BoundingRectangle.slots(70, 22, 3, 1),
            { stack, _ -> CompoundMakerRegistry.acceptsTop(stack)},
            { _, _ -> false }, true)

        this.bottomInventory = this.addSimpleInventory(3, "input_bottom", EnumDyeColor.BROWN, "Bottom Inputs",
            BoundingRectangle.slots(70, 64, 3, 1),
            { stack, _ -> CompoundMakerRegistry.acceptsBottom(stack) },
            { _, _ -> false }, true)

        this.rightFluid = this.addSimpleFluidTank(5000, "Right Tank", EnumDyeColor.PURPLE, 124, 25,
            FluidTankType.INPUT,
            { fluid -> CompoundMakerRegistry.acceptsRight(fluid) })

        this.outputInventory = this.addSimpleInventory(1, "output", EnumDyeColor.ORANGE, "Output",
            BoundingRectangle.slots(88, 43, 1, 1),
            { _, _ -> false },
            { _, _ -> true }, false)

        super.registerSyncStringPart(SYNC_CURRENT_RECIPE,
            Consumer { this.currentRecipe = if (it.string.isNotEmpty()) CompoundMakerRegistry.getRecipe(ResourceLocation(it.string)) else null
            },
            Supplier { NBTTagString(if (this.currentRecipe != null) this.currentRecipe!!.registryName.toString() else "") },
            SyncProviderLevel.GUI)

        super.registerSyncStringPart(SYNC_LOCKED_RECIPE,
            Consumer { this.lockedRecipe = if (it.string.isNotEmpty()) CompoundMakerRegistry.getRecipe(ResourceLocation(it.string)) else null },
            Supplier { NBTTagString(if (this.lockedRecipe != null) this.lockedRecipe!!.registryName.toString() else "") },
            SyncProviderLevel.GUI)

        super.registerSyncStringPart(SYNC_RECIPE_MODE,
            Consumer { this.recipeMode = RecipeRunType.valueOf(it.string) },
            Supplier { NBTTagString(this.recipeMode.name) },
            SyncProviderLevel.GUI)

        super.initializeInventories()
    }

    override fun shouldAddFluidItemsInventory() = false

    override fun getRenderers() = super.getRenderers().also {
        it.add(DualTankEntityRenderer)
    }

    override fun getTanks() =
        listOf(
            TankInfo(4.0, 8.0, this.leftFluid.fluid, this.leftFluid.capacity),
            TankInfo(22.0, 8.0, this.rightFluid.fluid, this.rightFluid.capacity)
        )

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>) =
        super.getGuiContainerPieces(container).also {
//            it.add(BasicRenderedGuiPiece(70, 40, 54, 24,
//                ThingiesTexture.MACHINES_TEXTURES.resource, 5, 105))
           CompoundMakerIcon.CENTER_BACKGROUND.addStaticPiece(it, 70,40)

            it.add(CompoundRecipeSelectorPiece(this))
            it.add(CompoundRecipeButtonPiece(this, CompoundRecipeButtonPiece.Direction.UP, top = 25))
            it.add(CompoundRecipeButtonPiece(this, CompoundRecipeButtonPiece.Direction.DOWN, top = 52))
            it.add(CompoundRecipeTriggerPiece(this))
        }

    //#endregion

    //#region selected recipe methods

    private var _availableRecipes: List<CompoundMakerRecipe>? = null
    private var recipeMode = RecipeRunType.PAUSED
    private var recipeIndex = 0

    private var currentRecipe: CompoundMakerRecipe? = null
    private var lockedRecipe: CompoundMakerRecipe? = null

    override fun onSyncPartUpdated(key: String) {
        if (key in arrayOf("input_top", "input_bottom", "fluids")) {
            this._availableRecipes = null
        }
        // TeslaThingiesMod.logger.info("UPDATED: ${key}")
    }

    val availableRecipes: List<CompoundMakerRecipe>
        get() {
            if (this._availableRecipes == null)
                this._availableRecipes = CompoundMakerRegistry.findRecipes(this.leftFluid, this.topInventory, this.rightFluid, this.bottomInventory)
            return this._availableRecipes ?: listOf()
        }

    val hasCurrentRecipe get() = (this.currentRecipe != null) || (this.lockedRecipe != null)

    val selectedRecipe: CompoundMakerRecipe?
        get() = this.currentRecipe ?: this.lockedRecipe ?: this.availableRecipes.let { if (it.isEmpty()) null else it[this.recipeIndex % it.size] }

    var selectedRecipeIndex: Int
        get() = this.availableRecipes.let { if (it.isEmpty()) 0 else (this.recipeIndex % it.size) }
        set(value) {
            if (value in this.availableRecipes.indices) {
                this.recipeIndex = value

                if (this.world?.isRemote == true) {
                    this.sendToServer(this.setupSpecialNBTMessage("SET_RECIPE_INDEX").also {
                        it.setInteger("recipe_index", this.recipeIndex)
                    })
                }
            }
        }

    var selectedRecipeMode: RecipeRunType
        get() = this.recipeMode
        set(value) {
            if (this.recipeMode != value) {
                this.recipeMode = value
                if (this.recipeMode != RecipeRunType.PAUSED) {
                    this.setLockedRecipe(this.selectedRecipe)
                }
                else {
                    this.setLockedRecipe(null)
                }

                if (this.world?.isRemote == true) {
                    this.sendToServer(this.setupSpecialNBTMessage("SET_RECIPE_MODE").also {
                        it.setInteger("recipe_mode", this.recipeMode.ordinal)
                    })
                }
                else {
                    this.partialSync(CompoundMakerEntity.SYNC_RECIPE_MODE)
                }
            }
        }

    private fun setLockedRecipe(recipe: CompoundMakerRecipe?) {
        if (this.lockedRecipe?.registryName != recipe?.registryName) {
            this.lockedRecipe = recipe

            if (this.world?.isRemote == false) {
                this.partialSync(CompoundMakerEntity.SYNC_LOCKED_RECIPE)
            }
        }
    }

    override fun processClientMessage(messageType: String?, player: EntityPlayerMP?, compound: NBTTagCompound): SimpleNBTMessage? {
        when(messageType) {
            "SET_RECIPE_INDEX" -> {
                if (compound.hasKey("recipe_index", Constants.NBT.TAG_INT)) {
                    this.selectedRecipeIndex = compound.getInteger("recipe_index")
                }
                return null
            }
            "SET_RECIPE_MODE" -> {
                if (compound.hasKey("recipe_mode", Constants.NBT.TAG_INT)) {
                    this.selectedRecipeMode = RecipeRunType.byOrdinal(compound.getInteger("recipe_mode"))
                }
                return null
            }
        }
        return super.processClientMessage(messageType, player, compound)
    }

    enum class RecipeRunType(val langKey: String) {
        PAUSED("Machine Paused"),
        SINGLE("Make Single Item"),
        ALL("Make Maximum Items"),
        LOCK("Lock Recipe");

        val next get() = RecipeRunType.values()[(this.ordinal + 1) % RecipeRunType.values().size]

        companion object {
            fun byOrdinal(ordinal: Int) = RecipeRunType.values()[ordinal % RecipeRunType.values().size]
        }
    }

    //#endregion

    override fun getEnergyRequiredForWork(): Long {
        if (this.recipeMode != RecipeRunType.PAUSED) {
            if (this.lockedRecipe?.matches(this.leftFluid, this.topInventory, this.rightFluid, this.bottomInventory) == true) {
                this.currentRecipe = this.lockedRecipe
                TeslaThingiesMod.logger.info("Current Recipe: ${this.currentRecipe!!.registryName}")
                this.currentRecipe!!.processInventories(this.leftFluid, this.topInventory, this.rightFluid, this.bottomInventory)
                return super.getEnergyRequiredForWork()
            }
        }
        return 0L
    }

    override fun performWork(): Float {
        if (this.currentRecipe != null) {
            val stack = this.currentRecipe!!.output.copy()
            if (this.outputInventory.insertItem(0, stack, true).isEmpty) {
                this.outputInventory.insertItem(0, stack, false)
                when (this.recipeMode) {
                    RecipeRunType.SINGLE -> {
                        this.selectedRecipeMode = RecipeRunType.PAUSED
                    }
                    RecipeRunType.ALL -> {
                        if (!this.currentRecipe!!.matches(this.leftFluid, this.topInventory, this.rightFluid, this.bottomInventory)) {
                            this.selectedRecipeMode = RecipeRunType.PAUSED
                        }
                    }
                    else -> { }
                }
                TeslaThingiesMod.logger.info("Current Recipe: [null]; Locked Recipe: ${this.lockedRecipe?.registryName}")
                this.currentRecipe = null
                this.partialSync(SYNC_CURRENT_RECIPE)
                return 1.0f
            }
        }
        return 0.0f
    }

    companion object {
        const val SYNC_CURRENT_RECIPE = "current_recipe"
        const val SYNC_LOCKED_RECIPE = "locked_recipe"
        const val SYNC_RECIPE_MODE = "recipe_mode"
    }
}
