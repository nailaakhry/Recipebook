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

    fun getRecipeById(id: Int, onResult: (RecipeEntity?) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getRecipeById(id))
        }
    }

    fun insertRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.insertRecipe(recipe)
        }
    }

    fun updateRecipe(
        recipe: RecipeEntity,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.updateRecipe(recipe)
            onComplete()
        }
    }

    fun deleteRecipe(
        recipe: RecipeEntity,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
            onComplete()
        }
    }
}
