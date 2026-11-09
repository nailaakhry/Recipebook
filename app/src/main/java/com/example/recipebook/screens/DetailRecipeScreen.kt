package com.example.recipebook.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import com.example.recipebook.data.RecipeEntity

private val Brown = Color(0xFF8B5E3C)
private val Cream = Color(0xFFFFE8D6)
private val Background = Color(0xFFFFF8F2)
private val TextDark = Color(0xFF29211D)
private val Terracotta = Color(0xFFD97745)

@Composable
fun DetailRecipeScreen(
    recipe: RecipeEntity,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val recipeBitmap = remember(recipe.imageUri) {
        recipe.imageUri?.let { path ->
            runCatching {
                android.graphics.BitmapFactory
                    .decodeFile(path)
                    ?.asImageBitmap()
            }.getOrNull()
        }
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = (-14).dp, y = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Brown
                )
            }

            Text(
                text = "Kembali",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Brown
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            if (recipeBitmap != null) {
                Image(
                    bitmap = recipeBitmap,
                    contentDescription = "Foto ${recipe.title}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // TOMBOL EDIT
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    modifier = Modifier.clickable { onEdit() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Resep",
                        tint = Brown,
                        modifier = Modifier.padding(12.dp).size(24.dp)
                    )
                }

                // TOMBOL HAPUS
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    modifier = Modifier.clickable { showDeleteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Resep",
                        tint = Brown,
                        modifier = Modifier.padding(12.dp).size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = recipe.title,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Brown
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = recipe.category,
            color = Brown,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

// BAHAN BAHAN
        Text(
            text = "Bahan-bahan",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Brown
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFFFF1E6),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            recipe.ingredients
                .lines()
                .filter { it.isNotBlank() }
                .forEach { ingredient ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .size(5.dp)
                                .background(
                                    Terracotta,
                                    RoundedCornerShape(50)
                                )
                        )

                        Text(
                            text = ingredient.trim(),
                            fontSize = 16.sp,
                            color = TextDark,
                            lineHeight = 24.sp
                        )
                    }
                }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // LANGKAH MEMASAK
        Text(
            text = "Cara Memasak",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Brown
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            recipe.steps
                .lines()
                .filter { it.isNotBlank() }
                .forEachIndexed { index, step ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    Brown,
                                    RoundedCornerShape(50)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = step.trim(),
                            fontSize = 16.sp,
                            color = TextDark,
                            lineHeight = 24.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

        // BUTTON
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Hapus Resep?")
            },
            text = {
                Text("Apakah kamu yakin ingin menghapus resep ini?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Ya, Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Batal", color = Brown)
                }
            }
        )
    }
}