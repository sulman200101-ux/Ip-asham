package com.example.data.repository

import com.example.data.model.BlockColor
import com.example.data.model.BlueprintLevel
import com.example.data.model.BlueprintPiece
import com.example.data.model.ShapeType

object BlueprintCatalog {

    val levels = listOf(
        // Level 1: Rocket 🚀
        BlueprintLevel(
            levelId = 1,
            titleAr = "صاروخ الفضاء الصغير",
            titleEn = "Mini Space Rocket",
            category = "مركبات",
            emoji = "🚀",
            descriptionAr = "ركّب الصاروخ واستعد للانطلاق نحو القمر والنجوم!",
            requiredStarsToUnlock = 0,
            gridWidth = 7,
            gridHeight = 8,
            pieces = listOf(
                BlueprintPiece("p1", ShapeType.TRIANGLE, BlockColor.RED, targetX = 3, targetY = 1),
                BlueprintPiece("p2", ShapeType.CUBE, BlockColor.CYAN, targetX = 3, targetY = 2),
                BlueprintPiece("p3", ShapeType.EYES, BlockColor.YELLOW, targetX = 3, targetY = 3),
                BlueprintPiece("p4", ShapeType.CUBE, BlockColor.CYAN, targetX = 3, targetY = 4),
                BlueprintPiece("p5", ShapeType.TRIANGLE, BlockColor.ORANGE, targetX = 2, targetY = 5, rotation = 270),
                BlueprintPiece("p6", ShapeType.TRIANGLE, BlockColor.ORANGE, targetX = 4, targetY = 5, rotation = 90)
            )
        ),

        // Level 2: Happy Cottage 🏠
        BlueprintLevel(
            levelId = 2,
            titleAr = "البيت السعيد الملوّن",
            titleEn = "Happy Cottage",
            category = "مباني",
            emoji = "🏠",
            descriptionAr = "ابنِ بيتاً دافئاً مع سقف أحمر وباب جميل وحديقة!",
            requiredStarsToUnlock = 2,
            gridWidth = 7,
            gridHeight = 8,
            pieces = listOf(
                BlueprintPiece("h1", ShapeType.WIDE_TRIANGLE, BlockColor.RED, targetX = 2, targetY = 2),
                BlueprintPiece("h2", ShapeType.CUBE, BlockColor.YELLOW, targetX = 2, targetY = 3),
                BlueprintPiece("h3", ShapeType.CUBE, BlockColor.YELLOW, targetX = 4, targetY = 3),
                BlueprintPiece("h4", ShapeType.ARCH, BlockColor.BLUE, targetX = 2, targetY = 4),
                BlueprintPiece("h5", ShapeType.FLAG, BlockColor.GREEN, targetX = 4, targetY = 1)
            )
        ),

        // Level 3: Smart Robot Beepo 🤖
        BlueprintLevel(
            levelId = 3,
            titleAr = "الروبوت الذكي بيبو",
            titleEn = "Smart Robot Beep",
            category = "روبوتات",
            emoji = "🤖",
            descriptionAr = "ساعد الروبوت بيبو في تركيب رأسه وذراعيه وعينيه الذكيتين!",
            requiredStarsToUnlock = 5,
            gridWidth = 7,
            gridHeight = 8,
            pieces = listOf(
                BlueprintPiece("r1", ShapeType.STAR_TOP, BlockColor.GOLD, targetX = 3, targetY = 1),
                BlueprintPiece("r2", ShapeType.EYES, BlockColor.CYAN, targetX = 3, targetY = 2),
                BlueprintPiece("r3", ShapeType.RECTANGLE, BlockColor.PURPLE, targetX = 2, targetY = 3),
                BlueprintPiece("r4", ShapeType.TALL_RECTANGLE, BlockColor.BLUE, targetX = 2, targetY = 4),
                BlueprintPiece("r5", ShapeType.TALL_RECTANGLE, BlockColor.BLUE, targetX = 4, targetY = 4),
                BlueprintPiece("r6", ShapeType.WHEEL, BlockColor.YELLOW, targetX = 2, targetY = 6),
                BlueprintPiece("r7", ShapeType.WHEEL, BlockColor.YELLOW, targetX = 4, targetY = 6)
            )
        ),

        // Level 4: Super Race Car 🏎️
        BlueprintLevel(
            levelId = 4,
            titleAr = "سيارة السباق النفاثة",
            titleEn = "Turbo Racecar",
            category = "مركبات",
            emoji = "🏎️",
            descriptionAr = "ركّب هيكل السيارة والعجلات السريعة لتفوز بالسباق!",
            requiredStarsToUnlock = 8,
            gridWidth = 8,
            gridHeight = 7,
            pieces = listOf(
                BlueprintPiece("c1", ShapeType.TRIANGLE, BlockColor.RED, targetX = 2, targetY = 3, rotation = 270),
                BlueprintPiece("c2", ShapeType.RECTANGLE, BlockColor.YELLOW, targetX = 3, targetY = 3),
                BlueprintPiece("c3", ShapeType.FLAG, BlockColor.RED, targetX = 5, targetY = 2),
                BlueprintPiece("c4", ShapeType.RECTANGLE, BlockColor.BLUE, targetX = 2, targetY = 4),
                BlueprintPiece("c5", ShapeType.RECTANGLE, BlockColor.BLUE, targetX = 4, targetY = 4),
                BlueprintPiece("c6", ShapeType.WHEEL, BlockColor.PURPLE, targetX = 2, targetY = 5),
                BlueprintPiece("c7", ShapeType.WHEEL, BlockColor.PURPLE, targetX = 5, targetY = 5)
            )
        ),

        // Level 5: Golden Castle 🏰
        BlueprintLevel(
            levelId = 5,
            titleAr = "قلعة الفرسان الشجاعة",
            titleEn = "Knights Castle",
            category = "مباني",
            emoji = "🏰",
            descriptionAr = "ابنِ قلعة حصينة بأبراج شاهقة وأعلام ترفرف في الهواء!",
            requiredStarsToUnlock = 11,
            gridWidth = 8,
            gridHeight = 8,
            pieces = listOf(
                BlueprintPiece("k1", ShapeType.FLAG, BlockColor.RED, targetX = 1, targetY = 1),
                BlueprintPiece("k2", ShapeType.FLAG, BlockColor.RED, targetX = 6, targetY = 1),
                BlueprintPiece("k3", ShapeType.TRIANGLE, BlockColor.GOLD, targetX = 1, targetY = 2),
                BlueprintPiece("k4", ShapeType.TRIANGLE, BlockColor.GOLD, targetX = 6, targetY = 2),
                BlueprintPiece("k5", ShapeType.TALL_RECTANGLE, BlockColor.PURPLE, targetX = 1, targetY = 3),
                BlueprintPiece("k6", ShapeType.TALL_RECTANGLE, BlockColor.PURPLE, targetX = 6, targetY = 3),
                BlueprintPiece("k7", ShapeType.RECTANGLE, BlockColor.CYAN, targetX = 3, targetY = 3),
                BlueprintPiece("k8", ShapeType.ARCH, BlockColor.WOOD, targetX = 3, targetY = 4)
            )
        ),

        // Level 6: Sky Airplane ✈️
        BlueprintLevel(
            levelId = 6,
            titleAr = "طائرة المغامرات السريعة",
            titleEn = "Sky Explorer",
            category = "مركبات",
            emoji = "✈️",
            descriptionAr = "ركّب أجنحة الطائرة والمروحة لتطير عالياً فوق الغيوم!",
            requiredStarsToUnlock = 14,
            gridWidth = 8,
            gridHeight = 8,
            pieces = listOf(
                BlueprintPiece("a1", ShapeType.PROPELLER, BlockColor.YELLOW, targetX = 1, targetY = 3),
                BlueprintPiece("a2", ShapeType.TRIANGLE, BlockColor.BLUE, targetX = 2, targetY = 3, rotation = 270),
                BlueprintPiece("a3", ShapeType.RECTANGLE, BlockColor.CYAN, targetX = 3, targetY = 3),
                BlueprintPiece("a4", ShapeType.TRIANGLE, BlockColor.RED, targetX = 4, targetY = 2),
                BlueprintPiece("a5", ShapeType.TRIANGLE, BlockColor.RED, targetX = 4, targetY = 4, rotation = 180),
                BlueprintPiece("a6", ShapeType.TALL_RECTANGLE, BlockColor.CYAN, targetX = 5, targetY = 3, rotation = 90),
                BlueprintPiece("a7", ShapeType.TRIANGLE, BlockColor.YELLOW, targetX = 6, targetY = 2)
            )
        ),

        // Level 7: Friendly Dino Rex 🦖
        BlueprintLevel(
            levelId = 7,
            titleAr = "الديناصور الصديق ريكس",
            titleEn = "Friendly Dino Rex",
            category = "حيوانات",
            emoji = "🦖",
            descriptionAr = "اجمع مكعبات الديناصور الأخضر اللطيف الذي يحب اللعب!",
            requiredStarsToUnlock = 17,
            gridWidth = 8,
            gridHeight = 8,
            pieces = listOf(
                BlueprintPiece("d1", ShapeType.EYES, BlockColor.GREEN, targetX = 5, targetY = 1),
                BlueprintPiece("d2", ShapeType.TRIANGLE, BlockColor.GREEN, targetX = 6, targetY = 1, rotation = 90),
                BlueprintPiece("d3", ShapeType.TALL_RECTANGLE, BlockColor.GREEN, targetX = 4, targetY = 2),
                BlueprintPiece("d4", ShapeType.RECTANGLE, BlockColor.GREEN, targetX = 3, targetY = 4),
                BlueprintPiece("d5", ShapeType.TRIANGLE, BlockColor.YELLOW, targetX = 3, targetY = 3),
                BlueprintPiece("d6", ShapeType.TALL_RECTANGLE, BlockColor.GREEN, targetX = 3, targetY = 5),
                BlueprintPiece("d7", ShapeType.TALL_RECTANGLE, BlockColor.GREEN, targetX = 5, targetY = 5),
                BlueprintPiece("d8", ShapeType.TRIANGLE, BlockColor.GREEN, targetX = 1, targetY = 5, rotation = 270)
            )
        ),

        // Level 8: Secret Treehouse 🌳
        BlueprintLevel(
            levelId = 8,
            titleAr = "بيت الشجرة السري",
            titleEn = "Secret Treehouse",
            category = "مباني",
            emoji = "🌳",
            descriptionAr = "ابنِ مخبأك السري فوق أغصان الشجرة الخضراء العالية!",
            requiredStarsToUnlock = 20,
            gridWidth = 8,
            gridHeight = 8,
            pieces = listOf(
                BlueprintPiece("t1", ShapeType.TRIANGLE, BlockColor.RED, targetX = 3, targetY = 1),
                BlueprintPiece("t2", ShapeType.TRIANGLE, BlockColor.GREEN, targetX = 1, targetY = 2),
                BlueprintPiece("t3", ShapeType.CUBE, BlockColor.YELLOW, targetX = 3, targetY = 2),
                BlueprintPiece("t4", ShapeType.TRIANGLE, BlockColor.GREEN, targetX = 5, targetY = 2),
                BlueprintPiece("t5", ShapeType.RECTANGLE, BlockColor.GREEN, targetX = 2, targetY = 3),
                BlueprintPiece("t6", ShapeType.TALL_RECTANGLE, BlockColor.WOOD, targetX = 3, targetY = 4),
                BlueprintPiece("t7", ShapeType.TALL_RECTANGLE, BlockColor.WOOD, targetX = 3, targetY = 6)
            )
        )
    )
}
