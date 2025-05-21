// ────────────────────────────────────────────────
// Konfigurasi repositori plugin & dependency
// ────────────────────────────────────────────────
pluginManagement {
    repositories {
        // 1) Google Maven – berisi AGP, AndroidX, Firebase plugin, dsb.
        google()

        // 2) Maven Central – library Java/Kotlin publik
        mavenCentral()

        // 3) Gradle Plugin Portal – plugin komunitas (org.jetbrains.kotlin.android, dst.)
        gradlePluginPortal()

        // 4) JitPack – library dari GitHub (mis. Material-MultiSelection-Spinner)
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    // Pakai repositori yang dideklarasikan di blok ini,
    // bukan di setiap modul (direkomendasikan Gradle 8+)
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

// ────────────────────────────────────────────────
// Deklarasi modul
// ────────────────────────────────────────────────
rootProject.name = "projectakhirpam"
include(":app")
