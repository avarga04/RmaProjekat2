package com.example.rmaprojekat2.data.domain.quiz

import com.example.rmaprojekat2.data.local.ActorEntry
import com.example.rmaprojekat2.data.local.MovieEntry
import kotlin.random.Random
import kotlin.time.Clock

actual class QuizGenerator {
    actual companion object {
        actual val QUESTIONS_PER_SESSION: Int = 10
        actual val TIME_LIMIT_SECONDS: Long = 60L
        actual val MAX_SAME_TYPE_PER_SESSION: Int = 4
    }

    actual fun generateQuestions(
        movies: List<MovieEntry>,
        castMap: Map<String, List<ActorEntry>>
    ): List<QuizQuestion> {
        if (movies.size < QUESTIONS_PER_SESSION) return emptyList()

        val shuffledMovies = movies.shuffled()
        val types = generateQuestionTypes()
        val usedImages = mutableSetOf<String>()
        val questions = mutableListOf<QuizQuestion>()
        var movieIndex = 0
        var attempts = 0
        val maxAttempts = movies.size * 3

        while (questions.size < QUESTIONS_PER_SESSION && attempts < maxAttempts) {
            attempts++
            val movie = shuffledMovies.getOrNull(movieIndex % shuffledMovies.size) ?: break

            val type = types.getOrNull(questions.size) ?: break
            val question = when (type) {
                QuizQuestionType.GUESS_MOVIE -> generateGuessMovieQuestion(movie, shuffledMovies, usedImages)
                QuizQuestionType.GUESS_YEAR -> generateGuessYearQuestion(movie, shuffledMovies)
                QuizQuestionType.GUESS_ACTOR -> generateGuessActorQuestion(movie, shuffledMovies, castMap)
            }

            question?.let {
                usedImages.add(it.imageUrl ?: "")
                questions.add(it)
            }

            movieIndex++
        }

        return questions
    }

    private fun generateQuestionTypes(): List<QuizQuestionType> {
        val types = mutableListOf<QuizQuestionType>()
        val allTypes = QuizQuestionType.values().toList()

        while (types.size < QUESTIONS_PER_SESSION) {
            val shuffled = allTypes.shuffled()
            val available = shuffled.filter { type ->
                types.count { it == type } < MAX_SAME_TYPE_PER_SESSION
            }
            val chosen = if (available.isNotEmpty()) available.first() else allTypes.random()
            types.add(chosen)
        }

        return types
    }

    private fun generateGuessMovieQuestion(
        movie: MovieEntry,
        allMovies: List<MovieEntry>,
        usedImages: Set<String>
    ): QuizQuestion? {
        val imageUrl = movie.posterUrl ?: movie.backdropUrl
        if (imageUrl == null || imageUrl in usedImages) return null

        val wrongOptions = allMovies
            .filter { it.id != movie.id && !usedImages.contains(it.posterUrl ?: it.backdropUrl) }
            .shuffled()
            .take(3)
            .mapNotNull { it.title }

        if (wrongOptions.size < 3) return null

        val options = (listOf(movie.title) + wrongOptions).shuffled()

        return QuizQuestion(
            id = "guess_movie_${movie.id}_${Clock.System.now().toEpochMilliseconds()}",
            type = QuizQuestionType.GUESS_MOVIE,
            movieId = movie.id,
            imageUrl = imageUrl,
            questionText = "Can you guess what movie this is?",
            options = options.mapIndexed { index, text ->
                QuizOption(id = "opt_$index", text = text)
            },
            correctOptionIndex = options.indexOf(movie.title)
        )
    }

    private fun generateGuessActorQuestion(
        movie: MovieEntry,
        allMovies: List<MovieEntry>,
        castMap: Map<String, List<ActorEntry>>
    ): QuizQuestion? {
        val cast = castMap[movie.id] ?: return null
        if (cast.size < 3) return null

        val correctActor = cast.shuffled().first()
        val allActors = castMap.values.flatten().map { it.name }.distinct()

        val wrongActors = allActors
            .filter { it != correctActor.name && !cast.any { a -> a.name == it } }
            .shuffled()
            .take(3)

        if (wrongActors.size < 3) return null

        val options = (listOf(correctActor.name) + wrongActors).shuffled()

        return QuizQuestion(
            id = "guess_actor_${movie.id}_${Clock.System.now().toEpochMilliseconds()}",
            type = QuizQuestionType.GUESS_ACTOR,
            movieId = movie.id,
            imageUrl = movie.posterUrl,
            questionText = "Who was the lead actor in ${movie.title}?",
            options = options.mapIndexed { index, name ->
                QuizOption(id = "opt_$index", text = name)
            },
            correctOptionIndex = options.indexOf(correctActor.name)
        )
    }

    private fun generateGuessYearQuestion(
        movie: MovieEntry,
        allMovies: List<MovieEntry>
    ): QuizQuestion? {
        val correctYear = movie.releaseYear
        if (correctYear <= 0) return null

        val offsets = listOf(-5, -3, 3, 5, -8, 8, -10, 10)
        val wrongYears = offsets
            .map { correctYear + it }
            .filter { it in 1900..2030 && it != correctYear }
            .shuffled()
            .take(3)

        if (wrongYears.size < 3) return null

        val options = (listOf(correctYear) + wrongYears).shuffled()

        return QuizQuestion(
            id = "guess_year_${movie.id}_${Clock.System.now().toEpochMilliseconds()}",
            type = QuizQuestionType.GUESS_YEAR,
            movieId = movie.id,
            imageUrl = movie.posterUrl,
            questionText = "When was ${movie.title} released?",
            options = options.mapIndexed { index, year ->
                QuizOption(id = "opt_$index", text = year.toString())
            },
            correctOptionIndex = options.indexOf(correctYear)
        )
    }
}