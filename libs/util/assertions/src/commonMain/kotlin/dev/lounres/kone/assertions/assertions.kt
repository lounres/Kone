/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.assertions

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName


/**
 * Executes the given assertion [checker] and consumes its result.
 *
 * The checker function should return an [AssertionScope.Assertion] describing the assertion result.
 * If the checker throws an exception, it is wrapped in an [AssertionError] and rethrown.
 * Otherwise, the resulting assertion is passed to [consumeAssertion].
 *
 * @receiver The assertion scope that will consume the assertion result.
 * @param checker A function that performs an assertion check and returns an [AssertionScope.Assertion].
 *   Called exactly once.
 */
public inline fun AssertionScope.assert(checker: () -> AssertionScope.Assertion) {
    contract {
        callsInPlace(checker, InvocationKind.EXACTLY_ONCE)
    }
    val assertionResult = try {
        checker()
    } catch (throwable: Throwable) {
        throw AssertionError("Assertion was interrupted with an exception.", throwable)
    }
    consumeAssertion(assertionResult)
}

/**
 * Executes the given assertion [checker] within the current assertion scope.
 *
 * This contextual version uses the [assertionScope] from the surrounding context.
 * The checker function should return an [AssertionScope.Assertion] describing the assertion result.
 *
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param checker A function that performs an assertion check and returns an [AssertionScope.Assertion].
 *   Called exactly once.
 * @see AssertionScope.assert
 */
@JvmName("assertContextual")
context(assertionScope: AssertionScope)
public inline fun assert(checker: () -> AssertionScope.Assertion) {
    contract {
        callsInPlace(checker, InvocationKind.EXACTLY_ONCE)
    }
    assertionScope.assert(checker)
}

/**
 * Executes the given assertion [checker] on the value exposed by this [Expect] instance.
 *
 * The checker function receives the exposed value and should return an [AssertionScope.Assertion]
 * describing the assertion result. The assertion is executed within the current assertion scope.
 *
 * @receiver The [Expect] instance whose value will be asserted on.
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param Value The type of the value being asserted on.
 * @param checker A function that receives the exposed value and returns an [AssertionScope.Assertion].
 *   Called exactly once.
 */
context(assertionScope: AssertionScope)
public inline fun <Value> Expect<Value>.assert(checker: (Value) -> AssertionScope.Assertion) {
    contract {
        callsInPlace(checker, InvocationKind.EXACTLY_ONCE)
    }
    assertionScope.assert { checker(exposeValue()) }
}

/**
 * Fails the current assertion scope with the given [message] and optional [cause].
 *
 * This function creates an assertion result with the provided failure message and cause,
 * then passes it to [consumeAssertion], which typically throws an [AssertionError].
 *
 * @receiver The assertion scope that will consume the failure.
 * @param message The failure message describing what went wrong.
 * @param cause The throwable that caused the failure, if any. Defaults to null.
 */
public fun AssertionScope.fail(message: String, cause: Throwable? = null) {
    consumeAssertion(
        AssertionScope.Assertion(
            message = message,
            cause = cause,
        )
    )
}

/**
 * Fails the current assertion scope with the given [message] and optional [cause].
 *
 * This contextual version uses the [assertionScope] from the surrounding context.
 * It creates an assertion result with the provided failure message and cause.
 *
 * @param assertionScope The assertion scope that will consume the failure.
 * @param message The failure message describing what went wrong.
 * @param cause The throwable that caused the failure, if any. Defaults to null.
 * @see AssertionScope.fail
 */
@JvmName("failContextual")
context(assertionScope: AssertionScope)
public fun fail(message: String, cause: Throwable? = null) {
    assertionScope.consumeAssertion(
        AssertionScope.Assertion(
            message = message,
            cause = cause,
        )
    )
}

/**
 * Asserts that the given [block] does not throw any exception.
 *
 * If the block throws an exception, the assertion fails with a message indicating that
 * an exception was thrown, and the exception is included as the cause.
 *
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param block The code block that should not throw an exception. Called exactly once.
 */
context(assertionScope: AssertionScope)
public inline fun Expect.Companion.notToThrow(crossinline block: () -> Unit) {
    try {
        block()
    } catch (throwable: Throwable) {
        fail(
            message = "Exception was thrown.",
            cause = throwable,
        )
    }
}

/**
 * Asserts that the given [block] does not throw any exception, and if it succeeds,
 * asserts on the result using the provided [checker].
 *
 * If the block throws an exception, the assertion fails immediately with a message indicating
 * that an exception was thrown. If the block succeeds, the result is passed to the checker
 * for further assertions.
 *
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param Result The type of the result produced by the block.
 * @param block The code block that should not throw an exception. Called exactly once.
 * @param checker The assertion block to execute on the result if no exception is thrown.
 */
context(assertionScope: AssertionScope)
public inline fun <Result> Expect.Companion.notToThrow(crossinline block: () -> Result, checker: Expect<Result>.() -> Unit) {
    var result: Result?
    var toCheck: Boolean
    try {
        result = block()
        toCheck = true
    } catch (throwable: Throwable) {
        fail(
            message = "Exception was thrown.",
            cause = throwable,
        )
        result = null
        toCheck = false
    }
    @Suppress("UNCHECKED_CAST")
    if (toCheck) of(result as Result, checker)
}

/**
 * Asserts that the given [block] throws an exception.
 *
 * If the block completes without throwing an exception, the assertion fails with a message
 * indicating that no exception was thrown. Any exception thrown by the block causes the
 * assertion to pass.
 *
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param block The code block that should throw an exception. Called exactly once.
 */
context(assertionScope: AssertionScope)
public inline fun Expect.Companion.toThrow(crossinline block: () -> Unit) {
    try {
        block()
        fail("No exception was thrown.")
    } catch (_: Throwable) {}
}

/**
 * Asserts that the given [block] throws an exception, and if it does,
 * asserts on the thrown exception using the provided [checker].
 *
 * If the block completes without throwing an exception, the assertion fails with a message
 * indicating that no exception was thrown. If the block throws an exception, it is passed
 * to the checker for further assertions.
 *
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param block The code block that should throw an exception. Called exactly once.
 * @param checker The assertion block to execute on the thrown exception.
 */
context(assertionScope: AssertionScope)
public inline fun Expect.Companion.toThrow(crossinline block: () -> Unit, checker: Expect<Throwable>.() -> Unit) {
    try {
        block()
        fail("No exception was thrown.")
    } catch (throwable: Throwable) {
        of(throwable, checker)
    }
}

/**
 * Asserts that the given [block] throws an exception of the specified type [ThrowableType].
 *
 * If the block completes without throwing an exception, the assertion fails with a message
 * indicating that no exception was thrown. If the block throws an exception of the wrong type,
 * the assertion fails with a message indicating the type mismatch.
 *
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param ThrowableType The expected type of the exception. The exception must be an instance of this type.
 * @param block The code block that should throw an exception of type [ThrowableType]. Called exactly once.
 */
@JvmName("orThrowCheckingType")
context(assertionScope: AssertionScope)
public inline fun <reified ThrowableType: Throwable> Expect.Companion.toThrow(crossinline block: () -> Unit) {
    try {
        block()
        fail("No exception was thrown.")
    } catch (_: ThrowableType) {
    } catch (throwable: Throwable) {
        fail("Exception of unexpected type was thrown.\nExpected: ${ThrowableType::class.simpleName}\nActual: ${throwable::class.simpleName}")
    }
}

/**
 * Asserts that the given [block] throws an exception of the specified type [ThrowableType],
 * and if it does, asserts on the thrown exception using the provided [checker].
 *
 * If the block completes without throwing an exception, the assertion fails with a message
 * indicating that no exception was thrown. If the block throws an exception of the wrong type,
 * the assertion fails with a message indicating the type mismatch. If the block throws an
 * exception of the correct type, it is passed to the checker for further assertions.
 *
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param ThrowableType The expected type of the exception. The exception must be an instance of this type.
 * @param block The code block that should throw an exception of type [ThrowableType]. Called exactly once.
 * @param checker The assertion block to execute on the thrown exception if it is of the expected type.
 */
@JvmName("orThrowCheckingType")
context(assertionScope: AssertionScope)
public inline fun <reified ThrowableType: Throwable> Expect.Companion.toThrow(crossinline block: () -> Unit, checker: Expect<ThrowableType>.() -> Unit) {
    try {
        block()
        fail("No exception was thrown.")
    } catch (throwable: ThrowableType) {
        of(throwable, checker)
    } catch (throwable: Throwable) {
        fail("Exception of unexpected type was thrown.\nExpected: ${ThrowableType::class.simpleName}\nActual: ${throwable::class.simpleName}")
    }
}

/**
 * Asserts that the exposed value is equal to the [expected] value using `==` comparison.
 *
 * This performs structural equality comparison (using `equals` under the hood).
 *
 * @receiver The [Expect] instance whose value will be compared.
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param expected The expected value to compare against.
 */
context(assertionScope: AssertionScope)
public infix fun Expect<Any?>.toBe(expected: Any?) {
    val actual = exposeValue()
    if (actual != expected) fail("Expected of $actual to be $expected")
}

/**
 * Asserts that the exposed value is not equal to the [expected] value using `==` comparison.
 *
 * This performs structural equality comparison (using `equals` under the hood).
 *
 * @receiver The [Expect] instance whose value will be compared.
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param expected The value that the exposed value should not equal.
 */
context(assertionScope: AssertionScope)
public infix fun Expect<Any?>.notToBe(expected: Any?) {
    val actual = exposeValue()
    if (actual == expected) fail("Expected of $actual not to be $expected")
}

/**
 * Asserts that the exposed value is the same instance as the [expected] value using `===` comparison.
 *
 * This performs referential equality comparison, checking that both references point to
 * the exact same object in memory.
 *
 * @receiver The [Expect] instance whose value will be compared.
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param expected The expected instance to compare against.
 */
context(assertionScope: AssertionScope)
public infix fun Expect<Any?>.toBeTheSameInstanceAs(expected: Any?) {
    val actual = exposeValue()
    if (actual !== expected) fail("Expected of $actual to be the same instance as $expected")
}

/**
 * Asserts that the exposed value is not the same instance as the [expected] value using `!==` comparison.
 *
 * This performs referential equality comparison, checking that the references do not point to
 * the exact same object in memory.
 *
 * @receiver The [Expect] instance whose value will be compared.
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param expected The instance that the exposed value should not be the same as.
 */
context(assertionScope: AssertionScope)
public infix fun Expect<Any?>.notToBeTheSameInstanceAs(expected: Any?) {
    val actual = exposeValue()
    if (actual === expected) fail("Expected of $actual not to be the same instance as $expected")
}

/**
 * Asserts that the exposed value is less than the [expected] value.
 *
 * The exposed value must implement [Comparable] and be comparable with the expected value.
 * Uses the natural ordering defined by the type's `compareTo` function.
 *
 * @receiver The [Expect] instance whose value will be compared.
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param Element The comparable type of both the exposed value and the expected value.
 * @param expected The upper bound value (exclusive).
 */
context(assertionScope: AssertionScope)
public infix fun <Element : Comparable<Element>> Expect<Element>.toBeLessThan(expected: Element) {
    val actual = exposeValue()
    if (actual >= expected) fail("Expected of $actual to be less than $expected")
}

/**
 * Asserts that the exposed value is greater than the [expected] value.
 *
 * The exposed value must implement [Comparable] and be comparable with the expected value.
 * Uses the natural ordering defined by the type's `compareTo` function.
 *
 * @receiver The [Expect] instance whose value will be compared.
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param Element The comparable type of both the exposed value and the expected value.
 * @param expected The lower bound value (exclusive).
 */
context(assertionScope: AssertionScope)
public infix fun <Element : Comparable<Element>> Expect<Element>.toBeGreaterThan(expected: Element) {
    val actual = exposeValue()
    if (actual <= expected) fail("Expected of $actual to be greater than $expected")
}

/**
 * Asserts that the exposed value is less than or equal to the [expected] value.
 *
 * The exposed value must implement [Comparable] and be comparable with the expected value.
 * Uses the natural ordering defined by the type's `compareTo` function.
 *
 * @receiver The [Expect] instance whose value will be compared.
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param Element The comparable type of both the exposed value and the expected value.
 * @param expected The upper bound value (inclusive).
 */
context(assertionScope: AssertionScope)
public infix fun <Element : Comparable<Element>> Expect<Element>.toBeLessThanOrEqualTo(expected: Element) {
    val actual = exposeValue()
    if (actual > expected) fail("Expected of $actual to be less than or equal to $expected")
}

/**
 * Asserts that the exposed value is greater than or equal to the [expected] value.
 *
 * The exposed value must implement [Comparable] and be comparable with the expected value.
 * Uses the natural ordering defined by the type's `compareTo` function.
 *
 * @receiver The [Expect] instance whose value will be compared.
 * @param assertionScope The assertion scope that will consume the assertion result.
 * @param Element The comparable type of both the exposed value and the expected value.
 * @param expected The lower bound value (inclusive).
 */
context(assertionScope: AssertionScope)
public infix fun <Element : Comparable<Element>> Expect<Element>.toBeGreaterThanOrEqualTo(expected: Element) {
    val actual = exposeValue()
    if (actual < expected) fail("Expected of $actual to be greater than or equal to $expected")
}