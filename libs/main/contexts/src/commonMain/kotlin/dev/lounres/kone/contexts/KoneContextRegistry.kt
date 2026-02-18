/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.contexts

import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.OwnedRegistry
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.build
import dev.lounres.kone.registry.get
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName


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
    public companion object;
    
    public fun interface Provider {
        public fun get(): KoneContextRegistry
    }
}

public fun KoneContextRegistry.tryToGetAll() {
    for ((_, provider) in this) {
        val _ = provider.get()
    }
}

///**
// * Provides receiver for Kone context registry.
// */
//public inline operator fun <R> KoneContextRegistry.invoke(block: KoneContextRegistry.() -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
//    return block(this)
//}

@Suppress("UnusedReceiverParameter")
public inline fun <Result> KoneContextRegistry.koneContext(
    block: () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block()
}

@JvmName("koneContextContextual")
context(_: KoneContextRegistry)
public inline fun <Result> koneContext(
    block: () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block()
}

public inline fun <Context1, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    block: context(Context1) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
    )
}

@JvmName("koneContextContextual")
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

public inline fun <Context1, Context2, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    block: context(Context1, Context2) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
    )
}

@JvmName("koneContextContextual")
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

public inline fun <Context1, Context2, Context3, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    block: context(Context1, Context2, Context3) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
    )
}

@JvmName("koneContextContextual")
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

public inline fun <Context1, Context2, Context3, Context4, Result> KoneContextRegistry.koneContext(
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
        this[key1],
        this[key2],
        this[key3],
        this[key4],
    )
}

@JvmName("koneContextContextual")
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

public inline fun <Context1, Context2, Context3, Context4, Context5, Result> KoneContextRegistry.koneContext(
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
        this[key1],
        this[key2],
        this[key3],
        this[key4],
        this[key5],
    )
}

@JvmName("koneContextContextual")
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
public inline fun KoneContextRegistry.Companion.build(block: (@KoneContextRegistryBuilderDsl MutableOwnedRegistry<KoneContextRegistry>).() -> Unit): KoneContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneContextRegistry(OwnedRegistry.build { this.block() })
}

public inline fun KoneContextRegistry.Companion.buildWithProvider(
    block: context(KoneContextRegistry.Provider) (@KoneContextRegistryBuilderDsl MutableOwnedRegistry<KoneContextRegistry>).() -> Unit
): KoneContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val provider = object : KoneContextRegistry.Provider {
        var result: KoneContextRegistry? = null
        override fun get(): KoneContextRegistry =
            result ?: error("KoneContextRegistry is not yet initialized but was requested by its properties.")
    }
    val result = KoneContextRegistry(OwnedRegistry.build { block(provider, this) })
    provider.result = result
    return result
}