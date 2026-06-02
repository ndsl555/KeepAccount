package com.example.keepaccount.di

import DayPieViewModel
import MonthPieViewModel
import com.example.keepaccount.ViewModels.AddEventViewModel
import com.example.keepaccount.ViewModels.AddItemViewModel
import com.example.keepaccount.ViewModels.BarcodeViewModel
import com.example.keepaccount.ViewModels.EventDetailViewModel
import com.example.keepaccount.ViewModels.EventListViewModel
import com.example.keepaccount.ViewModels.ItemListViewModel
import com.example.keepaccount.ViewModels.LotteryCheckViewModel
import com.example.keepaccount.ViewModels.StripViewModel
import com.example.keepaccount.ViewModels.VisualSharedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModule: Module =
    module {
        includes(ioDispatcherModule, domainModule, moshiModule)
        viewModel { BarcodeViewModel(get(), get()) }
        viewModel { LotteryCheckViewModel(get(), get(), get()) }
        viewModel { AddItemViewModel(get(), get()) }
        viewModel { ItemListViewModel(get(), get(), get()) }
        viewModel { StripViewModel(get(), get(), get()) }
        viewModel { DayPieViewModel(get()) }
        viewModel { MonthPieViewModel(get()) }
        viewModel { EventDetailViewModel(get(), get(), get(), get()) }
        viewModel { AddEventViewModel(get(), get(), get(), get(), get()) }
        viewModel { EventListViewModel(get()) }
        viewModel { VisualSharedViewModel(get(), get()) }
    }
