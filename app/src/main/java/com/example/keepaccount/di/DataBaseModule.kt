package com.example.keepaccount.di

import androidx.room.Room
import com.example.keepaccount.DataSource.BarDataSource
import com.example.keepaccount.DataSource.BudGetDataSource
import com.example.keepaccount.DataSource.EventDataSource
import com.example.keepaccount.DataSource.IBarDataSource
import com.example.keepaccount.DataSource.IBudGetDataSource
import com.example.keepaccount.DataSource.IEventDataSource
import com.example.keepaccount.DataSource.IInvoiceDataSource
import com.example.keepaccount.DataSource.ISpendItemDataSource
import com.example.keepaccount.DataSource.InvoiceDataSource
import com.example.keepaccount.DataSource.SpendItemDataSource
import com.example.keepaccount.Transaction.DatabaseTransactionRunner
import com.example.keepaccount.Transaction.RoomTransactionRunner
import com.example.keepaccount.database.KeepAccountDatabase
import com.example.keepaccount.database.KeepAccountRoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule =
    module {
        single {
            val builder =
                Room.databaseBuilder(
                    androidContext(),
                    KeepAccountRoomDatabase::class.java,
                    KeepAccountRoomDatabase.DATABASE_NAME,
                )
            builder
                .addMigrations(KeepAccountRoomDatabase.MIGRATION_1_2)
                .addMigrations(KeepAccountRoomDatabase.MIGRATION_2_3)
                .addMigrations(KeepAccountRoomDatabase.MIGRATION_3_4)
                .addMigrations(KeepAccountRoomDatabase.MIGRATION_4_5)
                .addMigrations(KeepAccountRoomDatabase.MIGRATION_5_6)
                .addMigrations(KeepAccountRoomDatabase.MIGRATION_6_7)
            builder
                .build()
        }
        single { get<KeepAccountRoomDatabase>() as KeepAccountDatabase }
        single<DatabaseTransactionRunner> { RoomTransactionRunner(get()) }

        // Dao
        factory { get<KeepAccountRoomDatabase>().itemDao() }
        factory { get<KeepAccountRoomDatabase>().barCodeDao() }
        factory { get<KeepAccountRoomDatabase>().budGetDao() }
        factory { get<KeepAccountRoomDatabase>().eventDao() }
        factory { get<KeepAccountRoomDatabase>().invoiceDao() }

        // DataSource

        factory<ISpendItemDataSource> { SpendItemDataSource(get(), get(koinIO)) }
        factory<IBarDataSource> { BarDataSource(get(), get(koinIO)) }
        factory<IBudGetDataSource> { BudGetDataSource(get(), get(koinIO)) }
        factory<IEventDataSource> { EventDataSource(get(), get(koinIO)) }
        factory<IInvoiceDataSource> { InvoiceDataSource(get(), get(koinIO)) }
    }
