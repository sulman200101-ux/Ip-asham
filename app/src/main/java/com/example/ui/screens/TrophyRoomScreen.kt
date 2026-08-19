package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlockThemeSkin
import com.example.data.model.KidBadge
import com.example.data.sound.SoundSynthesizer
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import com.example.data.ads.AdMobManager
import com.example.ui.components.AdBannerView
import com.example.ui.components.KidButton
import com.example.ui.components.StarBadge
import com.example.ui.theme.PlayfulGreen
import com.example.ui.theme.PlayfulPrimary
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.PlayfulSecondary
import com.example.ui.theme.StarGold
import com.example.ui.viewmodel.KidsGameViewModel

@Composable
fun TrophyRoomScreen(
    viewModel: KidsGameViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val availableSkins = viewModel.getAvailableThemeSkins(profile.unlockedThemes)
    val badges = viewModel.getBadges(
        unlockedIds = emptySet(),
        stars = profile.totalStars,
        creations = profile.creationsCount,
        level = profile.highestUnlockedLevel
    )

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("trophy_room_screen"),
        contentPadding = PaddingValues(
            top = topInset + 8.dp,
            bottom = bottomInset + 24.dp,
            start = 16.dp,
            end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP BAR
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    Text(
                        text = "قاعة الجوائز 🏆",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                StarBadge(starsCount = profile.totalStars)
            }
        }

        // THEMES / SKINS SHOP SECTION
        item {
            Text(
                text = "مظاهر المكعبات السحرية 🎨",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(availableSkins) { skin ->
            val isSelected = skin.id == profile.selectedTheme

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(skin.previewColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🧱", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = skin.nameAr,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = skin.descriptionAr,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (skin.isUnlocked) {
                        if (isSelected) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PlayfulGreen.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "مُفعّل",
                                        tint = PlayfulGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "مُفعّل",
                                        color = PlayfulGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        } else {
                            KidButton(
                                text = "اختيار",
                                onClick = { viewModel.selectSkin(skin) },
                                backgroundColor = PlayfulSecondary,
                                elevationDp = 2.dp,
                                modifier = Modifier.height(38.dp)
                            )
                        }
                    } else {
                        KidButton(
                            text = "فتح (${skin.costStars} ⭐)",
                            onClick = { viewModel.unlockSkin(skin) },
                            backgroundColor = PlayfulPurple,
                            enabled = profile.totalStars >= skin.costStars,
                            elevationDp = 2.dp,
                            modifier = Modifier.height(38.dp)
                        )
                    }
                }
            }
        }

        // BADGES SECTION
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "أوسمة وإنجازات البطل 🌟",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(badges) { badge ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                if (badge.isUnlocked) StarGold.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (badge.isUnlocked) badge.emoji else "🔒",
                            fontSize = 28.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = badge.titleAr,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray
                        )
                        Text(
                            text = badge.descriptionAr,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!badge.isUnlocked) {
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { badge.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PlayfulPrimary,
                                trackColor = Color.LightGray.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }

        // REWARDED VIDEO AD TO EARN STARS
        item {
            val context = LocalContext.current
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .clickable {
                        (context as? Activity)?.let { activity ->
                            AdMobManager.showRewardedAd(
                                activity = activity,
                                onUserEarnedReward = { amount ->
                                    viewModel.rewardStarsForAd(amount)
                                }
                            )
                        }
                    },
                color = StarGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(StarGold.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⭐", fontSize = 26.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "احصل على 5 نجوم مجاناً!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "شاهد إعلاناً لفتح المزيد من المظاهر",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    KidButton(
                        text = "مشاهدة 📺",
                        onClick = {
                            (context as? Activity)?.let { activity ->
                                AdMobManager.showRewardedAd(
                                    activity = activity,
                                    onUserEarnedReward = { amount ->
                                        viewModel.rewardStarsForAd(amount)
                                    }
                                )
                            }
                        },
                        backgroundColor = PlayfulSecondary,
                        elevationDp = 2.dp,
                        modifier = Modifier.height(38.dp)
                    )
                }
            }
        }

        // ADMOB BANNER
        item {
            AdBannerView(modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}
