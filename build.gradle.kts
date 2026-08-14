import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        androidStudio("2026.1.2.10")
        bundledPlugin("org.jetbrains.kotlin")
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatformTesting {
    runIde {
        register("runIdeClean") {
            plugins {
                disablePlugin("com.google.tools.ij.aiplugin")   // Gemini AI
                disablePlugin("com.google.services.firebase")   // Firebase
            }
        }
    }
}
changelog {
    version.set("0.1.0")
    headerParserRegex.set("""(\d+\.\d+\.\d+)""".toRegex())
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
}
tasks {
    patchPluginXml {
        changeNotes.set(provider {
            changelog.renderItem(
                changelog.getUnreleased(),
                org.jetbrains.changelog.Changelog.OutputType.HTML
            )
        })
    }
}