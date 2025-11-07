plugins {
    `java-library`
    alias(libs.plugins.teavm) // order matters?
}

val thisVersion = "0.4.5"

group = "run.slicer"
version = "$thisVersion-${libs.versions.jasm.get()}"
description = "A JavaScript port of the Jasm dis/assembler."

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://teavm.org/maven/repository")
}

dependencies {
    api(libs.jasm.composition.jvm)
    implementation("org.ow2.asm:asm:+") // override transitive dep scope
    compileOnly(libs.teavm.core)
}

java.toolchain {
    languageVersion = JavaLanguageVersion.of(21)
}

teavm.wasmGC {
    mainClass = "run.slicer.jasm.Main"
    modularRuntime = true
    // obfuscated = false
}

tasks {
    register<Copy>("copyDist") {
        group = "build"

        from(
            "README.md", "LICENSE", "LICENSE-JASM", "jasm.js", "jasm.d.ts",
            copyWasmGCRuntime, generateWasmGC
        )
        into("dist")

        duplicatesStrategy = DuplicatesStrategy.INCLUDE

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

    clean {
        delete("dist")
    }
}
