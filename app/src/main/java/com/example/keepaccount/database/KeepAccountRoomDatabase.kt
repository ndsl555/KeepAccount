package com.example.keepaccount.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.keepaccount.Entity.BarEntity
import com.example.keepaccount.Entity.BudGet
import com.example.keepaccount.Entity.Converters
import com.example.keepaccount.Entity.Event
import com.example.keepaccount.Entity.InvoiceNumber
import com.example.keepaccount.Entity.Item

@Database(
    entities = [
        BarEntity::class,
        Item::class,
        BudGet::class,
        Event::class,
        InvoiceNumber::class,
    ],
    version = 8,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class KeepAccountRoomDatabase : RoomDatabase(), KeepAccountDatabase {
    companion object {
        const val DATABASE_NAME = "KeepAccount.db"
        val MIGRATION_1_2: Migration =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // 創建 Event table
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `Event` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `name` TEXT NOT NULL,
                            `colorcode` TEXT NOT NULL
                        )
                        """.trimIndent(),
                    )
                }
            }
        val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // 1. 建立新表（新版 Item）
                    db.execSQL(
                        """
            CREATE TABLE IF NOT EXISTS ItemTable (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                colorcode TEXT NOT NULL,
                year TEXT NOT NULL,
                month TEXT NOT NULL,
                day TEXT NOT NULL
            )
        """,
                    )

                    // 2. 將舊資料複製過去
                    db.execSQL(
                        """
            INSERT INTO ItemTable (id, name, price, colorcode, year, month, day)
            SELECT id, name, price, colorcode, year, month, day
            FROM Item
        """,
                    )

                    // 3. 刪除舊表
                    db.execSQL("DROP TABLE Item")
                }
            }
        val MIGRATION_3_4 =
            object : Migration(3, 4) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    //    創建 TutorialStatus table

                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS tutorialStatusTable (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            isShown INTEGER NOT NULL
                        )
                        """.trimIndent(),
                    )
                }
            }

        val MIGRATION_4_5 =
            object : Migration(4, 5) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("DROP TABLE IF EXISTS tutorialStatusTable")
                }
            }

        val MIGRATION_5_6: Migration =
            object : Migration(5, 6) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // 1. Create a new temp table with the correct schema (price as INTEGER)
                    db.execSQL(
                        """
                        CREATE TABLE ItemTable_new (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            name TEXT NOT NULL,
                            price INTEGER NOT NULL,
                            colorcode TEXT NOT NULL,
                            year TEXT NOT NULL,
                            month TEXT NOT NULL,
                            day TEXT NOT NULL
                        )
                        """.trimIndent(),
                    )

                    // 2. Copy the data from the old table to the new one, casting the price.
                    db.execSQL(
                        """
                        INSERT INTO ItemTable_new (id, name, price, colorcode, year, month, day)
                        SELECT id, name, CAST(price AS INTEGER), colorcode, year, month, day FROM ItemTable
                        """.trimIndent(),
                    )

                    // 3. Remove the old table
                    db.execSQL("DROP TABLE ItemTable")

                    // 4. Rename the new table to the original name
                    db.execSQL("ALTER TABLE ItemTable_new RENAME TO ItemTable")
                }
            }

        val MIGRATION_6_7 =
            object : Migration(6, 7) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    //    創建 InvoiceTable

                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `InvoiceTable` (
                            `id` INTEGER PRIMARY KEY NOT NULL,
                            `specialistPrize` TEXT NOT NULL,
                            `specialPrize` TEXT NOT NULL,
                            `firstPrize` TEXT NOT NULL
                        )
                        """.trimIndent(),
                    )
                }
            }

        val MIGRATION_7_8 =
            object : Migration(7, 8) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // 新增 topic 欄位
                    db.execSQL("ALTER TABLE `InvoiceTable` ADD COLUMN `topic` TEXT NOT NULL DEFAULT ''")
                }
            }
    }
}
