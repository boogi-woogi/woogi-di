package com.boogiwoogi.woogidi.pure

import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

class DiInjector(
    val applicationContainer: InstanceContainer,
    module: Module = DefaultModule(),
) {

    var applicationModule: Module = module
    val instantiator = Instantiator()

    inline fun <reified T : Any> inject(
        module: Module? = null,
        container: InstanceContainer? = null
    ): T {
        val primaryConstructor = requireNotNull(T::class.primaryConstructor)
        val arguments = primaryConstructor.parameters.map { parameter ->
            val instanceOfParam = applicationContainer.find(parameter)
                ?: container?.run { find(parameter) }
                ?: instantiator.instantiate(applicationModule, parameter)
                ?: module?.run { instantiator.instantiate(this, parameter) }
                ?: throw IllegalArgumentException("${parameter::class} 타입의 인스턴스를 생성할 수 없습니다.")

            if (parameter.hasAnnotation<Singleton>()) {
                applicationContainer.add(Instance(instanceOfParam))
            }
            instanceOfParam
        }

        return primaryConstructor.call(*arguments.toTypedArray())
    }

    inline fun <reified T : Any> inject(
        target: T,
        module: Module? = null,
        container: InstanceContainer? = null,
    ) {
        ClazzInfoExtractor.extractInjectMemberProperties(target::class).forEach { memberProperty ->
            val instanceOfProperty = applicationContainer.find(memberProperty.returnType.jvmErasure)
                ?: container?.run { find(memberProperty.returnType.jvmErasure) }
                ?: instantiator.instantiateProperty(applicationModule, memberProperty)
                ?: module?.run { instantiator.instantiateProperty(this, memberProperty) }
                ?: throw IllegalArgumentException("${memberProperty.returnType.jvmErasure} 타입의 인스턴스를 생성할 수 없습니다.")

            if (memberProperty.hasAnnotation<Singleton>()) {
                applicationContainer.add(Instance(memberProperty))
            }
            memberProperty.isAccessible = true
            memberProperty.setter.call(target, instanceOfProperty)
        }
    }
}
