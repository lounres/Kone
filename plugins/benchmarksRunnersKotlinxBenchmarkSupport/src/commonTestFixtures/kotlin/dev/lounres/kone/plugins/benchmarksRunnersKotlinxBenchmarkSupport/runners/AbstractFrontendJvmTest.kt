/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.runners

import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.services.FirCompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.services.FirDeclarationsConfigurator
import dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.services.FirDiagnosticConfigurator
import dev.lounres.kone.plugins.testUtils.runners.KoneFrontendJvmTestRunner


open class AbstractFrontendJvmTestDeclarations : KoneFrontendJvmTestRunner(::FirDeclarationsConfigurator)
open class AbstractFrontendJvmTestDiagnostic : KoneFrontendJvmTestRunner(::FirDiagnosticConfigurator)
open class AbstractFrontendJvmTestComplete : KoneFrontendJvmTestRunner(::FirCompleteExtensionRegistrarConfigurator)
