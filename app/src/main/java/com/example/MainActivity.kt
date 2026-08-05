package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.ui.game.*
import com.example.ui.audio.BgmControllerCard
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]
        
        setContent {
            MyApplicationTheme {
                
                // Observe states
                val currentScreen by gameViewModel.currentScreen.collectAsState()
                val activeOverlay by gameViewModel.activeOverlay.collectAsState()
                val userProgress by gameViewModel.userProgress.collectAsState()
                val completedSection by gameViewModel.completedSection.collectAsState()
                
                val currentLevel by gameViewModel.currentLevel.collectAsState()
                val tubes by gameViewModel.tubes.collectAsState()
                val selectedTubeIndex by gameViewModel.selectedTubeIndex.collectAsState()
                val movesCount by gameViewModel.movesCount.collectAsState()
                val maxMoves by gameViewModel.maxMoves.collectAsState()
                val timerSeconds by gameViewModel.timerSeconds.collectAsState()
                val extraTubeAdded by gameViewModel.extraTubeAdded.collectAsState()
                
                val isSpinning by gameViewModel.isSpinning.collectAsState()
                val wheelRotationAngle by gameViewModel.wheelRotationAngle.collectAsState()
                val leaderboardEntries by gameViewModel.leaderboard.collectAsState()
                val gameFeedback by gameViewModel.gameFeedback.collectAsState()
                val activePour by gameViewModel.activePour.collectAsState()
                val completedTubeAnimationIndex by gameViewModel.completedTubeAnimationIndex.collectAsState()

                val activePowerUp by gameViewModel.activePowerUp.collectAsState()
                val frozenTubeIndices by gameViewModel.frozenTubeIndices.collectAsState()
                val volatileTubeIndex by gameViewModel.volatileTubeIndex.collectAsState()
                val volatileMovesLeft by gameViewModel.volatileMovesLeft.collectAsState()
                val portalTubePairs by gameViewModel.portalTubePairs.collectAsState()
                val crystalLockTubeIndex by gameViewModel.crystalLockTubeIndex.collectAsState()
                val victoryStars by gameViewModel.victoryStars.collectAsState()
                val coinsEarned by gameViewModel.coinsEarned.collectAsState()
                val gemsEarned by gameViewModel.gemsEarned.collectAsState()

                val quests by gameViewModel.quests.collectAsState()
                val eventTimeRemaining by gameViewModel.eventTimeRemaining.collectAsState()

                // Resolve equipped alchemical theme
                val currentTheme = AvailableThemes.find { it.id == userProgress.equippedTheme } ?: AvailableThemes.first()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        // Apply dynamic theme gradients to background
                        GameBackground(theme = currentTheme) {
                            
                            // Render correct screens
                            Crossfade(targetState = currentScreen, label = "screen_trans") { screen ->
                                when (screen) {
                                    GameScreen.SPLASH -> {
                                        SplashScreen()
                                    }
                                    GameScreen.WELCOME -> {
                                        WelcomeScreen(
                                            onPlayClick = { gameViewModel.setScreen(GameScreen.HOME) },
                                            onShowSettings = { gameViewModel.showOverlay(ActiveOverlay.SETTINGS) }
                                        )
                                    }
                                    GameScreen.HOME -> {
                                        HomeScreen(
                                             selectedAvatarId = userProgress.selectedAvatarId,
                                            currentLevel = userProgress.currentLevel,
                                            coins = userProgress.coins,
                                            gems = userProgress.gems,
                                            lives = userProgress.lives,
                                            quests = quests,
                                            eventTimeRemaining = eventTimeRemaining,
                                            onPlayCurrentLevel = { gameViewModel.startGame(userProgress.currentLevel) },
                                            onShowDailyRewards = { gameViewModel.showOverlay(ActiveOverlay.DAILY_REWARDS) },
                                            onShowLuckyWheel = { gameViewModel.showOverlay(ActiveOverlay.LUCKY_WHEEL) },
                                            onShowShop = { gameViewModel.showOverlay(ActiveOverlay.SHOP) },
                                            onShowLeaderboard = { gameViewModel.showOverlay(ActiveOverlay.LEADERBOARD) },
                                            onShowAchievements = { gameViewModel.showOverlay(ActiveOverlay.ACHIEVEMENTS) },
                                            onShowProfile = { gameViewModel.showOverlay(ActiveOverlay.PLAYER_PROFILE) },
                                            onShowSettings = { gameViewModel.showOverlay(ActiveOverlay.SETTINGS) },
                                            onShowLevels = { gameViewModel.setScreen(GameScreen.LEVEL_SELECTION) },
                                            onShowRecipeBook = { gameViewModel.showOverlay(ActiveOverlay.RECIPE_BOOK) },
                                            onShowDailyQuests = { gameViewModel.showOverlay(ActiveOverlay.DAILY_QUESTS) },
                                            onClaimQuest = { gameViewModel.claimQuest(it) }
                                        )
                                    }
                                    GameScreen.WORLD_SELECTION -> {
                                        WorldSelectionScreen(
                                            currentLevel = userProgress.currentLevel,
                                            claimedSectionRewards = userProgress.claimedSectionRewards,
                                            onClaimSectionReward = { gameViewModel.claimSectionReward(it) },
                                            onWorldSelect = { startLvl -> gameViewModel.startGame(startLvl) },
                                            onBack = { gameViewModel.setScreen(GameScreen.HOME) }
                                        )
                                    }
                                    GameScreen.LEVEL_SELECTION -> {
                                        LevelSelectionScreen(
                                            currentLevel = userProgress.currentLevel,
                                            onLevelSelect = { lvl -> gameViewModel.startGame(lvl) },
                                            onBack = { gameViewModel.setScreen(GameScreen.HOME) }
                                        )
                                    }
                                    GameScreen.GAMEPLAY -> {
                                        GameplayScreen(
                                            levelNumber = currentLevel,
                                            tubes = tubes,
                                            selectedTubeIndex = selectedTubeIndex,
                                            movesCount = movesCount,
                                            maxMoves = maxMoves,
                                            timerSeconds = timerSeconds,
                                            extraTubeAdded = extraTubeAdded,
                                            activePour = activePour,
                                            onCompletePour = { gameViewModel.completePour() },
                                            skinType = userProgress.equippedTube,
                                            onSelectTube = { gameViewModel.selectTube(it) },
                                            onUndo = { gameViewModel.undoMove() },
                                            onRestart = { gameViewModel.restartLevel() },
                                            onAddTube = { gameViewModel.addExtraTube() },
                                            onSkipLevel = { gameViewModel.skipLevel() },
                                            onPause = { gameViewModel.pauseGame() },
                                            completedTubeAnimationIndex = completedTubeAnimationIndex,
                                            onClearCompletedTubeAnimation = { gameViewModel.clearCompletedTubeAnimation() },
                                            activePowerUp = activePowerUp,
                                            frozenTubeIndices = frozenTubeIndices,
                                            volatileTubeIndex = volatileTubeIndex,
                                            volatileMovesLeft = volatileMovesLeft,
                                            portalPairs = portalTubePairs,
                                            crystalLockTubeIndex = crystalLockTubeIndex,
                                            onToggleSwapper = { gameViewModel.togglePowerUp(PowerUpType.SWAPPER) },
                                            onToggleCatalyst = { gameViewModel.togglePowerUp(PowerUpType.CATALYST) }
                                        )
                                    }
                                    GameScreen.TUTORIAL -> {
                                        // Standard screens handle tutorials reactively
                                    }
                                }
                            }

                            // --- ACTIVE MODAL DIALOGS OVERLAYS ---
                            when (activeOverlay) {
                                ActiveOverlay.NONE -> {}
                                
                                ActiveOverlay.PAUSE_MENU -> {
                                    PauseMenuOverlay(
                                        onResume = { gameViewModel.resumeGame() },
                                        onRestart = { gameViewModel.restartLevel() },
                                        onHome = {
                                            gameViewModel.dismissOverlay()
                                            gameViewModel.setScreen(GameScreen.HOME)
                                        },
                                        isSoundEnabled = userProgress.isSoundEnabled,
                                        isMusicEnabled = userProgress.isMusicEnabled,
                                        onToggleSound = { gameViewModel.toggleSound() },
                                        onToggleMusic = { gameViewModel.toggleMusic() }
                                    )
                                }
                                
                                ActiveOverlay.VICTORY -> {
                                    VictoryOverlay(
                                        levelJustCleared = currentLevel - 1,
                                        coinsEarned = coinsEarned,
                                        gemsEarned = gemsEarned,
                                        onNextLevel = {
                                            gameViewModel.dismissOverlay()
                                            gameViewModel.startGame(currentLevel)
                                        },
                                        onReplay = {
                                            gameViewModel.dismissOverlay()
                                            gameViewModel.startGame(currentLevel - 1)
                                        },
                                        stars = victoryStars,
                                        isVibrationEnabled = userProgress.isVibrationEnabled
                                    )
                                }

                                ActiveOverlay.LEVEL_SET_COMPLETE -> {
                                    val sec = completedSection ?: com.example.ui.game.LevelSectionsList.first()
                                    com.example.ui.game.LevelSetMilestoneOverlay(
                                        section = sec,
                                        isVibrationEnabled = userProgress.isVibrationEnabled,
                                        onClaim = {
                                            gameViewModel.dismissOverlay()
                                            gameViewModel.startGame(currentLevel)
                                        }
                                    )
                                }
                                
                                ActiveOverlay.AD_VIDEO -> {
                                    AdOverlay(
                                        onClose = {
                                            gameViewModel.dismissAd()
                                        }
                                    )
                                }
                                
                                ActiveOverlay.GAME_OVER -> {
                                    GameOverOverlay(
                                        onUndo = {
                                            gameViewModel.dismissOverlay()
                                            gameViewModel.undoMove()
                                        },
                                        onRestart = {
                                            gameViewModel.dismissOverlay()
                                            gameViewModel.restartLevel()
                                        },
                                        onSkip = {
                                            gameViewModel.dismissOverlay()
                                            gameViewModel.skipLevel()
                                        },
                                        onHome = {
                                            gameViewModel.dismissOverlay()
                                            gameViewModel.setScreen(GameScreen.HOME)
                                        }
                                    )
                                }
                                
                                ActiveOverlay.DAILY_REWARDS -> {
                                    DailyRewardsOverlay(
                                        currentDayIndex = userProgress.dailyRewardDayIndex,
                                        lastClaimedTime = userProgress.dailyRewardLastClaimed,
                                        onClaim = { gameViewModel.claimDailyReward() },
                                        onDismiss = { gameViewModel.dismissOverlay() }
                                    )
                                }
                                
                                ActiveOverlay.LUCKY_WHEEL -> {
                                    LuckyWheelOverlay(
                                        isSpinning = isSpinning,
                                        rotationAngle = wheelRotationAngle,
                                        onSpin = { gameViewModel.spinLuckyWheel() },
                                        onDismiss = { gameViewModel.dismissOverlay() }
                                    )
                                }
                                
                                ActiveOverlay.SHOP -> {
                                    ShopOverlay(
                                        coins = userProgress.coins,
                                        gems = userProgress.gems,
                                        unlockedThemes = userProgress.unlockedThemes,
                                        equippedTheme = userProgress.equippedTheme,
                                        unlockedTubes = userProgress.unlockedTubes,
                                        equippedTube = userProgress.equippedTube,
                                        onBuyTheme = { gameViewModel.buyTheme(it) },
                                        onEquipTheme = { gameViewModel.equipTheme(it) },
                                        onBuyTube = { gameViewModel.buyTube(it) },
                                        onEquipTube = { gameViewModel.equipTube(it) },
                                        onDismiss = { gameViewModel.dismissOverlay() }
                                    )
                                }
                                
                                ActiveOverlay.INVENTORY -> {
                                    // Shop doubles as inventory with equip status toggles
                                    gameViewModel.showOverlay(ActiveOverlay.SHOP)
                                }
                                
                                ActiveOverlay.ACHIEVEMENTS -> {
                                    AchievementsOverlay(
                                        completedIds = userProgress.completedAchievements,
                                        onDismiss = { gameViewModel.dismissOverlay() }
                                    )
                                }
                                
                                ActiveOverlay.LEADERBOARD -> {
                                    LeaderboardOverlay(
                                        entries = leaderboardEntries,
                                        onDismiss = { gameViewModel.dismissOverlay() }
                                    )
                                }
                                
                                ActiveOverlay.PLAYER_PROFILE -> {
                                    PlayerProfileOverlay(
                                         selectedAvatarId = userProgress.selectedAvatarId,
                                         onSelectAvatar = { gameViewModel.updateAvatar(it) },
                                        gamesPlayed = userProgress.totalGamesPlayed,
                                        gamesWon = userProgress.totalGamesWon,
                                        bestTime = userProgress.bestTimeSeconds,
                                        currentLevel = userProgress.currentLevel,
                                        onDismiss = { gameViewModel.dismissOverlay() }
                                    )
                                }

                                ActiveOverlay.SETTINGS -> {
                                    SettingsOverlay(
                                        isSoundEnabled = userProgress.isSoundEnabled,
                                        isMusicEnabled = userProgress.isMusicEnabled,
                                        isVibrationEnabled = userProgress.isVibrationEnabled,
                                        fps = userProgress.fpsLimit,
                                        quality = userProgress.graphicsQuality,
                                        currentLevel = userProgress.currentLevel,
                                        unlockedThemes = userProgress.unlockedThemes,
                                        equippedTheme = userProgress.equippedTheme,
                                        unlockedTubes = userProgress.unlockedTubes,
                                        equippedTube = userProgress.equippedTube,
                                        coins = userProgress.coins,
                                        onPlayTutorial = { tid ->
                                            gameViewModel.dismissOverlay()
                                            gameViewModel.triggerTutorial(tid)
                                        },
                                        onToggleSound = { gameViewModel.toggleSound() },
                                        onToggleMusic = { gameViewModel.toggleMusic() },
                                        onToggleVibration = { gameViewModel.toggleVibration() },
                                        onGraphicsSelect = { gameViewModel.updateGraphicsQuality(it) },
                                        onBuyTheme = { themeId -> gameViewModel.buyTheme(themeId) },
                                        onEquipTheme = { themeId -> gameViewModel.equipTheme(themeId) },
                                        onBuyTube = { tubeId -> gameViewModel.buyTube(tubeId) },
                                        onEquipTube = { tubeId -> gameViewModel.equipTube(tubeId) },
                                        onDismiss = { gameViewModel.dismissOverlay() }
                                    )
                                }

                                ActiveOverlay.RECIPE_BOOK -> {
                                    RecipeBookOverlay(
                                        currentLevel = userProgress.currentLevel,
                                        onDismiss = { gameViewModel.dismissOverlay() }
                                    )
                                }

                                ActiveOverlay.DAILY_QUESTS -> {
                                    DailyQuestsOverlay(
                                        quests = quests,
                                        onClaimQuest = { gameViewModel.claimQuest(it) },
                                        onDismiss = { gameViewModel.dismissOverlay() }
                                    )
                                }

                                ActiveOverlay.TUTORIAL -> {
                                    val activeTutorialId by gameViewModel.activeTutorialId.collectAsState()
                                    activeTutorialId?.let { tid ->
                                        TutorialOverlay(
                                            tutorialId = tid,
                                            onDismiss = { gameViewModel.dismissTutorial() }
                                        )
                                    }
                                }

                                ActiveOverlay.LOADING -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.9f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            CircularProgressIndicator(color = ColorSecondary)
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text("Loading Level...", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // --- PREMIUM TOP SLIDING FEEDBACK NOTIFICATION ---
                            AnimatedVisibility(
                                visible = gameFeedback != null,
                                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 80.dp)
                            ) {
                                gameFeedback?.let { msg ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFA1E293B))
                                            .border(1.dp, ColorPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                            .padding(horizontal = 20.dp, vertical = 12.dp)
                                    ) {
                                        Text(
                                            text = msg,
                                            color = ColorAccent,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SETTINGS OVERLAY ---
@Composable
fun SettingsOverlay(
    isSoundEnabled: Boolean,
    isMusicEnabled: Boolean,
    isVibrationEnabled: Boolean,
    fps: Int,
    quality: String,
    currentLevel: Int,
    unlockedThemes: String,
    equippedTheme: String,
    unlockedTubes: String,
    equippedTube: String,
    coins: Int,
    onPlayTutorial: (String) -> Unit,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleVibration: () -> Unit,
    onGraphicsSelect: (String) -> Unit,
    onBuyTheme: (String) -> Unit,
    onEquipTheme: (String) -> Unit,
    onBuyTube: (String) -> Unit,
    onEquipTube: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = General, 1 = Themes, 2 = Bottle Styles

    OverlayDialog(title = "Settings & Personalization", onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Personalization & Settings Tab Selector Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassBg)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("General", "BGM Music", "Themes", "Bottles").forEachIndexed { idx, title ->
                    val isSelected = activeTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ColorPrimary else Color.Transparent)
                            .clickable { activeTab = idx }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            if (activeTab == 0) {
                // --- GENERAL SETTINGS TAB ---
                Text("AUDIO & INPUTS", color = ColorAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassBg)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Effects", color = Color.White)
                    Switch(
                        checked = isSoundEnabled,
                        onCheckedChange = { onToggleSound() },
                        colors = SwitchDefaults.colors(checkedThumbColor = ColorSecondary)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassBg)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ambient Music", color = Color.White)
                    Switch(
                        checked = isMusicEnabled,
                        onCheckedChange = { onToggleMusic() },
                        colors = SwitchDefaults.colors(checkedThumbColor = ColorSecondary)
                    )
                }

                if (isMusicEnabled) {
                    BgmControllerCard(onToggleMusic = onToggleMusic)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassBg)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Vibration", color = Color.White)
                    Switch(
                        checked = isVibrationEnabled,
                        onCheckedChange = { onToggleVibration() },
                        colors = SwitchDefaults.colors(checkedThumbColor = ColorSecondary)
                    )
                }

                // Performance Settings
                Text("PERFORMANCE & GRAPHICS", color = ColorAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassBg)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("FPS Rate Limit", color = Color.White)
                    Text("$fps FPS", color = ColorSecondary, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GlassBg)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Graphics Quality", color = Color.White)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Low", "High").forEach { q ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (quality == q) ColorPrimary else Color.Black.copy(alpha = 0.3f))
                                    .clickable { onGraphicsSelect(q) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(q, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tutorials & Guides
                Text("TUTORIALS & ALCHEMICAL GUIDES", color = ColorAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                listOf(
                    Triple("cyber_city", "1. Cyber City (Sorting Basics)", 1),
                    Triple("emerald_forest", "2. Emerald Forest (Frozen Tubes)", 11),
                    Triple("desert_sunset", "3. Desert Sunset (Volatile Potions)", 31),
                    Triple("frozen_glacier", "4. Frozen Glacier (Mystery Layers)", 61)
                ).forEach { (id, title, reqLvl) ->
                    val isUnlocked = currentLevel >= reqLvl
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isUnlocked) GlassBg else Color.Black.copy(alpha = 0.4f))
                            .border(1.dp, if (isUnlocked) ColorSecondary.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(16.dp))
                            .clickable(enabled = isUnlocked) { onPlayTutorial(id) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, color = if (isUnlocked) Color.White else Color.Gray, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            if (!isUnlocked) {
                                Text("Unlocks at Level $reqLvl", color = Color.Gray.copy(alpha = 0.8f), fontSize = 11.sp)
                            } else {
                                Text("Tap to play interactive guide", color = ColorSecondary, fontSize = 11.sp)
                            }
                        }
                        if (isUnlocked) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play Tutorial", tint = ColorSecondary, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Filled.Lock, contentDescription = "Locked", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            } else if (activeTab == 1) {
                // --- BGM MUSIC TAB ---
                BgmControllerCard(onToggleMusic = onToggleMusic)
            } else if (activeTab == 2) {
                // --- LIQUID COLOR THEMES TAB ---
                Text("LIQUID COLOR THEMES", color = ColorAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                AvailableThemes.forEach { theme ->
                    val isOwned = unlockedThemes.split(",").contains(theme.id)
                    val isEquipped = equippedTheme == theme.id

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isEquipped) ColorSecondary.copy(alpha = 0.15f) else GlassBg)
                            .border(
                                1.dp,
                                if (isEquipped) ColorSecondary else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Theme Preview Dot & Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(theme.primaryBg)
                                    .border(2.dp, theme.accentColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(theme.accentColor)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = theme.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = theme.label,
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Action Button
                        if (isEquipped) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Check, null, tint = Color(0xFF22C55E), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ACTIVE", color = Color(0xFF22C55E), fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        } else if (isOwned) {
                            Button(
                                onClick = { onEquipTheme(theme.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorSecondary),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("EQUIP", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { onBuyTheme(theme.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.MonetizationOn, null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${theme.cost}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // --- BOTTLE & VESSEL STYLES TAB ---
                Text("BOTTLE & VESSEL STYLES", color = ColorAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                AvailableTubes.forEach { tube ->
                    val isOwned = unlockedTubes.split(",").contains(tube.id)
                    val isEquipped = equippedTube == tube.id

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isEquipped) ColorSecondary.copy(alpha = 0.15f) else GlassBg)
                            .border(
                                1.dp,
                                if (isEquipped) ColorSecondary else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .border(1.dp, ColorSecondary, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Science,
                                    contentDescription = null,
                                    tint = ColorSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = tube.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = tube.shapeDescription,
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (isEquipped) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Check, null, tint = Color(0xFF22C55E), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ACTIVE", color = Color(0xFF22C55E), fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        } else if (isOwned) {
                            Button(
                                onClick = { onEquipTube(tube.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorSecondary),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("EQUIP", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { onBuyTube(tube.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.MonetizationOn, null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${tube.cost}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = GlassBorder)
            
            Text(
                text = "Aqua Sort Mobile Game v2.5.0\nDesigned & Developed in 2026",
                color = Color.Gray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
