package dev.lounres.kone.assertions

import kotlin.jvm.JvmName


public inline fun AssertionScope.assert(checker: () -> AssertionScope.Assertion) {
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
    assertionScope.assert(checker)
}

context(assertionScope: AssertionScope)
public inline fun <Value> Expect<Value>.assert(checker: (Value) -> AssertionScope.Assertion) {
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
public inline fun Expect.Companion.toThrow(block: () -> Unit)  {
    try {
        block()
        fail("No exception was thrown.")
    } catch (_: Throwable) {}
}

context(assertionScope: AssertionScope)
public inline fun Expect.Companion.toThrow(block: () -> Unit, checker: Expect<Throwable>.() -> Unit)  {
    try {
        block()
        fail("No exception was thrown.")
    } catch (throwable: Throwable) {
        of(throwable, checker)
    }
}

@JvmName("orThrowCheckingType")
context(assertionScope: AssertionScope)
public inline fun <reified ThrowableType: Throwable> Expect.Companion.toThrow(block: () -> Unit)  {
    try {
        block()
        fail("No exception was thrown.")
    } catch (_: ThrowableType) {
    } catch (throwable: Throwable) {
        fail("Exception of unexpected type was thrown.\nExpected: ${ThrowableType::class.qualifiedName}\nActual: ${throwable::class.qualifiedName}")
    }
}

@JvmName("orThrowCheckingType")
context(assertionScope: AssertionScope)
public inline fun <reified ThrowableType: Throwable> Expect.Companion.toThrow(block: () -> Unit, checker: Expect<ThrowableType>.() -> Unit)  {
    try {
        block()
        fail("No exception was thrown.")
    } catch (throwable: ThrowableType) {
        of(throwable, checker)
    } catch (throwable: Throwable) {
        fail("Exception of unexpected type was thrown.\nExpected: ${ThrowableType::class.qualifiedName}\nActual: ${throwable::class.qualifiedName}")
    }
}