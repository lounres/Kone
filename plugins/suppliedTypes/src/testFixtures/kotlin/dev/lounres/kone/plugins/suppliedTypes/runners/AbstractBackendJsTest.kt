/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.suppliedTypes.runners

import dev.lounres.kone.plugins.suppliedTypes.services.CompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugins.suppliedTypes.services.IrPartialExtensionRegistrarConfigurator
import dev.lounres.kone.plugins.suppliedTypes.services.TestExtensionRegistrarConfigurator
import dev.lounres.kone.util.kotlinCompilerTestUtils.runners.KoneBackendJsTestRunner
import org.jetbrains.kotlin.test.services.TestPhase


open class AbstractBackendJsTestForPhase(
    lastPhase: UInt = MAX_VALUE,
) : KoneBackendJsTestRunner(IrPartialExtensionRegistrarConfigurator.Constructor(lastPhase)) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.FIR2IR
}

open class AbstractBackendJsTestForPhase0 : AbstractBackendJsTestForPhase(0u)
open class AbstractBackendJsTestForPhase1 : AbstractBackendJsTestForPhase(1u)
open class AbstractBackendJsTestForPhase2 : AbstractBackendJsTestForPhase(2u)
open class AbstractBackendJsTestForPhase3 : AbstractBackendJsTestForPhase(3u)
open class AbstractBackendJsTestForPhase4 : AbstractBackendJsTestForPhase(4u)
open class AbstractBackendJsTestForPhase5 : AbstractBackendJsTestForPhase(5u)
open class AbstractBackendJsTestForPhase6 : AbstractBackendJsTestForPhase(6u)
open class AbstractBackendJsTestForPhase7 : AbstractBackendJsTestForPhase(7u)

open class AbstractBackendJsTestComplete : KoneBackendJsTestRunner(::CompleteExtensionRegistrarConfigurator) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.CODEGEN
}

open class AbstractBoxJsTest : KoneBackendJsTestRunner(::CompleteExtensionRegistrarConfigurator, runJsBoxTest = true) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.CODEGEN
}

open class AbstractJsTestWithoutPlugin: KoneBackendJsTestRunner(::TestExtensionRegistrarConfigurator, runJsBoxTest = true) {
    override val runPipelineTillPhase: TestPhase = TestPhase.CODEGEN
}