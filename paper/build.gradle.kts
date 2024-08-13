plugins {
    alias(libs.plugins.mc.commons.gradle.jvm.conventions)
    alias(libs.plugins.mc.commons.gradle.paper.conventions)
}

dependencies {
    implementation(project(":api"))
    annotationProcessor(libs.mc.commons.common)
    compileOnly(libs.mc.commons.paper)
}
