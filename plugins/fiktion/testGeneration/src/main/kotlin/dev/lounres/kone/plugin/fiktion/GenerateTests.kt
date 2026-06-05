/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.fiktion

import dev.lounres.kone.plugin.fiktion.runners.AbstractFrontendTestComplete
import dev.lounres.kone.plugin.fiktion.runners.AbstractFrontendTestDeclarations
import dev.lounres.kone.plugin.fiktion.runners.AbstractFrontendTestDiagnostic
import dev.lounres.kone.plugin.fiktion.runners.AbstractTestWithoutPlugin
import dev.lounres.kone.plugin.fiktion.runners.ErasureAbstractBackendTestComplete
import dev.lounres.kone.plugin.fiktion.runners.ErasureAbstractBoxTest
import dev.lounres.kone.plugin.fiktion.runners.InterceptionAbstractBackendTestComplete
import dev.lounres.kone.plugin.fiktion.runners.InterceptionAbstractBoxTest
import generatedTestsPath
import org.jetbrains.kotlin.generators.dsl.junit5.generateTestGroupSuiteWithJUnit5
import testDataPath


fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = testDataPath, testsRoot = generatedTestsPath) {
            testClass<AbstractFrontendTestDeclarations> {
                model("fir/declarations")
            }
            testClass<AbstractFrontendTestDiagnostic> {
                model("fir/diagnostics")
            }
            testClass<AbstractFrontendTestComplete> {
                model("fir/complete")
            }

//            testClass<ErasureAbstractBackendTestForPhase0> {
//                model("ir/erasure/phase0")
//            }
//            testClass<ErasureAbstractBackendTestForPhase1> {
//                model("ir/erasure/phase1")
//            }
//            testClass<ErasureAbstractBackendTestForPhase2> {
//                model("ir/erasure/phase2")
//            }
//            testClass<ErasureAbstractBackendTestForPhase3> {
//                model("ir/erasure/phase3")
//            }
//            testClass<ErasureAbstractBackendTestForPhase4> {
//                model("ir/erasure/phase4")
//            }
//            testClass<ErasureAbstractBackendTestForPhase5> {
//                model("ir/erasure/phase5")
//            }
            testClass<ErasureAbstractBackendTestComplete> {
                model("ir/erasure/complete")
            }
            testClass<ErasureAbstractBoxTest> {
                model("ir/erasure/box")
            }

//            testClass<InterceptionAbstractBackendTestForPhase0> {
//                model("ir/interception/phase0")
//            }
//            testClass<InterceptionAbstractBackendTestForPhase1> {
//                model("ir/interception/phase1")
//            }
//            testClass<InterceptionAbstractBackendTestForPhase2> {
//                model("ir/interception/phase2")
//            }
//            testClass<InterceptionAbstractBackendTestForPhase3> {
//                model("ir/interception/phase3")
//            }
//            testClass<InterceptionAbstractBackendTestForPhase4> {
//                model("ir/interception/phase4")
//            }
//            testClass<InterceptionAbstractBackendTestForPhase5> {
//                model("ir/interception/phase5")
//            }
            testClass<InterceptionAbstractBackendTestComplete> {
                model("ir/interception/complete")
            }
            testClass<InterceptionAbstractBoxTest> {
                model("ir/interception/box")
            }

            testClass<AbstractTestWithoutPlugin> {
                model("ir/test")
            }
        }
    }
}
