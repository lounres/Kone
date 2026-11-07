/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks.runners

import dev.lounres.kone.plugin.benchmarks.services.FirCompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.benchmarks.services.FirDeclarationsConfigurator
import dev.lounres.kone.plugin.benchmarks.services.FirDiagnosticConfigurator
import dev.lounres.kone.util.kotlinCompilerTestUtils.runners.KoneFrontendJvmTestRunner


open class AbstractFrontendJvmTestDeclarations : KoneFrontendJvmTestRunner(::FirDeclarationsConfigurator)
open class AbstractFrontendJvmTestDiagnostic : KoneFrontendJvmTestRunner(::FirDiagnosticConfigurator)
open class AbstractFrontendJvmTestComplete : KoneFrontendJvmTestRunner(::FirCompleteExtensionRegistrarConfigurator)
