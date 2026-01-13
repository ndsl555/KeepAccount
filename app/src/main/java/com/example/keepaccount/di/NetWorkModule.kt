package com.example.keepaccount.di

import com.example.keepaccount.DataSource.ILotteryRemoteDataSource
import com.example.keepaccount.DataSource.LotteryDataRemoteSource
import com.example.keepaccount.NetWork.LotteryService
import com.google.gson.GsonBuilder
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

val networkModule =
    module {
        single {
            GsonBuilder()
                .setLenient()
                .create()
        }

        single(named("lotteryRetrofit")) {
            Retrofit.Builder()
                .baseUrl("https://invoice.etax.nat.gov.tw/")
                .addConverterFactory(GsonConverterFactory.create(get()))
                .build()
        }

        factory {
            get<Retrofit>(named("lotteryRetrofit")).create(LotteryService::class.java)
        }

        factory<ILotteryRemoteDataSource> {
            LotteryDataRemoteSource(
                get(),
                get(koinIO),
            )
        }
    }
