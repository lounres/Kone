/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.contexts

import dev.lounres.kone.registry.OwnedRegistry
import dev.lounres.kone.registry.OwnedRegistryBuilder
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.build
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


/**
 * Type-safe registry (a.k.a. type-safe map) made especially for contexts in Kone.
 */
@JvmInline
public value class KoneContextRegistry(
    /**
     * Underlying type-safe registry.
     */
    public val contexts: OwnedRegistry<KoneContextRegistry>
) : Registry by contexts {
    public companion object
}

/**
 * Provides receiver for Kone context registry.
 */
public inline operator fun <R> KoneContextRegistry.invoke(block: KoneContextRegistry.() -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this)
}

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Result> koneContext(
    key1: RegistryKey<Context1>,
    block: context(Context1) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
    )
}

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    block: context(Context1, Context2) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
    )
}

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    block: context(Context1, Context2, Context3) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
    )
}

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    block: context(Context1, Context2, Context3, Context4) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
    )
}

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Context5, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    block: context(Context1, Context2, Context3, Context4, Context5) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
        koneContextRegistry[key5],
    )
}

@DslMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
public annotation class KoneContextRegistryBuilderDsl

/**
 * Builder function for [KoneContextRegistry].
 */
public inline fun KoneContextRegistry.Companion.build(block: (@KoneContextRegistryBuilderDsl OwnedRegistryBuilder<KoneContextRegistry>).() -> Unit): KoneContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneContextRegistry(OwnedRegistry.build { this.block() })
}