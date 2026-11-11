/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.suppliedTypes.runners

import dev.lounres.kone.plugins.suppliedTypes.services.CompleteExtensionRegistrarConfigurator
import dev.lounres.kone.plugins.suppliedTypes.services.IrPartialExtensionRegistrarConfigurator
import dev.lounres.kone.plugins.suppliedTypes.services.TestExtensionRegistrarConfigurator
import dev.lounres.kone.plugins.testUtils.runners.KoneBackendJvmTestRunner
import org.jetbrains.kotlin.test.services.TestPhase


open class AbstractBackendJvmTestForPhase(
    lastPhase: UInt = UInt.MAX_VALUE,
) : KoneBackendJvmTestRunner(IrPartialExtensionRegistrarConfigurator.Constructor(lastPhase)) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.FIR2IR
}

open class AbstractBackendJvmTestForPhase0 : AbstractBackendJvmTestForPhase(0u)
open class AbstractBackendJvmTestForPhase1 : AbstractBackendJvmTestForPhase(1u)
open class AbstractBackendJvmTestForPhase2 : AbstractBackendJvmTestForPhase(2u)
open class AbstractBackendJvmTestForPhase3 : AbstractBackendJvmTestForPhase(3u)
open class AbstractBackendJvmTestForPhase4 : AbstractBackendJvmTestForPhase(4u)
open class AbstractBackendJvmTestForPhase5 : AbstractBackendJvmTestForPhase(5u)
open class AbstractBackendJvmTestForPhase6 : AbstractBackendJvmTestForPhase(6u)
open class AbstractBackendJvmTestForPhase7 : AbstractBackendJvmTestForPhase(7u)

open class AbstractBackendJvmTestComplete : KoneBackendJvmTestRunner(::CompleteExtensionRegistrarConfigurator) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.CODEGEN
}

open class AbstractBoxJvmTest : KoneBackendJvmTestRunner(::CompleteExtensionRegistrarConfigurator, runJvmBoxTest = true) {
    override val runPipelineTillPhase: TestPhase get() = TestPhase.CODEGEN
}

open class AbstractJvmTestWithoutPlugin: KoneBackendJvmTestRunner(::TestExtensionRegistrarConfigurator, runJvmBoxTest = true) {
    override val runPipelineTillPhase: TestPhase = TestPhase.CODEGEN
}