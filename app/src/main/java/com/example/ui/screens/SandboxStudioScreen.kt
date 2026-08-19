package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlockColor
import com.example.data.model.ShapeType
import com.example.data.sound.SoundSynthesizer
import com.example.ui.components.BlockView
import com.example.ui.components.KidButton
import com.example.ui.theme.PlayfulCyan
import com.example.ui.theme.PlayfulGreen
import com.example.ui.theme.PlayfulOrange
import com.example.ui.theme.PlayfulPrimary
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.PlayfulSecondary
import com.example.ui.theme.PlayfulTertiary
import com.example.ui.viewmodel.KidsGameViewModel
import com.example.ui.viewmodel.SandboxTool

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandboxStudioScreen(
    viewModel: KidsGameViewModel,
    onBack: () -> Unit
) {
    val blocks by viewModel.sandboxBlocks.collectAsState()
    val currentTool by viewModel.selectedTool.collectAsState()
    val currentShape by viewModel.selectedShape.collectAsState()
    val currentColor by viewModel.selectedColor.collectAsState()
    val isAnimationRunning by viewModel.isAnimationRunning.collectAsState()
    val savedCreations by viewModel.savedCreations.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitleInput by remember { mutableStateOf("") }
    var showGallerySheet by remember { mutableStateOf(false) }

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val infiniteTransition = rememberInfiniteTransition(label = "animation")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim_spin"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("sandbox_studio_screen")
    ) {
        // TOP CONTROLS (Back, Title, Clear, Gallery, Save)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = topInset + 6.dp,
                    bottom = 8.dp,
                    start = 14.dp,
                    end = 14.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        SoundSynthesizer.playClick()
                        onBack()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ورشة الإبداع 🎨",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${blocks.size} مكعبات موضوعة",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // GALLERY
                IconButton(
                    onClick = {
                        SoundSynthesizer.playClick()
                        showGallerySheet = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(PlayfulPurple.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = "المعرض",
                        tint = PlayfulPurple
                    )
                }

                // CLEAR
                IconButton(
                    onClick = { viewModel.clearSandbox() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "مسح الكل",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // SAVE
                IconButton(
                    onClick = {
                        SoundSynthesizer.playClick()
                        saveTitleInput = "مجسم ${savedCreations.size + 1}"
                        showSaveDialog = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(PlayfulGreen.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "حفظ",
                        tint = PlayfulGreen
                    )
                }
            }
        }

        // TOOL SELECTOR ROW (Add, Erase, Rotate, Color)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ToolChip(
                label = "إضافة",
                emoji = "➕",
                isSelected = currentTool == SandboxTool.ADD,
                color = PlayfulGreen,
                onClick = { viewModel.setSandboxTool(SandboxTool.ADD) },
                modifier = Modifier.weight(1f)
            )
            ToolChip(
                label = "حذف",
                emoji = "🔨",
                isSelected = currentTool == SandboxTool.REMOVE,
                color = PlayfulPrimary,
                onClick = { viewModel.setSandboxTool(SandboxTool.REMOVE) },
                modifier = Modifier.weight(1f)
            )
            ToolChip(
                label = "تدوير",
                emoji = "🔄",
                isSelected = currentTool == SandboxTool.ROTATE,
                color = PlayfulSecondary,
                onClick = { viewModel.setSandboxTool(SandboxTool.ROTATE) },
                modifier = Modifier.weight(1f)
            )
            ToolChip(
                label = "تلوين",
                emoji = "🎨",
                isSelected = currentTool == SandboxTool.COLOR,
                color = PlayfulOrange,
                onClick = { viewModel.setSandboxTool(SandboxTool.COLOR) },
                modifier = Modifier.weight(1f)
            )
        }

        // 8x8 BUILDING BOARD
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .shadow(6.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                val gridDim = 8
                val cellSize = minOf(maxWidth / gridDim, maxHeight / gridDim)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    for (y in 0 until gridDim) {
                        Row {
                            for (x in 0 until gridDim) {
                                val block = blocks.find { it.gridX == x && it.gridY == y }

                                Box(
                                    modifier = Modifier
                                        .size(cellSize)
                                        .padding(1.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if ((x + y) % 2 == 0) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                                        )
                                        .clickable {
                                            viewModel.handleSandboxGridTap(x, y)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (block != null) {
                                        BlockView(
                                            shapeType = block.shapeType,
                                            blockColor = block.color,
                                            rotation = block.rotation,
                                            sizeDp = cellSize - 2.dp,
                                            animationProgress = if (isAnimationRunning) animProgress else 0f
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // SHAPES & COLORS PALETTE DRAWER
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        top = 10.dp,
                        bottom = bottomInset + 8.dp,
                        start = 12.dp,
                        end = 12.dp
                    )
            ) {
                // COLOR SELECTOR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BlockColor.entries.forEach { col ->
                        val isSelected = col == currentColor
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(col.color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.setSandboxColor(col) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // SHAPE SELECTOR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShapeType.entries.forEach { shape ->
                        val isSelected = shape == currentShape
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) PlayfulSecondary.copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) PlayfulSecondary else Color.Transparent,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.setSandboxShape(shape) }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BlockView(
                                shapeType = shape,
                                blockColor = currentColor,
                                sizeDp = 38.dp,
                                animationProgress = if (isAnimationRunning) animProgress else 0f
                            )
                        }
                    }
                }
            }
        }
    }

    // SAVE DIALOG
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text(
                    text = "حفظ المجسم في المعرض 🎨",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text("أدخل اسماً لمجسمك الجميل:", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = saveTitleInput,
                        onValueChange = { saveTitleInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("مثال: قطاري السريع") }
                    )
                }
            },
            confirmButton = {
                KidButton(
                    text = "حفظ",
                    onClick = {
                        viewModel.saveCurrentCreation(saveTitleInput) {
                            showSaveDialog = false
                        }
                    },
                    backgroundColor = PlayfulGreen
                )
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // GALLERY MODAL SHEET
    if (showGallerySheet) {
        ModalBottomSheet(
            onDismissRequest = { showGallerySheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "معرض مجسماتك المحفوظة 🌟",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (savedCreations.isEmpty()) {
                    Text(
                        text = "لا توجد مجسمات محفوظة بعد. ابنِ شيئاً رائعاً واضغط زر الحفظ 💾!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        items(savedCreations) { item ->
                            Card(
                                modifier = Modifier
                                    .width(160.dp)
                                    .clickable {
                                        viewModel.loadCreationIntoSandbox(item)
                                        showGallerySheet = false
                                    },
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "🏰", fontSize = 32.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${item.blocksCount} مكعبات",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row {
                                        IconButton(
                                            onClick = { viewModel.deleteCreation(item.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "حذف",
                                                tint = Color.Red,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolChip(
    label: String,
    emoji: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) color else color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = if (isSelected) Color.White else color,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
