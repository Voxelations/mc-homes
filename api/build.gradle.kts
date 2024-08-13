plugins {
    alias(libs.plugins.mc.commons.gradle.jvm.conventions)
    alias(libs.plugins.mc.commons.gradle.publishing.conventions)
}

dependencies {
    compileOnlyApi(libs.mc.commons.common)
}