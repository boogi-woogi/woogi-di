package com.boogiwoogi.di.injectionexample

import com.boogiwoogi.di.injectionexample.fragment.DefaultExampleRepository
import com.boogiwoogi.di.injectionexample.fragment.ExampleRepository
import com.boogiwoogi.woogidi.application.DiApplication
import com.boogiwoogi.woogidi.pure.Instance

class WoogiDiApplication : DiApplication() {

    override fun onCreate() {
        super.onCreate()

        with(injector) {
            applicationContainer.add(
                Instance<ExampleRepository>(
                    DefaultExampleRepository()
                )
            )
        }
    }
}
