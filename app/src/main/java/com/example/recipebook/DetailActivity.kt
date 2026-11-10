package com.example.recipebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipebook.data.RecipeDatabase
import com.example.recipebook.data.RecipeRepository
import com.example.recipebook.data.RecipeEntity
import com.example.recipebook.viewmodel.RecipeViewModel
import com.example.recipebook.viewmodel.RecipeViewModelFactory
import com.example.recipebook.screens.DetailRecipeScreen
import com.example.recipebook.ui.theme.RecipebookTheme

class DetailActivity : ComponentActivity() {
    private var refreshRecipe: (() -> Unit)? = null

    override fun onResume() {
        super.onResume()
        refreshRecipe?.invoke()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recipeId = intent.getIntExtra("RECIPE_ID", -1)

        val database = RecipeDatabase.getDatabase(applicationContext)
        val repository = RecipeRepository(database.recipeDao())
        val factory = RecipeViewModelFactory(repository)

        val recipeViewModel = ViewModelProvider(
            this,
            factory
        )[RecipeViewModel::class.java]

        setContent {
            RecipebookTheme {
                var recipe by remember {
                    mutableStateOf<RecipeEntity?>(null)
                }

                var isLoading by remember {
                    mutableStateOf(true)
                }

                LaunchedEffect(recipeId) {
                    refreshRecipe = {
                        isLoading = true

                        recipeViewModel.getRecipeById(recipeId) {
                            recipe = it
                            isLoading = false
                        }
                    }

                    if (recipeId != -1) {
                        refreshRecipe?.invoke()
                    } else {
                        isLoading = false
                    }
                }

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF8B5E3C))
                        }
                    }

                    recipe != null -> {
                        DetailRecipeScreen(
                            recipe = recipe!!,
                            onBack = {
                                finish()
                            },
                            onEdit = {
                                val intent = Intent(
                                    this@DetailActivity,
                                    EditActivity::class.java
                                )

                                intent.putExtra("RECIPE_ID", recipe!!.id)
                                startActivity(intent)
                            },

                            onDelete = {
                                recipeViewModel.deleteRecipe(recipe!!) {
                                    finish()
                                }
                            }
                        )
                    }

                    else -> {
                        finish()
                    }
                }
            }
        }
    }
}
