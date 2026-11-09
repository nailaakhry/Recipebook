package com.example.recipebook.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.ui.text.style.TextAlign
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import com.example.recipebook.data.RecipeEntity

private val Brown = Color(0xFF8B5E3C)
private val Terracotta = Color(0xFFD97745)
private val Orange = Color(0xFFF4A261)
private val Cream = Color(0xFFFFE8D6)
private val Background = Color(0xFFFFF8F2)
private val TextDark = Color(0xFF29211D)

@Composable
fun EditRecipeScreen(
    recipe: RecipeEntity,
    onCancel: () -> Unit,
    onSave: (RecipeEntity) -> Unit
) {
    var recipeName by remember {
        mutableStateOf(recipe.title)
    }

    var category by remember {
        mutableStateOf(recipe.category)
    }

    var ingredients by remember {
        mutableStateOf(recipe.ingredients)
    }

    var steps by remember {
        mutableStateOf(recipe.steps)
    }

    val context = LocalContext.current

    var imagePath by remember {
        mutableStateOf(recipe.imageUri)
    }

    val recipeBitmap = remember(imagePath) {
        imagePath?.let { path ->
            runCatching {
                BitmapFactory.decodeFile(path)?.asImageBitmap()
            }.getOrNull()
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            runCatching {
                val file = File(
                    context.filesDir,
                    "recipe_${UUID.randomUUID()}.jpg"
                )

                context.contentResolver.openInputStream(it)?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }

                imagePath = file.absolutePath
            }
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextDark,
        unfocusedTextColor = TextDark,
        cursorColor = Terracotta,
        focusedPlaceholderColor = Color(0xFF9A918B),
        unfocusedPlaceholderColor = Color(0xFF9A918B),
        focusedBorderColor = Terracotta,
        unfocusedBorderColor = Color(0xFFD8C7BA),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = (-14).dp, y = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancel
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Brown
                )
            }

            Text(
                text = "Edit Resep",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Brown
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Foto Resep",
            color = Brown,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        Cream,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        imagePicker.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (recipeBitmap != null) {
                    Image(
                        bitmap = recipeBitmap!!,
                        contentDescription = "Foto Resep",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Tambah Foto",
                            tint = Brown,
                            modifier = Modifier.size(55.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tambah Foto\nResep",
                            color = Brown,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Nama Resep",
            color = Brown,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = recipeName,
            onValueChange = { recipeName = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Nama resep")
            },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Kategori",
            color = Brown,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Makanan", "Minuman", "Camilan").forEach {
                FilterChip(
                    selected = category == it,
                    onClick = { category = it },
                    label = { Text(it) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Terracotta,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Bahan-bahan",
            color = Brown,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = ingredients,
            onValueChange = { ingredients = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Tuliskan bahan-bahan resep...")
            },
            minLines = 4,
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Cara Memasak",
            color = Brown,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = steps,
            onValueChange = { steps = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Tuliskan cara memasak...")
            },
            minLines = 5,
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors
        )
        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Batal", color = Brown)
            }

            Button(
                onClick = {
                    if (
                        recipeName.isNotBlank() &&
                        ingredients.isNotBlank() &&
                        steps.isNotBlank()
                    ) {
                        onSave(
                            recipe.copy(
                                title = recipeName,
                                category = category,
                                ingredients = ingredients,
                                steps = steps,
                                imageUri = imagePath
                            )
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Brown
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan", color = Color.White)
            }
        }
    }

}