package ru.islab.evilcomments

import androidx.multidex.MultiDexApplication
import ru.islab.evilcomments.di.AppComponent
import ru.islab.evilcomments.di.DaggerAppComponent
import ru.islab.evilcomments.di.module.ContextModule

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
    }

    companion object {
        var appComponent: AppComponent? = null
            private set
    }
}