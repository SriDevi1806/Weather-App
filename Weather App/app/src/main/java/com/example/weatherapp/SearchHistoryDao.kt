package com.example.weatherapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SearchHistoryDao {
    @Insert
    suspend fun insert(searchHistory: SearchHistory)

    @Query("SELECT * FROM search_history_table ORDER BY id DESC")
    suspend fun getRecentSearchHistory(): List<SearchHistory>
}