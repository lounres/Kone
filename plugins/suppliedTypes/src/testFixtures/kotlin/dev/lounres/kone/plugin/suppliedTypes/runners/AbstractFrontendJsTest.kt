/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.runners

import dev.lounres.kone.plugin.suppliedTypes.services.FirCompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.FirDeclarationsConfigurator
import dev.lounres.kone.plugin.suppliedTypes.services.FirDiagnosticConfigurator
import dev.lounres.kone.util.kotlinCompilerTestUtils.runners.KoneFrontendJsTestRunner


open class AbstractFrontendJsTestDeclarations : KoneFrontendJsTestRunner(::FirDeclarationsConfigurator)
open class AbstractFrontendJsTestDiagnostic : KoneFrontendJsTestRunner(::FirDiagnosticConfigurator)
open class AbstractFrontendJsTestComplete : KoneFrontendJsTestRunner(::FirCompleteExtensionRegistrarConfigurator)
