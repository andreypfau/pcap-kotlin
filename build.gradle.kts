plugins {
    kotlin("multiplatform") version "1.7.20"
    `maven-publish`
}

group = "com.github.andreypfau"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

object NativePlatform {
    val arch = System.getProperty("os.arch")
    val os = System.getProperty("os.name")

    fun isArm() = arch.startsWith("arm") || arch.startsWith("aarch")
    fun isX64() = arch == "x86_64" || arch == "amd64"
    fun isWindows() = os.startsWith("Windows")
    fun isMac() = os.startsWith("Mac") || os.startsWith("Darwin")
    fun isLinux() = os.startsWith("Linux")

    override fun toString(): String = "$os $arch"
}

kotlin {
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
        val mingwMain by creating {
            dependsOn(commonMain)
        }
        if (NativePlatform.isWindows()) {
            mingwX64 {
                val mingwX64Main by getting {
                    dependsOn(mingwMain)
                }
                binaries {
                    sharedLib()
                    staticLib()
                }
                compilations.configureEach {
                    cinterops {
                        val pcap by creating {
                            packageName = "pcap"
                            defFile(file("src/nativeInterop/cinterop/pcap.def"))
                        }
                    }
                }
            }
        }

        if (NativePlatform.isMac()) {
            val darwinMain by creating {
                dependsOn(commonMain)
            }
            val macosMain by creating {
                dependsOn(darwinMain)
            }
            macosX64 {
                val macosX64Main by getting {
                    dependsOn(macosMain)
                }
                binaries {
                    sharedLib()
                    staticLib()
                }
            }
            macosArm64 {
                val macosArm64Main by getting {
                    dependsOn(macosMain)
                }
                binaries {
                    sharedLib()
                    staticLib()
                }
            }
        }

        if (NativePlatform.isLinux()) {
            val linuxMain by creating {
                dependsOn(commonMain)
            }
            linuxX64 {
                val linuxX64Main by getting {
                    dependsOn(linuxMain)
                }
                binaries {
                    sharedLib()
                    staticLib()
                }
                compilations.configureEach {
                    cinterops {
                        val pcap by creating {
                            packageName = "pcap"
                            defFile(file("src/nativeInterop/cinterop/pcap.def"))
                            headers(file("src/nativeInterop/cinterop/pcap/pcap.h"))
                            includeDirs("src/nativeInterop/cinterop/pcap")
                        }
                    }
                }
            }
        }
    }
}
