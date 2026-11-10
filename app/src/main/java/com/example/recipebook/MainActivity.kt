package com.example.recipebook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.border
import android.content.Intent
import com.example.recipebook.R
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.saveable.rememberSaveable
import android.net.Uri
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.asImageBitmap
import com.example.recipebook.data.RecipeEntity
import com.example.recipebook.screens.AddRecipeScreen
import com.example.recipebook.data.RecipeDatabase
import com.example.recipebook.data.RecipeRepository
import com.example.recipebook.viewmodel.RecipeViewModel
import com.example.recipebook.viewmodel.RecipeViewModelFactory
import com.example.recipebook.ui.theme.RecipebookTheme

private val Brown = Color(0xFF8B5E3C)
private val Terracotta = Color(0xFFD97745)
private val Orange = Color(0xFFF4A261)
private val Cream = Color(0xFFFFE8D6)
private val Background = Color(0xFFFFF8F2)
private val TextDark = Color(0xFF29211D)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = RecipeDatabase.getDatabase(applicationContext)
        val repository = RecipeRepository(database.recipeDao())
        val factory = RecipeViewModelFactory(repository)

        setContent {
            RecipebookTheme {
                val recipeViewModel: RecipeViewModel = viewModel(
                    factory = factory
                )

                var currentScreen by rememberSaveable {
                    mutableStateOf("home")
                }

                when (currentScreen) {
                    "home" -> {
                        RecipeBookHome(
                            viewModel = recipeViewModel,
                            onAddClick = { currentScreen = "add" },
                            onRecipeClick = { recipe ->
                                val intent = Intent(
                                    this@MainActivity,
                                    DetailActivity::class.java
                                )

                                intent.putExtra("RECIPE_ID", recipe.id)
                                startActivity(intent)
                            }
                        )
                    }

                    "add" -> {
                        AddRecipeScreen(
                            onCancel = {
                                currentScreen = "home"
                            },
                            onSave = { recipe ->
                                recipeViewModel.insertRecipe(recipe)
                                currentScreen = "home"
                            }
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun RecipeBookHome(
    viewModel: RecipeViewModel? = null,
    onAddClick: () -> Unit,
    onRecipeClick: (RecipeEntity) -> Unit
) {

    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }

    val categories = listOf(
        "Semua", "Makanan", "Minuman", "Camilan"
    )

    val recipesFromDatabase = viewModel
        ?.allRecipes
        ?.collectAsState(initial = emptyList())
        ?.value ?: emptyList()

    val filteredRecipes = recipesFromDatabase.filter { recipe ->
        (selectedCategory == "Semua" ||
                recipe.category == selectedCategory) &&
                recipe.title.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Brown,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("+", fontSize = 30.sp)
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Text("⌂", fontSize = 25.sp) },
                    label = { Text("Beranda") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Brown,
                        selectedTextColor = Brown,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color(0xFF8B8178),
                        unselectedTextColor = Color(0xFF6F625A)
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Text("♡", fontSize = 25.sp) },
                    label = { Text("Favorit") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Brown,
                        selectedTextColor = Brown,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color(0xFF8B8178),
                        unselectedTextColor = Color(0xFF6F625A)
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Text("♙", fontSize = 25.sp) },
                    label = { Text("Profil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Brown,
                        selectedTextColor = Brown,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color(0xFF8B8178),
                        unselectedTextColor = Color(0xFF6F625A)
                    )
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.recipebook_logo),
                        contentDescription = "Logo RecipeBook",
                        modifier = Modifier.size(52.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Brown,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            ) {
                                append("Recipe")
                            }

                            withStyle(
                                SpanStyle(
                                    color = Terracotta,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            ) {
                                append("Book")
                            }
                        },
                        fontSize = 26.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "Notifikasi",
                    tint = Brown,
                    modifier = Modifier.size(34.dp)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            // GREETING
            Text(
                text = "Halo, Nai! 👋",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Text(
                text = "Yuk, kelola koleksi resepmu!",
                fontSize = 14.sp,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(20.dp))

            // SEARCH BAR
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = Cream,
                        shape = RoundedCornerShape(30.dp)
                    ),
                placeholder = {
                    Text("Cari resep...", fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari resep",
                        tint = Brown,
                        modifier = Modifier.size(25.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Cream,
                    focusedBorderColor = Terracotta,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark,
                )
            )

            Spacer(modifier = Modifier.height(22.dp))

            // CATEGORIES
            Text(
                text = "Kategori",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Brown
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                category,
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Terracotta,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Brown
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // RECIPE LIST HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Resep",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Text(
                    text = "(${filteredRecipes.size} resep)",
                    fontSize = 12.sp,
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // RECIPE CARDS
            filteredRecipes.forEach { recipe ->
                RecipeCard(
                    name = recipe.title,
                    category = recipe.category,
                    emoji = "🍽️",
                    imageUri = recipe.imageUri,
                    onClick = {
                        onRecipeClick(recipe)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun RecipeCard(
    name: String,
    category: String,
    emoji: String,
    imageUri: String?,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val recipeBitmap = remember(imageUri) {
        imageUri?.let { path ->
            runCatching {
                android.graphics.BitmapFactory
                    .decodeFile(path)
                    ?.asImageBitmap()
            }.getOrNull()
        }
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {

            // TEMPORARY FOOD IMAGE
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        Cream,
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (recipeBitmap != null) {
                    Image(
                        bitmap = recipeBitmap,
                        contentDescription = "Foto $name",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = emoji,
                        fontSize = 38.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(7.dp))

                Surface(
                    color = Cream,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = category,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        ),
                        fontSize = 11.sp,
                        color = Brown
                    )
                }
            }

            Box(
                modifier = Modifier
                    .height(90.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♡",
                    fontSize = 25.sp,
                    color = Terracotta
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeBookHomePreview() {
    RecipebookTheme {
        RecipeBookHome(
            onAddClick = {},
            onRecipeClick = {}
        )
    }
}
