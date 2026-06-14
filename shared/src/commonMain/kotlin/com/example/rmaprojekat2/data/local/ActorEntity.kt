package com.example.rmaprojekat2.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cast_members",
    foreignKeys = [ForeignKey(
        entity = MovieEntry::class,
        parentColumns = ["id"],
        childColumns = ["movieId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("movieId")],
)
data class ActorEntry(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val movieId: String,
    val name: String,
    val character: String,
    val profileUrl: String?,
)