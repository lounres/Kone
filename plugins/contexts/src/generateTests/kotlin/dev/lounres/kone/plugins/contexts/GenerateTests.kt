/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.contexts

import dev.lounres.kone.plugins.contexts.runners.AbstractBackendJvmTestComplete
import dev.lounres.kone.plugins.contexts.runners.AbstractBackendJvmTestForPhase0
import dev.lounres.kone.plugins.contexts.runners.AbstractBackendJvmTestForPhase1
import dev.lounres.kone.plugins.contexts.runners.AbstractBackendJvmTestForPhase2
import dev.lounres.kone.plugins.contexts.runners.AbstractBoxJvmTest
import dev.lounres.kone.plugins.contexts.runners.AbstractJvmTestWithoutPlugin
import dev.lounres.kone.plugins.contexts.runners.AbstractFrontendJvmTestDeclarations
import dev.lounres.kone.plugins.contexts.runners.AbstractFrontendJvmTestDiagnostic
import dev.lounres.kone.plugins.contexts.runners.AbstractFrontendJvmTestComplete
import org.jetbrains.kotlin.compiler.plugin.devkit.generateDevKitTestsWithJUnit5


fun main() {
    generateDevKitTestsWithJUnit5 {
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
//        testClass<AbstractBackendTestForPhase3> {
//            model("jvm/ir/phase3")
//        }
//        testClass<AbstractBackendTestForPhase4> {
//            model("jvm/ir/phase4")
//        }
//        testClass<AbstractBackendTestForPhase5> {
//            model("jvm/ir/phase5")
//        }
//        testClass<AbstractBackendTestForPhase6> {
//            model("jvm/ir/phase6")
//        }
//        testClass<AbstractBackendTestForPhase7> {
//            model("jvm/ir/phase7")
//        }
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
