import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    id("org.jetbrains.kotlin.js") version "1.3.61"
}

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
        implementation("io.jumpco.open:kfsm-js:1.0.1-SNAPSHOT")
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
}
