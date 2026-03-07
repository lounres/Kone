package dev.lounres.kone.assertions


@Expect.Dsl
public fun interface Expect<out Value> {
    public fun exposeValue(): Value
    
    public companion object;
    
    @DslMarker
    public annotation class Dsl
}

public fun <Value> Expect.Companion.of(value: Value): Expect<Value> = Expect { value }
public inline fun <Value> Expect.Companion.of(value: Value, block: Expect<Value>.() -> Unit) {
    of(value).block()
}

public fun <Value> Expect.Companion.using(provider: () -> Value): Expect<Value> = Expect { provider() }
public fun <Value> Expect.Companion.using(provider: () -> Value, block: Expect<Value>.() -> Unit) {
    using(provider).block()
}
public fun <OldValue, NewValue> Expect<OldValue>.using(provider: (OldValue) -> NewValue): Expect<NewValue> = Expect { provider(this.exposeValue()) }
public fun <OldValue, NewValue> Expect<OldValue>.using(provider: (OldValue) -> NewValue, block: Expect<NewValue>.() -> Unit) {
    using(provider).block()
}

public fun <Value> Expect.Companion.usingLazily(provider: () -> Value): Expect<Value> {
    val value by lazy(provider)
    return Expect { value }
}
public fun <Value> Expect.Companion.usingLazily(provider: () -> Value, block: Expect<Value>.() -> Unit) {
    usingLazily(provider).block()
}
public fun <OldValue, NewValue> Expect<OldValue>.usingLazily(provider: (OldValue) -> NewValue): Expect<NewValue> {
    val value by lazy { provider(this.exposeValue()) }
    return Expect { value }
}
public fun <OldValue, NewValue> Expect<OldValue>.usingLazily(provider: (OldValue) -> NewValue, block: Expect<NewValue>.() -> Unit) {
    usingLazily(provider).block()
}