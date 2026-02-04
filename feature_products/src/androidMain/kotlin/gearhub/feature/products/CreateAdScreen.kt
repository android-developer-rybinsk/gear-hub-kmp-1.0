package gearhub.feature.products

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import gear.hub.core.di.koinViewModel
import gearhub.feature.products.presentation.create.AdCategory
import gearhub.feature.products.presentation.create.CreateAdAction
import gearhub.feature.products.presentation.create.CreateAdState
import gearhub.feature.products.presentation.create.CreateAdStep
import gearhub.feature.products.presentation.create.CreateAdViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CreateAdScreen(
    viewModel: CreateAdViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val images = remember { mutableStateListOf<ImageBitmap>() }
    var showSheet by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch {
                val bitmaps = loadBitmaps(context, uris)
                images.addAll(bitmaps)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { images.add(it.asImageBitmap()) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onAction(CreateAdAction.LoadCategories)
    }

    BackHandler(enabled = state.isLoading) {
        // Блокируем системную кнопку назад во время загрузки.
    }

    Scaffold(
        topBar = {
            if (state.step != CreateAdStep.Category) {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Создание объявления") },
                    navigationIcon = {
                        TextButton(onClick = { viewModel.onAction(CreateAdAction.Back) }) {
                            Text("Назад")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StepContent(
                        state = state,
                        images = images,
                        onAction = viewModel::onAction,
                        onAttachPhotoClick = { showSheet = true },
                    )

                    state.errorMessage?.let {
                        Text(text = it, color = Color.Red)
                    }
                }

                Button(
                    onClick = { viewModel.onAction(CreateAdAction.NextStep) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                ) {
                    Text(if (state.step == CreateAdStep.Price) "Опубликовать" else "Продолжить")
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = {
                    showSheet = false
                    galleryLauncher.launch(
                        ActivityResultContracts.PickVisualMedia.Request.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            .build()
                    )
                }) {
                    Text("Галерея")
                }
                TextButton(onClick = {
                    showSheet = false
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }) {
                    Text("Камера")
                }
            }
        }
    }
}

@Composable
private fun StepContent(
    state: CreateAdState,
    images: List<ImageBitmap>,
    onAction: (CreateAdAction) -> Unit,
    onAttachPhotoClick: () -> Unit,
) {
    when (state.step) {
        CreateAdStep.Category -> CategoryStep(state, onAction)
        CreateAdStep.Title -> TitleStep(state, onAction)
        CreateAdStep.Vin -> VinStep(state, onAction)
        CreateAdStep.Details -> DetailsStep(state, onAction)
        CreateAdStep.Description -> DescriptionStep(state, onAction)
        CreateAdStep.Photos -> PhotosStep(images, onAttachPhotoClick)
        CreateAdStep.Price -> PriceStep(state, onAction)
    }
}

@Composable
private fun CategoryStep(state: CreateAdState, onAction: (CreateAdAction) -> Unit) {
    Text(text = "Категория объявления")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        state.categories.forEach { category ->
            CategoryCard(
                category = category,
                selected = state.selectedCategory?.id == category.id,
                onClick = { onAction(CreateAdAction.SelectCategory(category.id)) },
            )
        }
    }
}

@Composable
private fun CategoryCard(category: AdCategory, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFFE0F0FF) else Color(0xFFF5F5F5),
    ) {
        Text(
            text = category.title,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Composable
private fun TitleStep(state: CreateAdState, onAction: (CreateAdAction) -> Unit) {
    Text(text = "Заголовок объявления")
    if (isVehicleCategory(state.selectedCategory)) {
        OutlinedTextField(
            value = state.brand,
            onValueChange = { onAction(CreateAdAction.UpdateBrand(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Марка") },
        )
        OutlinedTextField(
            value = state.model,
            onValueChange = { onAction(CreateAdAction.UpdateModel(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Модель") },
        )
    } else {
        OutlinedTextField(
            value = state.title,
            onValueChange = { onAction(CreateAdAction.UpdateTitle(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Название объявления") },
        )
    }
}

@Composable
private fun VinStep(state: CreateAdState, onAction: (CreateAdAction) -> Unit) {
    Text(text = "VIN номер")
    OutlinedTextField(
        value = state.vin,
        onValueChange = { onAction(CreateAdAction.UpdateVin(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("VIN") },
    )
}

@Composable
private fun DetailsStep(state: CreateAdState, onAction: (CreateAdAction) -> Unit) {
    Text(text = "Данные объявления")
    OutlinedTextField(
        value = state.location,
        onValueChange = { onAction(CreateAdAction.UpdateLocation(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Город") },
    )
    OutlinedTextField(
        value = state.condition,
        onValueChange = { onAction(CreateAdAction.UpdateCondition(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Состояние") },
    )
}

@Composable
private fun DescriptionStep(state: CreateAdState, onAction: (CreateAdAction) -> Unit) {
    Text(text = "Описание")
    OutlinedTextField(
        value = state.description,
        onValueChange = { onAction(CreateAdAction.UpdateDescription(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Описание") },
    )
}

@Composable
private fun PhotosStep(images: List<ImageBitmap>, onAttachPhotoClick: () -> Unit) {
    Text(text = "Фотографии")
    if (images.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.height(180.dp)
        ) {
            items(images) { image ->
                androidx.compose.foundation.Image(
                    bitmap = image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                )
            }
        }
    } else {
        Text(text = "Фотографии еще не добавлены")
    }
    Spacer(modifier = Modifier.height(8.dp))
    Button(onClick = onAttachPhotoClick, modifier = Modifier.fillMaxWidth()) {
        Text("Прикрепить фото")
    }
}

@Composable
private fun PriceStep(state: CreateAdState, onAction: (CreateAdAction) -> Unit) {
    Text(text = "Цена")
    OutlinedTextField(
        value = state.price,
        onValueChange = { onAction(CreateAdAction.UpdatePrice(it.filter(Char::isDigit))) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Цена") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

private fun isVehicleCategory(category: AdCategory?): Boolean {
    if (category == null) return false
    val slug = category.slug.lowercase()
    val title = category.title.lowercase()
    return slug.contains("auto") || slug.contains("moto") || title.contains("авто") || title.contains("мото")
}

private suspend fun loadBitmaps(context: Context, uris: List<Uri>): List<ImageBitmap> {
    return withContext(Dispatchers.IO) {
        uris.mapNotNull { uri ->
            loadBitmap(context, uri)?.asImageBitmap()
        }
    }
}

private fun loadBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (_: Exception) {
        null
    }
}
