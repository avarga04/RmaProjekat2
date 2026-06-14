package com.example.rmaprojekat2.data.local

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MovieDetails(
    @Embedded val movie: MovieEntry,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MovieCategoryJoin::class,
            parentColumn = "movieId",
            entityColumn = "genreId",
        ),
    )
    val categories: List<CategoryEntry>,
    @Relation(
        parentColumn = "id",
        entityColumn = "movieId",
    )
    val actors: List<ActorEntry>,
    @Relation(
        parentColumn = "id",
        entityColumn = "movieId",
    )
    val pictures: List<ImageEntry>,
)