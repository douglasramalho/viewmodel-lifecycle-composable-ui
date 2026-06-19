package com.example.viewmodelcomposablescopeplayground

import android.app.Application
import com.example.viewmodelcomposablescopeplayground.di.diModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ArenaInfoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ArenaInfoApp)
            modules(diModule)
        }
    }
}
