package com.boogiwoogi.woogidi.activity

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.boogiwoogi.woogidi.pure.Instance
import com.boogiwoogi.woogidi.pure.InstanceContainer
import com.boogiwoogi.woogidi.pure.Qualifier
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.jvmErasure

class ActivityInstanceContainer(
    value: List<Instance<out Any>>? = listOf()
) : InstanceContainer, DefaultLifecycleObserver {

    override var value: MutableList<Instance<out Any>>? = value?.toMutableList()

    override fun add(instance: Instance<*>) {
        value?.add(instance)
    }

    override fun find(clazz: KClass<*>): Any? = value?.find {
        it.clazz == clazz
    }?.value

    override fun find(simpleName: String?): Any? = value?.find {
        it.isSameAs(simpleName)
    }?.value

    override fun clear() {
        value = null
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        value = mutableListOf()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        clear()

        super.onDestroy(owner)
    }

    override fun find(parameter: KParameter): Any? {
        return when (parameter.hasAnnotation<Qualifier>()) {
            true -> find(parameter.findAnnotation<Qualifier>()?.simpleName)
            false -> find(parameter.type.jvmErasure)
        }
    }
}
