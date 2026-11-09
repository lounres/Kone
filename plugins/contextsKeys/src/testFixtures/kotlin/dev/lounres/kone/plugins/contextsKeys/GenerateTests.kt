/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.contextsKeys

import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJvmTestComplete
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJvmTestForPhase0
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJvmTestForPhase1
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJvmTestForPhase2
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJvmTestForPhase3
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBoxJvmTest
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractJvmTestWithoutPlugin
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractFrontendJvmTestDeclarations
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractFrontendJvmTestDiagnostic
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractFrontendJvmTestComplete
import org.jetbrains.kotlin.generators.dsl.junit5.generateTestGroupSuiteWithJUnit5

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = System.getProperty("test.data.dir"), testsRoot = System.getProperty("test.generated.dir")) {
            // JVM
            testClass<AbstractFrontendJvmTestDeclarations> {
                model("jvm/fir/declarations")
            }
            testClass<AbstractFrontendJvmTestDiagnostic> {
                model("jvm/fir/diagnostics")
            }
            testClass<AbstractFrontendJvmTestComplete> {
                model("jvm/fir/complete")
            }
            
            testClass<AbstractBackendJvmTestForPhase0> {
                model("jvm/ir/phase0")
            }
            testClass<AbstractBackendJvmTestForPhase1> {
                model("jvm/ir/phase1")
            }
            testClass<AbstractBackendJvmTestForPhase2> {
                model("jvm/ir/phase2")
            }
            testClass<AbstractBackendJvmTestForPhase3> {
                model("jvm/ir/phase3")
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
            testClass<AbstractBackendJvmTestComplete> {
                model("jvm/ir/complete")
            }
            testClass<AbstractBoxJvmTest> {
                model("jvm/ir/box")
            }

            testClass<AbstractJvmTestWithoutPlugin> {
                model("jvm/ir/test")
            }
        }
    }
}
