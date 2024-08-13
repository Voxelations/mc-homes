rootProject.name = "mc-homes"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.voxelations.com/public")
    }
}

include("api")
include("paper")