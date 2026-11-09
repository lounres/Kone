/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport

import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractBackendJsTestComplete
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractBackendJsTestForPhase0
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractBackendJsTestForPhase1
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractBackendJvmTestComplete
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractBackendJvmTestForPhase0
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractBackendJvmTestForPhase1
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractBoxJsTest
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractBoxJvmTest
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractFrontendJsTestComplete
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractFrontendJsTestDeclarations
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractFrontendJsTestDiagnostic
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractFrontendJvmTestComplete
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractFrontendJvmTestDeclarations
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractFrontendJvmTestDiagnostic
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractJsTestWithoutPlugin
import dev.lounres.kone.plugins.benchmarksKotlinxBenchmarkSupport.runners.AbstractJvmTestWithoutPlugin
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
//            testClass<AbstractBackendJvmTestForPhase2> {
//                model("jvm/ir/phase2")
//            }
//            testClass<AbstractBackendJvmTestForPhase3> {
//                model("jvm/ir/phase3")
//            }
//            testClass<AbstractBackendJvmTestForPhase4> {
//                model("jvm/ir/phase4")
//            }
//            testClass<AbstractBackendJvmTestForPhase5> {
//                model("jvm/ir/phase5")
//            }
//            testClass<AbstractBackendJvmTestForPhase6> {
//                model("jvm/ir/phase6")
//            }
//            testClass<AbstractBackendJvmTestForPhase7> {
//                model("jvm/ir/phase7")
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
            
            // JS
            testClass<AbstractFrontendJsTestDeclarations> {
                model("js/fir/declarations")
            }
            testClass<AbstractFrontendJsTestDiagnostic> {
                model("js/fir/diagnostics")
            }
            testClass<AbstractFrontendJsTestComplete> {
                model("js/fir/complete")
            }
            
            testClass<AbstractBackendJsTestForPhase0> {
                model("js/ir/phase0")
            }
            testClass<AbstractBackendJsTestForPhase1> {
                model("js/ir/phase1")
            }
//            testClass<AbstractBackendJsTestForPhase2> {
//                model("js/ir/phase2")
//            }
//            testClass<AbstractBackendJsTestForPhase3> {
//                model("js/ir/phase3")
//            }
//            testClass<AbstractBackendJsTestForPhase4> {
//                model("js/ir/phase4")
//            }
//            testClass<AbstractBackendJsTestForPhase5> {
//                model("js/ir/phase5")
//            }
//            testClass<AbstractBackendJsTestForPhase6> {
//                model("js/ir/phase6")
//            }
//            testClass<AbstractBackendJsTestForPhase7> {
//                model("js/ir/phase7")
//            }
            testClass<AbstractBackendJsTestComplete> {
                model("js/ir/complete")
            }
            testClass<AbstractBoxJsTest> {
                model("js/ir/box")
            }

            testClass<AbstractJsTestWithoutPlugin> {
                model("js/ir/test")
            }
        }
    }
}
