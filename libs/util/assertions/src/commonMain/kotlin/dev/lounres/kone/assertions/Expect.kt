/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.assertions

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@Expect.Dsl
public fun interface Expect<out Value> {
    public fun exposeValue(): Value
    
    public companion object;
    
    @DslMarker
    public annotation class Dsl
}

public infix fun <Value> Expect.Companion.of(value: Value): Expect<Value> = Expect { value }
public inline fun <Value> Expect.Companion.of(value: Value, block: Expect<Value>.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    of(value).block()
}

public infix fun <Value> Expect.Companion.using(provider: () -> Value): Expect<Value> = Expect { provider() }
public inline fun <Value> Expect.Companion.using(noinline provider: () -> Value, block: Expect<Value>.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    using(provider).block()
}
public infix fun <OldValue, NewValue> Expect<OldValue>.using(provider: (OldValue) -> NewValue): Expect<NewValue> = Expect { provider(this.exposeValue()) }
public inline fun <OldValue, NewValue> Expect<OldValue>.using(noinline provider: (OldValue) -> NewValue, block: Expect<NewValue>.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    using(provider).block()
}

public infix fun <Value> Expect.Companion.ofLazy(provider: () -> Value): Expect<Value> {
    val value by lazy(provider)
    return Expect { value }
}
public inline fun <Value> Expect.Companion.ofLazy(noinline provider: () -> Value, block: Expect<Value>.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    ofLazy(provider).block()
}
public infix fun <OldValue, NewValue> Expect<OldValue>.usingLazily(provider: (OldValue) -> NewValue): Expect<NewValue> {
    val value by lazy { provider(this.exposeValue()) }
    return Expect { value }
}
public inline fun <OldValue, NewValue> Expect<OldValue>.usingLazily(noinline provider: (OldValue) -> NewValue, block: Expect<NewValue>.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    usingLazily(provider).block()
}