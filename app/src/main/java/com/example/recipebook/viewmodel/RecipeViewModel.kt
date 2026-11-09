package com.example.recipebook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.data.RecipeEntity
import com.example.recipebook.data.RecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    val allRecipes = repository.allRecipes

    fun insertRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.insertRecipe(recipe)
        }
    }

    fun updateRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.updateRecipe(recipe)
        }
    }

    fun deleteRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }
}
