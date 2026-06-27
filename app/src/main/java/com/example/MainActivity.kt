package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.repository.SearchRepository
import com.example.ui.screens.MainSearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SearchViewModel
import com.example.ui.viewmodel.SearchViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Local Room Database and Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = SearchRepository(database.postDao())

        // Obtain SearchViewModel using Simple Constructor Factory Injection
        val viewModel: SearchViewModel by viewModels {
            SearchViewModelFactory(application, repository)
        }

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainSearchScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

