/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.assertions

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * A type representing a value under assertion.
 *
 * The [Expect] interface wraps a value and provides a way to expose it for assertion checks.
 * It serves as the foundation for the assertion DSL, allowing for chained assertion calls
 * in a type-safe manner.
 *
 * Implementations of this functional interface must provide the [exposeValue] function,
 * which returns the underlying value being asserted on.
 *
 * @param Value The type of the value being asserted on. This is a covariant type parameter,
 *   allowing for flexible subtyping in assertion chains.
 */
@Expect.Dsl
public fun interface Expect<out Value> {
    /**
     * Exposes the underlying value being asserted on.
     *
     * This function is the single abstract method of the [Expect] functional interface.
     * Implementations must return the value that will be used for assertion checks.
     *
     * @return The value being asserted on.
     */
    public fun exposeValue(): Value
    
    /**
     * Companion object for the [Expect] interface, providing factory methods for creating assertions.
     */
    public companion object;
    
    /**
     * DSL marker annotation to prevent unwanted implicit receivers in assertion lambdas.
     *
     * This annotation ensures that only explicitly imported or qualified functions from
     * the [Expect] scope are available within assertion blocks.
     */
    @DslMarker
    public annotation class Dsl
}

/**
 * Creates an [Expect] assertion for the given [value].
 *
 * This is the simplest way to start an assertion chain. The value is captured eagerly.
 *
 * @param Value The type of the value being asserted on.
 * @param value The value to assert on.
 * @return An [Expect] instance wrapping the value.
 */
public infix fun <Value> Expect.Companion.of(value: Value): Expect<Value> = Expect { value }

/**
 * Creates an [Expect] assertion for the given [value] and executes the assertion [block] on it.
 *
 * The value is captured eagerly, and the block is called exactly once with the value as the receiver.
 *
 * @param Value The type of the value being asserted on.
 * @param value The value to assert on.
 * @param block The assertion block to execute with the value as the receiver.
 */
public inline fun <Value> Expect.Companion.of(value: Value, block: Expect<Value>.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    of(value).block()
}

/**
 * Creates an [Expect] assertion using a value [provider] function.
 *
 * The value is obtained by calling the provider function. This allows for lazy evaluation
 * of the value at the time the assertion is created.
 *
 * @param Value The type of the value being asserted on.
 * @param provider A function that provides the value to assert on.
 * @return An [Expect] instance wrapping the provided value.
 */
public infix fun <Value> Expect.Companion.using(provider: () -> Value): Expect<Value> = Expect { provider() }

/**
 * Creates an [Expect] assertion using a value [provider] function and executes the assertion [block] on it.
 *
 * The value is obtained by calling the provider function, and the block is called exactly once
 * with the provided value as the receiver.
 *
 * @param Value The type of the value being asserted on.
 * @param provider A function that provides the value to assert on.
 * @param block The assertion block to execute with the provided value as the receiver.
 */
public inline fun <Value> Expect.Companion.using(noinline provider: () -> Value, block: Expect<Value>.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    using(provider).block()
}

/**
 * Transforms an [Expect] assertion from one value type to another using a [provider] function.
 *
 * The provider function is called with the current value (obtained via [exposeValue]) and the
 * result is wrapped in a new [Expect] instance.
 *
 * @param OldValue The type of the current value.
 * @param NewValue The type of the transformed value.
 * @param provider A function that transforms the current value to a new value.
 * @return A new [Expect] instance wrapping the transformed value.
 */
public infix fun <OldValue, NewValue> Expect<OldValue>.using(provider: (OldValue) -> NewValue): Expect<NewValue> = Expect { provider(this.exposeValue()) }

/**
 * Transforms an [Expect] assertion from one value type to another using a [provider] function
 * and executes the assertion [block] on the transformed value.
 *
 * The provider function is called with the current value, and the block is called exactly once
 * with the transformed value as the receiver.
 *
 * @param OldValue The type of the current value.
 * @param NewValue The type of the transformed value.
 * @param provider A function that transforms the current value to a new value.
 * @param block The assertion block to execute with the transformed value as the receiver.
 */
public inline fun <OldValue, NewValue> Expect<OldValue>.using(noinline provider: (OldValue) -> NewValue, block: Expect<NewValue>.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    using(provider).block()
}

/**
 * Creates an [Expect] assertion using a lazily-evaluated value [provider] function.
 *
 * The value is obtained by calling the provider function only when [exposeValue] is first called.
 * The value is cached after the first evaluation.
 *
 * @param Value The type of the value being asserted on.
 * @param provider A function that provides the value to assert on. Called at most once.
 * @return An [Expect] instance wrapping the lazily-provided value.
 */
public infix fun <Value> Expect.Companion.ofLazy(provider: () -> Value): Expect<Value> {
    val value by lazy(provider)
    return Expect { value }
}

/**
 * Creates an [Expect] assertion using a lazily-evaluated value [provider] function
 * and executes the assertion [block] on it.
 *
 * The value is obtained lazily, and the block is called exactly once with the value as the receiver.
 *
 * @param Value The type of the value being asserted on.
 * @param provider A function that provides the value to assert on. Called at most once.
 * @param block The assertion block to execute with the lazily-provided value as the receiver.
 */
public inline fun <Value> Expect.Companion.ofLazy(noinline provider: () -> Value, block: Expect<Value>.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    ofLazy(provider).block()
}

/**
 * Transforms an [Expect] assertion from one value type to another using a lazily-evaluated [provider] function.
 *
 * The provider function is called lazily (only when [exposeValue] is first called on the result)
 * with the current value, and the result is cached.
 *
 * @param OldValue The type of the current value.
 * @param NewValue The type of the transformed value.
 * @param provider A function that transforms the current value to a new value. Called at most once.
 * @return A new [Expect] instance wrapping the lazily-transformed value.
 */
public infix fun <OldValue, NewValue> Expect<OldValue>.usingLazily(provider: (OldValue) -> NewValue): Expect<NewValue> {
    val value by lazy { provider(this.exposeValue()) }
    return Expect { value }
}

/**
 * Transforms an [Expect] assertion from one value type to another using a lazily-evaluated [provider] function
 * and executes the assertion [block] on the transformed value.
 *
 * The provider function is called lazily with the current value, and the block is called exactly once
 * with the transformed value as the receiver.
 *
 * @param OldValue The type of the current value.
 * @param NewValue The type of the transformed value.
 * @param provider A function that transforms the current value to a new value. Called at most once.
 * @param block The assertion block to execute with the lazily-transformed value as the receiver.
 */
public inline fun <OldValue, NewValue> Expect<OldValue>.usingLazily(noinline provider: (OldValue) -> NewValue, block: Expect<NewValue>.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    usingLazily(provider).block()
}