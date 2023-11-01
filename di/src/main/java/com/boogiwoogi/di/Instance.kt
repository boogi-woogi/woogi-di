package com.boogiwoogi.di

import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

data class Instance<T : Any>(val value: T) {

    val clazz: List<KClass<*>> = value::class.superclasses + value::class

    val isSameAs: (name: String?) -> Boolean
        get() = { name ->
            name?.run { value::class.simpleName == this }
                ?: false
        }
}
