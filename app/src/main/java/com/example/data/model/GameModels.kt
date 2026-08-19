package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class ShapeType(
    val titleAr: String,
    val titleEn: String,
    val iconName: String,
    val baseWidth: Int = 1,
    val baseHeight: Int = 1
) {
    CUBE("مكعب", "Cube", "cube", 1, 1),
    RECTANGLE("مستطيل عريض", "Wide Block", "rect", 2, 1),
    TALL_RECTANGLE("عمود طويل", "Tall Column", "tall", 1, 2),
    TRIANGLE("سقف مثلث", "Roof Triangle", "triangle", 1, 1),
    WIDE_TRIANGLE("سقف عريض", "Wide Roof", "triangle_wide", 2, 1),
    CYLINDER("أسطوانة", "Cylinder", "cylinder", 1, 1),
    ARCH("قوس مدخل", "Archway", "arch", 2, 1),
    WHEEL("عجلة دائرية", "Wheel", "wheel", 1, 1),
    EYES("عيون كرتونية", "Cute Eyes", "eyes", 1, 1),
    PROPELLER("مروحة طائرة", "Propeller", "propeller", 1, 1),
    FLAG("راية وقلعة", "Flag", "flag", 1, 1),
    STAR_TOP("نجمة مضيئة", "Star Top", "star", 1, 1)
}

enum class BlockColor(
    val id: String,
    val nameAr: String,
    val color: Color,
    val highlightColor: Color,
    val shadowColor: Color
) {
    RED("red", "أحمر ياقوتي", Color(0xFFFF4757), Color(0xFFFF6B81), Color(0xFFC0392B)),
    BLUE("blue", "أزرق سماوي", Color(0xFF2E86DE), Color(0xFF54A0FF), Color(0xFF1E5BB0)),
    YELLOW("yellow", "أصفر شمسي", Color(0xFFFFA502), Color(0xFFFFD32A), Color(0xFFE58E26)),
    GREEN("green", "أخضر زمردي", Color(0xFF2ED573), Color(0xFF7BED9F), Color(0xFF26AF61)),
    PURPLE("purple", "بنفسجي سحري", Color(0xFF9B59B6), Color(0xFFAF7AC5), Color(0xFF7D3C98)),
    ORANGE("orange", "برتقالي مبهج", Color(0xFFFF793F), Color(0xFFFFB142), Color(0xFFD35400)),
    CYAN("cyan", "تركواز بلوري", Color(0xFF00D2D3), Color(0xFF48DBFB), Color(0xFF01A3A4)),
    PINK("pink", "وردي حلوى", Color(0xFFFF69B4), Color(0xFFFF9FF3), Color(0xFFDB0A5B)),
    GOLD("gold", "ذهبي لامع", Color(0xFFF1C40F), Color(0xFFF9E79F), Color(0xFFD4AC0D)),
    WOOD("wood", "خشب طبيعي", Color(0xFFC89562), Color(0xFFE2B785), Color(0xFF9E6B38))
}

data class PlacedBlock(
    val id: String,
    val shapeType: ShapeType,
    val color: BlockColor,
    val gridX: Int,
    val gridY: Int,
    val rotation: Int = 0 // 0, 90, 180, 270
)

data class BlueprintPiece(
    val pieceId: String,
    val shapeType: ShapeType,
    val color: BlockColor,
    val targetX: Int,
    val targetY: Int,
    val rotation: Int = 0
)

data class BlueprintLevel(
    val levelId: Int,
    val titleAr: String,
    val titleEn: String,
    val category: String,
    val emoji: String,
    val descriptionAr: String,
    val pieces: List<BlueprintPiece>,
    val gridWidth: Int = 8,
    val gridHeight: Int = 8,
    val requiredStarsToUnlock: Int = 0
)

data class KidBadge(
    val id: String,
    val titleAr: String,
    val descriptionAr: String,
    val emoji: String,
    val isUnlocked: Boolean = false,
    val progress: Float = 0f
)

data class BlockThemeSkin(
    val id: String,
    val nameAr: String,
    val costStars: Int,
    val isUnlocked: Boolean,
    val descriptionAr: String,
    val previewColor: Color
)
