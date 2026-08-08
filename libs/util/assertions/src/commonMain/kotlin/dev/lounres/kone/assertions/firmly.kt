/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.assertions

import kotlin.jvm.JvmName


public class FirmAssertionError(public val assertionResult: AssertionScope.Assertion) : Throwable()

public inline fun AssertionScope.firmly(block: context(AssertionScope) () -> Unit) {
    val softAssertionScope = object : AssertionScope {
        override fun consumeAssertion(assertionResult: AssertionScope.Assertion) {
            throw FirmAssertionError(assertionResult)
        }
    }
    try {
        block(softAssertionScope)
    } catch (e: FirmAssertionError) {
        this.consumeAssertion(
            AssertionScope.Assertion(
                message = e.assertionResult.message,
                cause = e.assertionResult.cause?.apply { addSuppressed(e) } ?: e,
            )
        )
    }
}

@JvmName("softlyContextual")
context(assertionScope: AssertionScope)
public inline fun firmly(block: context(AssertionScope) () -> Unit) {
    assertionScope.firmly(block)
}