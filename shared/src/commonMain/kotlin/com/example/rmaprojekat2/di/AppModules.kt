package com.example.rmaprojekat2.di

import com.example.rmaprojekat2.data.local.MovieDB
import com.example.rmaprojekat2.data.local.MovieDao
import com.example.rmaprojekat2.data.remote.MovieService
import com.example.rmaprojekat2.data.remote.createMovieService
import com.example.rmaprojekat2.data.repo.NetworkMovieCatalog
import com.example.rmaprojekat2.data.repo.MovieCatalog
import com.example.rmaprojekat2.data.repo.QuizRepository
import com.example.rmaprojekat2.ui.details.DetailViewModel
import com.example.rmaprojekat2.ui.home.HomeViewModel
import com.example.rmaprojekat2.ui.quiz.QuizViewModel
import de.jensklingenberg.ktorfit.Ktorfit
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

private val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(get())
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(message, tag = "HTTP")
                    }
                }
            }
        }
    }

    single<MovieService> {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>())
            .baseUrl("https://rma.finlab.rs/")
            .build()
            .createMovieService()
    }
}

private val repositoryModule = module {
    single<MovieCatalog> {
        NetworkMovieCatalog(
            api = get(),
            db = get()
        )
    }
    single<MovieDao> { get<MovieDB>().movieDao() }
    single<QuizRepository> {
        QuizRepository(movieDao = get())
    }
}

private val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    factory { params ->
        DetailViewModel(
            movieId = params.get(),
            repo = get()
        )
    }
    factory { QuizViewModel(quizRepository = get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration? = null) {
    startKoin {
        appDeclaration?.invoke(this)
        modules(
            networkModule,
            repositoryModule,
            viewModelModule,
            databaseModule()
        )
    }
}

expect fun databaseModule(): org.koin.core.module.Module