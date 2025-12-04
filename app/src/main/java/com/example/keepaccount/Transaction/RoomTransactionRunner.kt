package com.example.keepaccount.Transaction

import androidx.room.withTransaction
import com.example.keepaccount.database.KeepAccountRoomDatabase

class RoomTransactionRunner(private val db: KeepAccountRoomDatabase) : DatabaseTransactionRunner {
    override suspend fun <T> invoke(block: suspend () -> T): T {
        return db.withTransaction {
            block()
        }
    }
}
