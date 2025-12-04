/*
 * right (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.keepaccount.Dao

import androidx.room.*
import com.example.keepaccount.Entity.Event

/**
 * Database access object to access the Inventory database
 */
@Dao
interface EventDao {
    // Event-part
    @Query("SELECT * from Event")
    fun getEvents(): List<Event>

    @Query("SELECT * from Event WHERE id = :id")
    fun getEvent(id: Int): Event

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Event into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: Event)

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)
}
