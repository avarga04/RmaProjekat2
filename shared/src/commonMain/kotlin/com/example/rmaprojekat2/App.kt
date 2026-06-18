package com.example.rmaprojekat2

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.rmaprojekat2.ui.auth.AuthScreen
import com.example.rmaprojekat2.ui.auth.AuthViewModel
import com.example.rmaprojekat2.ui.details.DetailScreen
import com.example.rmaprojekat2.ui.details.DetailViewModel
import com.example.rmaprojekat2.ui.home.HomeScreen
import com.example.rmaprojekat2.ui.home.HomeViewModel
import com.example.rmaprojekat2.ui.quiz.QuizScreen
import com.example.rmaprojekat2.ui.quiz.QuizViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
internal object AuthNode

@Serializable
internal object HomeNode

@Serializable
internal data class DetailNode(val movieKey: String)

@Serializable
internal object QuizNode

@Composable
fun App() {
    val navHandler = rememberNavController()
    //val authViewModel = koinViewModel<AuthViewModel>()

    MaterialTheme {
        NavHost(navController = navHandler, startDestination = AuthNode) {
            composable<AuthNode> {
                val vm = koinViewModel<AuthViewModel>()
                AuthScreen(
                    state = vm.uiState,
                    onAction = vm::dispatch,
                    effects = vm.effects,
                    onNavigateToHome = {
                        navHandler.navigate(HomeNode) {
                            popUpTo(AuthNode) { inclusive = true }
                        }
                    }
                )
            }

            composable<HomeNode> {
                val vm = koinViewModel<HomeViewModel>()
                HomeScreen(
                    state = vm.uiState,
                    onAction = vm::dispatch,
                    onNavigateToDetail = { movieId ->
                        navHandler.navigate(DetailNode(movieKey = movieId)) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navHandler.graph.findStartDestination().id) { saveState = true }
                        }
                    },
                    onNavigateToQuiz = {
                        navHandler.navigate(QuizNode)
                    },

                    sideEffects = vm.sideEffects,
                    onLogout = {
                        navHandler.navigate(AuthNode) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable<DetailNode> { backStackEntry ->
                val detailNode = backStackEntry.toRoute<DetailNode>()
                val movieId = detailNode.movieKey
                val vm = koinViewModel<DetailViewModel> { parametersOf(movieId) }
                DetailScreen(
                    state = vm.uiState,
                    onAction = vm::dispatch,
                    onGoBack = { navHandler.popBackStack() },
                    sideEffects = vm.sideEffects
                )
            }

            composable<QuizNode> {
                val vm = koinViewModel<QuizViewModel>()
                QuizScreen(
                    state = vm.uiState,
                    onAction = vm::dispatch,
                    effects = vm.effects,
                    onNavigateToResult = { result ->
                        navHandler.popBackStack()
                    },
                    onNavigateBack = { navHandler.popBackStack() }
                )
            }
        }
    }
}