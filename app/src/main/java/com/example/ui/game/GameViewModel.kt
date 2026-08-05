package com.example.ui.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.data.UserProgress
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Random

enum class GameScreen {
    SPLASH,
    WELCOME,
    HOME,
    WORLD_SELECTION,
    LEVEL_SELECTION,
    GAMEPLAY,
    TUTORIAL
}

enum class ActiveOverlay {
    NONE,
    DAILY_REWARDS,
    LUCKY_WHEEL,
    SHOP,
    INVENTORY,
    ACHIEVEMENTS,
    LEADERBOARD,
    PLAYER_PROFILE,
    SETTINGS,
    PAUSE_MENU,
    VICTORY,
    LEVEL_SET_COMPLETE,
    GAME_OVER,
    LOADING,
    AD_VIDEO,
    RECIPE_BOOK,
    DAILY_QUESTS,
    TUTORIAL
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    
    // UI State for persistence
    private val _userProgress = MutableStateFlow(UserProgress())
    val userProgress: StateFlow<UserProgress> = _userProgress.asStateFlow()

    // Navigation and Overlays
    private val _currentScreen = MutableStateFlow(GameScreen.SPLASH)
    val currentScreen: StateFlow<GameScreen> = _currentScreen.asStateFlow()

    private val _activeOverlay = MutableStateFlow(ActiveOverlay.NONE)
    val activeOverlay: StateFlow<ActiveOverlay> = _activeOverlay.asStateFlow()

    private val _activeTutorialId = MutableStateFlow<String?>(null)
    val activeTutorialId: StateFlow<String?> = _activeTutorialId.asStateFlow()

    // Gameplay States
    private val _currentLevel = MutableStateFlow(1)
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    private val _tubes = MutableStateFlow<List<List<Int>>>(emptyList())
    val tubes: StateFlow<List<List<Int>>> = _tubes.asStateFlow()

    private val _selectedTubeIndex = MutableStateFlow<Int?>(null)
    val selectedTubeIndex: StateFlow<Int?> = _selectedTubeIndex.asStateFlow()

    data class PourAnimationState(
        val srcIndex: Int,
        val destIndex: Int,
        val colorId: Int,
        val amount: Int
    )

    private val _activePour = MutableStateFlow<PourAnimationState?>(null)
    val activePour: StateFlow<PourAnimationState?> = _activePour.asStateFlow()

    private val _movesCount = MutableStateFlow(0)
    val movesCount: StateFlow<Int> = _movesCount.asStateFlow()

    private val _maxMoves = MutableStateFlow(15)
    val maxMoves: StateFlow<Int> = _maxMoves.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _isLevelComplete = MutableStateFlow(false)
    val isLevelComplete: StateFlow<Boolean> = _isLevelComplete.asStateFlow()

    private val _nextOverlayAfterAd = MutableStateFlow(ActiveOverlay.NONE)
    val nextOverlayAfterAd: StateFlow<ActiveOverlay> = _nextOverlayAfterAd.asStateFlow()

    private val _completedSection = MutableStateFlow<LevelSection?>(null)
    val completedSection: StateFlow<LevelSection?> = _completedSection.asStateFlow()

    private val _completedTubeAnimationIndex = MutableStateFlow<Int?>(null)
    val completedTubeAnimationIndex: StateFlow<Int?> = _completedTubeAnimationIndex.asStateFlow()

    private val _extraTubeAdded = MutableStateFlow(false)
    val extraTubeAdded: StateFlow<Boolean> = _extraTubeAdded.asStateFlow()

    // --- CHALLENGE AND POWER-UP STATES ---
    private val _volatileTubeIndex = MutableStateFlow<Int?>(null)
    val volatileTubeIndex: StateFlow<Int?> = _volatileTubeIndex.asStateFlow()

    private val _volatileMovesLeft = MutableStateFlow(5)
    val volatileMovesLeft: StateFlow<Int> = _volatileMovesLeft.asStateFlow()

    private var volatileTubeInitialContents: List<Int> = emptyList()

    private val _activePowerUp = MutableStateFlow(PowerUpType.NONE)
    val activePowerUp: StateFlow<PowerUpType> = _activePowerUp.asStateFlow()

    private val _frozenTubeIndices = MutableStateFlow<Set<Int>>(emptySet())
    val frozenTubeIndices: StateFlow<Set<Int>> = _frozenTubeIndices.asStateFlow()

    // Portal pairs state (maps tube index -> portal group ID 1 or 2)
    private val _portalTubePairs = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val portalTubePairs: StateFlow<Map<Int, Int>> = _portalTubePairs.asStateFlow()

    // Crystal Lock & Key tube indices
    private val _crystalLockTubeIndex = MutableStateFlow<Int?>(null)
    val crystalLockTubeIndex: StateFlow<Int?> = _crystalLockTubeIndex.asStateFlow()

    private val _keyTubeIndex = MutableStateFlow<Int?>(null)
    val keyTubeIndex: StateFlow<Int?> = _keyTubeIndex.asStateFlow()

    private val _victoryStars = MutableStateFlow(3)
    val victoryStars: StateFlow<Int> = _victoryStars.asStateFlow()

    private val _coinsEarned = MutableStateFlow(35)
    val coinsEarned: StateFlow<Int> = _coinsEarned.asStateFlow()

    private val _gemsEarned = MutableStateFlow(5)
    val gemsEarned: StateFlow<Int> = _gemsEarned.asStateFlow()

    // --- DAILY QUESTS & WEEKLY EVENT ---
    private val _quests = MutableStateFlow<List<Quest>>(emptyList())
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    private val _eventTimeRemaining = MutableStateFlow("23h 45m 12s")
    val eventTimeRemaining: StateFlow<String> = _eventTimeRemaining.asStateFlow()

    private val undoStack = mutableListOf<List<List<Int>>>()
    private var timerJob: Job? = null

    // Lucky Wheel State
    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()
    
    private val _wheelRotationAngle = MutableStateFlow(0f)
    val wheelRotationAngle: StateFlow<Float> = _wheelRotationAngle.asStateFlow()

    // Leaderboard Simulations (Weekly live rankings)
    data class LeaderboardEntry(val name: String, val level: Int, val isUser: Boolean = false, val avatarId: Int)
    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    // Toast and Animations Feedback
    private val _gameFeedback = MutableStateFlow<String?>(null)
    val gameFeedback: StateFlow<String?> = _gameFeedback.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GameRepository(database.userProgressDao(), application)

        // Initialize daily quests list
        _quests.value = listOf(
            Quest("perfect_pours", "Perfect Pours", "Perform 10 potion transfers", 10, 0, 50, 2, false),
            Quest("blitz_solver", "Blitz Alchemist", "Solve any puzzle level in under 60 seconds", 1, 0, 100, 5, false),
            Quest("star_collector", "Star Collector", "Earn a 3-star victory on any level", 1, 0, 150, 8, false)
        )

        // Run background event countdown ticking
        viewModelScope.launch {
            var totalSecs = 23 * 3600 + 45 * 60 + 12
            while (true) {
                delay(1000)
                totalSecs--
                if (totalSecs <= 0) {
                    totalSecs = 24 * 3600 - 1
                }
                val hours = totalSecs / 3600
                val minutes = (totalSecs % 3600) / 60
                val seconds = totalSecs % 60
                _eventTimeRemaining.value = String.format("%02dh %02dm %02ds", hours, minutes, seconds)
            }
        }

        // Load progress from database and initialize
        viewModelScope.launch {
            var isFirstLoad = true
            repository.userProgress.collect { progress ->
                _userProgress.value = progress
                com.example.ui.audio.SoothingAudioEngine.init(progress.isSoundEnabled, progress.isMusicEnabled)
                if (isFirstLoad) {
                    _currentLevel.value = progress.currentLevel
                    isFirstLoad = false
                }
                generateLeaderboard()
            }
        }

        // Initialize Splash Screen auto-advance
        viewModelScope.launch {
            delay(2500) // Splash delay
            _currentScreen.value = GameScreen.WELCOME
        }

        generateLeaderboard()
    }

    // --- GAMEPLAY FLOWS ---

    fun startGame(levelIndex: Int) {
        viewModelScope.launch {
            _activeOverlay.value = ActiveOverlay.LOADING
            delay(800) // Loading screen transition
            
            _currentLevel.value = levelIndex
            val generated = generateLevel(levelIndex)
            _tubes.value = generated
            _selectedTubeIndex.value = null
            _movesCount.value = 0
            _maxMoves.value = getMaxMovesForLevel(levelIndex)
            _timerSeconds.value = 0
            _isLevelComplete.value = false
            _extraTubeAdded.value = false
            undoStack.clear()

            // Initialize power-ups and challenges
            _activePowerUp.value = PowerUpType.NONE

            // Level 11+ features Frozen Tubes obstacle
            if (levelIndex >= 11) {
                // Freeze the first non-empty tube
                val firstNonEmpty = generated.indexOfFirst { it.isNotEmpty() }
                if (firstNonEmpty != -1) {
                    _frozenTubeIndices.value = setOf(firstNonEmpty)
                } else {
                    _frozenTubeIndices.value = emptySet()
                }
            } else {
                _frozenTubeIndices.value = emptySet()
            }

            // Level 31+ features Volatile Potion segments (Moves bomb)
            if (levelIndex >= 31) {
                var foundIdx = -1
                for (i in generated.indices) {
                    // Try to pick a non-empty tube that is NOT frozen
                    if (generated[i].isNotEmpty() && !_frozenTubeIndices.value.contains(i)) {
                        foundIdx = i
                        break
                    }
                }
                // Fallback to any non-empty
                if (foundIdx == -1) {
                    foundIdx = generated.indexOfFirst { it.isNotEmpty() }
                }
                
                if (foundIdx != -1) {
                    _volatileTubeIndex.value = foundIdx
                    _volatileMovesLeft.value = 5
                    volatileTubeInitialContents = generated[foundIdx].toList()
                } else {
                    _volatileTubeIndex.value = null
                    _volatileMovesLeft.value = 5
                    volatileTubeInitialContents = emptyList()
                }
            } else {
                _volatileTubeIndex.value = null
                _volatileMovesLeft.value = 5
                volatileTubeInitialContents = emptyList()
            }

            // Level 101+ features Portal Tubes
            if (levelIndex >= 101 && generated.size >= 4) {
                _portalTubePairs.value = mapOf(0 to 1, 1 to 2)
            } else {
                _portalTubePairs.value = emptyMap()
            }

            // Level 181+ features Crystal Lock & Key Vials
            if (levelIndex >= 181 && generated.size >= 4) {
                _crystalLockTubeIndex.value = 2
                _keyTubeIndex.value = 0
            } else {
                _crystalLockTubeIndex.value = null
                _keyTubeIndex.value = null
            }

            startTimer()
            _currentScreen.value = GameScreen.GAMEPLAY
            
            val tutorialId = getTutorialIdForLevel(levelIndex)
            val viewedList = _userProgress.value.viewedTutorials.split(",").filter { it.isNotEmpty() }
            if (!viewedList.contains(tutorialId)) {
                _activeTutorialId.value = tutorialId
                _activeOverlay.value = ActiveOverlay.TUTORIAL
                markTutorialAsViewed(tutorialId)
            } else {
                _activeOverlay.value = ActiveOverlay.NONE
            }
        }
    }

    fun getTutorialIdForLevel(level: Int): String {
        return when {
            level >= 301 -> "archmage_fusion"
            level >= 181 -> "crystal_locks"
            level >= 101 -> "portal_warp"
            level >= 61 -> "chameleon_realm"
            level >= 31 -> "desert_sunset"
            level >= 11 -> "emerald_forest"
            else -> "cyber_city"
        }
    }

    fun markTutorialAsViewed(tutorialId: String) {
        viewModelScope.launch {
            val currentList = _userProgress.value.viewedTutorials.split(",").filter { it.isNotEmpty() }.toMutableList()
            if (!currentList.contains(tutorialId)) {
                currentList.add(tutorialId)
                val newListStr = currentList.joinToString(",")
                updateUserProgress { it.copy(viewedTutorials = newListStr) }
            }
        }
    }

    fun triggerTutorial(tutorialId: String) {
        _activeTutorialId.value = tutorialId
        _activeOverlay.value = ActiveOverlay.TUTORIAL
    }

    fun dismissTutorial() {
        _activeOverlay.value = ActiveOverlay.NONE
        _activeTutorialId.value = null
    }

    fun togglePowerUp(type: PowerUpType) {
        if (_activePowerUp.value == type) {
            _activePowerUp.value = PowerUpType.NONE
            showFeedback("Power-up Canceled")
        } else {
            _activePowerUp.value = type
            val msg = when (type) {
                PowerUpType.SWAPPER -> "🧪 Transmute Active: Tap a tube to swap top 2! (Costs 150)"
                PowerUpType.CATALYST -> "✨ Magic Catalyst Active: Tap a tube to auto-sort optimal move! (Costs 100)"
                else -> ""
            }
            showFeedback(msg)
            if (type == PowerUpType.CATALYST) {
                applyCatalystPowerUp()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _timerSeconds.value += 1
            }
        }
    }

    fun pauseGame() {
        timerJob?.cancel()
        _activeOverlay.value = ActiveOverlay.PAUSE_MENU
    }

    fun resumeGame() {
        startTimer()
        _activeOverlay.value = ActiveOverlay.NONE
    }

    fun addExtraTube() {
        if (_extraTubeAdded.value) return
        val cost = 100
        if (_userProgress.value.coins >= cost) {
            updateCoins(-cost)
            val updatedTubes = _tubes.value.toMutableList()
            updatedTubes.add(emptyList()) // Add an empty tube
            _tubes.value = updatedTubes
            _extraTubeAdded.value = true
            showFeedback("Extra Tube Added!")
        } else {
            showFeedback("Not enough coins!")
        }
    }

    fun restartLevel() {
        startGame(_currentLevel.value)
    }

    fun skipLevel() {
        val cost = 250
        if (_userProgress.value.coins >= cost) {
            updateCoins(-cost)
            val levelJustSkipped = _currentLevel.value
            val nextLvl = levelJustSkipped + 1
            _currentLevel.value = nextLvl
            updateUserProgress { current ->
                val highestLvl = if (levelJustSkipped >= current.currentLevel) nextLvl else current.currentLevel
                current.copy(currentLevel = highestLvl)
            }
            startGame(nextLvl)
        } else {
            showFeedback("Not enough coins to skip!")
        }
    }

    fun undoMove() {
        if (undoStack.isNotEmpty()) {
            _tubes.value = undoStack.removeAt(undoStack.size - 1)
            _movesCount.value -= 1
            _selectedTubeIndex.value = null
            showFeedback("Move Undone")
            if (_activeOverlay.value == ActiveOverlay.GAME_OVER) {
                dismissOverlay()
                startTimer()
            }
        } else {
            showFeedback("Nothing to undo!")
        }
    }

    fun applyCatalystPowerUp(targetIndex: Int? = null) {
        val cost = 100
        if (_userProgress.value.coins < cost) {
            showFeedback("Not enough coins! (Requires $cost)")
            _activePowerUp.value = PowerUpType.NONE
            return
        }

        val currentTubes = _tubes.value
        var bestSrc = -1
        var bestDest = -1

        if (targetIndex != null) {
            for (i in currentTubes.indices) {
                if (i != targetIndex) {
                    if (isValidMove(targetIndex, i, currentTubes)) {
                        bestSrc = targetIndex
                        bestDest = i
                        break
                    } else if (isValidMove(i, targetIndex, currentTubes)) {
                        bestSrc = i
                        bestDest = targetIndex
                        break
                    }
                }
            }
        }

        if (bestSrc == -1) {
            for (i in currentTubes.indices) {
                for (j in currentTubes.indices) {
                    if (i != j && isValidMove(i, j, currentTubes)) {
                        bestSrc = i
                        bestDest = j
                        break
                    }
                }
                if (bestSrc != -1) break
            }
        }

        if (bestSrc != -1 && bestDest != -1) {
            updateCoins(-cost)
            _activePowerUp.value = PowerUpType.NONE
            com.example.ui.audio.SoothingAudioEngine.playCatalystSound()
            attemptPour(bestSrc, bestDest)
            showFeedback("✨ Magic Catalyst executed optimal pour!")
        } else {
            showFeedback("No valid catalyst moves available!")
            _activePowerUp.value = PowerUpType.NONE
        }
    }

    private fun isValidMove(src: Int, dest: Int, tubes: List<List<Int>>): Boolean {
        val s = tubes[src]
        val d = tubes[dest]
        if (s.isEmpty()) return false
        val color = s.last()
        return d.isEmpty() || (d.size < 4 && d.last() == color)
    }

    fun claimSectionReward(sectionId: String) {
        val section = LevelSectionsList.find { it.id == sectionId } ?: return
        val currentClaimed = _userProgress.value.claimedSectionRewards.split(",").filter { it.isNotEmpty() }
        if (!currentClaimed.contains(sectionId)) {
            val newClaimed = (currentClaimed + sectionId).joinToString(",")
            updateCoins(section.rewardCoins)
            updateGems(section.rewardGems)
            updateUserProgress { it.copy(claimedSectionRewards = newClaimed) }
            _completedSection.value = section
            _activeOverlay.value = ActiveOverlay.LEVEL_SET_COMPLETE
        }
    }

    fun selectTube(index: Int) {
        if (_isLevelComplete.value) return
        if (_activePour.value != null) return // Block selection while pouring is animating

        com.example.ui.audio.SoothingAudioEngine.playSelectSound()

        // Intercept for active powerups
        val currentPowerUp = _activePowerUp.value
        if (currentPowerUp != PowerUpType.NONE) {
            when (currentPowerUp) {
                PowerUpType.SWAPPER -> {
                    val cost = 150
                    if (_userProgress.value.coins < cost) {
                        showFeedback("Not enough coins! (Requires $cost)")
                        _activePowerUp.value = PowerUpType.NONE
                        return
                    }
                    val targetTube = _tubes.value[index]
                    if (targetTube.size >= 2) {
                        undoStack.add(_tubes.value.map { it.toList() })
                        val updatedTubes = _tubes.value.map { it.toMutableList() }
                        val t = updatedTubes[index]
                        val lastIdx = t.lastIndex
                        val temp = t[lastIdx]
                        t[lastIdx] = t[lastIdx - 1]
                        t[lastIdx - 1] = temp

                        _tubes.value = updatedTubes.map { it.toList() }
                        updateCoins(-cost)
                        _activePowerUp.value = PowerUpType.NONE
                        showFeedback("Potion transmuted! 🧪")
                        _movesCount.value += 1
                        checkWinCondition()
                    } else {
                        showFeedback("Tube must have at least 2 layers to swap!")
                    }
                    return
                }
                PowerUpType.CATALYST -> {
                    applyCatalystPowerUp(index)
                    return
                }
                else -> {}
            }
        }

        val selectedIndex = _selectedTubeIndex.value

        if (selectedIndex == null) {
            if (_tubes.value[index].isNotEmpty()) {
                _selectedTubeIndex.value = index
            }
        } else {
            if (selectedIndex == index) {
                _selectedTubeIndex.value = null
            } else {
                if (attemptPour(selectedIndex, index)) {
                    _selectedTubeIndex.value = null
                } else {
                    if (_tubes.value[index].isNotEmpty()) {
                        _selectedTubeIndex.value = index
                    } else {
                        _selectedTubeIndex.value = null
                    }
                }
            }
        }
    }

    private fun attemptPour(srcIndex: Int, destIndex: Int): Boolean {
        if (_activePour.value != null) return false
        val tubesList = _tubes.value
        val srcTube = tubesList[srcIndex]
        val destTube = tubesList[destIndex]

        if (srcTube.isEmpty()) return false

        // Check if frozen
        if (_frozenTubeIndices.value.contains(destIndex)) {
            showFeedback("This tube is frozen! ❄️")
            return false
        }

        // Check if crystal locked
        if (_crystalLockTubeIndex.value == destIndex) {
            showFeedback("Fill the Key Tube to unlock! 🔒")
            return false
        }

        val topColor = srcTube.last()
        
        // Count how many of the top color are contiguous
        var contiguousCount = 0
        for (i in srcTube.indices.reversed()) {
            if (srcTube[i] == topColor) {
                contiguousCount++
            } else {
                break
            }
        }

        // Check compatibility (Chameleon ID 99 matches anything!)
        val destTop = destTube.lastOrNull()
        val canPour = destTube.isEmpty() || (destTube.size < 4 && (destTop == topColor || destTop == 99 || topColor == 99))
        if (!canPour) return false

        val availableSpace = 4 - destTube.size
        val pourAmount = minOf(contiguousCount, availableSpace)

        if (pourAmount <= 0) return false

        // Save current state to undo stack BEFORE updating
        undoStack.add(tubesList.map { it.toList() })

        // Set the active pour animation event (Compose will run the animation)
        _activePour.value = PourAnimationState(
            srcIndex = srcIndex,
            destIndex = destIndex,
            colorId = topColor,
            amount = pourAmount
        )

        return true
    }

    fun completePour() {
        val pour = _activePour.value ?: return
        val tubesList = _tubes.value
        val updatedTubes = tubesList.map { it.toMutableList() }.toMutableList()
        
        // Execute the pour state change
        repeat(pour.amount) {
            if (updatedTubes[pour.srcIndex].isNotEmpty()) {
                updatedTubes[pour.srcIndex].removeAt(updatedTubes[pour.srcIndex].size - 1)
            }
        }
        repeat(pour.amount) {
            updatedTubes[pour.destIndex].add(pour.colorId)
        }

        // Chameleon Liquid Adaptation: Convert 99 to match adjacent layer
        val destList = updatedTubes[pour.destIndex]
        if (destList.contains(99)) {
            val nonChameleonColor = destList.firstOrNull { it != 99 } ?: pour.colorId
            val adapted = destList.map { if (it == 99) nonChameleonColor else it }
            updatedTubes[pour.destIndex] = adapted.toMutableList()
        }

        val destTubeAfter = updatedTubes[pour.destIndex]
        val isNewlySorted = destTubeAfter.size == 4 && destTubeAfter.all { it == destTubeAfter.first() }

        _tubes.value = updatedTubes.map { it.toList() }
        _activePour.value = null
        _movesCount.value += 1
        incrementQuestProgress("perfect_pours", 1)

        if (isNewlySorted) {
            _completedTubeAnimationIndex.value = pour.destIndex
            showFeedback("Tube Sorted! ✨")
            com.example.ui.audio.SoothingAudioEngine.playTubeCompletedSound()

            // Unlock Crystal Lock if Key Tube is sorted
            val keyIdx = _keyTubeIndex.value
            if (keyIdx != null && keyIdx == pour.destIndex) {
                _crystalLockTubeIndex.value = null
                _keyTubeIndex.value = null
                showFeedback("🔓 Golden Crystal Padlock Shattered! +50 Gems!")
                updateGems(50)
            }

            // Unfreeze adjacent frozen tubes when sorted
            val adjacentIndices = listOf(pour.destIndex - 1, pour.destIndex + 1)
            val currentFrozen = _frozenTubeIndices.value
            val toThaw = adjacentIndices.filter { it in updatedTubes.indices && currentFrozen.contains(it) }
            if (toThaw.isNotEmpty()) {
                _frozenTubeIndices.value = currentFrozen - toThaw.toSet()
                showFeedback("Adjacent Tube Thawed! ❄️🔥")
            }
        }

        // Handle Volatile Tube countdown/stabilization
        val volTubeIdx = _volatileTubeIndex.value
        if (volTubeIdx != null) {
            val volTubeContents = updatedTubes[volTubeIdx]
            val isStabilized = volTubeContents.isEmpty() || (volTubeContents.size == 4 && volTubeContents.all { it == volTubeContents.first() })
            if (isStabilized) {
                _volatileTubeIndex.value = null
                showFeedback("✨ Volatile Potion Stabilized!")
            } else {
                _volatileMovesLeft.value -= 1
                if (_volatileMovesLeft.value <= 0) {
                    showFeedback("💥 Volatile tube fizzed over! Potion reset!")
                    val resetTubes = updatedTubes.mapIndexed { idx, list ->
                        if (idx == volTubeIdx) volatileTubeInitialContents.toList() else list.toList()
                    }
                    _tubes.value = resetTubes
                    _volatileMovesLeft.value = 5 // Reset moves countdown
                }
            }
        }

        checkWinCondition()
    }

    private fun checkWinCondition() {
        val currentTubesList = _tubes.value
        var isAllSorted = true

        for (tube in currentTubesList) {
            if (tube.isEmpty()) continue
            if (tube.size != 4) {
                isAllSorted = false
                break
            }
            val firstColor = tube.first()
            if (tube.any { it != firstColor }) {
                isAllSorted = false
                break
            }
        }

        if (isAllSorted) {
            handleVictory()
        } else {
            if (_movesCount.value >= _maxMoves.value) {
                handleGameOver()
            }
        }
    }

    private fun handleGameOver() {
        timerJob?.cancel()
        _nextOverlayAfterAd.value = ActiveOverlay.GAME_OVER
        _activeOverlay.value = ActiveOverlay.AD_VIDEO
    }

    fun getMaxMovesForLevel(levelNumber: Int): Int {
        return when {
            levelNumber <= 1 -> 15
            levelNumber <= 2 -> 15
            levelNumber <= 5 -> 20
            levelNumber <= 10 -> 25
            levelNumber <= 20 -> 35
            levelNumber <= 40 -> 50
            levelNumber <= 60 -> 70
            else -> 90
        }
    }

    private fun handleVictory() {
        timerJob?.cancel()
        _isLevelComplete.value = true
        
        // Calculate stars based on efficiency
        val moves = _movesCount.value
        val maxM = _maxMoves.value
        val stars = when {
            moves <= maxM * 0.5 -> 3
            moves <= maxM * 0.75 -> 2
            else -> 1
        }
        _victoryStars.value = stars

        // Update Quest Progress
        if (_timerSeconds.value <= 60) {
            incrementQuestProgress("blitz_solver", 1)
        }
        if (stars == 3) {
            incrementQuestProgress("star_collector", 1)
        }

        // Calculate rewards
        val baseCoins = 30 + Random().nextInt(15)
        val baseGems = 5 + (if (Random().nextFloat() > 0.8f) 2 else 0)
        _coinsEarned.value = baseCoins
        _gemsEarned.value = baseGems
        
        viewModelScope.launch {
            delay(800) // Small delay to let the final pour complete visually
            
            updateCoins(baseCoins)
            updateGems(baseGems)
            
            val levelJustCompleted = _currentLevel.value
            val nextLvl = levelJustCompleted + 1
            _currentLevel.value = nextLvl

            val completedSec = LevelSectionsList.find { it.endLevel == levelJustCompleted }
            if (completedSec != null) {
                _completedSection.value = completedSec
                val currentClaimed = _userProgress.value.claimedSectionRewards.split(",").filter { it.isNotEmpty() }
                if (!currentClaimed.contains(completedSec.id)) {
                    val newClaimed = (currentClaimed + completedSec.id).joinToString(",")
                    updateCoins(completedSec.rewardCoins)
                    updateGems(completedSec.rewardGems)
                    updateUserProgress { it.copy(claimedSectionRewards = newClaimed) }
                }
            } else {
                _completedSection.value = null
            }
            
            updateUserProgress { current ->
                val highestLvl = if (levelJustCompleted >= current.currentLevel) nextLvl else current.currentLevel
                current.copy(
                    currentLevel = highestLvl,
                    totalGamesPlayed = current.totalGamesPlayed + 1,
                    totalGamesWon = current.totalGamesWon + 1,
                    totalStars = current.totalStars + stars,
                    bestTimeSeconds = if (current.bestTimeSeconds == 0 || _timerSeconds.value < current.bestTimeSeconds) _timerSeconds.value else current.bestTimeSeconds
                )
            }

            checkAchievementsProgress()
            generateLeaderboard()
            _nextOverlayAfterAd.value = if (completedSec != null) ActiveOverlay.LEVEL_SET_COMPLETE else ActiveOverlay.VICTORY
            _activeOverlay.value = ActiveOverlay.AD_VIDEO
        }
    }

    // --- DETERMINISTIC LEVEL GENERATION ---

    fun generateLevel(levelNumber: Int): List<List<Int>> {
        // Simple deterministic parameters based on level
        val colorCount = when {
            levelNumber <= 2 -> 3
            levelNumber <= 6 -> 4
            levelNumber <= 15 -> 5
            levelNumber <= 30 -> 6
            levelNumber <= 60 -> 7
            else -> 8
        }

        val pool = mutableListOf<Int>()
        for (colorId in 1..colorCount) {
            repeat(4) {
                pool.add(colorId)
            }
        }

        // Level 61+ introduces Chameleon Wildcard Adapter liquid (Color ID 99)
        if (levelNumber >= 61 && pool.isNotEmpty()) {
            pool[0] = 99
        }

        // Shuffle with seed=levelNumber to make it deterministic but unique
        val rand = Random(levelNumber.toLong() * 31)
        for (i in pool.size - 1 downTo 1) {
            val j = rand.nextInt(i + 1)
            val temp = pool[i]
            pool[i] = pool[j]
            pool[j] = temp
        }

        val result = mutableListOf<List<Int>>()
        for (i in 0 until colorCount) {
            val tubeBlocks = pool.subList(i * 4, (i + 1) * 4).toList()
            result.add(tubeBlocks)
        }

        // Add 2 empty tubes
        repeat(2) {
            result.add(emptyList())
        }

        return result
    }

    // --- LEADERBOARD ---

    private fun generateLeaderboard() {
        val rand = Random()
        val botNames = listOf(
            "SortMaster", "AquaNinja", "ZenPipette", "GradientGuru", "PourKing", 
            "FluidWizard", "ColorFlow", "BubbleSort", "LiquidLover", "AquaQueen"
        )
        
        val list = mutableListOf<LeaderboardEntry>()
        val userLvl = _currentLevel.value

        // Generate bot entries around user's level
        botNames.forEachIndexed { idx, name ->
            val offset = (5 - idx) * 3 + rand.nextInt(4)
            val lvl = maxOf(1, userLvl + offset)
            list.add(LeaderboardEntry(name, lvl, false, idx % 5))
        }

        // Insert User
        list.add(LeaderboardEntry("You", userLvl, true, _userProgress.value.selectedAvatarId))
        
        // Sort
        list.sortByDescending { it.level }
        _leaderboard.value = list
    }

    // --- DAILY REWARDS ---

    fun claimDailyReward() {
        val progress = _userProgress.value
        val now = System.currentTimeMillis()
        val coolingPeriod = 12 * 60 * 60 * 1000 // 12 hours for testing ease
        
        if (now - progress.dailyRewardLastClaimed >= coolingPeriod) {
            val nextDay = (progress.dailyRewardDayIndex % 7) + 1
            
            // Give reward
            val (coinsReward, gemsReward) = when (nextDay) {
                1 -> 100 to 0
                2 -> 250 to 10
                3 -> 0 to 50
                4 -> 500 to 0
                5 -> 0 to 100
                6 -> 1000 to 20
                else -> 1500 to 150 // Day 7 massive reward!
            }

            updateCoins(coinsReward)
            updateGems(gemsReward)

            updateUserProgress { current ->
                current.copy(
                    dailyRewardDayIndex = nextDay,
                    dailyRewardLastClaimed = now
                )
            }

            checkAchievementsProgress()
            showFeedback("Claimed Day $nextDay Rewards!")
        } else {
            showFeedback("Reward already claimed today!")
        }
    }

    // --- LUCKY WHEEL SPIN ---

    fun spinLuckyWheel() {
        if (_isSpinning.value) return
        
        val progress = _userProgress.value
        val now = System.currentTimeMillis()
        val cost = 25
        
        if (progress.gems < cost && progress.coins < 200) {
            showFeedback("Need 25 Gems or 200 Coins to spin!")
            return
        }

        // Charge
        if (progress.gems >= cost) {
            updateGems(-cost)
        } else {
            updateCoins(-200)
        }

        _isSpinning.value = true
        
        viewModelScope.launch {
            val randomAngle = 1080f + Random().nextFloat() * 360f // 3 full spins + random index
            _wheelRotationAngle.value = 0f
            
            // Animate angle manually with a decelerating speed curve
            var speed = 25f
            while (speed > 0.1f) {
                _wheelRotationAngle.value = (_wheelRotationAngle.value + speed) % 360f
                speed *= 0.98f
                delay(16)
            }

            // Determine item based on angle
            val finalAngle = _wheelRotationAngle.value
            val sector = ((finalAngle + 22.5f) % 360f / 45f).toInt()
            
            val (coinsGained, gemsGained, livesGained, msg) = when (sector) {
                0 -> quadruple(100, 0, 0, "100 Coins")
                1 -> quadruple(0, 10, 0, "10 Gems")
                2 -> quadruple(500, 0, 0, "500 Coins")
                3 -> quadruple(0, 0, 5, "5 Lives!")
                4 -> quadruple(0, 50, 0, "50 Gems!")
                5 -> quadruple(1000, 0, 0, "1,000 Coins!")
                6 -> quadruple(0, 100, 0, "100 Gems!")
                else -> quadruple(250, 25, 1, "Jackpot Bundle!")
            }

            updateCoins(coinsGained)
            updateGems(gemsGained)
            updateUserProgress { it.copy(lives = minOf(5, it.lives + livesGained)) }

            _isSpinning.value = false
            showFeedback("Won: $msg")
            checkAchievementsProgress()
        }
    }

    private fun quadruple(a: Int, b: Int, c: Int, d: String) = Quadruple(a, b, c, d)
    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    // --- SHOP & INVENTORY ---

    fun buyTheme(themeId: String) {
        val theme = AvailableThemes.find { it.id == themeId } ?: return
        val progress = _userProgress.value
        val unlockedList = progress.unlockedThemes.split(",").toMutableSet()
        
        if (unlockedList.contains(themeId)) {
            showFeedback("Theme already owned")
            return
        }

        if (progress.coins >= theme.cost) {
            updateCoins(-theme.cost)
            unlockedList.add(themeId)
            updateUserProgress { current ->
                current.copy(
                    unlockedThemes = unlockedList.joinToString(",")
                )
            }
            showFeedback("Unlocked Theme: ${theme.name}!")
        } else {
            showFeedback("Not enough coins!")
        }
    }

    fun equipTheme(themeId: String) {
        val progress = _userProgress.value
        val unlockedList = progress.unlockedThemes.split(",")
        if (unlockedList.contains(themeId)) {
            updateUserProgress { it.copy(equippedTheme = themeId) }
            showFeedback("Equipped Theme: $themeId")
        } else {
            showFeedback("Theme not unlocked yet!")
        }
    }

    fun buyTube(tubeId: String) {
        val tube = AvailableTubes.find { it.id == tubeId } ?: return
        val progress = _userProgress.value
        val unlockedList = progress.unlockedTubes.split(",").toMutableSet()
        
        if (unlockedList.contains(tubeId)) {
            showFeedback("Tube skin already owned")
            return
        }

        if (progress.coins >= tube.cost) {
            updateCoins(-tube.cost)
            unlockedList.add(tubeId)
            updateUserProgress { current ->
                current.copy(
                    unlockedTubes = unlockedList.joinToString(",")
                )
            }
            showFeedback("Unlocked Tube Skin: ${tube.name}!")
        } else {
            showFeedback("Not enough coins!")
        }
    }

    fun equipTube(tubeId: String) {
        val progress = _userProgress.value
        val unlockedList = progress.unlockedTubes.split(",")
        if (unlockedList.contains(tubeId)) {
            updateUserProgress { it.copy(equippedTube = tubeId) }
            showFeedback("Equipped Tube Skin: $tubeId")
        } else {
            showFeedback("Skin not unlocked yet!")
        }
    }

    // --- ACHIEVEMENTS AND PROGRESSION ---

    private fun checkAchievementsProgress() {
        val progress = _userProgress.value
        val currentAchievements = progress.completedAchievements.split(",").toMutableSet()

        AchievementsList.forEach { achievement ->
            if (!currentAchievements.contains(achievement.id)) {
                val meetsCriteria = when (achievement.id) {
                    "first_level" -> progress.totalGamesWon >= 1
                    "levels_10" -> progress.totalGamesWon >= 10
                    "levels_50" -> progress.totalGamesWon >= 50
                    "spend_coins" -> false // Placeholder checked on purchase
                    "claim_daily" -> progress.dailyRewardDayIndex >= 3
                    "spin_wheel" -> false // Checked on spin
                    else -> false
                }
                if (meetsCriteria) {
                    currentAchievements.add(achievement.id)
                    updateCoins(achievement.rewardCoins)
                    updateGems(achievement.rewardGems)
                    showFeedback("Unlocked Achievement: ${achievement.title}!")
                }
            }
        }

        updateUserProgress { it.copy(completedAchievements = currentAchievements.filter { id -> id.isNotEmpty() }.joinToString(",")) }
    }

    // --- SETTINGS TOGGLES ---

    fun toggleSound() {
        val newSound = !_userProgress.value.isSoundEnabled
        updateUserProgress { it.copy(isSoundEnabled = newSound) }
        com.example.ui.audio.SoothingAudioEngine.isSoundEnabled = newSound
        if (newSound) com.example.ui.audio.SoothingAudioEngine.playClickSound()
    }

    fun toggleMusic() {
        val newMusic = !_userProgress.value.isMusicEnabled
        updateUserProgress { it.copy(isMusicEnabled = newMusic) }
        com.example.ui.audio.SoothingAudioEngine.isMusicEnabled = newMusic
    }

    fun toggleVibration() {
        updateUserProgress { it.copy(isVibrationEnabled = !it.isVibrationEnabled) }
    }

    fun updateGraphicsQuality(quality: String) {
        updateUserProgress { it.copy(graphicsQuality = quality) }
    }

    // --- CORE UTILITIES ---

    fun setScreen(screen: GameScreen) {
        _currentScreen.value = screen
    }

    fun showOverlay(overlay: ActiveOverlay) {
        _activeOverlay.value = overlay
    }

    fun dismissOverlay() {
        _activeOverlay.value = ActiveOverlay.NONE
    }

    fun dismissAd() {
        _activeOverlay.value = _nextOverlayAfterAd.value
        _nextOverlayAfterAd.value = ActiveOverlay.NONE
    }

    fun clearCompletedTubeAnimation() {
        _completedTubeAnimationIndex.value = null
    }

    fun updateCoins(delta: Int) {
        updateUserProgress { it.copy(coins = maxOf(0, it.coins + delta)) }
    }

    fun updateGems(delta: Int) {
        updateUserProgress { it.copy(gems = maxOf(0, it.gems + delta)) }
    }

    fun updateAvatar(avatarId: Int) {
        updateUserProgress { it.copy(selectedAvatarId = avatarId) }
    }

    private fun updateUserProgress(update: (UserProgress) -> UserProgress) {
        viewModelScope.launch {
            val updated = update(_userProgress.value)
            _userProgress.value = updated
            repository.saveProgress(updated)
        }
    }

    private fun showFeedback(message: String) {
        viewModelScope.launch {
            _gameFeedback.value = message
            delay(2000)
            if (_gameFeedback.value == message) {
                _gameFeedback.value = null
            }
        }
    }

    fun incrementQuestProgress(id: String, amount: Int) {
        _quests.value = _quests.value.map {
            if (it.id == id && it.current < it.target && !it.isClaimed) {
                it.copy(current = minOf(it.target, it.current + amount))
            } else it
        }
    }

    fun claimQuest(questId: String) {
        val quest = _quests.value.find { it.id == questId } ?: return
        if (quest.current >= quest.target && !quest.isClaimed) {
            _quests.value = _quests.value.map {
                if (it.id == questId) it.copy(isClaimed = true) else it
            }
            updateCoins(quest.rewardCoins)
            updateGems(quest.rewardGems)
            showFeedback("Quest Claimed! +${quest.rewardCoins} 🪙, +${quest.rewardGems} 💎")
        }
    }
}
