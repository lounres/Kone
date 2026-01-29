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
    public companion object
}

public fun <Owner> OwnedRegistry.Companion.empty(): OwnedRegistry<Owner> = OwnedRegistry(Registry.Empty)

@JvmInline
public value class MutableOwnedRegistry<Owner>(public val registry: MutableRegistry) : MutableRegistry by registry

public fun <Owner> MutableOwnedRegistry(): MutableOwnedRegistry<Owner> = MutableOwnedRegistry(MutableRegistry())

@JvmInline
public value class OwnedRegistryBuilder<Owner> @PublishedApi internal constructor(public val registry: RegistryBuilder) : MutableRegistry by registry

public inline fun <Owner> OwnedRegistry.Companion.build(@BuilderInference block: OwnedRegistryBuilder<Owner>.() -> Unit): OwnedRegistry<Owner> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return OwnedRegistry(registry = Registry.build { OwnedRegistryBuilder<Owner>(this).block() })
}