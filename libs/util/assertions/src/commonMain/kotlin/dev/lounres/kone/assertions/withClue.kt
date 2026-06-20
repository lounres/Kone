/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.assertions

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName


public inline fun AssertionScope.withClue(clue: String, block: context(AssertionScope) () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val softAssertionScope = object : AssertionScope {
        override fun consumeAssertion(assertionResult: AssertionScope.Assertion) {
            this@withClue.consumeAssertion(
                assertionResult = AssertionScope.Assertion(
                    message = buildString {
                        appendLine(clue)
                        for (line in assertionResult.message.lines()) {
                            appendLine("  $line")
                        }
                    },
                    cause = assertionResult.cause,
                ),
            )
        }
    }
    block(softAssertionScope)
}

@JvmName("withClueContextual")
context(assertionScope: AssertionScope)
public inline fun withClue(clue: String, block: context(AssertionScope) () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    assertionScope.withClue(clue, block)
}

public inline fun AssertionScope.withClue(crossinline clue: () -> String, block: context(AssertionScope) () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val softAssertionScope = object : AssertionScope {
        override fun consumeAssertion(assertionResult: AssertionScope.Assertion) {
            this@withClue.consumeAssertion(
                assertionResult = AssertionScope.Assertion(
                    message = buildString {
                        appendLine(clue())
                        for (line in assertionResult.message.lines()) {
                            appendLine("  $line")
                        }
                    },
                    cause = assertionResult.cause,
                ),
            )
        }
    }
    block(softAssertionScope)
}

@JvmName("withClueContextual")
context(assertionScope: AssertionScope)
public inline fun withClue(crossinline clue: () -> String, block: context(AssertionScope) () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    assertionScope.withClue(clue, block)
}