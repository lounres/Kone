/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.context

import dev.lounres.kone.util.registry.Registry
import dev.lounres.kone.util.registry.RegistryBuilder
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.registry.getOrDefault
import dev.lounres.kone.util.registry.getOrElse
import dev.lounres.kone.util.registry.getOrNull
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


@JvmInline
public value class KoneContextRegistry(public val contexts: Registry)

public inline operator fun <R> KoneContextRegistry.invoke(block: KoneContextRegistry.() -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
    return block(this)
}

public fun <T> KoneContextRegistry.load(key: RegistryKey<T>): T = this.contexts[key]
public fun <T> KoneContextRegistry.loadOrNull(key: RegistryKey<T>): T? = this.contexts.getOrNull(key)
public fun <T> KoneContextRegistry.loadOrDefault(key: RegistryKey<T>, default: T): T = this.contexts.getOrDefault(key, default)
public inline fun <T> KoneContextRegistry.loadOrElse(key: RegistryKey<T>, block: () -> T): T = this.contexts.getOrElse(key, block)

@JvmInline
public value class KoneContextRegistryBuilder(public val contextsBuilder: RegistryBuilder)

public inline fun KoneContextRegistry(block: KoneContextRegistryBuilder.() -> Unit): KoneContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneContextRegistry(
        Registry {
            KoneContextRegistryBuilder(this).block()
        }
    )
}

public fun <T> KoneContextRegistryBuilder.install(key: RegistryKey<T>, value: T) {
    contextsBuilder[key] = value
}

public fun KoneContextRegistryBuilder.installAllFrom(otherKoneContextRegistry: KoneContextRegistry) {
    contextsBuilder.setFrom(otherKoneContextRegistry.contexts)
}