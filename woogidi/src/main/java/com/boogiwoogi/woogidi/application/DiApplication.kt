package com.boogiwoogi.woogidi.application

import android.app.Application
import com.boogiwoogi.woogidi.pure.DiInjector

open class DiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        injector = DiInjector(
            applicationContainer = ApplicationInstanceContainer(),
        )
    }

    override fun onTerminate() {
        injector.applicationContainer.clear()

        super.onTerminate()
    }

    companion object {

        lateinit var injector: DiInjector
    }
}
