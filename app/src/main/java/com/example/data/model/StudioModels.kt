package com.example.data.model

import androidx.compose.ui.graphics.Color

// ==================== VOICE AI MODELS ====================

enum class VoiceEmotion(val labelAr: String, val labelEn: String, val pitchModifier: Float, val rateModifier: Float) {
    NEUTRAL("طبيعي", "Neutral", 1.0f, 1.0f),
    ENTHUSIASTIC("حماسي ومشرق", "Enthusiastic", 1.25f, 1.15f),
    DRAMATIC("درامي وسينمائي", "Dramatic", 0.75f, 0.85f),
    STORYTELLER("راوي قصص دافئ", "Storyteller", 0.9f, 0.95f),
    ANIME("أنمي كرتوني", "Anime Hero", 1.45f, 1.2f),
    ROBOTIC("روبوت مستقبلي", "Futuristic Robot", 0.6f, 0.9f),
    CALM("هادئ وتأملي", "Calm & Zen", 0.85f, 0.8f),
    WHISPER("همس غامض", "Whisper", 0.8f, 0.75f)
}

enum class VoiceGender {
    MALE, FEMALE, NEUTRAL_TECH
}

data class VoiceAvatar(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val descriptionAr: String,
    val gender: VoiceGender,
    val basePitch: Float,
    val baseRate: Float,
    val iconEmoji: String,
    val accentColor: Long,
    val isPremium: Boolean = false
)

object VoiceAvatarCatalog {
    val avatars = listOf(
        VoiceAvatar("tariq", "طارق - المعلق الوثائقي", "Tariq - Docu Narrator", "صوت سينمائي عميق وموثوق للوثائقيات والتقارير", VoiceGender.MALE, 0.8f, 0.9f, "🎙️", 0xFF8B5CF6),
        VoiceAvatar("layla", "ليلى - راوية القصص", "Layla - Storyteller", "صوت دافئ ومريح مناسب للقصص والبودكاست", VoiceGender.FEMALE, 1.05f, 0.95f, "✨", 0xFFEC4899),
        VoiceAvatar("zayd", "زيد - مذيع الأخبار", "Zayd - News Anchor", "صوت رسمي واضح وسريع لنشرات الأخبار والإعلانات", VoiceGender.MALE, 1.0f, 1.1f, "📻", 0xFF3B82F6),
        VoiceAvatar("noura", "نورا - المساعدة الذكية", "Noura - AI Assistant", "صوت ذكي نقي وعصري لأنظمة الذكاء الاصطناعي", VoiceGender.FEMALE, 1.15f, 1.0f, "🤖", 0xFF06B6D4),
        VoiceAvatar("kareem", "كريم - الحكيم القديم", "Kareem - Old Sage", "صوت وقور ورصين للاقتباسات والحكم", VoiceGender.MALE, 0.65f, 0.8f, "📜", 0xFFD97706),
        VoiceAvatar("mimi", "ميمي - بطل الكرتون", "Mimi - Cartoon Hero", "صوت مفعم بالحيوية والمرح للأطفال والرسوم المتحركة", VoiceGender.NEUTRAL_TECH, 1.5f, 1.25f, "🦊", 0xFF10B981),
        VoiceAvatar("cyborg", "سايبورغ X-9", "Cyborg X-9", "توليف إلكتروني نيون مستقبلي", VoiceGender.NEUTRAL_TECH, 0.55f, 0.95f, "⚡", 0xFF6366F1, isPremium = true),
        VoiceAvatar("yasmin", "ياسمين - صوت الإعلانات", "Yasmin - Promo Voice", "صوت جذاب مفعم بالبهجة للعروض الترويجية", VoiceGender.FEMALE, 1.1f, 1.15f, "🌟", 0xFFF59E0B, isPremium = true)
    )
}

// ==================== SINGING & MUSIC MODELS ====================

enum class MusicGenre(val titleAr: String, val titleEn: String, val bpm: Int, val emoji: String, val scaleNotes: List<Float>) {
    POP("بوب حيوي", "Upbeat Pop", 120, "🎤", listOf(261.63f, 293.66f, 329.63f, 349.23f, 392.00f, 440.00f, 493.88f, 523.25f)),
    LOFI("لو-فاي هادئ", "Lofi Chill", 85, "☕", listOf(220.00f, 246.94f, 261.63f, 293.66f, 329.63f, 349.23f, 392.00f, 440.00f)),
    ARABIC_TARAB("طرب ومقام شرقي", "Arabic Tarab", 95, "🪕", listOf(220.00f, 247.50f, 261.63f, 293.66f, 329.63f, 348.00f, 392.00f, 440.00f)),
    ELECTRONIC("إلكترودانس EDM", "EDM Cyber", 128, "⚡", listOf(130.81f, 146.83f, 164.81f, 174.61f, 196.00f, 220.00f, 246.94f, 261.63f)),
    ACOUSTIC("جيتار دافئ", "Acoustic Warmth", 90, "🎸", listOf(196.00f, 220.00f, 246.94f, 261.63f, 293.66f, 329.63f, 392.00f, 440.00f)),
    KIDS_SONG("أغنية أطفال ومرح", "Kids Happy Song", 115, "🎈", listOf(261.63f, 293.66f, 329.63f, 392.00f, 440.00f, 523.25f, 587.33f, 659.25f))
}

enum class VocalStyle(val labelAr: String, val labelEn: String, val vibrato: Float, val harmonicBoost: Float) {
    POP_STAR("نجم البوب", "Pop Vocal", 0.03f, 1.2f),
    OPERA("أوبرا ملحمية", "Epic Opera", 0.08f, 1.6f),
    CHILL_FOLK("فلكلور هادئ", "Chill Folk", 0.02f, 0.9f),
    AUTOTUNE_CYBER("أوتوتيون الكتروني", "Cyber Autotune", 0.0f, 1.8f),
    CHOIR("كورال جماعي", "Angelic Choir", 0.05f, 1.4f)
}

data class SongLyricLine(
    val arabicText: String,
    val noteFreq: Float,
    val durationMs: Long,
    val chordName: String = "C"
)

// ==================== ANIMATION & VIDEO MODELS ====================

enum class VideoAspectRatio(val titleAr: String, val titleEn: String, val widthRatio: Float, val heightRatio: Float, val iconEmoji: String) {
    PORTRAIT_9_16("ريلز وشورتس (9:16)", "Reels & Shorts (9:16)", 9f, 16f, "📱"),
    LANDSCAPE_16_9("يوتيوب سينمائي (16:9)", "YouTube (16:9)", 16f, 9f, "🖥️"),
    SQUARE_1_1("مربع إنستغرام (1:1)", "Square (1:1)", 1f, 1f, "🖼️")
}

enum class AnimationStyle(val titleAr: String, val titleEn: String, val primaryColor: Long, val secondaryColor: Long, val emoji: String) {
    NEON_PULSE("نيون سيبراني متوهج", "Neon Cyberpulse", 0xFF8B5CF6, 0xFF06B6D4, "🔮"),
    SUNSET_LOFI("غروب كرتوني دافئ", "Sunset Lofi Dream", 0xFFF43F5E, 0xFFF59E0B, "🌅"),
    COSMIC_SPACE("فضاء كوني ونجوم", "Cosmic Starlight", 0xFF3B82F6, 0xFFEC4899, "🌌"),
    NATURE_VIBE("طبيعة حية ورياح", "Living Nature", 0xFF10B981, 0xFF14B8A6, "🍃"),
    RETRO_DISCO("ريترو ثمانينات", "Retro Synth 80s", 0xFFEC4899, 0xFF8B5CF6, "🪩")
}

data class StoryboardScene(
    val id: String,
    val title: String,
    val textCaption: String,
    val durationSeconds: Int,
    val characterEmoji: String,
    val visualAction: String,
    val backgroundTheme: String,
    val particleType: String = "STARS"
)

// Project Types
enum class ProjectType {
    VOICE_OVER,
    AI_SONG,
    ANIMATED_VIDEO
}
