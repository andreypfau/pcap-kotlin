plugins {
    kotlin("multiplatform") version "1.7.20-RC"
    `maven-publish`
}

group = "com.github.andreypfau"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val allTarget = false
    val isMingw = hostOs.startsWith("Windows")
    val isLinux = hostOs.startsWith("Linux")
    val isMacos = hostOs.startsWith("Mac OS")

    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
        }

        if (isMacos) {
            macosX64()
            macosArm64()

            val darwinMain by creating {
                dependsOn(nativeMain)
            }
            val macosMain by creating {
                dependsOn(darwinMain)
            }
            val macosX64Main by getting {
                dependsOn(macosMain)
            }
            val macosArm64Main by getting {
                dependsOn(macosMain)
            }
        }

        if (isMingw || allTarget) {
            mingwX64()

            val mingwMain by creating {
                dependsOn(nativeMain)
            }
            val mingwX64Main by getting {
                dependsOn(mingwMain)
            }
        }

        if (isLinux || allTarget) {
            linuxX64() {
                binaries {
                    executable()
                }
            }
//            linuxArm64()

            val linuxMain by creating {
                dependsOn(nativeMain)
            }
            val linuxX64Main by getting {
                dependsOn(linuxMain)
            }
//            val linuxArm64Main by getting {
//                dependsOn(linuxMain)
//            }
        }

        targets.filterIsInstance(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java).forEach {
            it.compilations {
                getByName("main") {
                    cinterops {
                        val libpcap by creating {
                            defFile(file("src/nativeInterop/cinterop/libpcap.def"))
                        }
                    }
                }
            }
        }
    }
}
