/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.scopes.AbstractContainerScope
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.test.Identifiers
import io.kotest.core.test.TestType


/**
 * Registers tests inside the given test context for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
public suspend fun <T> ContainerScope.withData(
    nameIndFn: (Int, T) -> String,
    first: T,
    second: T,
    vararg rest: T,
    test: suspend ContainerScope.(T) -> Unit
): Unit = withData(nameIndFn, listOf(first, second) + rest, test)

/**
 * Registers tests inside the given test context for each element of [ts].
 *
 * The test name will be generated from the stable properties of the elements. See [Identifiers].
 */
@OptIn(ExperimentalKotest::class)
public suspend fun <T> ContainerScope.withData(
    nameIndFn: (Int, T) -> String,
    ts: Iterable<T>,
    test: suspend ContainerScope.(T) -> Unit
) {
    ts.forEachIndexed { index, t ->
        registerTest(TestName(nameIndFn(index, t)), false, null, TestType.Dynamic) { AbstractContainerScope(this).test(t) }
    }
}