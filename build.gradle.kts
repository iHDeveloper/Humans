plugins {
    java
    kotlin("jvm") version "1.4.10"
}

group = "me.ihdeveloper.humans"
version = "0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")
}
