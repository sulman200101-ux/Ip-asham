package com.example.data.model

data class ModelInfo(
    val id: String,
    val name: String,
    val category: ModelCategory,
    val description: String,
    val contextWindow: String,
    val maxOutputTokens: String,
    val trainingCutoff: String,
    val supportsVision: Boolean = true,
    val supportsReasoning: Boolean = false,
    val supportsAudio: Boolean = false,
    val supportsFunctionCalling: Boolean = true
)

enum class ModelCategory(val displayName: String) {
    FLAGSHIP("النماذج الرائدة (GPT-4o)"),
    REASONING("نماذج التفكير (o1 / o3)"),
    FAST("النماذج السريعة (Mini)"),
    IMAGE("توليد الصور (DALL·E)"),
    AUDIO("الصوتيات (Whisper & TTS)"),
    EMBEDDINGS("التضمينات (Embeddings)")
}

object AvailableModels {
    val CHAT_MODELS = listOf(
        ModelInfo(
            id = "gpt-4o",
            name = "GPT-4o",
            category = ModelCategory.FLAGSHIP,
            description = "النموذج الرائد فائق الذكاء للمهام المعقدة متعددة الوسائط عبر النصوص والصور والرؤية.",
            contextWindow = "128,000 توكن",
            maxOutputTokens = "16,384 توكن",
            trainingCutoff = "أكتوبر 2023",
            supportsVision = true
        ),
        ModelInfo(
            id = "gpt-4o-mini",
            name = "GPT-4o mini",
            category = ModelCategory.FAST,
            description = "نموذج سريع واقتصادي وخفيف للمهام اليومية الفورية والبرمجة وحل المشكلات.",
            contextWindow = "128,000 توكن",
            maxOutputTokens = "16,384 توكن",
            trainingCutoff = "أكتوبر 2023",
            supportsVision = true
        ),
        ModelInfo(
            id = "o3-mini",
            name = "o3-mini",
            category = ModelCategory.REASONING,
            description = "أحدث نموذج تفكير فائق السرعة مخصص للعلوم والرياضيات والبرمجة المتقدمة المعقدة.",
            contextWindow = "200,000 توكن",
            maxOutputTokens = "100,000 توكن",
            trainingCutoff = "أكتوبر 2023",
            supportsReasoning = true
        ),
        ModelInfo(
            id = "o1",
            name = "o1",
            category = ModelCategory.REASONING,
            description = "نموذج التفكير المنطقي الكامل المصمم لقضاء وقت أطول في التفكير والتحليل قبل تقديم الإجابة.",
            contextWindow = "200,000 توكن",
            maxOutputTokens = "100,000 توكن",
            trainingCutoff = "أكتوبر 2023",
            supportsReasoning = true
        ),
        ModelInfo(
            id = "o1-mini",
            name = "o1-mini",
            category = ModelCategory.REASONING,
            description = "نسخة سريعة واقتصادية من نماذج التفكير، فعالة جداً في كتابة وتصحيح الأكواد البرمجية.",
            contextWindow = "128,000 توكن",
            maxOutputTokens = "65,536 توكن",
            trainingCutoff = "أكتوبر 2023",
            supportsReasoning = true
        ),
        ModelInfo(
            id = "gpt-4-turbo",
            name = "GPT-4 Turbo",
            category = ModelCategory.FLAGSHIP,
            description = "الجيل السابق عالي الكفاءة مع دعم الرؤية الحاسوبية واستدعاء الدوال والأدوات البرمجية.",
            contextWindow = "128,000 توكن",
            maxOutputTokens = "4,096 توكن",
            trainingCutoff = "ديسمبر 2023"
        ),
        ModelInfo(
            id = "gpt-3.5-turbo",
            name = "GPT-3.5 Turbo",
            category = ModelCategory.FAST,
            description = "النموذج الكلاسيكي السريع للمحادثات البسيطة والترجمة والمهام السريعة.",
            contextWindow = "16,385 توكن",
            maxOutputTokens = "4,096 توكن",
            trainingCutoff = "سبتمبر 2021"
        )
    )

    val IMAGE_MODELS = listOf(
        ModelInfo(
            id = "dall-e-3",
            name = "DALL-E 3",
            category = ModelCategory.IMAGE,
            description = "أحدث نموذج توليد صور مع التزام استثنائي بتفاصيل النص ودقة بصرية فائقة.",
            contextWindow = "غير متاح",
            maxOutputTokens = "1024x1024 / 1024x1792",
            trainingCutoff = "غير متاح"
        ),
        ModelInfo(
            id = "dall-e-2",
            name = "DALL-E 2",
            category = ModelCategory.IMAGE,
            description = "الجيل السابق لإنشاء وتعديل الصور بدقة واقعية وتوضيحية متوازنة.",
            contextWindow = "غير متاح",
            maxOutputTokens = "512x512 / 1024x1024",
            trainingCutoff = "غير متاح"
        )
    )

    val AUDIO_MODELS = listOf(
        ModelInfo(
            id = "tts-1",
            name = "TTS-1",
            category = ModelCategory.AUDIO,
            description = "نموذج تحويل النص إلى صوت قياسي ومحسن للبث المباشر الفوري بزمن استجابة منخفض.",
            contextWindow = "4,096 حرف",
            maxOutputTokens = "ملف صوتي دافق",
            trainingCutoff = "غير متاح",
            supportsAudio = true
        ),
        ModelInfo(
            id = "tts-1-hd",
            name = "TTS-1 HD",
            category = ModelCategory.AUDIO,
            description = "نموذج صوتي عالي الدقة والوضوح لإنتاج نبرة صوتية نقية واحترافية.",
            contextWindow = "4,096 حرف",
            maxOutputTokens = "ملف صوتي دافق",
            trainingCutoff = "غير متاح",
            supportsAudio = true
        ),
        ModelInfo(
            id = "whisper-1",
            name = "Whisper-1",
            category = ModelCategory.AUDIO,
            description = "نموذج التعرف على الكلام والنسخ الصوتي متعدد اللغات بدقة استثنائية تشمل اللغة العربية.",
            contextWindow = "25 ميجابايت صوت",
            maxOutputTokens = "نص مفرّغ",
            trainingCutoff = "غير متاح",
            supportsAudio = true
        )
    )

    val ALL_MODELS = CHAT_MODELS + IMAGE_MODELS + AUDIO_MODELS
}

data class SystemPromptPreset(
    val title: String,
    val iconName: String,
    val prompt: String,
    val defaultSampleUserMessage: String
)

object PromptPresets {
    val PRESETS = listOf(
        SystemPromptPreset(
            title = "خبير بايثون و OpenAI SDK",
            iconName = "code",
            prompt = "أنت مهندس برمجيات محترف وخبير في مكتبة openai-python الرسمية. قدّم دائماً حلولاً وأكواداً حديثة بلغة Python 3.12+ متوافقة تماماً مع أحدث إصدارات OpenAI SDK، مع دعم الأنواع والتعامل مع الأخطاء.",
            defaultSampleUserMessage = "كيف يمكنني استخدام البث الحي (Streaming) في إكمال المحادثات واستقبال ردود نموذج o3-mini في بايثون؟"
        ),
        SystemPromptPreset(
            title = "مطور ومراجع أكواد خبير",
            iconName = "build",
            prompt = "أنت مهندس معماري برمجيات أول. قم بتحليل الأكواد المعطاة من حيث الأداء، والأمان، ونظافة الكود (Clean Code)، والتعقيد الحسابي. اشرح دائماً أسباب التعديلات بوضوح.",
            defaultSampleUserMessage = "قم بمراجعة هذه الدالة البرمجية وإعادة كتابتها لتكون آمنة للخيوط (Thread-safe) ومثالية في استهلاك الذاكرة."
        ),
        SystemPromptPreset(
            title = "كاتب إبداعي وصانع محتوى",
            iconName = "edit",
            prompt = "أنت كاتب ومؤلف إبداعي متمكن. اكتب نصوصاً بليغة باللغة العربية الفصحى غنية بالصور البيانية، والتفاصيل المشوقة، والأسلوب الأدبي الجذاب.",
            defaultSampleUserMessage = "اكتب مشهداً افتتاحياً مشوقاً عن مسبار ذكاء اصطناعي مستقل يصل إلى مدار غامض حول كوكب نبتون لأول مرة."
        ),
        SystemPromptPreset(
            title = "مستخرج بيانات و JSON دقيق",
            iconName = "schema",
            prompt = "أنت مساعد استخراج بيانات عالي الدقة. أجب دائماً بصيغة JSON صحيحة وصالحة فقط دون أي مقدمات محادثة أو نصوص إضافية خارج كائن الـ JSON.",
            defaultSampleUserMessage = "استخرج الكيانات الرئيسية (الشركة، الإيرادات، نسبة النمو، الربع المالي): حققت شركة أرامكو إيرادات بلغت 4.2 مليار دولار بنمو 18%."
        ),
        SystemPromptPreset(
            title = "مترجم لغوي محترف",
            iconName = "translate",
            prompt = "أنت مترجم لغوي محترف وأكاديمي. قدّم ترجمات عربية طبيعية ودقيقة مع مراعاة السياق الثقافي والاصطلاحي والمصطلحات التقنية الحديثة.",
            defaultSampleUserMessage = "ترجم المثل 'The proof of the pudding is in the eating' إلى العربية مع شرح سياقه وبدائله البلاغية."
        )
    )
}
