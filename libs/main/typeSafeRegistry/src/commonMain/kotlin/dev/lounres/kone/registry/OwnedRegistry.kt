/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline


@JvmInline
public value class OwnedRegistry<Owner>(public val registry: Registry) : Registry by registry {
    override fun toString(): String = registry.toString()
    
    public companion object
}

public fun <Owner> OwnedRegistry.Companion.empty(): OwnedRegistry<Owner> = OwnedRegistry(Registry.Empty)

@JvmInline
public value class MutableOwnedRegistry<Owner>(public val registry: MutableRegistry) : MutableRegistry by registry {
    override fun toString(): String = registry.toString()
}

public fun <Owner> MutableOwnedRegistry(): MutableOwnedRegistry<Owner> = MutableOwnedRegistry(MutableRegistry())

public fun <Owner> MutableOwnedRegistry<Owner>.asImmutable(): OwnedRegistry<Owner> = OwnedRegistry(this.registry)

public inline fun <Owner> OwnedRegistry.Companion.build(block: MutableOwnedRegistry<Owner>.() -> Unit): OwnedRegistry<Owner> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return OwnedRegistry(registry = Registry.build { MutableOwnedRegistry<Owner>(this).block() })
}