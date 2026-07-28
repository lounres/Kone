/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.assertions


/**
 * The scope in which assertions are executed.
 *
 * An assertion scope defines how assertion results are consumed. The default implementation
 * throws an [AssertionError] when an assertion fails. Custom implementations can define
 * different behaviour, such as collecting failures for soft assertions or adding
 * additional context to failure messages.
 *
 * This interface uses a DSL marker annotation ([AssertionScope.Dsl]) to prevent unwanted
 * implicit receivers in assertion lambdas, ensuring type safety and preventing
 * accidental calls to functions from other scopes.
 */
@AssertionScope.Dsl
public interface AssertionScope {
    /**
     * Consumes an assertion result, typically by throwing an [AssertionError] if the assertion fails.
     *
     * Implementations of this interface define how assertion failures are handled.
     * The default companion object implementation throws an [AssertionError] with the assertion message and cause.
     *
     * @param assertionResult The assertion to consume, containing the failure message and optional cause.
     */
    public fun consumeAssertion(assertionResult: Assertion)
    
    /**
     * Default implementation of [AssertionScope] that throws an [AssertionError] when an assertion fails.
     *
     * This companion object serves as the default assertion scope for top-level assertion functions.
     */
    public companion object : AssertionScope {
        override fun consumeAssertion(assertionResult: Assertion) {
            throw AssertionError("Assertion failed:\n${assertionResult.message}", assertionResult.cause)
        }
    }
    
    /**
     * Represents the result of an assertion check.
     *
     * @property message The failure message describing what went wrong. This message is used to construct
     *   the error message when the assertion fails.
     * @property cause The throwable that caused the assertion to fail, if any. This is included as the
     *   cause of the thrown [AssertionError].
     */
    public data class Assertion(val message: String, val cause: Throwable? = null)
    
    /**
     * DSL marker annotation to prevent unwanted implicit receivers in assertion lambdas.
     *
     * This annotation ensures that only explicitly imported or qualified functions are available
     * within assertion blocks, preventing accidental calls to functions from other scopes.
     */
    @DslMarker
    public annotation class Dsl
}

/**
 * Invokes the given assertion block with this [AssertionScope] as the context receiver.
 *
 * This operator allows for a more idiomatic syntax when using assertion scopes.
 *
 * @receiver The assertion scope that will be passed as the context receiver to the block.
 * @param block The assertion block to execute with this scope as the context receiver.
 */
public inline operator fun AssertionScope.invoke(block: context(AssertionScope) () -> Unit) {
    block(this)
}