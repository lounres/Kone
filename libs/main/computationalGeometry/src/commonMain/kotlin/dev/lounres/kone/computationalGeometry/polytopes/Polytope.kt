/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.registry.OwnedRegistry
import dev.lounres.kone.registry.OwnedRegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.build
import dev.lounres.kone.registry.empty
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


@Suppress("EqualsOrHashCode")
public class Polytope(
    public val dimension: UInt,
    public val faces: KoneList<KoneReifiedSet<Polytope>>,
    public val properties: OwnedRegistry<Polytope> = OwnedRegistry.empty(),
) {
    init {
        require(faces.size == dimension) { "Cannot instantiate polytope: dimension parameter $dimension is not equal to size ${faces.size} of faces list" }
    }
    
    override fun equals(other: Any?): Boolean = this === other
    
    override fun toString(): String = properties.getOrElse(NameKey) { super.toString() }
    
    public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, Polytope> =
        Delegate(
            Polytope(
                dimension = dimension,
                faces = faces,
            ) {
                setFrom(properties)
                name = property.name
            }
        )
    
    public data object NameKey : RegistryKey<String>
    
    private class Delegate(val vertex: Polytope) : ReadOnlyProperty<Any?, Polytope> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Polytope = vertex
    }
}

public fun Polytope(
    dimension: UInt,
    faces: KoneList<KoneReifiedSet<Polytope>>,
    propertiesBuilder: OwnedRegistryBuilder<Polytope>.() -> Unit
): Polytope =
    Polytope(
        dimension = dimension,
        faces = faces,
        properties = OwnedRegistry.build(propertiesBuilder),
    )

public val Polytope.verticesOrSelf: KoneReifiedSet<Polytope>
    get() =
        if (dimension != 0u) faces[0u]
        else KoneReifiedSet.of(
            this,
            elementReification = Reification.defaultFor(),
            elementEquality = Equality.absoluteFor(),
            elementHashing = Hashing.defaultFor(),
        )