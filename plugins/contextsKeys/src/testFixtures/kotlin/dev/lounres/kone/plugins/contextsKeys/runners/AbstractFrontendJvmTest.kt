/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.contextsKeys.runners

import dev.lounres.kone.plugins.contextsKeys.services.FirCompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugins.contextsKeys.services.FirDeclarationsConfigurator
import dev.lounres.kone.plugins.contextsKeys.services.FirDiagnosticConfigurator
import dev.lounres.kone.util.kotlinCompilerTestUtils.runners.KoneFrontendJvmTestRunner


open class AbstractFrontendJvmTestDeclarations : KoneFrontendJvmTestRunner(::FirDeclarationsConfigurator)
open class AbstractFrontendJvmTestDiagnostic : KoneFrontendJvmTestRunner(::FirDiagnosticConfigurator)
open class AbstractFrontendJvmTestComplete : KoneFrontendJvmTestRunner(::FirCompleteExtensionRegistrarConfigurator)
