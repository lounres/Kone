rootProject.name = "Kone-project"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.kotlin.link")
    }
}

operator fun File.invoke(block: File.() -> Unit): Unit = block()
fun File.dir(name: String): File = resolve(name)
fun File.dir(name: String, block: File.() -> Unit): Unit = resolve(name).block()
fun File.forEachDir(block: (File) -> Unit) = listFiles()?.forEach(block)
fun include(file: File) {
    val thisFileNames = file.toPath().toList()
    val rootFileLength = rootDir.toPath().toList().size
    include(thisFileNames.subList(rootFileLength, thisFileNames.size).joinToString(separator = ":"))
}
fun File.includeSubDirs() {
    listFiles { file -> file.isDirectory }?.forEach { include(it) }
}

rootDir {
    dir("kone") {
        includeSubDirs()
        dir("misc").includeSubDirs()
    }
    dir("utils").includeSubDirs()
}

include(rootDir.listFiles { file -> file.isDirectory && file.name.startsWith("kone-", ignoreCase = true) }!!.map { it.name })
include(
    "docs",
    "mapUtil",
    "testUtil"
)