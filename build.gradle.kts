plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")

group = "de.germanminer"
version = providers.environmentVariable("VERSION").getOrElse("1.0.0")

labyMod {
    defaultPackageName = "de.germanminer.addon"

    minecraft {
        registerVersion(versions.toTypedArray()) {
            runs {
                getByName("client") {
                    // devLogin = true
                }
            }
        }
    }

    addonInfo {
        namespace = "germanminer"
        displayName = "GermanMiner"
        author = "Funboyy, JensausNGL"
        description = "Unofficial addon to improve various functions on the real-life server GermanMiner"
        minecraftVersion = "1.12.2"
        version = rootProject.version.toString()
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")

    group = rootProject.group
    version = rootProject.version
}
