/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.contextsKeys

import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJsTestComplete
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJsTestForPhase0
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJsTestForPhase1
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJsTestForPhase2
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJsTestForPhase3
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJvmTestComplete
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJvmTestForPhase0
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJvmTestForPhase1
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJvmTestForPhase2
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBackendJvmTestForPhase3
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBoxJsTest
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractBoxJvmTest
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractFrontendJsTestComplete
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractFrontendJsTestDeclarations
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractFrontendJsTestDiagnostic
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractJvmTestWithoutPlugin
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractFrontendJvmTestDeclarations
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractFrontendJvmTestDiagnostic
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractFrontendJvmTestComplete
import dev.lounres.kone.plugins.contextsKeys.runners.AbstractJsTestWithoutPlugin
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
        testClass<AbstractBackendJvmTestForPhase3> {
            model("jvm/ir/phase3")
        }
//            testClass<AbstractBackendTestForPhase4> {
//                model("jvm/ir/phase4")
//            }
//            testClass<AbstractBackendTestForPhase5> {
//                model("jvm/ir/phase5")
//            }
//            testClass<AbstractBackendTestForPhase6> {
//                model("jvm/ir/phase6")
//            }
//            testClass<AbstractBackendTestForPhase7> {
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
        testClass<AbstractBackendJsTestForPhase2> {
            model("js/ir/phase2")
        }
        testClass<AbstractBackendJsTestForPhase3> {
            model("js/ir/phase3")
        }
//            testClass<AbstractBackendTestForPhase4> {
//                model("js/ir/phase4")
//            }
//            testClass<AbstractBackendTestForPhase5> {
//                model("js/ir/phase5")
//            }
//            testClass<AbstractBackendTestForPhase6> {
//                model("js/ir/phase6")
//            }
//            testClass<AbstractBackendTestForPhase7> {
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
