package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.sound.SoundSynthesizer
import com.example.ui.components.BlockView
import com.example.ui.components.ConfettiExplosion
import com.example.ui.components.KidButton
import com.example.ui.theme.PlayfulGreen
import com.example.ui.theme.PlayfulOrange
import com.example.ui.theme.PlayfulPrimary
import com.example.ui.theme.PlayfulSecondary
import com.example.ui.theme.StarGold
import com.example.ui.viewmodel.KidsGameViewModel
import kotlin.math.abs

@Composable
fun PhysicsTowerScreen(
    viewModel: KidsGameViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.resetPhysicsTower()
    }

    val state by viewModel.physicsState.collectAsState()
    val animatedAngle by animateFloatAsState(
        targetValue = state.platformAngle,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "tilt_angle"
    )

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("physics_tower_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topInset + 6.dp,
                    bottom = bottomInset + 12.dp,
                    start = 14.dp,
                    end = 14.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP BAR
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
                        text = "برج التوازن الذكي 🗼",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "الارتفاع: ${state.currentHeight} / ${state.targetHeight} طوابق",
                        fontSize = 12.sp,
                        color = PlayfulOrange,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { viewModel.resetPhysicsTower() },
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

            // STABILITY METER
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "مؤشر ميلان المنصة:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val tiltSeverity = abs(state.platformAngle) / 30f
                    val meterColor = if (tiltSeverity > 0.7f) PlayfulPrimary else if (tiltSeverity > 0.4f) PlayfulOrange else PlayfulGreen
                    Text(
                        text = if (tiltSeverity > 0.7f) "⚠️ خطر السقوط!" else if (tiltSeverity > 0.4f) "⚡ مائل قليلاً" else "✅ متوازن ومستقر",
                        color = meterColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // PHYSICS SIMULATION CANVAS (Seesaw Platform + Stacked Blocks)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .shadow(6.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2
                        val cy = size.height * 0.78f

                        // Target Star Goal Line
                        val goalY = size.height * 0.22f
                        drawLine(
                            color = StarGold.copy(alpha = 0.7f),
                            start = Offset(20f, goalY),
                            end = Offset(size.width - 20f, goalY),
                            strokeWidth = 3f,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 10f))
                        )

                        // Fulcrum Triangle (Pivot Base)
                        val fulcrum = Path().apply {
                            moveTo(cx, cy)
                            lineTo(cx - 35f, cy + 60f)
                            lineTo(cx + 35f, cy + 60f)
                            close()
                        }
                        drawPath(fulcrum, color = Color(0xFF7F8C8D))
                        drawPath(fulcrum, color = Color(0xFF2C3E50), style = Stroke(width = 3f))

                        // Ground
                        drawRoundRect(
                            color = Color(0xFF2ED573),
                            topLeft = Offset(0f, cy + 55f),
                            size = Size(size.width, size.height - (cy + 55f))
                        )

                        // Rotating Seesaw Platform
                        rotate(degrees = animatedAngle, pivot = Offset(cx, cy)) {
                            // Plank
                            val plankWidth = size.width * 0.75f
                            drawRoundRect(
                                color = Color(0xFFC89562),
                                topLeft = Offset(cx - plankWidth / 2, cy - 12f),
                                size = Size(plankWidth, 24f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                            )
                            drawRoundRect(
                                color = Color(0xFF5D4037),
                                topLeft = Offset(cx - plankWidth / 2, cy - 12f),
                                size = Size(plankWidth, 24f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                                style = Stroke(width = 3f)
                            )

                            // Draw stacked blocks on the platform
                            val slotStep = plankWidth / 7f
                            val blockH = 34f

                            state.stackedBlocks.forEach { b ->
                                val bx = cx + (b.gridX * slotStep)
                                val by = cy - 12f - (b.gridY * blockH)
                                val bw = slotStep * 0.85f

                                drawRoundRect(
                                    color = b.color.color,
                                    topLeft = Offset(bx - bw / 2, by),
                                    size = Size(bw, blockH - 4f),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                                )
                                drawRoundRect(
                                    color = Color.Black.copy(alpha = 0.2f),
                                    topLeft = Offset(bx - bw / 2, by),
                                    size = Size(bw, blockH - 4f),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                                    style = Stroke(width = 2f)
                                )
                            }
                        }
                    }

                    // Target line indicator text
                    Text(
                        text = "⭐ خط الوصول للقمة",
                        color = StarGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 40.dp, end = 24.dp)
                    )
                }
            }

            // NEXT BLOCK PREVIEW + DROP CONTROLS
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المكعب القادم للإسقاط:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        BlockView(
                            shapeType = state.nextBlockShape,
                            blockColor = state.nextBlockColor,
                            sizeDp = 36.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "اختر عمود الإسقاط لموازنة المنصة ⚖️:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // DROP BUTTONS (-3 to +3)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (slot in -3..3) {
                            KidButton(
                                text = if (slot == 0) "الوسط" else "$slot",
                                onClick = { viewModel.dropBlockOnTower(slot) },
                                backgroundColor = if (slot == 0) PlayfulGreen else if (slot < 0) PlayfulSecondary else PlayfulOrange,
                                modifier = Modifier
                                    .size(42.dp)
                                    .padding(1.dp),
                                elevationDp = 2.dp,
                                testTag = "drop_slot_$slot"
                            )
                        }
                    }
                }
            }
        }

        // CELEBRATION / GAME OVER OVERLAYS
        ConfettiExplosion(isActive = state.isSuccess)

        if (state.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .shadow(16.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "💥", fontSize = 50.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "انقلب البرج!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PlayfulPrimary
                        )
                        Text(
                            text = "حاول وضع المكعبات بتوازن على الطرفين الأيمن والأيسر.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        KidButton(
                            text = "حاول ثانية 🔄",
                            onClick = { viewModel.resetPhysicsTower() },
                            backgroundColor = PlayfulSecondary,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (state.isSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .shadow(16.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🏆", fontSize = 54.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "مُهندس التوازن العبقري! ⭐",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PlayfulGreen
                        )
                        Text(
                            text = "وصلت لقمة البرج وحافظت على التوازن بدقة!",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        KidButton(
                            text = "تحدي جديد 🗼",
                            onClick = { viewModel.resetPhysicsTower() },
                            backgroundColor = PlayfulGreen,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
