package com.example.rmaprojekat2.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movie_images",
    foreignKeys = [ForeignKey(
        entity = MovieEntry::class,
        parentColumns = ["id"],
        childColumns = ["movieId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("movieId")],
)
data class ImageEntry(
    @PrimaryKey(autoGenerate = true) val rowId: Int = 0,
    val movieId: String,
    val imageUrl: String,
)