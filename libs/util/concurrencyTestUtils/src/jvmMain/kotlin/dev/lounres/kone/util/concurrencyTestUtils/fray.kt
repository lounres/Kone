/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.concurrencyTestUtils

import de.infix.testBalloon.framework.core.Test
import de.infix.testBalloon.framework.core.TestConfig
import de.infix.testBalloon.framework.core.TestSuiteScope
import de.infix.testBalloon.framework.shared.TestElementName
import de.infix.testBalloon.framework.shared.TestRegistering
import org.pastalab.fray.junit.plain.FrayInTestLauncher


@TestRegistering
public fun TestSuiteScope.testWithFray(
    @TestElementName name: String,
    testConfig: TestConfig = TestConfig,
    action: Test.ExecutionScope.() -> Unit
) {
    test(
        name = name,
        testConfig = testConfig,
    ) {
        FrayInTestLauncher.launchFrayTest {
            action()
        }
    }
}

@TestRegistering
public fun TestSuiteScope.testWithFrayReplay(
    @TestElementName name: String,
    testConfig: TestConfig = TestConfig,
    path: String,
    action: Test.ExecutionScope.() -> Unit
) {
    test(
        name = name,
        testConfig = testConfig,
    ) {
        FrayInTestLauncher.launchFrayReplay(
            { action() },
            path = path,
        )
    }
}