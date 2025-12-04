package com.example.keepaccount.di

import com.example.keepaccount.UseCase.AddBarCodeUseCase
import com.example.keepaccount.UseCase.AddBudGetUseCase
import com.example.keepaccount.UseCase.AddEventUseCase
import com.example.keepaccount.UseCase.AddItemUseCase
import com.example.keepaccount.UseCase.AddTutorialUseCase
import com.example.keepaccount.UseCase.DeleteEventUseCase
import com.example.keepaccount.UseCase.DeleteItemByDateAndNameUseCase
import com.example.keepaccount.UseCase.DeleteItemByNameUseCase
import com.example.keepaccount.UseCase.DeleteItemUseCase
import com.example.keepaccount.UseCase.GetBudGetUseCase
import com.example.keepaccount.UseCase.GetEventByIdNameUseCase
import com.example.keepaccount.UseCase.GetEventsUseCase
import com.example.keepaccount.UseCase.GetItemByIdUseCase
import com.example.keepaccount.UseCase.GetItemsByDateUseCase
import com.example.keepaccount.UseCase.GetItemsByMonthUseCase
import com.example.keepaccount.UseCase.GetItemsUseCase
import com.example.keepaccount.UseCase.LoadBarCodeUseCase
import com.example.keepaccount.UseCase.LoadTutorialStateUseCase
import com.example.keepaccount.UseCase.UpdateEventColorByEventNameUseCase
import com.example.keepaccount.UseCase.UpdateEventUseCase
import com.example.keepaccount.UseCase.UpdateItemUseCase
import org.koin.dsl.module

val domainModule =
    module {
        includes(ioDispatcherModule, dataModule, moshiModule)
        factory { AddItemUseCase(get(), get(koinIO)) }
        factory { UpdateItemUseCase(get(), get(koinIO)) }
        factory { AddBarCodeUseCase(get(), get(koinIO)) }
        factory { DeleteItemUseCase(get(), get(koinIO)) }
        factory { LoadBarCodeUseCase(get(), get(koinIO)) }
        factory { GetItemByIdUseCase(get(), get(koinIO)) }
        factory { GetItemsUseCase(get(), get(koinIO)) }
        factory { AddBudGetUseCase(get(), get(koinIO)) }
        factory { GetBudGetUseCase(get(), get(koinIO)) }
        factory { AddEventUseCase(get(), get(koinIO)) }
        factory { UpdateEventUseCase(get(), get(koinIO)) }
        factory { DeleteEventUseCase(get(), get(koinIO)) }
        factory { GetEventByIdNameUseCase(get(), get(koinIO)) }
        factory { GetEventsUseCase(get(), get(koinIO)) }
        factory { UpdateEventColorByEventNameUseCase(get(), get(koinIO)) }
        factory { DeleteItemByNameUseCase(get(), get(koinIO)) }
        factory { DeleteItemByDateAndNameUseCase(get(), get(koinIO)) }
        factory { GetItemsByDateUseCase(get(), get(koinIO)) }
        factory { GetItemsByMonthUseCase(get(), get(koinIO)) }
        factory { AddTutorialUseCase(get(), get(koinIO)) }
        factory { LoadTutorialStateUseCase(get(), get(koinIO)) }
    }
