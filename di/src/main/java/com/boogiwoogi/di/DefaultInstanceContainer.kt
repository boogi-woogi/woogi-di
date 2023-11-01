package com.boogiwoogi.di

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.jvmErasure

open class DefaultInstanceContainer(
    instances: List<Instance<out Any>> = listOf()
) : InstanceContainer {

    override var value: MutableList<Instance<out Any>>? = instances.toMutableList()

    override fun add(instance: Instance<*>) {
        value?.add(instance)
    }

    override fun find(clazz: KClass<*>): Any? = value?.find {
        it.clazz.contains(clazz)
    }?.value

    override fun find(simpleName: String?): Any? = value?.find {
        it.isSameAs(simpleName)
    }?.value

    override fun clear() {
        value = null
    }

    override fun find(parameter: KParameter): Any? {
        return when (parameter.hasAnnotation<Qualifier>()) {
            true -> find(parameter.findAnnotation<Qualifier>()?.simpleName)
            false -> find(parameter.type.jvmErasure)
        }
    }
}
