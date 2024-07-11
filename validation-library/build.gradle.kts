import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.net.URI

plugins {
    `maven-publish`
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
    id("net.kyori.blossom") version "2.1.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    configureScalaRepository()
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

val primary = "https://cds.liftgate.io/crypt/production/pubkey"
val secondary = "https://gist.githubusercontent.com/GrowlyX/11b56e42ae82053907c1bfbffb8965fa/raw/088ae86111c397c66c722a6ce0491e90a9374d77/public_key.liftgate.pem"

sourceSets {
    main {
        blossom {
            javaSources {
                val hash = runCatching {
                    URI("https://cds.liftgate.io/crypt/developers/hash")
                        .toURL()
                        .readText()
                }.getOrElse {
                    "invalid-hash"
                }

                property("hash", hash)
                property("secondary", secondary)
                property("primary", primary)
            }
        }
    }
}

publishing {
    if (hasProperty("artifactory_contextUrl"))
    {
        repositories.configureScalaRepository()
    }

    publications {
        register(
            name = "mavenJava",
            type = MavenPublication::class,
            configurationAction = shadow::component
        )
    }
}

tasks {
    withType<ShadowJar> {
        archiveClassifier.set("")

        archiveFileName.set(
            "Oxidator-Validation.jar"
        )
    }
}

fun RepositoryHandler.configureScalaRepository()
{
    maven("${property("artifactory_contextUrl")}/gradle-release") {
        name = "scala"
        credentials {
            username = "${property("artifactory_user")}"
            password = "${property("artifactory_password")}"
        }
    }
}
