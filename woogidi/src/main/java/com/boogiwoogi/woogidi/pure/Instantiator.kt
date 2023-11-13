package com.boogiwoogi.woogidi.pure

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

class Instantiator {

    private fun instantiateParameters(module: Module, parameters: List<KParameter>): Array<Any?> =
        parameters.map { instantiate(module, it) }.toTypedArray()

    fun instantiate(module: Module, parameter: KParameter): Any? = when {
        parameter.hasAnnotation<Qualifier>() -> parameter.findAnnotation<Qualifier>()?.run {
            module.provideInstanceOf(simpleName)
        }

        parameter.hasAnnotation<Inject>() -> module.provideInstanceOf(parameter.type.jvmErasure)

        else -> parameter.type.jvmErasure.instantiateRecursively(module)
    }

    private fun <T> KProperty<T>.instantiate(module: Module): Any? = when {
        hasAnnotation<Qualifier>() -> findAnnotation<Qualifier>()?.run {
            module.provideInstanceOf(simpleName)
        }

        hasAnnotation<Inject>() -> module.provideInstanceOf(this.returnType.jvmErasure)
            ?: returnType.jvmErasure.instantiateRecursively(module)

        else -> {}
    }

    private fun KClass<*>.instantiateRecursively(module: Module): Any? {
        primaryConstructor?.let { constructor ->
            if (constructor.parameters.isEmpty()) return constructor.call()

            val arguments = instantiateParameters(module, constructor.parameters)
            if (arguments.any { it == null }) return null
            return constructor.call(*arguments)
        }
        return module.provideInstanceOf(this)
    }

    fun instantiateProperty(module: Module, property: KMutableProperty<*>): Any? {
        return property.instantiate(module)
    }
}
