import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    id("com.google.cloud.tools.jib") version "3.2.1"
}

group = "ru.twoshoes"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    // Spring data
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate:hibernate-validator:7.0.4.Final")

    // Database migration
    implementation("org.liquibase:liquibase-core:4.9.1")

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
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.70.Final:osx-aarch_64")

    // ArrowKt
    implementation("io.arrow-kt:arrow-core:1.1.2")

    // Html parser
    implementation("org.jsoup:jsoup:1.14.3")

    // Postgres
    runtimeOnly("org.postgresql:postgresql")

    // Minio
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("io.minio:minio:8.3.8")
//    implementation("com.jlefebure:spring-boot-starter-minio:1.4")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1-native-mt")
    testImplementation("com.ninja-squad:springmockk:3.1.1")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation")
    testRuntimeOnly("com.h2database:h2:2.1.212")
}

configurations {
    all {
//        exclude(group = "com.jlefebure", module = "io.minio:minio")
        exclude(group = "io.minio", module = "com.squareup.okhttp3:okhttp")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {

    from {
        image = "pi41:5000/java-base-image:17"
    }

    to {
        image = "pi41:5000/game-picker/backend:latest"
    }
    container {
        // this.mainClass = "ru.gamepicker.gamepicker.GamepickerApplicationKt"

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
