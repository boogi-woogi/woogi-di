package com.boogiwoogi.woogidi.pure

import kotlin.reflect.KClass

/**
 * the class that provide how to instantiate some classes
 */
interface Module {

    fun provideInstanceOf(clazz: KClass<*>): Any?

    fun provideInstanceOf(simpleName: String): Any?
}
