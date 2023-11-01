package com.boogiwoogi.woogidi.pure

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

/**
 * the class that manages instances injected by DiInjector
 */
interface InstanceContainer {

    var value: MutableList<Instance<out Any>>?

    fun add(instance: Instance<*>)

    fun find(parameter: KParameter): Any?

    fun find(clazz: KClass<*>): Any?

    fun find(simpleName: String?): Any?

    fun clear()
}
