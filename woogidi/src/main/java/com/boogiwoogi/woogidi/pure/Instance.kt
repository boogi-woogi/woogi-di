package com.boogiwoogi.woogidi.pure

import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

data class Instance<T : Any>(val value: T) {

    private val clazz: List<KClass<*>> = value::class.superclasses + value::class

    val isTypeOf: (type: KClass<*>) -> Boolean
        get() = { type ->
            clazz.contains(type)
        }

    val isSameAs: (name: String?) -> Boolean
        get() = { name ->
            name?.run { value::class.simpleName == this }
                ?: false
        }
}
