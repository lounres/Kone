package dev.lounres.kone.plugin.suppliedTypes

import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestForPhase1
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestForPhase2
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestForPhase3
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestForPhase4
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestForPhase5
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTestWithoutPlugin
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractDiagnosticTest
import testDataPath

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = testDataPath, testsRoot = "../build/generated/kotlinCompilerPluginTestGenerator/test") {
            testClass<AbstractDiagnosticTest> {
                model("diagnostics")
            }

            testClass<AbstractBoxTestForPhase1> {
                model("box/phase1")
            }
            testClass<AbstractBoxTestForPhase2> {
                model("box/phase2")
            }
            testClass<AbstractBoxTestForPhase3> {
                model("box/phase3")
            }
            testClass<AbstractBoxTestForPhase4> {
                model("box/phase4")
            }
            testClass<AbstractBoxTestForPhase5> {
                model("box/phase5")
            }
            
            testClass<AbstractBoxTestWithoutPlugin> {
                model("box/test")
            }
        }
    }
}
