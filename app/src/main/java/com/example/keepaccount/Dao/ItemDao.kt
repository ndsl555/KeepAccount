package com.example.keepaccount.Dao

import androidx.room.*
import com.example.keepaccount.Entity.Item

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ItemDao {
    // Item-part
    @Query("SELECT * from ItemTable")
    fun getItems(): List<Item>

    @Query("SELECT * from ItemTable WHERE id = :id")
    fun getItem(id: Int): Item

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    // 根據 name 更新 colorcode
    @Query("UPDATE ItemTable SET colorcode = :newColorCode WHERE name = :name")
    suspend fun updateColorCodeByName(
        name: String,
        newColorCode: String,
    )

    // 根據 name 刪除所有符合的資料
    @Query("DELETE FROM ItemTable WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Query(
        """
    SELECT * FROM ItemTable
    WHERE year = :year AND month = :month AND day = :day
    ORDER BY id DESC
""",
    )
    fun getItemsByDate(
        year: String,
        month: String,
        day: String,
    ): List<Item>

    @Query(
        """
    SELECT * FROM ItemTable
    WHERE year = :year AND month = :month
    ORDER BY id DESC
""",
    )
    fun getItemsByMonth(
        year: String,
        month: String,
    ): List<Item>

    @Query(
        "DELETE FROM ItemTable WHERE year = :year AND month = :month AND day = :day AND name = :name",
    )
    suspend fun deleteByDateAndName(
        year: String,
        month: String,
        day: String,
        name: String,
    )

    @Query(
        """
    SELECT day FROM ItemTable
    WHERE year = :year AND month = :month
    GROUP BY day
""",
    )
    suspend fun getUsedDaysInMonth(
        year: String,
        month: String,
    ): List<String>
}
