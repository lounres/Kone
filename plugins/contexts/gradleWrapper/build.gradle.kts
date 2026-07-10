gradlePlugin {
    plugins {
        register("kotlinCompilerPluginWrapper") {
            id = "${extra["koneGroup"]}.${extra["artifactId"]}"
//            displayName = ""
//            description = ""
//            tags = listOf()
            implementationClass = "dev.lounres.kone.plugin.contexts.gradle.ContextsPlugin"
        }
    }
}