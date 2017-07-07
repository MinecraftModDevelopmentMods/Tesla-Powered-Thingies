package net.ndrei.teslapoweredthingies.integrations.jei

import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AnnotationPreInitHandler
import net.ndrei.teslacorelib.annotations.BaseAnnotationHandler

/**
 * Created by CF on 2017-07-06.
 */
@Target(AnnotationTarget.CLASS)
annotation class TeslaThingyJeiCategory

@Suppress("unused")
@AnnotationPreInitHandler
object TeslaThingyJeiCategoryHandler: BaseAnnotationHandler<BaseCategory<*>>({ it, _, _ ->
    if (TeslaCoreLib.isClientSide) run {
        TheJeiThing.registerCategory(it)
    }
}, TeslaThingyJeiCategory::class)