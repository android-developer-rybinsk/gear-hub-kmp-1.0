package gearhub.feature.products.product_feature.internal.presentation.create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import gear.hub.core.di.koinViewModel
import gearhub.feature.products.product_feature.internal.presentation.create.models.AdCategoryUI
import gearhub.feature.products.product_feature.internal.presentation.create.models.AdsWizardFieldUiModel
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
    val currentStep = state.wizardResult.steps.getOrNull(state.currentWizardStepIndex)
    val isCategoryStep = state.currentWizardStepIndex < 0

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(),
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch {
                images.addAll(loadBitmaps(context, uris))
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview(),
    ) { bitmap ->
        bitmap?.let { images.add(it.asImageBitmap()) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    LaunchedEffect(Unit) { viewModel.onAction(CreateAdAction.LoadCategories) }
    BackHandler(enabled = state.isLoading) {}

    Scaffold(
        topBar = {
            if (!isCategoryStep) {
                CenterAlignedTopAppBar(
                    title = { Text(text = currentStep?.title ?: "Создание объявления") },
                    navigationIcon = {
                        TextButton(onClick = { viewModel.onAction(CreateAdAction.Back) }) { Text("Назад") }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (isCategoryStep) {
                        CategoryStep(state, viewModel::onAction)
                    } else {
                        DynamicStepFields(
                            state = state,
                            fields = currentStep?.children
                                ?.mapNotNull { key -> state.wizardResult.fields.firstOrNull { it.key == key } }
                                .orEmpty(),
                            images = images,
                            onAddPhotoClick = { showSheet = true },
                            onAction = viewModel::onAction,
                        )
                    }
                    state.errorMessage?.let { Text(text = it, color = Color.Red) }
                }

                Button(
                    onClick = { viewModel.onAction(CreateAdAction.NextStep) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                ) {
                    val isLast = state.currentWizardStepIndex == state.wizardResult.steps.lastIndex
                    Text(if (isLast) "Опубликовать" else "Продолжить")
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            TextButton(onClick = {
                showSheet = false
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) { Text("Галерея") }
            TextButton(onClick = {
                showSheet = false
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }) { Text("Камера") }
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DynamicStepFields(
    state: CreateAdState,
    fields: List<AdsWizardFieldUiModel>,
    images: List<ImageBitmap>,
    onAddPhotoClick: () -> Unit,
    onAction: (CreateAdAction) -> Unit,
) {
    fields.forEach { field ->
        val isPhoto = field.widgetType.equals("photos", ignoreCase = true) ||
            field.widgetType.equals("uploader", ignoreCase = true)
        Text(text = field.label.ifBlank { field.key })
        if (isPhoto) {
            PhotosGridField(images = images, onAddPhotoClick = onAddPhotoClick)
            return@forEach
        }

        if (field.values.isNotEmpty()) {
            var expanded by remember(field.key) { mutableStateOf(false) }
            val value = state.fieldInputValues[field.key].orEmpty()
            val isError = field.required && field.key in state.invalidFieldKeys
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    isError = isError,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    placeholder = { Text(field.label) },
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    field.values.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                expanded = false
                                onAction(CreateAdAction.SelectFieldValue(field.key, option.label, option.value))
                            },
                        )
                    }
                }
            }
            if (isError) {
                Text(text = "Обязательное поле", color = Color(0xFFB00020))
            }
        } else {
            val value = state.fieldInputValues[field.key].orEmpty()
            val isError = field.required && field.key in state.invalidFieldKeys
            OutlinedTextField(
                value = value,
                onValueChange = { onAction(CreateAdAction.UpdateFieldInput(field.key, it)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (field.widgetType.equals("number", true)) KeyboardType.Number else KeyboardType.Text,
                ),
                placeholder = { Text(field.label) },
                isError = isError,
            )
            if (isError) {
                Text(text = "Обязательное поле", color = Color(0xFFB00020))
            }
        }
    }
}

@Composable
private fun PhotosGridField(images: List<ImageBitmap>, onAddPhotoClick: () -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.height(180.dp),
    ) {
        items(images) { image ->
            Image(
                bitmap = image,
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
            )
        }
        item {
            Surface(
                modifier = Modifier
                    .size(96.dp)
                    .clickable(onClick = onAddPhotoClick),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFEFEF),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("+", color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(category: AdCategoryUI, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFFE0F0FF) else Color(0xFFF5F5F5),
    ) {
        Text(text = category.title, modifier = Modifier.padding(12.dp))
    }
}

private suspend fun loadBitmaps(context: Context, uris: List<Uri>): List<ImageBitmap> =
    withContext(Dispatchers.IO) {
        uris.mapNotNull { uri -> loadBitmap(context, uri)?.asImageBitmap() }
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
