/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.generate
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.forEachIndexed
import dev.lounres.kone.collections.utils.lastIndexThat
import dev.lounres.kone.registry.MutableRegistry
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface PolytopicConstruction {
    public val polytopes: KoneList<KoneReifiedSet<Polytope>>
    
    public val properties: Registry get() = Registry.Empty
    
    public companion object
}

public interface MutablePolytopicConstruction : PolytopicConstruction {
    public fun add(polytope: Polytope)
    public fun remove(polytope: Polytope)
    
    override val properties: MutableRegistry
    
    public companion object
}

public fun MutablePolytopicConstruction(
    properties: MutableRegistry = MutableRegistry(),
): MutablePolytopicConstruction = MutablePolytopicConstructionImpl(
    properties = properties,
)

private class MutablePolytopicConstructionImpl(
    override val properties: MutableRegistry
) : MutablePolytopicConstruction {
    override var polytopes: KoneMutableList<KoneMutableReifiedSet<Polytope>> = KoneArrayGrowableList()
    
    override fun add(polytope: Polytope) {
        if (polytope.dimension >= polytopes.size)
            polytopes.addSeveral(polytope.dimension - polytopes.size + 1u) {
                KoneMutableReifiedSet.of(
                    elementReification = Reification.defaultFor(),
                    elementEquality = Equality.absoluteFor(),
                    elementHashing = Hashing.defaultFor(),
                )
            }
        polytope.faces.forEachIndexed { dim, faces -> polytopes[dim].addAllFrom(faces) }
        polytopes[polytope.dimension].add(polytope)
    }
    
    override fun remove(polytope: Polytope) {
        if (polytope.dimension >= polytopes.size) return
        polytopes[polytope.dimension].remove(polytope)
        for (dim in polytope.dimension + 1u ..< polytopes.size)
            polytopes[dim].removeAllThat { polytope in it.faces[polytope.dimension] }
        val lastNonEmptyIndex = polytopes.lastIndexThat { _, element -> element.isNotEmpty() }
        if (lastNonEmptyIndex != polytopes.lastIndex)
            polytopes = KoneArrayGrowableList.generate(lastNonEmptyIndex + 1u) { polytopes[it] }
    }
}

public interface PolytopicConstructionBuilder : MutablePolytopicConstruction {
    public operator fun Polytope.unaryPlus() { add(this) }
    public operator fun Polytope.unaryMinus() { remove(this) }
}

@PublishedApi
internal class PolytopicConstructionBuilderImpl : PolytopicConstructionBuilder {
    override var polytopes: KoneMutableList<KoneMutableReifiedSet<Polytope>> = KoneArrayGrowableList()
    
    override fun add(polytope: Polytope) {
        if (polytope.dimension >= polytopes.size)
            polytopes.addSeveral(polytope.dimension - polytopes.size + 1u) {
                KoneMutableReifiedSet.of(
                    elementReification = Reification.defaultFor(),
                    elementEquality = Equality.absoluteFor(),
                    elementHashing = Hashing.defaultFor(),
                )
            }
        polytope.faces.forEachIndexed { dim, faces -> polytopes[dim].addAllFrom(faces) }
        polytopes[polytope.dimension].add(polytope)
    }
    
    override fun remove(polytope: Polytope) {
        if (polytope.dimension >= polytopes.size) return
        polytopes[polytope.dimension].remove(polytope)
        for (dim in polytope.dimension + 1u ..< polytopes.size)
            polytopes[dim].removeAllThat { polytope in it.faces[polytope.dimension] }
        val lastNonEmptyIndex = polytopes.lastIndexThat { _, element -> element.isNotEmpty() }
        if (lastNonEmptyIndex != polytopes.lastIndex)
            polytopes = KoneArrayGrowableList.generate(lastNonEmptyIndex + 1u) { polytopes[it] }
    }
    
    override val properties: MutableRegistry = MutableRegistry()
}

public inline fun PolytopicConstruction.Companion.build(block: PolytopicConstructionBuilder.() -> Unit): PolytopicConstruction {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return PolytopicConstructionBuilderImpl().apply(block)
}