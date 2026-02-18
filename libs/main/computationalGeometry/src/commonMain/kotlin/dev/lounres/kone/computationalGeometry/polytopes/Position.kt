package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.get
import dev.lounres.kone.suppliedTypes.SuppliedType


public class Position<Point>(public val pointType: SuppliedType) : RegistryKey<Point> {
    override fun equals(other: Any?): Boolean = other is Position<*> && pointType == other.pointType
    override fun hashCode(): Int = pointType.hashCode()
    override fun toString(): String = "dev.lounres.kone.computationalGeometry.polytopes.Position<$pointType>"
}

public fun <Point> Polytope.positionOfType(pointType: SuppliedType): Point = properties[Position<Point>(pointType)]