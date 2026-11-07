/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys

import dev.lounres.kone.plugin.contextsKeys.runners.AbstractBackendTestComplete
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractBackendTestForPhase0
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractBackendTestForPhase1
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractBackendTestForPhase2
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractBackendTestForPhase3
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractBackendTestForPhase4
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractBackendTestForPhase5
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractBackendTestForPhase6
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractBackendTestForPhase7
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractBoxTest
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractTestWithoutPlugin
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractFrontendTestDeclarations
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractFrontendTestDiagnostic
import dev.lounres.kone.plugin.contextsKeys.runners.AbstractFrontendTestComplete
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
            
            testClass<AbstractBackendTestForPhase0> {
                model("ir/phase0")
            }
            testClass<AbstractBackendTestForPhase1> {
                model("ir/phase1")
            }
            testClass<AbstractBackendTestForPhase2> {
                model("ir/phase2")
            }
            testClass<AbstractBackendTestForPhase3> {
                model("ir/phase3")
            }
//            testClass<AbstractBackendTestForPhase4> {
//                model("ir/phase4")
//            }
//            testClass<AbstractBackendTestForPhase5> {
//                model("ir/phase5")
//            }
//            testClass<AbstractBackendTestForPhase6> {
//                model("ir/phase6")
//            }
//            testClass<AbstractBackendTestForPhase7> {
//                model("ir/phase7")
//            }
            testClass<AbstractBackendTestComplete> {
                model("ir/complete")
            }
            testClass<AbstractBoxTest> {
                model("ir/box")
            }

            testClass<AbstractTestWithoutPlugin> {
                model("ir/test")
            }
        }
    }
}
