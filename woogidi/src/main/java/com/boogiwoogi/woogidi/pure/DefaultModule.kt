package com.boogiwoogi.woogidi.pure

import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.jvmErasure

/**
 * todo: module에 특정 인스턴스를 생성하는 함수(방법)를 추가할 수 있는 방향으로 개선이 필요하다.
 */
open class DefaultModule : Module {

    override fun provideInstanceOf(clazz: KClass<*>): Any? {
        val functions = this::class
            .functions
            .filter { it.hasAnnotation<Provides>() }
            .firstOrNull { it.returnType.jvmErasure == clazz }

        return functions?.call(this)
    }

    override fun provideInstanceOf(simpleName: String): Any? {
        val function = this::class
            .functions
            .filter { it.hasAnnotation<Qualifier>() }
            .firstOrNull { it.findAnnotation<Qualifier>()!!.simpleName == simpleName }

        return function?.call(this)
    }
}
