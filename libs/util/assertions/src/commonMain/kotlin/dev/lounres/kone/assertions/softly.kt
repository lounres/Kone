/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.assertions

import kotlin.jvm.JvmName


/**
 * Executes the given assertion [block] in a "soft" mode, collecting all assertion failures
 * and reporting them together at the end.
 *
 * In soft assertion mode, individual assertion failures within the block do not immediately
 * throw exceptions. Instead, all failures are collected and reported together as a single
 * assertion failure at the end of the block execution. This is useful when you want to check
 * multiple conditions and see all failures at once, rather than stopping at the first failure.
 *
 * The failure message includes all individual assertion failures formatted in a tree structure
 * with their messages and stack traces (if available).
 *
 * @receiver The assertion scope that will receive the aggregated failure report.
 * @param block The assertion block to execute in soft mode. All assertion failures within this block
 *   are collected and reported together.
 */
public inline fun AssertionScope.softly(block: context(AssertionScope) () -> Unit) {
    val assertionResults = mutableListOf<AssertionScope.Assertion>()
    val softAssertionScope = object : AssertionScope {
        override fun consumeAssertion(assertionResult: AssertionScope.Assertion) {
            assertionResults.add(assertionResult)
        }
    }
    try {
        block(softAssertionScope)
    } finally {
        if (assertionResults.isNotEmpty()) {
            this.consumeAssertion(
                AssertionScope.Assertion(
                    message = assertionResults.joinToString(
                        prefix = "Soft assertion failed:\n",
                        separator = "",
                    ) {
                        buildString {
                            val message = it.message
                            val messageLines = message.lines()
                            if (messageLines.isNotEmpty()) {
                                appendLine("├─${messageLines[0]}")
                                for (line in messageLines.drop(1)) {
                                    appendLine("│ $line")
                                }
                            } else {
                                appendLine("├─<empty message>")
                            }
                            val cause = it.cause
                            if (cause != null) {
                                appendLine("│ ")
                                for (line in cause.stackTraceToString().lines()) {
                                    appendLine("│ $line")
                                }
                            }
                        }
                    },
                )
            )
        }
    }
}

/**
 * Executes the given assertion [block] in a "soft" mode within the current assertion scope.
 *
 * This contextual version uses the [assertionScope] from the surrounding context.
 * In soft assertion mode, individual assertion failures within the block do not immediately
 * throw exceptions. Instead, all failures are collected and reported together as a single
 * assertion failure at the end of the block execution.
 *
 * @param assertionScope The assertion scope that will receive the aggregated failure report.
 * @param block The assertion block to execute in soft mode. All assertion failures within this block
 *   are collected and reported together.
 * @see AssertionScope.softly
 */
@JvmName("softlyContextual")
context(assertionScope: AssertionScope)
public inline fun softly(block: context(AssertionScope) () -> Unit) {
    assertionScope.softly(block)
}