import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

buildscript {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
        mavenCentral()
    }
    dependencies {
        classpath("io.jumpco.open:kfsm-viz-plugin:1.0.4-SNAPSHOT")
    }
}
plugins {
    id("org.jetbrains.kotlin.js") version "1.3.61"
}

apply(plugin = "io.jumpco.open.kfsm.viz-plugin")

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    target.browser {

    }
    sourceSets["main"].dependencies {
        implementation(kotlin("stdlib-js"))
        implementation("io.jumpco.open:kfsm-js:1.0.2-SNAPSHOT")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.3")
    }
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
        input = file("src/main/kotlin/kfsm/Turnstile.kt")
        isGeneratePlantUml = true // Required default is false
        isGenerateAsciidoc = true // Required default is false
        output = "turnstile"
    }
}
