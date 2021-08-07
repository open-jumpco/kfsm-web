import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin("js") version "1.5.10"
    id("io.jumpco.open.kfsm.viz-plugin") version "1.5.0"
}

group = "io.jumpco.open.kfsm.example"
version = "1.5.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    js {
        browser {

        }
        binaries.executable()
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.5.0")
    implementation("io.jumpco.open:kfsm-js:1.5.1")
}

tasks {
    named<Kotlin2JsCompile>("compileKotlinJs") {
        kotlinOptions {
            moduleKind = "umd"
        }
    }
    register<Copy>("copyAssets") {
        dependsOn("browserWebpack")
        from("$buildDir/processedResources/Js/main")
        from("$buildDir/distributions")
        into("$buildDir/dist")
    }

}
val assemble by tasks.existing {
    dependsOn("copyAssets")
    dependsOn("generateFsmViz")
}

configure<io.jumpco.open.kfsm.gradle.VizPluginExtension> {
    fsm("TurnstileFSM") {
        outputFolder = file("generated")
        input = file("src/main/kotlin/com/example/kfsm/Turnstile.kt")
        isGeneratePlantUml = true // Required default is false
        isGenerateAsciidoc = true // Required default is false
        output = "turnstile"
    }
}
