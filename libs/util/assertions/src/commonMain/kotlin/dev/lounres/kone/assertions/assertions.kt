/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.assertions

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName


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

@JvmName("assertContextual")
context(assertionScope: AssertionScope)
public inline fun assert(checker: () -> AssertionScope.Assertion) {
    contract {
        callsInPlace(checker, InvocationKind.EXACTLY_ONCE)
    }
    assertionScope.assert(checker)
}

context(assertionScope: AssertionScope)
public inline fun <Value> Expect<Value>.assert(checker: (Value) -> AssertionScope.Assertion) {
    contract {
        callsInPlace(checker, InvocationKind.EXACTLY_ONCE)
    }
    assertionScope.assert { checker(exposeValue()) }
}

public fun AssertionScope.fail(message: String, cause: Throwable? = null) {
    consumeAssertion(
        AssertionScope.Assertion(
            message = message,
            cause = cause,
        )
    )
}

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

context(assertionScope: AssertionScope)
public inline fun Expect.Companion.toThrow(crossinline block: () -> Unit) {
    try {
        block()
        fail("No exception was thrown.")
    } catch (_: Throwable) {}
}

context(assertionScope: AssertionScope)
public inline fun Expect.Companion.toThrow(crossinline block: () -> Unit, checker: Expect<Throwable>.() -> Unit) {
    try {
        block()
        fail("No exception was thrown.")
    } catch (throwable: Throwable) {
        of(throwable, checker)
    }
}

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