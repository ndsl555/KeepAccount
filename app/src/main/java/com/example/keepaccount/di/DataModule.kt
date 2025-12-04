package com.example.keepaccount.di

import com.example.keepaccount.Repository.BarRepository
import com.example.keepaccount.Repository.BudGetRepository
import com.example.keepaccount.Repository.EventRepository
import com.example.keepaccount.Repository.IBarRepository
import com.example.keepaccount.Repository.IBudGetRepository
import com.example.keepaccount.Repository.IEventRepository
import com.example.keepaccount.Repository.ISpendItemRepository
import com.example.keepaccount.Repository.ITutorialStateRepository
import com.example.keepaccount.Repository.SpendItemRepository
import com.example.keepaccount.Repository.TutorialStateRepository
import org.koin.dsl.module

val dataModule =
    module {
        includes(ioDispatcherModule, databaseModule, moshiModule)
        factory<ISpendItemRepository> { SpendItemRepository(get(koinIO), get()) }
        factory<IBarRepository> { BarRepository(get(koinIO), get()) }
        factory<IBudGetRepository> { BudGetRepository(get(koinIO), get()) }
        factory<IEventRepository> { EventRepository(get(koinIO), get()) }
        factory<ITutorialStateRepository> { TutorialStateRepository(get(koinIO), get()) }
    }
