package com.example.ui.game

import androidx.compose.ui.graphics.Color

// Theme Configuration
data class GameTheme(
    val id: String,
    val name: String,
    val cost: Int,
    val isPremium: Boolean,
    val primaryBg: Color,
    val secondaryBg: Color,
    val tubeBorder: Color,
    val liquidGlowColor: Color,
    val accentColor: Color,
    val label: String
)

// List of Themes
val AvailableThemes = listOf(
    GameTheme(
        id = "cyberpunk",
        name = "Cyberpunk",
        cost = 0,
        isPremium = false,
        primaryBg = Color(0xFF090E17),
        secondaryBg = Color(0xFF161E2E),
        tubeBorder = Color(0x6600C2A8),
        liquidGlowColor = Color(0xAA6C63FF),
        accentColor = Color(0xFF00C2A8),
        label = "Default futuristic neon layout"
    ),
    GameTheme(
        id = "forest",
        name = "Emerald Forest",
        cost = 300,
        isPremium = false,
        primaryBg = Color(0xFF061A0C),
        secondaryBg = Color(0xFF0E2E19),
        tubeBorder = Color(0x6622C55E),
        liquidGlowColor = Color(0xAA22C55E),
        accentColor = Color(0xFF22C55E),
        label = "Soothe your mind under emerald boughs"
    ),
    GameTheme(
        id = "desert",
        name = "Desert Sunset",
        cost = 500,
        isPremium = false,
        primaryBg = Color(0xFF1C1105),
        secondaryBg = Color(0xFF331E0D),
        tubeBorder = Color(0x66FFB703),
        liquidGlowColor = Color(0xAAFFB703),
        accentColor = Color(0xFFFFB703),
        label = "Warm sands and golden dunes"
    ),
    GameTheme(
        id = "snow",
        name = "Frozen Glacier",
        cost = 800,
        isPremium = false,
        primaryBg = Color(0xFF0B141C),
        secondaryBg = Color(0xFF1B2C3C),
        tubeBorder = Color(0x6638BDF8),
        liquidGlowColor = Color(0xAA38BDF8),
        accentColor = Color(0xFF38BDF8),
        label = "Frosty crystalline breeze"
    ),
    GameTheme(
        id = "space",
        name = "Nebula Stardust",
        cost = 1000,
        isPremium = true,
        primaryBg = Color(0xFF07040E),
        secondaryBg = Color(0xFF150D27),
        tubeBorder = Color(0x66D946EF),
        liquidGlowColor = Color(0xAAD946EF),
        accentColor = Color(0xFFD946EF),
        label = "Premium cosmic outer-rim space layout"
    ),
    GameTheme(
        id = "volcano",
        name = "Molten Volcano",
        cost = 1200,
        isPremium = true,
        primaryBg = Color(0xFF140505),
        secondaryBg = Color(0xFF2E0C0C),
        tubeBorder = Color(0x66EF4444),
        liquidGlowColor = Color(0xAAEF4444),
        accentColor = Color(0xFFEF4444),
        label = "Pure lava flow and raw heat"
    )
)

// Tube Skin Configuration
data class TubeSkin(
    val id: String,
    val name: String,
    val cost: Int,
    val isPremium: Boolean,
    val shapeDescription: String
)

val AvailableTubes = listOf(
    TubeSkin("default", "Classic Cylinder", 0, false, "Standard glass cylinder with rounded bottom"),
    TubeSkin("beaker", "Hex Beaker", 250, false, "Ergonomic chemistry beaker flask layout"),
    TubeSkin("crystal", "Prismatic Crystal", 500, false, "Chiseled gemstone-faceted glass tube"),
    TubeSkin("futuristic", "Hyper-Chamber", 1000, true, "Cybernetic energy containment tank")
)

// Achievement Item
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val targetValue: Int,
    val rewardCoins: Int,
    val rewardGems: Int
)

val AchievementsList = listOf(
    Achievement("first_level", "First Drops", "Successfully sort your first level", 1, 50, 10),
    Achievement("levels_10", "Rising Chemist", "Complete 10 sort puzzles", 10, 200, 30),
    Achievement("levels_50", "Grand Alchemist", "Complete 50 sort puzzles", 50, 1000, 150),
    Achievement("spend_coins", "Wealth Spender", "Spend 500 coins in the Shop", 500, 150, 15),
    Achievement("claim_daily", "Consistent Player", "Claim daily rewards 3 times", 3, 100, 20),
    Achievement("spin_wheel", "Lucky Sort", "Spin the Lucky Wheel 5 times", 5, 250, 30)
)

// Liquid Gradients Definitions
data class LiquidColor(
    val id: Int,
    val name: String,
    val startColor: Color,
    val endColor: Color,
    val glowColor: Color
)

val LiquidColors = listOf(
    LiquidColor(1, "Neon Blue", Color(0xFF00F0FF), Color(0xFF0072FF), Color(0x4400F0FF)),
    LiquidColor(2, "Hot Pink", Color(0xFFFF007A), Color(0xFF85A7), Color(0x44FF007A)),
    LiquidColor(3, "Bright Yellow", Color(0xFFFFB703), Color(0xFFD97706), Color(0x44FFB703)),
    LiquidColor(4, "Neon Green", Color(0xFF00C2A8), Color(0xFF22C55E), Color(0x4400C2A8)),
    LiquidColor(5, "Acid Purple", Color(0xFF9000FF), Color(0xFFFF00FF), Color(0x449000FF)),
    LiquidColor(6, "Warm Orange", Color(0xFFFF4D00), Color(0xFFEA580C), Color(0x44FF4D00)),
    LiquidColor(7, "Crimson Red", Color(0xFFEF4444), Color(0xFF991B1B), Color(0x44EF4444)),
    LiquidColor(8, "Frozen Mint", Color(0xFF38BDF8), Color(0xFF0369A1), Color(0x4438BDF8))
)

enum class PowerUpType {
    NONE,
    SWAPPER,
    CATALYST
}

data class LevelSection(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val startLevel: Int,
    val endLevel: Int,
    val rewardCoins: Int,
    val rewardGems: Int,
    val rewardTitle: String
)

val LevelSectionsList = listOf(
    LevelSection("sec_1", "Novice Alchemist", "Levels 1 – 5", "Master basic color sorting fundamentals.", 1, 5, 300, 20, "Novice Chemist Badge"),
    LevelSection("sec_2", "Apprentice Sorcerer", "Levels 6 – 20", "Complex multi-color solutions and expansion flasks.", 6, 20, 800, 50, "Apprentice Flask Skin"),
    LevelSection("sec_3", "Potion Brewmaster", "Levels 21 – 50", "Volatile Potions requiring tactical stabilization.", 21, 50, 1500, 100, "Desert Sands Theme"),
    LevelSection("sec_4", "Mist & Shadows", "Levels 51 – 100", "Hidden Mystery Layers lurking beneath mist.", 51, 100, 3000, 200, "Shadow Crystal Skin"),
    LevelSection("sec_5", "Elemental Forge", "Levels 101 – 180", "Multi-tiered color catalysts and resonance chambers.", 101, 180, 5000, 350, "Golden Alchemy Frame"),
    LevelSection("sec_6", "Astral Laboratory", "Levels 181 – 300", "Harmonize cosmic energy blends across high-capacity tubes.", 181, 300, 8000, 500, "Nebula Stardust Theme"),
    LevelSection("sec_7", "Grand Archmage Sanctum", "Levels 301 – 500", "Master-tier trials for Alchemical Supreme Sorcerers.", 301, 500, 12000, 800, "Archmage Crown Title"),
    LevelSection("sec_8", "Infinite Transcendent Realm", "Levels 501+", "Infinite procedural master puzzles for endless daily play.", 501, 999999, 20000, 1500, "Transcendent Entity")
)

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    val current: Int,
    val rewardCoins: Int,
    val rewardGems: Int,
    val isClaimed: Boolean
)

data class PotionRecipe(
    val id: String,
    val name: String,
    val description: String,
    val color1: LiquidColor,
    val color2: LiquidColor,
    val unlockLevel: Int
)

val AlchemicalRecipes = listOf(
    PotionRecipe("elixir_life", "Elixir of Life", "A glowing restorative tincture representing ultimate cellular longevity.", LiquidColors[6], LiquidColors[0], 1),
    PotionRecipe("aqua_regia", "Aqua Regia", "A highly corrosive, shimmering solvent that can dissolve even the purest of gold.", LiquidColors[7], LiquidColors[3], 2),
    PotionRecipe("philosopher_tears", "Philosopher's Tears", "The legendary liquid catalyst for transmutation and spiritual enlightenment.", LiquidColors[6], LiquidColors[4], 3),
    PotionRecipe("astral_dew", "Astral Dew", "Harvested from the condensation of starlight during a lunar eclipse.", LiquidColors[7], LiquidColors[2], 4),
    PotionRecipe("solar_flare", "Solar Flare", "A highly volatile, heat-radiating fluid extracted from localized thermal energy.", LiquidColors[2], LiquidColors[5], 5),
    PotionRecipe("void_essence", "Void Essence", "A deep, light-absorbing fluid that exists on the boundary of gravity.", LiquidColors[4], LiquidColors[0], 6),
    PotionRecipe("everfrost", "Everfrost Draught", "Maintains absolute zero temperature indefinitely. Handle with insulated glassware.", LiquidColors[7], LiquidColors[0], 7),
    PotionRecipe("siren_call", "Siren's Call", "An oceanic elixir that glows in deep waters and emits a soft, enchanting hum.", LiquidColors[3], LiquidColors[0], 8),
    PotionRecipe("phoenix_fire", "Phoenix Fire", "Rejuvenates from the ashes of physical and spiritual exhaustion.", LiquidColors[6], LiquidColors[5], 9),
    PotionRecipe("chrono_fluid", "Chrono-fluid", "Bends the local flow of space-time, allowing brief glimpses of immediate timelines.", LiquidColors[0], LiquidColors[4], 10)
)
