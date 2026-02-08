/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.reificationException
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.registry.OwnedRegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImplied
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedType


@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
public class IntModuloRing(modulus: Int) : Reification<Int>, Ring<Int>, Hashing<Int> {

    public val modulus: Int

    init {
        require(modulus != 0) { "modulus can not be zero" }
        this.modulus = if (modulus < 0) -modulus else modulus
    }
    
    override fun contains(element: Any?): Boolean = element is Int
    override fun reifyMaybe(element: Any?): Maybe<Int> = if (element is Int) Some(element) else None
    override fun reifyOrNull(element: Any?): Int? = element as? Int
    override fun reify(element: Any?): Int = element as? Int ?: reificationException()

    override fun Int.hash(): Int = this.mod(modulus)

    public override val zero: Int = 0
    public override val one: Int = 1

    // TODO: Replace `%` with `mod`

    public override fun valueOf(arg: Int): Int = arg % modulus
    public override fun valueOf(arg: UInt): Int = arg.toInt() % modulus
    public override fun valueOf(arg: Long): Int = (arg % modulus).toInt()
    public override fun valueOf(arg: ULong): Int = (arg.toLong() % modulus).toInt()

    // TODO: Upgrade operations. Fix cases of big numbers.

    override inline operator fun Int.unaryMinus(): Int = if (this == 0) 0 else modulus - this
    override inline operator fun Int.plus(other: Int): Int = (this + other) % modulus
    override inline operator fun Int.minus(other: Int): Int = (this - other) % modulus
    override inline operator fun Int.times(other: Int): Int = (this * other) % modulus
    
    override operator fun Int.plus(other: UInt): Int = ((this + other.toInt()) % modulus).toInt()
    override operator fun Int.minus(other: UInt): Int = ((this - other.toInt()) % modulus).toInt()
    override operator fun Int.times(other: UInt): Int = ((this * other.toInt()) % modulus).toInt()
    
    override operator fun Int.plus(other: Long): Int = ((this + other) % modulus).toInt()
    override operator fun Int.minus(other: Long): Int = ((this - other) % modulus).toInt()
    override operator fun Int.times(other: Long): Int = ((this * other) % modulus).toInt()

    override operator fun Int.plus(other: ULong): Int = ((this + other.toLong()) % modulus).toInt()
    override operator fun Int.minus(other: ULong): Int = ((this - other.toLong()) % modulus).toInt()
    override operator fun Int.times(other: ULong): Int = ((this * other.toLong()) % modulus).toInt()

    override operator fun UInt.plus(other: Int): Int = ((this.toInt() + other) % modulus).toInt()
    override operator fun UInt.minus(other: Int): Int = ((this.toInt() - other) % modulus).toInt()
    override operator fun UInt.times(other: Int): Int = ((this.toInt() * other) % modulus).toInt()
    
    override operator fun Long.plus(other: Int): Int = ((this + other) % modulus).toInt()
    override operator fun Long.minus(other: Int): Int = ((this - other) % modulus).toInt()
    override operator fun Long.times(other: Int): Int = ((this * other) % modulus).toInt()
    
    override operator fun ULong.plus(other: Int): Int = ((this.toLong() + other) % modulus).toInt()
    override operator fun ULong.minus(other: Int): Int = ((this.toLong() - other) % modulus).toInt()
    override operator fun ULong.times(other: Int): Int = ((this.toLong() * other) % modulus).toInt()
    
    public companion object
}

context(_: OwnedRegistryBuilder<KoneContextRegistry>)
public fun IntModuloRing.Companion.set(modulus: Int) {
    val ring = IntModuloRing(modulus)
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val intModuloSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.Int",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in IntModuloRing>>(
        Reification.Key(intModuloSuppliedType),
        Ring.Key(intModuloSuppliedType),
        Hashing.Key(intModuloSuppliedType),
    ).forEach {
        it.withImplied correspondsTo ring
    }
}