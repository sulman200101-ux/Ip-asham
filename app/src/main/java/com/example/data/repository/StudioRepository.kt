package com.example.data.repository

import com.example.data.api.GeminiApiClient
import com.example.data.local.StudioDao
import com.example.data.local.StudioProjectEntity
import com.example.data.model.AnimationStyle
import com.example.data.model.MusicGenre
import com.example.data.model.ProjectType
import com.example.data.model.SongLyricLine
import com.example.data.model.StoryboardScene
import com.example.data.model.VideoAspectRatio
import com.example.data.model.VoiceAvatar
import com.example.data.model.VoiceAvatarCatalog
import com.example.data.model.VoiceEmotion
import kotlinx.coroutines.flow.Flow

class StudioRepository(private val dao: StudioDao) {

    val allProjects: Flow<List<StudioProjectEntity>> = dao.getAllProjects()

    fun getProjectsByType(type: ProjectType): Flow<List<StudioProjectEntity>> =
        dao.getProjectsByType(type)

    suspend fun saveProject(project: StudioProjectEntity): Long =
        dao.insertProject(project)

    suspend fun deleteProject(id: Long) =
        dao.deleteProjectById(id)

    // ==================== GEMINI AI PROMPT GENERATORS ====================

    /**
     * Generate creative Arabic / Multilingual voice script with tone & emotion
     */
    suspend fun generateVoiceScript(topic: String, emotion: VoiceEmotion, avatar: VoiceAvatar): Result<String> {
        val prompt = """
            أنت كاتب نصوص تعليق صوتي احترافي (Voice-Over Scriptwriter).
            الموضوع المطلوب: "$topic"
            نبرة الصوت والشخصية: "${avatar.nameAr}" (${avatar.descriptionAr})
            المشاعر المطلوبة: "${emotion.labelAr}"
            
            اكتب نصاً صوتياً إبداعياً وجذاباً باللغة العربية (أو لغة الموضوع المطلوبة) بطول من 2 إلى 4 فقرات قصيرة.
            اجعل النص مناسباً تماماً للأداء الصوتي المعبر، مع علامات ترقيم واضحة لإبراز الوقفات.
            اكتب النص مباشرة بدون مقدمات أو شروحات إضافية.
        """.trimIndent()

        return GeminiApiClient.generate(prompt)
    }

    /**
     * Generate structured rhyming song lyrics with verse and chorus
     */
    suspend fun generateSongLyrics(theme: String, genre: MusicGenre): Result<String> {
        val prompt = """
            أنت مؤلف وملحن أغانٍ محترف (AI Songwriter & Composer).
            نوع الموسيقى: ${genre.titleAr} (${genre.titleEn}) - الإيقاع: ${genre.bpm} BPM
            موضوع الأغنية: "$theme"
            
            اكتب كلمات أغنية قصيرة متناسقة القافية والوزن (Rhyming Lyrics) مقسمة كالتالي:
            [المقطع الأول - Verse 1]
            (بيتان شعريان بإيقاع متدفق)
            
            [اللازمة - Chorus]
            (لازمة حماسية وقوية وسهلة الترديد)
            
            [المقطع الثاني - Verse 2]
            (بيتان شعريان يكملان الفكرة)
            
            [الخاتمة - Outro]
            (خاتمة نغمية هادئة)
            
            اكتب الكلمات فقط مباشرة ومنسقة وواضحة.
        """.trimIndent()

        return GeminiApiClient.generate(prompt)
    }

    /**
     * Generate animated video storyboard scenes from a prompt
     */
    suspend fun generateVideoStoryboard(storyPrompt: String, style: AnimationStyle): Result<List<StoryboardScene>> {
        val prompt = """
            أنت مخرج رسوم متحركة وكاتب ستوري بورد (Animation Director & Storyboard Artist).
            فكرة القصة أو الفيديو: "$storyPrompt"
            النمط البصري: "${style.titleAr}"
            
            قم بتقسيم الفيديو إلى 4 مشاهد متحركة متسلسلة (Scenes).
            لكل مشهد، اكتب بالتنسيق التالي بدقة:
            SCENE_1: عنوان المشهد الأول | رمز الإيموجي المناسب للشخصية أو العنصر | نص التعليق أو الترجمة البصرية | نوع الحركة البصرية (مثل: تقريب، دوران، ظهور نجمي، طيران)
            SCENE_2: عنوان المشهد الثاني | رمز الإيموجي | نص التعليق | نوع الحركة البصرية
            SCENE_3: عنوان المشهد الثالث | رمز الإيموجي | نص التعليق | نوع الحركة البصرية
            SCENE_4: عنوان المشهد الرابع | رمز الإيموجي | نص التعليق | نوع الحركة البصرية
        """.trimIndent()

        val aiResult = GeminiApiClient.generate(prompt)
        return if (aiResult.isSuccess) {
            val text = aiResult.getOrNull() ?: ""
            val scenes = parseStoryboardFromText(text, storyPrompt)
            Result.success(scenes)
        } else {
            // Provide high-quality fallback scenes if API offline
            Result.success(getFallbackScenes(storyPrompt, style))
        }
    }

    private fun parseStoryboardFromText(text: String, defaultPrompt: String): List<StoryboardScene> {
        val lines = text.lines().filter { it.contains("SCENE_") || it.contains("|") }
        if (lines.isEmpty()) {
            return getFallbackScenes(defaultPrompt, AnimationStyle.NEON_PULSE)
        }

        return lines.mapIndexed { index, line ->
            val parts = line.substringAfter(":").split("|").map { it.trim() }
            val title = parts.getOrNull(0) ?: "المشهد ${index + 1}"
            val emoji = parts.getOrNull(1)?.take(4) ?: getSceneEmoji(index)
            val caption = parts.getOrNull(2) ?: "مشهد متحرك رائع رقم ${index + 1}"
            val action = parts.getOrNull(3) ?: "حركة نيون متوهجة وانتقال ديناميكي"

            StoryboardScene(
                id = "scene_${index + 1}",
                title = title,
                textCaption = caption,
                durationSeconds = 4,
                characterEmoji = emoji,
                visualAction = action,
                backgroundTheme = "THEME_${index + 1}",
                particleType = if (index % 2 == 0) "STARS" else "NEON_BEAMS"
            )
        }
    }

    fun getFallbackScenes(prompt: String, style: AnimationStyle): List<StoryboardScene> {
        return listOf(
            StoryboardScene("s1", "البداية والانطلاق", "في عالم الإبداع والذكاء الاصطناعي... $prompt", 4, "✨", "ظهور نيون تدريجي وتكبير بؤري", "NEON_GRADIENT", "STARS"),
            StoryboardScene("s2", "توليد الأفكار الحية", "تتحول الكلمات إلى نغمات وصور ورسوم متحركة مبهرة", 4, "🎨", "تدفق جسيمات مضيئة وحركة موجية", "SUNSET_FLOW", "NEON_BEAMS"),
            StoryboardScene("s3", "ألحان وتناغم ساحر", "كل إطار ينبض بالموسيقى والحيوية والإلهام", 4, "🎵", "نبضات إيقاعية وتموج ألوان ثلاثي الأبعاد", "COSMIC_DEEP", "PARTICLE_VORTEX"),
            StoryboardScene("s4", "التحفة النهائية", "اكتمل المشهد الإبداعي! استمتع برحلتك الفنية", 4, "🚀", "ألعاب نارية ولمعان متوهج واحتفال", "GOLDEN_AURORA", "FIREWORKS")
        )
    }

    private fun getSceneEmoji(index: Int): String {
        return when (index) {
            0 -> "✨"
            1 -> "⚡"
            2 -> "🎵"
            else -> "🌟"
        }
    }

    // Default sample lyrics generator
    fun getSampleSongLyrics(genre: MusicGenre): Pair<String, List<SongLyricLine>> {
        val text = when (genre) {
            MusicGenre.POP -> "يا ليل النجوم الساطعة\nفي سما الإبداع اللامعة\nنغني بصوت الذكاء وفرحنا\nوالدنيا كلها بتسمعنا!"
            MusicGenre.ARABIC_TARAB -> "يا نسيم الصبح غنّي بالجمال\nواحكي للدنيا عن سحر الخيال\nنغمة تطرب قلبنا وتهدي السلام\nبين ضي النور وأحلى الكلام"
            MusicGenre.LOFI -> "قهوة وموسيقى هادية في ليل طويل\nأفكار بتطير وعالم جميل\nنغمات بسيطة وراحة بال\nأحلى من كل خيال"
            MusicGenre.ELECTRONIC -> "نور النيون بيضوي المكان\nإيقاع سريع في كل آن\nيلا نطير مع النغمات\nونعيش أحلى اللحظات!"
            MusicGenre.KIDS_SONG -> "أنا وأصحابي الأبطال\nنبني ونلعب في الخيال\nشمس وضحكة وأغنيات\nأحلى عالم للبنات والولاد!"
            MusicGenre.ACOUSTIC -> "وتر الجيتار يحكي حكاية\nعن فجر جديد ومعاه البداية\nلحن دافي يسكن القلوب\nويمحي كل المتاعب والهموم"
        }

        val lines = text.lines().filter { it.isNotBlank() }
        val scale = genre.scaleNotes
        val noteLines = lines.mapIndexed { idx, line ->
            SongLyricLine(
                arabicText = line,
                noteFreq = scale[idx % scale.size],
                durationMs = (60_000 / genre.bpm).toLong() * 2,
                chordName = when (idx % 4) {
                    0 -> "C"
                    1 -> "Am"
                    2 -> "F"
                    else -> "G"
                }
            )
        }

        return Pair(text, noteLines)
    }
}
