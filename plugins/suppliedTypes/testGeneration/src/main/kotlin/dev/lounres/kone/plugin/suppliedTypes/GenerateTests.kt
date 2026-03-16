/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes

import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestComplete
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestForPhase0
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestForPhase1
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestForPhase2
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestForPhase3
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestForPhase4
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestForPhase5
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestWithoutPlugin
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractDeclarationsTest
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractDiagnosticTest
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractFirCompleteTest
import org.jetbrains.kotlin.generators.dsl.junit5.generateTestGroupSuiteWithJUnit5
import testDataPath

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = testDataPath, testsRoot = "../build/generated/kotlinCompilerPluginTestGenerator/test") {
            testClass<AbstractDeclarationsTest> {
                model("fir/declarations")
            }
            testClass<AbstractDiagnosticTest> {
                model("fir/diagnostics")
            }
            testClass<AbstractFirCompleteTest> {
                model("fir/complete")
            }
            
            testClass<AbstractBoxTestForPhase0> {
                model("ir/phase0")
            }
            testClass<AbstractBoxTestForPhase1> {
                model("ir/phase1")
            }
            testClass<AbstractBoxTestForPhase2> {
                model("ir/phase2")
            }
            testClass<AbstractBoxTestForPhase3> {
                model("ir/phase3")
            }
            testClass<AbstractBoxTestForPhase4> {
                model("ir/phase4")
            }
            testClass<AbstractBoxTestForPhase5> {
                model("ir/phase5")
            }
            
            testClass<AbstractBoxTestComplete> {
                model("ir/complete")
            }
            
            testClass<AbstractBoxTestWithoutPlugin> {
                model("ir/test")
            }
        }
    }
}
