plugins {
    alias(libs.plugins.jvm)
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

sourceSets {
    main {
        kotlin {
            srcDirs("src/main/")
        }
        resources {
            srcDirs("resources/main/")
        }
    }
    test {
        kotlin {
            srcDirs("src/test/")
        }
        resources {
            srcDirs("resources/test/")
        }
    }
}