package com.example.weatherapp

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history_table")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "city")
    val city: String,
    @ColumnInfo(name = "current_date")
    val date: String,
    @ColumnInfo(name = "current_time")
    val time: String,
    @ColumnInfo(name = "temperature")
    val temp: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(city)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(temp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchHistory> {
        override fun createFromParcel(parcel: Parcel): SearchHistory {
            return SearchHistory(parcel)
        }

        override fun newArray(size: Int): Array<SearchHistory?> {
            return arrayOfNulls(size)
        }
    }
}