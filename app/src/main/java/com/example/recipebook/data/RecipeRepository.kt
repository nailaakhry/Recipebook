package com.example.recipebook.data

import kotlinx.coroutines.flow.Flow

class RecipeRepository(
    private val recipeDao: RecipeDao
) {

    val allRecipes: Flow<List<RecipeEntity>> =
        recipeDao.getAllRecipes()

    suspend fun getRecipeById(id: Int): RecipeEntity? {
        return recipeDao.getRecipeById(id)
    }

    suspend fun insertRecipe(recipe: RecipeEntity) {
        recipeDao.insertRecipe(recipe)
    }

    suspend fun updateRecipe(recipe: RecipeEntity) {
        recipeDao.updateRecipe(recipe)
    }

    suspend fun deleteRecipe(recipe: RecipeEntity) {
        recipeDao.deleteRecipe(recipe)
    }
}
