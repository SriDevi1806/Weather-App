package com.example.weatherapp

import android.app.Application
import androidx.room.Room

class MyApplication : Application() {
    lateinit var database: AppDatabase
    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "weather_database")
            .addMigrations(AppDatabase.MIGRATION_2_3) // Add your migration here
            .build()
    }
}