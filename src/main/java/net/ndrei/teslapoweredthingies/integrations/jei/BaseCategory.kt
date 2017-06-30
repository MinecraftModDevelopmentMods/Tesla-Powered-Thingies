package net.ndrei.teslapoweredthingies.integrations.jei

import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper

/**
 * Created by CF on 2017-06-30.
 */
abstract class BaseCategory<T: IRecipeWrapper> : IRecipeCategory<T> {
    override fun getModName(): String {
        return "Tesla Powered Thingies"
    }
}