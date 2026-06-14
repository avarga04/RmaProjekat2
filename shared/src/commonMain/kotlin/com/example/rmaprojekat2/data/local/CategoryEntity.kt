package com.example.rmaprojekat2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class CategoryEntry(
    @PrimaryKey val id: Int,
    val name: String,
)