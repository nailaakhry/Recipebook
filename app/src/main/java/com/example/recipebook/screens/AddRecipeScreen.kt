package com.example.recipebook.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.imePadding
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.runtime.saveable.rememberSaveable
import android.graphics.BitmapFactory
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.drawBehind
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.clickable
import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.example.recipebook.data.RecipeEntity

private val Brown = Color(0xFF8B5E3C)
private val Terracotta = Color(0xFFD97745)
private val Orange = Color(0xFFF4A261)
private val Cream = Color(0xFFFFE8D6)
private val Background = Color(0xFFFFF8F2)
private val TextDark = Color(0xFF29211D)

@Composable
fun AddRecipeScreen(
    onCancel: () -> Unit,
    onSave: (RecipeEntity) -> Unit
) {
    var recipeName by rememberSaveable {
        mutableStateOf("")
    }
    var category by rememberSaveable {
        mutableStateOf("Makanan")
    }
    var ingredients by rememberSaveable {
        mutableStateOf("")
    }
    var steps by rememberSaveable {
        mutableStateOf("")
    }
    var errorMessage by rememberSaveable {
        mutableStateOf("")
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
    val context = LocalContext.current

    var savedImagePath by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        savedImagePath = null

        if (uri != null) {
            val file = java.io.File(
                context.filesDir,
                "recipe_${java.util.UUID.randomUUID()}.jpg"
            )

            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    java.io.FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }

                if (file.exists() && file.length() > 0) {
                    savedImagePath = file.absolutePath
                } else {
                    file.delete()
                }

            } catch (e: Exception) {
                file.delete()
                savedImagePath = null
            }
        }
    }

    val selectedBitmap = remember(savedImagePath) {
        savedImagePath?.let { path ->
            BitmapFactory.decodeFile(path)?.asImageBitmap()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 20.dp)
    ) {
        // HEADER
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
                text = "Tambah Resep",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = Brown
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // FOTO RESEP
        Text(
            text = "Foto Resep",
            fontWeight = FontWeight.Bold,
            color = Brown
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
                        galleryLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                    .then(
                        Modifier.drawBehind {
                            drawRoundRect(
                                color = Brown.copy(alpha = 0.4f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = androidx.compose.ui.graphics.PathEffect
                                        .dashPathEffect(floatArrayOf(12f, 8f))
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx())
                            )
                        },
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedBitmap != null) {
                    Image(
                        bitmap = selectedBitmap,
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
                            fontSize = 16.sp,
                            color = Brown,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // NAMA RESEP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nama Resep",
                color = Brown,
                fontWeight = FontWeight.Bold
            )

            if (errorMessage == "Nama resep wajib diisi.") {
                Text(
                    text = "Wajib diisi!",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        OutlinedTextField(
            value = recipeName,
            onValueChange = {
                recipeName = it

                if (it.isNotBlank() && errorMessage == "Nama resep wajib diisi.") {
                    errorMessage = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Contoh: Nasi Goreng Spesial") },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        // KATEGORI
        Text("Kategori", color = Brown, fontWeight = FontWeight.Bold)

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

        // BAHAN
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bahan-bahan",
                color = Brown,
                fontWeight = FontWeight.Bold
            )

            if (errorMessage == "Bahan-bahan wajib diisi.") {
                Text(
                    text = "Wajib diisi!",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        OutlinedTextField(
            value = ingredients,
            onValueChange = {
                ingredients = it

                if (it.isNotBlank() && errorMessage == "Bahan-bahan wajib diisi.") {
                    errorMessage = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Tuliskan bahan-bahan resep...")
            },
            minLines = 4,
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(18.dp))

        // LANGKAH MEMASAK
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cara Memasak",
                color = Brown,
                fontWeight = FontWeight.Bold
            )

            if (errorMessage == "Cara memasak wajib diisi.") {
                Text(
                    text = "Wajib diisi!",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        OutlinedTextField(
            value = steps,
            onValueChange = {
                steps = it

                if (it.isNotBlank() && errorMessage == "Cara memasak wajib diisi.") {
                    errorMessage = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Tuliskan cara memasak...")
            },
            minLines = 5,
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors
        )

        Spacer(modifier = Modifier.height(25.dp))

        // TOMBOL BATAL DAN SIMPAN
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
                    when {
                        recipeName.isBlank() -> {
                            errorMessage = "Nama resep wajib diisi."
                        }

                        ingredients.isBlank() -> {
                            errorMessage = "Bahan-bahan wajib diisi."
                        }

                        steps.isBlank() -> {
                            errorMessage = "Cara memasak wajib diisi."
                        }

                        else -> {
                            errorMessage = ""

                            onSave(
                                RecipeEntity(
                                    title = recipeName,
                                    category = category,
                                    ingredients = ingredients,
                                    steps = steps,
                                    imageUri = savedImagePath
                                )
                            )
                        }
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