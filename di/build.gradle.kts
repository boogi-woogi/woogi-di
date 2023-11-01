plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.8.2")
    testImplementation("org.assertj", "assertj-core", "3.22.0")
    testImplementation("io.mockk:mockk:1.13.5")
    implementation(kotlin("reflect"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}
