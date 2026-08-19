package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.KidsDatabase
import com.example.data.local.PlayerProfileEntity
import com.example.data.local.SavedCreationEntity
import com.example.data.model.BlockColor
import com.example.data.model.BlockThemeSkin
import com.example.data.model.BlueprintLevel
import com.example.data.model.BlueprintPiece
import com.example.data.model.KidBadge
import com.example.data.model.PlacedBlock
import com.example.data.model.ShapeType
import com.example.data.repository.BlueprintCatalog
import com.example.data.repository.KidsGameRepository
import com.example.data.sound.SoundSynthesizer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.abs

enum class SandboxTool {
    ADD,
    REMOVE,
    ROTATE,
    COLOR
}

data class PuzzleGameState(
    val currentLevel: BlueprintLevel? = null,
    val placedPieces: Map<String, PlacedBlock> = emptyMap(), // slotId -> PlacedBlock
    val remainingPieces: List<BlueprintPiece> = emptyList(),
    val selectedPiece: BlueprintPiece? = null,
    val isCompleted: Boolean = false,
    val starsEarned: Int = 0,
    val elapsedSeconds: Int = 0,
    val showCelebration: Boolean = false,
    val hintPieceId: String? = null
)

data class PhysicsTowerState(
    val platformAngle: Float = 0f, // degrees (-30 to +30)
    val stackedBlocks: List<PlacedBlock> = emptyList(),
    val currentHeight: Int = 0,
    val isGameOver: Boolean = false,
    val isSuccess: Boolean = false,
    val targetHeight: Int = 8,
    val nextBlockColor: BlockColor = BlockColor.RED,
    val nextBlockShape: ShapeType = ShapeType.CUBE
)

data class MemoryCard(
    val id: Int,
    val shapeType: ShapeType,
    val color: BlockColor,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false
)

data class MemoryGameState(
    val cards: List<MemoryCard> = emptyList(),
    val flippedCardIndices: List<Int> = emptyList(),
    val matchesCount: Int = 0,
    val totalPairs: Int = 6,
    val isCompleted: Boolean = false,
    val movesCount: Int = 0
)

class KidsGameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = KidsDatabase.getDatabase(application)
    private val repository = KidsGameRepository(db.kidsDao())

    val profile: StateFlow<PlayerProfileEntity> = repository.profileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerProfileEntity())

    val savedCreations: StateFlow<List<SavedCreationEntity>> = repository.savedCreationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val levelRecords = repository.levelRecordsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // PUZZLE MODE STATE
    private val _puzzleState = MutableStateFlow(PuzzleGameState())
    val puzzleState: StateFlow<PuzzleGameState> = _puzzleState.asStateFlow()
    private var puzzleTimerJob: Job? = null

    // SANDBOX MODE STATE
    private val _sandboxBlocks = MutableStateFlow<List<PlacedBlock>>(emptyList())
    val sandboxBlocks: StateFlow<List<PlacedBlock>> = _sandboxBlocks.asStateFlow()

    private val _selectedTool = MutableStateFlow(SandboxTool.ADD)
    val selectedTool: StateFlow<SandboxTool> = _selectedTool.asStateFlow()

    private val _selectedShape = MutableStateFlow(ShapeType.CUBE)
    val selectedShape: StateFlow<ShapeType> = _selectedShape.asStateFlow()

    private val _selectedColor = MutableStateFlow(BlockColor.RED)
    val selectedColor: StateFlow<BlockColor> = _selectedColor.asStateFlow()

    private val _isAnimationRunning = MutableStateFlow(true)
    val isAnimationRunning: StateFlow<Boolean> = _isAnimationRunning.asStateFlow()

    // PHYSICS TOWER STATE
    private val _physicsState = MutableStateFlow(PhysicsTowerState())
    val physicsState: StateFlow<PhysicsTowerState> = _physicsState.asStateFlow()

    // MEMORY GAME STATE
    private val _memoryState = MutableStateFlow(MemoryGameState())
    val memoryState: StateFlow<MemoryGameState> = _memoryState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
        startNewMemoryGame()
    }

    // ==========================================
    // PUZZLE BLUEPRINT LOGIC
    // ==========================================

    fun loadLevel(levelId: Int) {
        val level = BlueprintCatalog.levels.find { it.levelId == levelId } ?: BlueprintCatalog.levels.first()
        puzzleTimerJob?.cancel()

        _puzzleState.value = PuzzleGameState(
            currentLevel = level,
            placedPieces = emptyMap(),
            remainingPieces = level.pieces.shuffled(),
            selectedPiece = level.pieces.firstOrNull(),
            isCompleted = false,
            starsEarned = 0,
            elapsedSeconds = 0,
            showCelebration = false,
            hintPieceId = null
        )

        puzzleTimerJob = viewModelScope.launch {
            while (!_puzzleState.value.isCompleted) {
                delay(1000)
                _puzzleState.value = _puzzleState.value.copy(
                    elapsedSeconds = _puzzleState.value.elapsedSeconds + 1
                )
            }
        }
    }

    fun selectPieceFromTray(piece: BlueprintPiece) {
        SoundSynthesizer.playClick()
        _puzzleState.value = _puzzleState.value.copy(
            selectedPiece = piece,
            hintPieceId = null
        )
    }

    fun placePieceOnGrid(gridX: Int, gridY: Int) {
        val current = _puzzleState.value
        val selected = current.selectedPiece ?: return
        val level = current.currentLevel ?: return

        // Check if this grid position matches the selected piece target position
        val matchingTargetPiece = level.pieces.find {
            it.pieceId == selected.pieceId && it.targetX == gridX && it.targetY == gridY
        }

        if (matchingTargetPiece != null) {
            // Correct Placement!
            SoundSynthesizer.playSnap()
            val newPlaced = current.placedPieces.toMutableMap()
            newPlaced[matchingTargetPiece.pieceId] = PlacedBlock(
                id = matchingTargetPiece.pieceId,
                shapeType = matchingTargetPiece.shapeType,
                color = matchingTargetPiece.color,
                gridX = gridX,
                gridY = gridY,
                rotation = matchingTargetPiece.rotation
            )

            val newRemaining = current.remainingPieces.filter { it.pieceId != matchingTargetPiece.pieceId }
            val nextSelected = newRemaining.firstOrNull()

            val isDone = newRemaining.isEmpty()
            val earnedStars = if (current.elapsedSeconds < 30) 3 else if (current.elapsedSeconds < 60) 2 else 1

            _puzzleState.value = current.copy(
                placedPieces = newPlaced,
                remainingPieces = newRemaining,
                selectedPiece = nextSelected,
                isCompleted = isDone,
                starsEarned = if (isDone) earnedStars else 0,
                showCelebration = isDone,
                hintPieceId = null
            )

            if (isDone) {
                SoundSynthesizer.playFanfare()
                viewModelScope.launch {
                    repository.completeLevel(level.levelId, earnedStars, current.elapsedSeconds)
                }
            }
        } else {
            // Incorrect position
            SoundSynthesizer.playBoing()
        }
    }

    fun showHint() {
        val current = _puzzleState.value
        val selected = current.selectedPiece ?: return
        SoundSynthesizer.playPop()
        _puzzleState.value = current.copy(hintPieceId = selected.pieceId)
    }

    // ==========================================
    // SANDBOX CREATIVE BUILDER LOGIC
    // ==========================================

    fun setSandboxTool(tool: SandboxTool) {
        SoundSynthesizer.playClick()
        _selectedTool.value = tool
    }

    fun setSandboxShape(shape: ShapeType) {
        SoundSynthesizer.playClick()
        _selectedShape.value = shape
    }

    fun setSandboxColor(color: BlockColor) {
        SoundSynthesizer.playClick()
        _selectedColor.value = color
    }

    fun toggleAnimation() {
        _isAnimationRunning.value = !_isAnimationRunning.value
    }

    fun handleSandboxGridTap(gridX: Int, gridY: Int) {
        val tool = _selectedTool.value
        val currentBlocks = _sandboxBlocks.value.toMutableList()
        val existingIndex = currentBlocks.indexOfFirst { it.gridX == gridX && it.gridY == gridY }

        when (tool) {
            SandboxTool.ADD -> {
                if (existingIndex >= 0) {
                    // Replace block
                    currentBlocks[existingIndex] = PlacedBlock(
                        id = UUID.randomUUID().toString(),
                        shapeType = _selectedShape.value,
                        color = _selectedColor.value,
                        gridX = gridX,
                        gridY = gridY,
                        rotation = 0
                    )
                } else {
                    currentBlocks.add(
                        PlacedBlock(
                            id = UUID.randomUUID().toString(),
                            shapeType = _selectedShape.value,
                            color = _selectedColor.value,
                            gridX = gridX,
                            gridY = gridY,
                            rotation = 0
                        )
                    )
                }
                SoundSynthesizer.playPop()
                _sandboxBlocks.value = currentBlocks
            }
            SandboxTool.REMOVE -> {
                if (existingIndex >= 0) {
                    currentBlocks.removeAt(existingIndex)
                    SoundSynthesizer.playBoing()
                    _sandboxBlocks.value = currentBlocks
                }
            }
            SandboxTool.ROTATE -> {
                if (existingIndex >= 0) {
                    val b = currentBlocks[existingIndex]
                    val newRot = (b.rotation + 90) % 360
                    currentBlocks[existingIndex] = b.copy(rotation = newRot)
                    SoundSynthesizer.playSnap()
                    _sandboxBlocks.value = currentBlocks
                }
            }
            SandboxTool.COLOR -> {
                if (existingIndex >= 0) {
                    val b = currentBlocks[existingIndex]
                    currentBlocks[existingIndex] = b.copy(color = _selectedColor.value)
                    SoundSynthesizer.playPop()
                    _sandboxBlocks.value = currentBlocks
                }
            }
        }
    }

    fun clearSandbox() {
        SoundSynthesizer.playBoing()
        _sandboxBlocks.value = emptyList()
    }

    fun saveCurrentCreation(title: String, onSuccess: () -> Unit) {
        val blocks = _sandboxBlocks.value
        if (blocks.isEmpty()) return

        viewModelScope.launch {
            repository.saveCreation(title.ifBlank { "مُجَسّم رائع" }, blocks)
            SoundSynthesizer.playSuccess()
            onSuccess()
        }
    }

    fun loadCreationIntoSandbox(creation: SavedCreationEntity) {
        try {
            val jsonArray = org.json.JSONArray(creation.blocksJson)
            val list = mutableListOf<PlacedBlock>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val shape = ShapeType.valueOf(obj.optString("shape", ShapeType.CUBE.name))
                val colorId = obj.optString("color", "red")
                val color = BlockColor.entries.find { it.id == colorId } ?: BlockColor.RED
                list.add(
                    PlacedBlock(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        shapeType = shape,
                        color = color,
                        gridX = obj.optInt("gx", 0),
                        gridY = obj.optInt("gy", 0),
                        rotation = obj.optInt("rot", 0)
                    )
                )
            }
            _sandboxBlocks.value = list
            SoundSynthesizer.playSuccess()
        } catch (_: Exception) {
        }
    }

    fun deleteCreation(id: Long) {
        viewModelScope.launch {
            repository.deleteCreation(id)
        }
    }

    // ==========================================
    // PHYSICS TOWER LOGIC
    // ==========================================

    fun resetPhysicsTower() {
        _physicsState.value = PhysicsTowerState(
            platformAngle = 0f,
            stackedBlocks = emptyList(),
            currentHeight = 0,
            isGameOver = false,
            isSuccess = false,
            targetHeight = 8,
            nextBlockColor = BlockColor.entries.random(),
            nextBlockShape = listOf(ShapeType.CUBE, ShapeType.RECTANGLE, ShapeType.CYLINDER).random()
        )
    }

    fun dropBlockOnTower(slotX: Int) { // slotX: -3 to +3 relative to center
        val cur = _physicsState.value
        if (cur.isGameOver || cur.isSuccess) return

        val newBlocks = cur.stackedBlocks.toMutableList()
        val newHeight = cur.currentHeight + 1

        val newBlock = PlacedBlock(
            id = UUID.randomUUID().toString(),
            shapeType = cur.nextBlockShape,
            color = cur.nextBlockColor,
            gridX = slotX,
            gridY = newHeight,
            rotation = 0
        )
        newBlocks.add(newBlock)

        // Calculate Torque & Angle
        var totalTorque = 0f
        for (b in newBlocks) {
            totalTorque += (b.gridX * 2.8f)
        }

        val newAngle = totalTorque.coerceIn(-35f, 35f)
        val isTippingOver = abs(newAngle) >= 30f
        val isWon = newHeight >= cur.targetHeight && !isTippingOver

        if (isTippingOver) {
            SoundSynthesizer.playBoing()
        } else if (isWon) {
            SoundSynthesizer.playFanfare()
            viewModelScope.launch {
                repository.updateTowerBestHeight(newHeight)
            }
        } else {
            SoundSynthesizer.playPop()
        }

        _physicsState.value = cur.copy(
            platformAngle = newAngle,
            stackedBlocks = newBlocks,
            currentHeight = newHeight,
            isGameOver = isTippingOver,
            isSuccess = isWon,
            nextBlockColor = BlockColor.entries.random(),
            nextBlockShape = listOf(ShapeType.CUBE, ShapeType.RECTANGLE, ShapeType.CYLINDER, ShapeType.TRIANGLE).random()
        )
    }

    // ==========================================
    // MEMORY MATCH LOGIC
    // ==========================================

    fun startNewMemoryGame() {
        val shapes = listOf(
            ShapeType.CUBE to BlockColor.RED,
            ShapeType.TRIANGLE to BlockColor.YELLOW,
            ShapeType.CYLINDER to BlockColor.BLUE,
            ShapeType.WHEEL to BlockColor.PURPLE,
            ShapeType.EYES to BlockColor.GREEN,
            ShapeType.STAR_TOP to BlockColor.GOLD
        )

        val cards = (shapes + shapes).shuffled().mapIndexed { index, pair ->
            MemoryCard(
                id = index,
                shapeType = pair.first,
                color = pair.second,
                isFaceUp = false,
                isMatched = false
            )
        }

        _memoryState.value = MemoryGameState(
            cards = cards,
            flippedCardIndices = emptyList(),
            matchesCount = 0,
            totalPairs = shapes.size,
            isCompleted = false,
            movesCount = 0
        )
    }

    fun onMemoryCardClicked(index: Int) {
        val cur = _memoryState.value
        if (cur.isCompleted) return

        val cards = cur.cards.toMutableList()
        val card = cards[index]
        if (card.isFaceUp || card.isMatched) return
        if (cur.flippedCardIndices.size >= 2) return

        SoundSynthesizer.playClick()
        cards[index] = card.copy(isFaceUp = true)
        val newFlipped = cur.flippedCardIndices + index

        if (newFlipped.size == 2) {
            val firstCard = cards[newFlipped[0]]
            val secondCard = cards[newFlipped[1]]
            val isMatch = firstCard.shapeType == secondCard.shapeType && firstCard.color == secondCard.color

            val newMoves = cur.movesCount + 1

            if (isMatch) {
                SoundSynthesizer.playSnap()
                cards[newFlipped[0]] = firstCard.copy(isMatched = true)
                cards[newFlipped[1]] = secondCard.copy(isMatched = true)
                val newMatches = cur.matchesCount + 1
                val isAllMatched = newMatches >= cur.totalPairs

                if (isAllMatched) {
                    SoundSynthesizer.playSuccess()
                    viewModelScope.launch {
                        repository.completeLevel(levelId = 99, starsEarned = 3, timeSeconds = newMoves * 2)
                    }
                }

                _memoryState.value = cur.copy(
                    cards = cards,
                    flippedCardIndices = emptyList(),
                    matchesCount = newMatches,
                    isCompleted = isAllMatched,
                    movesCount = newMoves
                )
            } else {
                _memoryState.value = cur.copy(
                    cards = cards,
                    flippedCardIndices = newFlipped,
                    movesCount = newMoves
                )
                viewModelScope.launch {
                    delay(900)
                    val resetCards = _memoryState.value.cards.toMutableList()
                    resetCards[newFlipped[0]] = resetCards[newFlipped[0]].copy(isFaceUp = false)
                    resetCards[newFlipped[1]] = resetCards[newFlipped[1]].copy(isFaceUp = false)
                    _memoryState.value = _memoryState.value.copy(
                        cards = resetCards,
                        flippedCardIndices = emptyList()
                    )
                }
            }
        } else {
            _memoryState.value = cur.copy(
                cards = cards,
                flippedCardIndices = newFlipped
            )
        }
    }

    // ==========================================
    // SHOP & THEMES
    // ==========================================

    fun getAvailableThemeSkins(unlockedThemesStr: String): List<BlockThemeSkin> {
        val set = unlockedThemesStr.split(",").toSet()
        return listOf(
            BlockThemeSkin("classic", "المكعبات الكلاسيكية", 0, true, "ألوان زاهية وجميلة للبناء اليومي", BlockColor.RED.color),
            BlockThemeSkin("candy", "مكعبات حلوى البونبون", 5, set.contains("candy"), "ألوان وردية وباستيلية فائقة اللطافة", BlockColor.PINK.color),
            BlockThemeSkin("neon", "مكعبات النيون المضيئة", 12, set.contains("neon"), "توهج سحري يشبه الفضاء والمستقبل", BlockColor.CYAN.color),
            BlockThemeSkin("gold", "المكعبات الذهبية الملكية", 25, set.contains("gold"), "أحجار ذهبية وجواهر متلألئة للأبطال", BlockColor.GOLD.color)
        )
    }

    fun unlockSkin(theme: BlockThemeSkin) {
        viewModelScope.launch {
            val success = repository.unlockThemeSkin(theme.id, theme.costStars)
            if (success) {
                SoundSynthesizer.playSuccess()
                repository.selectThemeSkin(theme.id)
            } else {
                SoundSynthesizer.playBoing()
            }
        }
    }

    fun selectSkin(theme: BlockThemeSkin) {
        viewModelScope.launch {
            repository.selectThemeSkin(theme.id)
            SoundSynthesizer.playSnap()
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSoundSetting(enabled)
        }
    }

    fun getBadges(unlockedIds: Set<String>, stars: Int, creations: Int, level: Int): List<KidBadge> {
        return repository.getAllBadges(unlockedIds, stars, creations, level)
    }
}
