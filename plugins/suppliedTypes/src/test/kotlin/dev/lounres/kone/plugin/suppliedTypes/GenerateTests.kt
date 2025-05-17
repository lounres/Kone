package dev.lounres.kone.plugin.suppliedTypes

import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractBoxTest
import dev.lounres.kone.plugin.suppliedTypes.runners.AbstractDiagnosticTest

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "src/test/data", testsRoot = "build/generated/kotlinCompilerPluginTestGenerator/test") {
            testClass<AbstractDiagnosticTest> {
                model("diagnostics")
            }

            testClass<AbstractBoxTest> {
                model("box")
            }
        }
    }
}
