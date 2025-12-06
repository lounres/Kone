/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class Position<Point>(public val pointType: SuppliedType) : RegistryKey<Point> {
    override fun equals(other: Any?): Boolean = other is Position<*> && pointType == other.pointType
    override fun hashCode(): Int = pointType.hashCode()
    override fun toString(): String = "dev.lounres.kone.computationalGeometry.polytopes.Position<$pointType>"
}