
plugins {
    kotlin("jvm") version "1.9.0"
    `java-library`
}

group = "com.seunome"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}
