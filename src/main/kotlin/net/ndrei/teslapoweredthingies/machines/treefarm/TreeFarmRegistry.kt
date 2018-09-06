package net.ndrei.teslapoweredthingies.machines.treefarm

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.registries.IForgeRegistry
import net.modcrafters.mclib.ingredients.IItemIngredient
import net.modcrafters.mclib.ingredients.implementations.IngredientFactory
import net.modcrafters.mclib.mapFirstOrNull
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslapoweredthingies.common.BaseTeslaRegistry
import net.ndrei.teslapoweredthingies.config.readExtraRecipesFile

@RegistryHandler
object TreeFarmRegistry
    : BaseTeslaRegistry<TreeFarmModRecipe>("tree_farm_mod_recipes", TreeFarmModRecipe::class.java), ITreeFactory {

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        super.registerRecipes(asm, registry)

        readExtraRecipesFile(TreeFarmBlock.registryName!!.path) { json ->
            val modId = JsonUtils.getString(json, "mod_id", "no_mod")

            val logs = JsonUtils.getJsonArray(json, "logs").map {
                IngredientFactory.getIngredientFromString(it.asString)
            }
            if (logs.isNotEmpty()) {
                this.addRecipe(TreeFarmModRecipe(ResourceLocation(modId, "tree_farm_logs"),
                        TreeFarmModRecipe.RecipeType.LOG,
                        *logs.fold(mutableListOf<ItemStack>()) { list, item ->
                            if (item is IItemIngredient)
                                list.addAll(item.itemStacks)
                            list
                        }.toTypedArray()
                    ))
            }

            val leaves = JsonUtils.getJsonArray(json, "leaves").map {
                IngredientFactory.getIngredientFromString(it.asString)
            }
            if (leaves.isNotEmpty()) {
                this.addRecipe(TreeFarmModRecipe(ResourceLocation(modId, "tree_farm_leaves"),
                    TreeFarmModRecipe.RecipeType.LEAF,
                    *leaves.fold(mutableListOf<ItemStack>()) { list, item ->
                        if (item is IItemIngredient)
                            list.addAll(item.itemStacks)
                        list
                    }.toTypedArray()
                ))
            }

            val saplings = JsonUtils.getJsonArray(json, "saplings").map {
                IngredientFactory.getIngredientFromString(it.asString)
            }
            if (saplings.isNotEmpty()) {
                this.addRecipe(TreeFarmModRecipe(ResourceLocation(modId, "tree_farm_saplings"),
                    TreeFarmModRecipe.RecipeType.SAPLING,
                    *saplings.fold(mutableListOf<ItemStack>()) { list, item ->
                        if (item is IItemIngredient)
                            list.addAll(item.itemStacks)
                        list
                    }.toTypedArray()
                ))
            }
        }
    }

    override fun getHarvestableLog(world: World, pos: BlockPos, block: IBlockState) =
        this.getAllRecipes().mapFirstOrNull { it.getHarvestableLog(world, pos, block) }

    override fun getHarvestableLeaf(world: World, pos: BlockPos, block: IBlockState) =
        this.getAllRecipes().mapFirstOrNull { it.getHarvestableLeaf(world, pos, block) }

    override fun getPlantableSapling(stack: ItemStack) =
        this.getAllRecipes().mapFirstOrNull { it.getPlantableSapling(stack) }
}
