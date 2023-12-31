package com.boogiwoogi.woogidi.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.boogiwoogi.woogidi.application.DiApplication
import com.boogiwoogi.woogidi.pure.InstanceContainer
import com.boogiwoogi.woogidi.pure.Module

abstract class DiActivity : AppCompatActivity() {

    val instanceContainer: InstanceContainer = ActivityInstanceContainer()
    abstract val module: Module

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupInjector()
    }

    override fun onPause() {
        if (isFinishing) {
            instanceContainer.clear()
        }
        super.onPause()
    }

    private fun setupInjector() {
        DiApplication.injector.inject(
            target = this,
            container = instanceContainer,
            module = module,
        )
    }
}
