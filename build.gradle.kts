import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.openapi.generator") version "5.4.0"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["testcontainersVersion"] = "1.16.2"

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("$projectDir/config/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

tasks.register<GenerateTask>("generateInventoryDTOs") {
    doFirst { project.delete(project.fileTree("$buildDir/generated/src/inventory")) }
    inputSpec.set("$projectDir/src/main/resources/public/inventory-api-specification.yml")
    packageName.set("com.example.warehouse.inventory")
    configOptions.putAll(mapOf("sourceFolder" to "generated/src/inventory"))
}

tasks.register<GenerateTask>("generateInventoryArticleDTOs") {
    doFirst { project.delete(project.fileTree("$buildDir/generated/src/inventory-article")) }
    inputSpec.set("$projectDir/src/main/resources/public/inventory-articles-api-specification.yml")
    packageName.set("com.example.warehouse.inventory.article")
    configOptions.putAll(mapOf("sourceFolder" to "generated/src/inventory-article"))
}

tasks.register("generateOpenApiSpecificationDTOs") {
    dependsOn(
        "generateInventoryDTOs",
        "generateInventoryArticleDTOs"
    )
}

kotlin {
    sourceSets["main"].apply {
        kotlin.srcDirs(
            "$buildDir/generated/src/inventory",
            "$buildDir/generated/src/inventory-article")
    }
}

tasks.withType<KotlinCompile> {
    dependsOn("generateOpenApiSpecificationDTOs")
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<GenerateTask> {
    generatorName.set("kotlin")
    modelNameSuffix.set("Dto")
    outputDir.set("$buildDir")
    globalProperties.putAll(mapOf("models" to "", "modelDocs" to "false", "modelTests" to "false"))
    additionalProperties.putAll(mapOf("removeEnumValuePrefix" to "false"))
    configOptions.putAll(
        mapOf(
            "java8" to "true",
            "dateLibrary" to "java8",
            "serializationLibrary" to "jackson",
            "enumPropertyNaming" to "snake_case",
            "modelPropertyNaming" to "snake_case",
            "collectionType" to "list",
        )
    )
    typeMappings.putAll(mapOf("time" to "java.time.LocalTime"))
}
