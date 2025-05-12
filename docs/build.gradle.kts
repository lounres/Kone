plugins {
    alias(versions.plugins.dokka)
}

dokka {
    moduleName = "Kone"
    
    pluginsConfiguration.html {
        customAssets.from(projectDir.resolve("images/logo-icon.svg"), projectDir.resolve("images/favicon.svg"))
        footerMessage = "Copyright © 2025 Gleb Minaev<br>All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE"
        templatesDir = projectDir.resolve("templates")
    }
    
    // DOKKA-3885
    dokkaGeneratorIsolation = ClassLoaderIsolation()
}