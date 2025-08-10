/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package io.kotest.datatest

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneSequence
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.component1
import dev.lounres.kone.collections.map.component2
import dev.lounres.kone.collections.utils.forEach
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.scopes.AbstractContainerScope
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.test.TestType
import kotlin.jvm.JvmName


/**
 * Registers tests inside the given test context for each element.
 *
 * The test name will be generated from the stable properties of the elements. See [StableIdentifiers].
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
 * The test name will be generated from the stable properties of the elements. See [StableIdentifiers].
 */
public suspend fun <T> ContainerScope.withData(
    nameIndFn: (Int, T) -> String,
    ts: Iterable<T>,
    test: suspend ContainerScope.(T) -> Unit
) {
    ts.forEachIndexed { index, t ->
        registerTest(TestName(nameIndFn(index, t)), false, null, TestType.Dynamic) { AbstractContainerScope(this).test(t) }
    }
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdentifiers].
 */
public suspend fun <T> ContainerScope.withData(
    ts: KoneSequence<T>,
    test: suspend ContainerScope.(T) -> Unit
) {
    withData({ getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test names will be generated from the stable properties of the elements. See [StableIdentifiers].
 */
public suspend fun <T> ContainerScope.withData(
    ts: Iterable<T>,
    test: suspend ContainerScope.(T) -> Unit
) {
    withData({ getStableIdentifier(it) }, ts, test)
}

/**
 * Registers tests inside the given test context for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
public suspend fun <T> ContainerScope.withData(
    nameFn: (T) -> String,
    ts: KoneSequence<T>,
    test: suspend ContainerScope.(T) -> Unit
) {
    ts.forEach { t ->
        registerTest(TestName(nameFn(t)), false, null, TestType.Dynamic) { AbstractContainerScope(this).test(t) }
    }
}

/**
 * Registers tests inside the given [ContainerScope] for each element of [ts].
 * The test name will be generated from the given [nameFn] function.
 */
public suspend fun <T> ContainerScope.withData(
    nameFn: (T) -> String,
    @BuilderInference ts: KoneIterable<T>,
    @BuilderInference test: suspend ContainerScope.(T) -> Unit
) {
    ts.forEach { t ->
        registerTest(TestName(nameFn(t)), false, null, TestType.Dynamic) { AbstractContainerScope(this).test(t) }
    }
}

/**
 * Registers tests inside the given test context for each tuple of [data], with the first value
 * of the tuple used as the test name, and the second value passed to the test.
 */
@JvmName("withDataMap")
public suspend fun <T> ContainerScope.withData(data: KoneMap<String, T>, test: suspend ContainerScope.(T) -> Unit) {
    data.nodes.forEach { (name, t) ->
        registerTest(TestName(name), false, null, TestType.Dynamic) { AbstractContainerScope(this).test(t) }
    }
}