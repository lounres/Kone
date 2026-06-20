/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.assertions

import kotlin.jvm.JvmName


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
                    }
                )
            )
        }
    }
}

@JvmName("softlyContextual")
context(assertionScope: AssertionScope)
public inline fun softly(block: context(AssertionScope) () -> Unit) {
    assertionScope.softly(block)
}