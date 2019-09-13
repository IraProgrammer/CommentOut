package ru.trmedia.trbtlservice.comment

import android.app.Application
import ru.trmedia.trbtlservice.comment.di.AppComponent
import ru.trmedia.trbtlservice.comment.di.DaggerAppComponent
import ru.trmedia.trbtlservice.comment.di.module.ContextModule

class App : Application() {

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