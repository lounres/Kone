import dev.lounres.kone.buildSrc.dsl.konePlugins

konePlugins {
    +"contexts"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
            }
        }
    }
}