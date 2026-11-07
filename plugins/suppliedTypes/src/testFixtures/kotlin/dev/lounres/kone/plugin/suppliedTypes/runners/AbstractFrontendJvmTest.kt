/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.runners

import dev.lounres.kone.plugin.suppliedTypes.services.FirCompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.FirDeclarationsConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.FirDiagnosticConfigurator
import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestPhase


open class AbstractFrontendJvmTest(private val pluginConfigurator: Constructor<EnvironmentConfigurator>) : KoneSuppliedTypesJvmTestRunner() {
    final override val runPipelineTillPhase: TestPhase = TestPhase.FRONTEND
    final override fun configure(builder: TestConfigurationBuilder) {
        builder.useConfigurators(pluginConfigurator)
    }
}

open class AbstractFrontendJvmTestDeclarations : AbstractFrontendJvmTest(::FirDeclarationsConfigurator)
open class AbstractFrontendJvmTestDiagnostic : AbstractFrontendJvmTest(::FirDiagnosticConfigurator)
open class AbstractFrontendJvmTestComplete : AbstractFrontendJvmTest(::FirCompleteExtensionRegistrarConfigurator)
