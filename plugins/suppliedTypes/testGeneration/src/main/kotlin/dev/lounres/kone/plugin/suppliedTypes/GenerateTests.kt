/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes

import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBackendTestComplete
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBackendTestForPhase0
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBackendTestForPhase1
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBackendTestForPhase2
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBackendTestForPhase3
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBackendTestForPhase4
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTest
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractTestWithoutPlugin
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractDeclarationsTest
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractDiagnosticTest
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractFirCompleteTest
import generatedTestsPath
import org.jetbrains.kotlin.generators.dsl.junit5.generateTestGroupSuiteWithJUnit5
import testDataPath

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = testDataPath, testsRoot = generatedTestsPath) {
            testClass<AbstractDeclarationsTest> {
                model("fir/declarations")
            }
            testClass<AbstractDiagnosticTest> {
                model("fir/diagnostics")
            }
            testClass<AbstractFirCompleteTest> {
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
            testClass<AbstractBackendTestForPhase4> {
                model("ir/phase4")
            }
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
