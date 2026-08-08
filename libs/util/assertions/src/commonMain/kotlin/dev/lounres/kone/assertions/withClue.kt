/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.assertions

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName


/**
 * Executes the given assertion [block] with a descriptive [clue] that is prefixed to any
 * assertion failure messages.
 *
 * The clue provides additional context for assertion failures. When an assertion within the
 * block fails, the failure message will include the clue on the first line, followed by the
 * original failure message (indented). This makes it easier to understand which assertion
 * failed and why.
 *
 * @receiver The assertion scope that will receive the prefixed failure messages.
 * @param clue A descriptive string that will be prefixed to any assertion failure messages
 *   originating from within the block.
 * @param block The assertion block to execute with the clue as context.
 *   Called exactly once.
 */
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
    try {
        block(softAssertionScope)
    } catch (e: FirmAssertionError) {
        throw FirmAssertionError(
            AssertionScope.Assertion(
                message = buildString {
                    appendLine(clue)
                    for (line in e.assertionResult.message.lines()) {
                        appendLine("  $line")
                    }
                },
                cause = e.assertionResult.cause,
            )
        )
    }
}

/**
 * Executes the given assertion [block] with a descriptive [clue] that is prefixed to any
 * assertion failure messages within the current assertion scope.
 *
 * This contextual version uses the [assertionScope] from the surrounding context.
 * The clue provides additional context for assertion failures. When an assertion within the
 * block fails, the failure message will include the clue on the first line, followed by the
 * original failure message (indented).
 *
 * @param assertionScope The assertion scope that will receive the prefixed failure messages.
 * @param clue A descriptive string that will be prefixed to any assertion failure messages
 *   originating from within the block.
 * @param block The assertion block to execute with the clue as context.
 *   Called exactly once.
 * @see AssertionScope.withClue
 */
@JvmName("withClueContextual")
context(assertionScope: AssertionScope)
public inline fun withClue(clue: String, block: context(AssertionScope) () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    assertionScope.withClue(clue, block)
}

/**
 * Executes the given assertion [block] with a lazily-evaluated [clue] that is prefixed to any
 * assertion failure messages.
 *
 * The clue is obtained by calling the [clue] function only when an assertion within the block
 * fails. This allows for expensive clue computation to be deferred until needed.
 * When an assertion within the block fails, the failure message will include the result of
 * the clue function on the first line, followed by the original failure message (indented).
 *
 * @receiver The assertion scope that will receive the prefixed failure messages.
 * @param clue A function that provides a descriptive string. Called only if an assertion within
 *   the block fails.
 * @param block The assertion block to execute with the lazily-evaluated clue as context.
 *   Called exactly once.
 */
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
    try {
        block(softAssertionScope)
    } catch (e: FirmAssertionError) {
        throw FirmAssertionError(
            AssertionScope.Assertion(
                message = buildString {
                    appendLine(clue())
                    for (line in e.assertionResult.message.lines()) {
                        appendLine("  $line")
                    }
                },
                cause = e.assertionResult.cause,
            )
        )
    }
}

/**
 * Executes the given assertion [block] with a lazily-evaluated [clue] that is prefixed to any
 * assertion failure messages within the current assertion scope.
 *
 * This contextual version uses the [assertionScope] from the surrounding context.
 * The clue is obtained by calling the [clue] function only when an assertion within the block
 * fails. This allows for expensive clue computation to be deferred until needed.
 * When an assertion within the block fails, the failure message will include the result of
 * the clue function on the first line, followed by the original failure message (indented).
 *
 * @param assertionScope The assertion scope that will receive the prefixed failure messages.
 * @param clue A function that provides a descriptive string. Called only if an assertion within
 *   the block fails.
 * @param block The assertion block to execute with the lazily-evaluated clue as context.
 *   Called exactly once.
 * @see AssertionScope.withClue
 */
@JvmName("withClueContextual")
context(assertionScope: AssertionScope)
public inline fun withClue(crossinline clue: () -> String, block: context(AssertionScope) () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    assertionScope.withClue(clue, block)
}