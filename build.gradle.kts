import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.3.21"
}

group = "com.gamasoft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}


val junitVersion = "5.1.1"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))


    testCompile(   "com.willowtreeapps.assertk:assertk-jvm:0.13")
    testCompile(   "org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testCompile(   "org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testCompile(   "org.junit.jupiter:junit-jupiter-params:${junitVersion}")

}



tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}