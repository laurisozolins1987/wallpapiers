package com.example.wallpapiers.data

import com.example.wallpapiers.model.Category
import com.example.wallpapiers.model.Wallpaper
import com.example.wallpapiers.model.WallpaperSrc
import java.util.Locale

object LocalWallpaperCatalog {

    val categories = listOf(
        Category("All", buildCategoryUrl("https://images.pexels.com/photos/32237/pexels-photo.jpg"), query = "wallpaper", pixabayCategory = null),
        Category("Nature", buildCategoryUrl("https://images.pexels.com/photos/15286/pexels-photo.jpg"), query = "nature wallpaper", pixabayCategory = "nature"),
        Category("AMOLED", buildCategoryUrl("https://images.pexels.com/photos/1341279/pexels-photo-1341279.jpeg"), query = "dark black amoled wallpaper", pixabayCategory = "backgrounds"),
        Category("Abstract", buildCategoryUrl("https://images.pexels.com/photos/2110951/pexels-photo-2110951.jpeg"), query = "abstract wallpaper", pixabayCategory = "backgrounds"),
        Category("Ocean", buildCategoryUrl("https://images.pexels.com/photos/1001682/pexels-photo-1001682.jpeg"), query = "ocean wallpaper", pixabayCategory = "nature"),
        Category("Wildlife", buildCategoryUrl("https://images.pexels.com/photos/47547/squirrel-animal-cute-rodent-47547.jpeg"), query = "wildlife wallpaper", pixabayCategory = "animals"),
        Category("Architecture", buildCategoryUrl("https://images.pexels.com/photos/186077/pexels-photo-186077.jpeg"), query = "architecture wallpaper", pixabayCategory = "buildings"),
        Category("Minimal", buildCategoryUrl("https://images.pexels.com/photos/2457284/pexels-photo-2457284.jpeg"), query = "minimal wallpaper", pixabayCategory = "backgrounds"),
        Category("Space", buildCategoryUrl("https://images.pexels.com/photos/41951/solar-system-planets-neptune-mars-41951.jpeg"), query = "space wallpaper", pixabayCategory = "science"),
        Category("Cars", buildCategoryUrl("https://images.pexels.com/photos/116675/pexels-photo-116675.jpeg"), query = "car wallpaper", pixabayCategory = "transportation"),
        Category("Travel", buildCategoryUrl("https://images.pexels.com/photos/346885/pexels-photo-346885.jpeg"), query = "travel wallpaper", pixabayCategory = "travel"),
        Category("Technology", buildCategoryUrl("https://images.pexels.com/photos/356056/pexels-photo-356056.jpeg"), query = "technology wallpaper", pixabayCategory = "computer")
    )

    val wallpapers = listOf(
        // ── Nature ──
        wallpaper(id = 32237, photographer = "Pixabay", title = "Summit lines for deep focus", collection = "Nature", baseUrl = "https://images.pexels.com/photos/32237/pexels-photo.jpg"),
        wallpaper(id = 15286, photographer = "Pixabay", title = "Mountain air in 4K", collection = "Nature", baseUrl = "https://images.pexels.com/photos/15286/pexels-photo.jpg"),
        wallpaper(id = 3408744, photographer = "Arnie Watkins", title = "Golden hour forest canopy", collection = "Nature", baseUrl = "https://images.pexels.com/photos/3408744/pexels-photo-3408744.jpeg"),
        wallpaper(id = 2662116, photographer = "Jaime Reimer", title = "River valley morning fog", collection = "Nature", baseUrl = "https://images.pexels.com/photos/2662116/pexels-photo-2662116.jpeg"),
        wallpaper(id = 3225517, photographer = "Michael Block", title = "Wildflower meadow at dusk", collection = "Nature", baseUrl = "https://images.pexels.com/photos/3225517/pexels-photo-3225517.jpeg"),
        wallpaper(id = 1287145, photographer = "Eberhard Grossgasteiger", title = "Alpine lake reflections", collection = "Nature", baseUrl = "https://images.pexels.com/photos/1287145/pexels-photo-1287145.jpeg"),
        wallpaper(id = 1366919, photographer = "Juan Salamanca", title = "Tropical waterfall cascade", collection = "Nature", baseUrl = "https://images.pexels.com/photos/1366919/pexels-photo-1366919.jpeg"),
        wallpaper(id = 1172253, photographer = "Stephan Seeber", title = "Autumn leaves on glassy lake", collection = "Nature", baseUrl = "https://images.pexels.com/photos/1172253/pexels-photo-1172253.jpeg"),

        // ── AMOLED ──
        wallpaper(id = 1341279, photographer = "Aron Visuals", title = "Pure black smoke tendrils", collection = "AMOLED", baseUrl = "https://images.pexels.com/photos/1341279/pexels-photo-1341279.jpeg"),
        wallpaper(id = 924824, photographer = "Jill Burrow", title = "Dark neon glow lines", collection = "AMOLED", baseUrl = "https://images.pexels.com/photos/924824/pexels-photo-924824.jpeg"),
        wallpaper(id = 1229042, photographer = "Sohel Patel", title = "Black water droplets macro", collection = "AMOLED", baseUrl = "https://images.pexels.com/photos/1229042/pexels-photo-1229042.jpeg"),
        wallpaper(id = 3075993, photographer = "Brady Knoll", title = "Light trails on black", collection = "AMOLED", baseUrl = "https://images.pexels.com/photos/3075993/pexels-photo-3075993.jpeg"),
        wallpaper(id = 1168940, photographer = "Mudassir Ali", title = "Dark moody thunderstorm", collection = "AMOLED", baseUrl = "https://images.pexels.com/photos/1168940/pexels-photo-1168940.jpeg"),
        wallpaper(id = 2469122, photographer = "Dom J", title = "Abstract AMOLED black and red", collection = "AMOLED", baseUrl = "https://images.pexels.com/photos/2469122/pexels-photo-2469122.jpeg"),

        // ── Abstract ──
        wallpaper(id = 2110951, photographer = "Matheus Bertelli", title = "Soft geometry for clean setups", collection = "Abstract", baseUrl = "https://images.pexels.com/photos/2110951/pexels-photo-2110951.jpeg"),
        wallpaper(id = 2693212, photographer = "Codioful", title = "Gradient wave in warm tones", collection = "Abstract", baseUrl = "https://images.pexels.com/photos/2693212/pexels-photo-2693212.jpeg"),
        wallpaper(id = 1762851, photographer = "Jill Burrow", title = "Fluid neon swirl", collection = "Abstract", baseUrl = "https://images.pexels.com/photos/1762851/pexels-photo-1762851.jpeg"),
        wallpaper(id = 3109816, photographer = "Digital Buggu", title = "Digital particle tunnel", collection = "Abstract", baseUrl = "https://images.pexels.com/photos/3109816/pexels-photo-3109816.jpeg"),
        wallpaper(id = 1939485, photographer = "Anni Roenkae", title = "Colorful smoke layers", collection = "Abstract", baseUrl = "https://images.pexels.com/photos/1939485/pexels-photo-1939485.jpeg"),
        wallpaper(id = 2832382, photographer = "Martin Péchy", title = "Ink diffusion in water", collection = "Abstract", baseUrl = "https://images.pexels.com/photos/2832382/pexels-photo-2832382.jpeg"),

        // ── Ocean ──
        wallpaper(id = 1001682, photographer = "Riccardo", title = "Ocean glow for lock screens", collection = "Ocean", baseUrl = "https://images.pexels.com/photos/1001682/pexels-photo-1001682.jpeg"),
        wallpaper(id = 1032650, photographer = "Emiliano Arano", title = "Crashing blue wave", collection = "Ocean", baseUrl = "https://images.pexels.com/photos/1032650/pexels-photo-1032650.jpeg"),
        wallpaper(id = 1295138, photographer = "Matt Hardy", title = "Sunset over coral reef", collection = "Ocean", baseUrl = "https://images.pexels.com/photos/1295138/pexels-photo-1295138.jpeg"),
        wallpaper(id = 1533720, photographer = "Kellie Churchman", title = "Turquoise shore from above", collection = "Ocean", baseUrl = "https://images.pexels.com/photos/1533720/pexels-photo-1533720.jpeg"),
        wallpaper(id = 2104152, photographer = "Asad Photo", title = "Deep sea blue horizon", collection = "Ocean", baseUrl = "https://images.pexels.com/photos/2104152/pexels-photo-2104152.jpeg"),
        wallpaper(id = 1480807, photographer = "Pixabay", title = "Lighthouse on rocky coast", collection = "Ocean", baseUrl = "https://images.pexels.com/photos/1480807/pexels-photo-1480807.jpeg"),

        // ── Wildlife ──
        wallpaper(id = 47547, photographer = "Pixabay", title = "Wildlife close-up with crisp detail", collection = "Wildlife", baseUrl = "https://images.pexels.com/photos/47547/squirrel-animal-cute-rodent-47547.jpeg"),
        wallpaper(id = 247502, photographer = "Pixabay", title = "Majestic lion portrait", collection = "Wildlife", baseUrl = "https://images.pexels.com/photos/247502/pexels-photo-247502.jpeg"),
        wallpaper(id = 56733, photographer = "Pixabay", title = "Eagle soaring over mountains", collection = "Wildlife", baseUrl = "https://images.pexels.com/photos/56733/pexels-photo-56733.jpeg"),
        wallpaper(id = 45853, photographer = "Pixabay", title = "Deer in misty forest", collection = "Wildlife", baseUrl = "https://images.pexels.com/photos/45853/grey-crowned-crane-bird-crane-animal-45853.jpeg"),
        wallpaper(id = 162140, photographer = "Pixabay", title = "Colorful parrot feathers", collection = "Wildlife", baseUrl = "https://images.pexels.com/photos/162140/duckling-birds-yellow-fluffy-162140.jpeg"),
        wallpaper(id = 2295744, photographer = "Magda Ehlers", title = "Tropical butterfly on flower", collection = "Wildlife", baseUrl = "https://images.pexels.com/photos/2295744/pexels-photo-2295744.jpeg"),

        // ── Architecture ──
        wallpaper(id = 186077, photographer = "Pixabay", title = "Architectural depth and symmetry", collection = "Architecture", baseUrl = "https://images.pexels.com/photos/186077/pexels-photo-186077.jpeg"),
        wallpaper(id = 256150, photographer = "Pixabay", title = "Modern glass tower reflection", collection = "Architecture", baseUrl = "https://images.pexels.com/photos/256150/pexels-photo-256150.jpeg"),
        wallpaper(id = 2404843, photographer = "Palu Malerba", title = "Spiral staircase from above", collection = "Architecture", baseUrl = "https://images.pexels.com/photos/2404843/pexels-photo-2404843.jpeg"),
        wallpaper(id = 830891, photographer = "Fancycrave", title = "Ancient ruins at golden hour", collection = "Architecture", baseUrl = "https://images.pexels.com/photos/830891/pexels-photo-830891.jpeg"),
        wallpaper(id = 1105766, photographer = "Essow K", title = "Urban skyline at night", collection = "Architecture", baseUrl = "https://images.pexels.com/photos/1105766/pexels-photo-1105766.jpeg"),
        wallpaper(id = 2119714, photographer = "Tom Fisk", title = "Aerial view of cityscape", collection = "Architecture", baseUrl = "https://images.pexels.com/photos/2119714/pexels-photo-2119714.jpeg"),

        // ── Minimal ──
        wallpaper(id = 2457284, photographer = "Zaksheuskaya", title = "Minimal layers with calm contrast", collection = "Minimal", baseUrl = "https://images.pexels.com/photos/2457284/pexels-photo-2457284.jpeg"),
        wallpaper(id = 1939500, photographer = "Anni Roenkae", title = "Single leaf on white surface", collection = "Minimal", baseUrl = "https://images.pexels.com/photos/1939500/pexels-photo-1939500.jpeg"),
        wallpaper(id = 2088170, photographer = "Lucas Pezeta", title = "Clean lines and soft shadow", collection = "Minimal", baseUrl = "https://images.pexels.com/photos/2088170/pexels-photo-2088170.jpeg"),
        wallpaper(id = 3178786, photographer = "Codioful", title = "Pastel gradient with grain", collection = "Minimal", baseUrl = "https://images.pexels.com/photos/3178786/pexels-photo-3178786.jpeg"),
        wallpaper(id = 1629236, photographer = "Tim Mossholder", title = "Paper texture with fold line", collection = "Minimal", baseUrl = "https://images.pexels.com/photos/1629236/pexels-photo-1629236.jpeg"),
        wallpaper(id = 2387793, photographer = "Merlin Lightpainting", title = "Geometric light trace", collection = "Minimal", baseUrl = "https://images.pexels.com/photos/2387793/pexels-photo-2387793.jpeg"),

        // ── Space ──
        wallpaper(id = 41951, photographer = "Pixabay", title = "Space drift for AMOLED screens", collection = "Space", baseUrl = "https://images.pexels.com/photos/41951/solar-system-planets-neptune-mars-41951.jpeg"),
        wallpaper(id = 1169754, photographer = "Philippe Donn", title = "Milky way over desert", collection = "Space", baseUrl = "https://images.pexels.com/photos/1169754/pexels-photo-1169754.jpeg"),
        wallpaper(id = 816608, photographer = "Felix Mittermeier", title = "Aurora borealis night sky", collection = "Space", baseUrl = "https://images.pexels.com/photos/816608/pexels-photo-816608.jpeg"),
        wallpaper(id = 998641, photographer = "Felix Mittermeier", title = "Star trail long exposure", collection = "Space", baseUrl = "https://images.pexels.com/photos/998641/pexels-photo-998641.jpeg"),
        wallpaper(id = 1252890, photographer = "Pixabay", title = "Full moon rising over horizon", collection = "Space", baseUrl = "https://images.pexels.com/photos/1252890/pexels-photo-1252890.jpeg"),
        wallpaper(id = 2150, photographer = "Pixabay", title = "Galaxy cluster in deep space", collection = "Space", baseUrl = "https://images.pexels.com/photos/2150/sky-space-dark-galaxy.jpg"),

        // ── Cars ──
        wallpaper(id = 116675, photographer = "Pixabay", title = "Performance car with night reflections", collection = "Cars", baseUrl = "https://images.pexels.com/photos/116675/pexels-photo-116675.jpeg"),
        wallpaper(id = 3764984, photographer = "Mike Bird", title = "Classic muscle car in red", collection = "Cars", baseUrl = "https://images.pexels.com/photos/3764984/pexels-photo-3764984.jpeg"),
        wallpaper(id = 3874337, photographer = "Cottonbro", title = "Luxury interior dashboard", collection = "Cars", baseUrl = "https://images.pexels.com/photos/3874337/pexels-photo-3874337.jpeg"),
        wallpaper(id = 1402787, photographer = "Adrian Newell", title = "Sports car on mountain road", collection = "Cars", baseUrl = "https://images.pexels.com/photos/1402787/pexels-photo-1402787.jpeg"),
        wallpaper(id = 241316, photographer = "Pixabay", title = "Vintage car close-up grille", collection = "Cars", baseUrl = "https://images.pexels.com/photos/241316/pexels-photo-241316.jpeg"),
        wallpaper(id = 1149137, photographer = "Mike Bird", title = "Supercar in motion blur", collection = "Cars", baseUrl = "https://images.pexels.com/photos/1149137/pexels-photo-1149137.jpeg"),

        // ── Travel ──
        wallpaper(id = 346885, photographer = "JÉSHOOTS", title = "Travel mood for city escapes", collection = "Travel", baseUrl = "https://images.pexels.com/photos/346885/pexels-photo-346885.jpeg"),
        wallpaper(id = 3389536, photographer = "Roberto Nickson", title = "Tropical bungalow sunset view", collection = "Travel", baseUrl = "https://images.pexels.com/photos/3389536/pexels-photo-3389536.jpeg"),
        wallpaper(id = 2104882, photographer = "Sami Anas", title = "Desert dunes golden hour", collection = "Travel", baseUrl = "https://images.pexels.com/photos/2104882/pexels-photo-2104882.jpeg"),
        wallpaper(id = 2356045, photographer = "Trace Hudson", title = "Venice canals in morning light", collection = "Travel", baseUrl = "https://images.pexels.com/photos/2356045/pexels-photo-2356045.jpeg"),
        wallpaper(id = 1458457, photographer = "Flo Maderebner", title = "Mountain hiker at summit", collection = "Travel", baseUrl = "https://images.pexels.com/photos/1458457/pexels-photo-1458457.jpeg"),
        wallpaper(id = 3155666, photographer = "Nextvoyage", title = "Japanese temple in cherry blossoms", collection = "Travel", baseUrl = "https://images.pexels.com/photos/3155666/pexels-photo-3155666.jpeg"),

        // ── Technology ──
        wallpaper(id = 356056, photographer = "Pixabay", title = "Technology desk with neon edges", collection = "Technology", baseUrl = "https://images.pexels.com/photos/356056/pexels-photo-356056.jpeg"),
        wallpaper(id = 1089440, photographer = "Josh Sorenson", title = "Glowing circuit board macro", collection = "Technology", baseUrl = "https://images.pexels.com/photos/1089440/pexels-photo-1089440.jpeg"),
        wallpaper(id = 2582937, photographer = "Canvast Supply", title = "Dark keyboard with RGB lighting", collection = "Technology", baseUrl = "https://images.pexels.com/photos/2582937/pexels-photo-2582937.jpeg"),
        wallpaper(id = 1714208, photographer = "Josh Sorenson", title = "Server rack blue glow", collection = "Technology", baseUrl = "https://images.pexels.com/photos/1714208/pexels-photo-1714208.jpeg"),
        wallpaper(id = 1779487, photographer = "Sebastian Voortman", title = "VR headset futuristic", collection = "Technology", baseUrl = "https://images.pexels.com/photos/1779487/pexels-photo-1779487.jpeg"),
        wallpaper(id = 373543, photographer = "Markus Spiske", title = "Code on dark terminal screen", collection = "Technology", baseUrl = "https://images.pexels.com/photos/373543/pexels-photo-373543.jpeg")
    )

    fun wallpapersForCategory(category: String): List<Wallpaper> {
        if (category.equals("All", ignoreCase = true)) return wallpapers
        return wallpapers.filter { it.collection.equals(category, ignoreCase = true) }
    }

    fun getCategory(name: String): Category? {
        return categories.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    fun search(query: String): List<Wallpaper> {
        val tokens = query.trim()
            .lowercase(Locale.getDefault())
            .split(Regex("\\s+"))
            .filter(String::isNotBlank)
        if (tokens.isEmpty()) return wallpapers

        return wallpapers.filter { wallpaper ->
            val haystack = listOf(
                wallpaper.title.orEmpty(),
                wallpaper.collection.orEmpty(),
                wallpaper.photographer,
                wallpaper.url,
                wallpaper.sourceName,
                wallpaper.sourceUrl.orEmpty(),
                wallpaper.tags.joinToString(" ")
            ).joinToString(" ").lowercase(Locale.getDefault())

            tokens.all(haystack::contains)
        }
    }

    private fun wallpaper(
        id: Int,
        photographer: String,
        title: String,
        collection: String,
        baseUrl: String
    ): Wallpaper {
        return Wallpaper(
            id = "local_$id",
            remoteId = id.toString(),
            provider = com.example.wallpapiers.model.WallpaperProvider.PEXELS,
            width = 1440,
            height = 2560,
            url = baseUrl,
            photographer = photographer,
            src = wallpaperSrc(baseUrl),
            title = title,
            collection = collection,
            downloads = 12000 + (id % 4000),
            likes = 400 + (id % 600),
            views = 250000 + (id % 100000),
            tags = listOf(collection, "wallpaper", "mobile"),
            sourceName = "Curated fallback"
        )
    }

    private fun wallpaperSrc(baseUrl: String): WallpaperSrc {
        return WallpaperSrc(
            original = "$baseUrl?auto=compress&cs=tinysrgb&w=1800",
            large2x = "$baseUrl?auto=compress&cs=tinysrgb&w=1600",
            large = "$baseUrl?auto=compress&cs=tinysrgb&w=1280",
            medium = "$baseUrl?auto=compress&cs=tinysrgb&w=960",
            small = "$baseUrl?auto=compress&cs=tinysrgb&w=640",
            portrait = "$baseUrl?auto=compress&cs=tinysrgb&fit=crop&w=1080&h=1920",
            landscape = "$baseUrl?auto=compress&cs=tinysrgb&fit=crop&w=1600&h=900",
            tiny = "$baseUrl?auto=compress&cs=tinysrgb&w=360"
        )
    }

    private fun buildCategoryUrl(baseUrl: String): String {
        return "$baseUrl?auto=compress&cs=tinysrgb&w=600"
    }
}
