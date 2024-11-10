plugins {
    alias(libs.plugins.dokka)
}

dokka {
    moduleName = "Kone"
    dokkaPublications.html {
    
    }
    
    // DOKKA-3885
    dokkaGeneratorIsolation = ClassLoaderIsolation()
}