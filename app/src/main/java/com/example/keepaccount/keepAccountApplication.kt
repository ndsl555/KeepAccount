package com.example.keepaccount

import android.app.Application
import com.example.keepaccount.di.dataModule
import com.example.keepaccount.di.databaseModule
import com.example.keepaccount.di.ioDispatcherModule
import com.example.keepaccount.di.moshiModule
import com.example.keepaccount.di.networkModule
import com.example.keepaccount.di.viewModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class keepAccountApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@keepAccountApplication)
            modules(
                listOf(
                    databaseModule,
                    dataModule,
                    viewModule,
                    ioDispatcherModule,
                    moshiModule,
                    networkModule,
                ),
            )
        }
    }
}
