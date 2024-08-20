plugins {
    `java-library`
    alias(libs.plugins.teavm) // order matters?
}

val thisVersion = "0.2.0"

group = "run.slicer"
version = "$thisVersion-${libs.versions.jasm.get()}"
description = "A JavaScript port of the Jasm dis/assembler."

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    api(libs.jasm.composition.jvm)
    implementation("org.ow2.asm:asm:+") // override transitive dep scope
    compileOnly(libs.teavm.core)
}

java.toolchain {
    languageVersion = JavaLanguageVersion.of(21)
}

teavm.js {
    mainClass = "run.slicer.jasm.Main"
    moduleType = org.teavm.gradle.api.JSModuleType.ES2015
    // obfuscated = false
    // optimization = org.teavm.gradle.api.OptimizationLevel.NONE
}

tasks {
    register<Copy>("copyDist") {
        group = "build"

        from("README.md", "LICENSE", "LICENSE-JASM", generateJavaScript, "jasm.d.ts")
        into("dist")

        doLast {
            file("dist/package.json").writeText(
                """
                    {
                      "name": "@run-slicer/jasm",
                      "version": "${project.version}",
                      "description": "A JavaScript port of the Jasm dis/assembler (https://github.com/jumanji144/Jasm).",
                      "main": "jasm.js",
                      "types": "jasm.d.ts",
                      "keywords": [
                        "assembler",
                        "disassembler",
                        "java",
                        "bytecode"
                      ],
                      "author": "run-slicer",
                      "license": "MIT"
                    }
                """.trimIndent()
            )
        }
    }

    build {
        dependsOn("copyDist")
    }
}
