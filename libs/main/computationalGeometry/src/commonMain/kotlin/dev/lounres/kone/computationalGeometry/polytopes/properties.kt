/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.OwnedRegistry
import dev.lounres.kone.registry.OwnedRegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.jvm.JvmName


public val OwnedRegistry<Polytope>.name: String
    @JvmName("getPolytopeOwnedRegistryName") get() = get(Polytope.NameKey)

public var MutableOwnedRegistry<Polytope>.name: String
    @JvmName("getPolytopeMutableOwnedRegistryName") get() = get(Polytope.NameKey)
    @JvmName("setPolytopeMutableOwnedRegistryName") set(value) { set(Polytope.NameKey, value) }

public var OwnedRegistryBuilder<Polytope>.name: String
    @JvmName("getPolytopeOwnedRegistryBuilderName") get() = get(Polytope.NameKey)
    @JvmName("setPolytopeOwnedRegistryBuilderName") set(value) { set(Polytope.NameKey, value) }

public val Polytope.name: String
    @JvmName("getPolytopeName") get() = properties.name

public class Position<Point>(public val pointType: SuppliedType) : RegistryKey<Point> {
    override fun equals(other: Any?): Boolean = other is Position<*> && pointType == other.pointType
    override fun hashCode(): Int = pointType.hashCode()
    override fun toString(): String = "dev.lounres.kone.computationalGeometry.polytopes.Position<$pointType>"
}