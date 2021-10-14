import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
    kotlin("plugin.jpa") version "1.5.31"
    id("com.google.cloud.tools.jib") version "3.1.4"
}

group = "ru.twoshoes"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    // Spring data
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate:hibernate-validator:6.1.5.Final")

    // Database migration
    implementation("org.liquibase:liquibase-core:4.4.3")

    // Spring web
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Quartz
    implementation("org.springframework.boot:spring-boot-starter-quartz")

    // Json
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // macOS netty fix
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.69.Final:osx-aarch_64")

    // ArrowKt
    implementation("io.arrow-kt:arrow-core:1.0.0")

    // Postgres
    runtimeOnly("org.postgresql:postgresql")

	// Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {

    from {
        image = "pi41:5000/java-base-image:11-jre"
    }

    to {
        image = "pi41:5000/game-picker/backend:latest"
    }
    container {
        this.mainClass = "ru.gamepicker.gamepicker.GamepickerApplicationKt"

        user = "nobody"

//        creationTime = "$rootProject.commitTime"

//        environment = [
//            "IMAGE_VERSION": "latest",
//            "IMAGE_REVISION": "",
//        ]

//        labels = [
//            "org.opencontainers.image.created": rootProject.commitTime as String,
//            "org.opencontainers.image.version": imageVersion,
//            "org.opencontainers.image.revision": rootProject.commitHash as String,
//        ]
    }

    setAllowInsecureRegistries(true)
}
