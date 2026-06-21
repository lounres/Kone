gradlePlugin {
    plugins {
        register("kotlinCompilerPluginWrapper") {
            id = "${properties["group"]}.${extra["artifactId"]}"
//            displayName = ""
//            description = ""
//            tags = listOf()
            implementationClass = "dev.lounres.kone.plugin.suppliedTypes.gradle.SuppliedTypesPlugin"
        }
    }
}