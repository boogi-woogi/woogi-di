package com.boogiwoogi.woogidi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.boogiwoogi.woogidi.activity.ActivityInstanceContainer
import com.boogiwoogi.woogidi.application.DiApplication
import com.boogiwoogi.woogidi.pure.InstanceContainer
import com.boogiwoogi.woogidi.pure.Module

abstract class DiFragment : Fragment() {

    val instanceContainer: InstanceContainer = ActivityInstanceContainer()
    abstract val module: Module

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupInjector()
    }

    /**
     * for preventing memory leak
     */
    override fun onDestroyView() {
        instanceContainer.clear()

        super.onDestroyView()
    }

    private fun setupInjector() {
        DiApplication.injector.inject(
            target = this,
            container = instanceContainer,
            module = module,
        )
    }
}
