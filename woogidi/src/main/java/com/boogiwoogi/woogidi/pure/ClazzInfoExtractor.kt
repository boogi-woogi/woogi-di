package com.boogiwoogi.woogidi.pure

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation

object ClazzInfoExtractor {

    fun extractInjectMemberProperties(clazz: KClass<*>): List<KMutableProperty<*>> =
        clazz
            .declaredMemberProperties
            .filterIsInstance<KMutableProperty<*>>()
            .filter { it.hasAnnotation<Inject>() }
}
