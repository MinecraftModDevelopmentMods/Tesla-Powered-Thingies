package net.ndrei.teslapoweredthingies.common

import net.ndrei.teslapoweredthingies.items.BaseAnimalFilterItem

/**
 * Created by CF on 2017-07-07.
 */
interface IAnimalAgeFilterAcceptor {
    fun acceptsFilter(item: BaseAnimalFilterItem): Boolean
}