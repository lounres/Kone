import com.github.gradle.node.npm.task.NpmTask
import com.github.gradle.node.npm.task.NpxTask
import com.github.gradle.node.NodeExtension

plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    id("com.github.node-gradle.node") version "3.4.0"
}

node {
    download.set(true)
    version.set("18.7.0")
}

val siteDirName = "site"
val siteDir: File get() = projectDir.toPath().resolve(siteDirName).toFile()

task<NpmTask>("createDocusaurusInstall") {
    group = "docusaurus"
    args.addAll("install", "create-docusaurus@latest", "-g")
}

val template = "classic"
val useTypeScript = false
val skipInstall = true

task<NpxTask>("create-docusaurus") {
    group = "docusaurus"
    command.set("create-docusaurus@latest")
    args.apply {
        addAll(siteDirName)
        add(template)
        if (useTypeScript) add("--typescript")
        if (skipInstall) add("--skip-install")
    }
}

task<NpmTask>("docusaurusInstall") {
    group = "docusaurus"
    workingDir.set(siteDir)
    args.addAll("install", "--production=false")
}

task<NpxTask>("docusaurusStart") {
    group = "docusaurus"
    workingDir.set(siteDir)
    command.set("docusaurus")
    args.addAll("start")
}