package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlueprintPiece
import com.example.data.repository.BlueprintCatalog
import com.example.data.sound.SoundSynthesizer
import com.example.ui.components.BlockView
import com.example.ui.components.ConfettiExplosion
import com.example.ui.components.KidButton
import com.example.ui.theme.PlayfulGreen
import com.example.ui.theme.PlayfulPrimary
import com.example.ui.theme.PlayfulSecondary
import com.example.ui.theme.PlayfulTertiary
import com.example.ui.theme.StarGold
import com.example.ui.viewmodel.KidsGameViewModel

@Composable
fun PuzzleGameScreen(
    levelId: Int,
    viewModel: KidsGameViewModel,
    onBack: () -> Unit,
    onNextLevel: (Int) -> Unit
) {
    LaunchedEffect(levelId) {
        viewModel.loadLevel(levelId)
    }

    val state by viewModel.puzzleState.collectAsState()
    val level = state.currentLevel ?: return

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hint_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("puzzle_game_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topInset + 4.dp,
                    bottom = bottomInset + 12.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP CONTROLS (Back, Title, Timer, Hint, Reset)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${level.emoji} ${level.titleAr}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "الوقت: ${state.elapsedSeconds} ثانية | متبقي: ${state.remainingPieces.size}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    IconButton(
                        onClick = { viewModel.showHint() },
                        modifier = Modifier
                            .size(42.dp)
                            .background(PlayfulTertiary.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "مساعدة",
                            tint = PlayfulTertiary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { viewModel.loadLevel(levelId) },
                        modifier = Modifier
                            .size(42.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "إعادة",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // BLUEPRINT CANVAS BOARD
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val gridW = level.gridWidth
                    val gridH = level.gridHeight

                    val cellSize = minOf(
                        maxWidth / gridW,
                        maxHeight / gridH
                    )

                    // DRAW GRID CELLS & PLACED BLOCKS
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        for (y in 0 until gridH) {
                            Row {
                                for (x in 0 until gridW) {
                                    val targetPiece = level.pieces.find { it.targetX == x && it.targetY == y }
                                    val placedBlock = state.placedPieces.values.find { it.gridX == x && it.gridY == y }
                                    val isHinted = targetPiece != null && targetPiece.pieceId == state.hintPieceId

                                    Box(
                                        modifier = Modifier
                                            .size(cellSize)
                                            .padding(1.5.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (targetPiece != null) {
                                                    if (isHinted) PlayfulTertiary.copy(alpha = 0.4f)
                                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                } else {
                                                    Color.Transparent
                                                }
                                            )
                                            .border(
                                                width = if (isHinted) 2.5.dp else 1.dp,
                                                color = if (isHinted) StarGold else if (targetPiece != null) Color.LightGray.copy(alpha = 0.4f) else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                viewModel.placePieceOnGrid(x, y)
                                            }
                                            .scale(if (isHinted) pulseScale else 1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (placedBlock != null) {
                                            BlockView(
                                                shapeType = placedBlock.shapeType,
                                                blockColor = placedBlock.color,
                                                rotation = placedBlock.rotation,
                                                sizeDp = cellSize - 3.dp
                                            )
                                        } else if (targetPiece != null) {
                                            // Ghost silhouette
                                            BlockView(
                                                shapeType = targetPiece.shapeType,
                                                blockColor = targetPiece.color,
                                                rotation = targetPiece.rotation,
                                                sizeDp = cellSize - 3.dp,
                                                isGhost = true,
                                                ghostAlpha = if (isHinted) 0.6f else 0.25f
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PIECES TRAY AT BOTTOM
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "صندوق القطع: اختر القطعة ثم المس مكانها في اللغز 🎯",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        state.remainingPieces.forEach { piece ->
                            val isSelected = state.selectedPiece?.pieceId == piece.pieceId

                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) PlayfulSecondary.copy(alpha = 0.25f)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) PlayfulSecondary else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        viewModel.selectPieceFromTray(piece)
                                    }
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                BlockView(
                                    shapeType = piece.shapeType,
                                    blockColor = piece.color,
                                    rotation = piece.rotation,
                                    sizeDp = 48.dp
                                )
                            }
                        }
                    }
                }
            }
        }

        // CONFETTI OVERLAY
        ConfettiExplosion(isActive = state.showCelebration)

        // VICTORY CELEBRATION DIALOG
        AnimatedVisibility(
            visible = state.isCompleted,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .shadow(16.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎉", fontSize = 54.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "أحسنت يا بطل! 🌟",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "لقد قمت بتركيب ${level.titleAr} بنجاح باهر!",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    // STARS
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (i <= state.starsEarned) StarGold else Color.LightGray,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val nextLevelId = level.levelId + 1
                    val hasNext = BlueprintCatalog.levels.any { it.levelId == nextLevelId }

                    if (hasNext) {
                        KidButton(
                            text = "اللغز التالي 🚀",
                            onClick = { onNextLevel(nextLevelId) },
                            backgroundColor = PlayfulGreen,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    KidButton(
                        text = "قائمة المستويات 📋",
                        onClick = { onBack() },
                        backgroundColor = PlayfulSecondary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
