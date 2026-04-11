/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.algebraic.equality
import dev.lounres.kone.algebraic.hashing
import dev.lounres.kone.algebraic.monoid
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.relations.equality
import dev.lounres.kone.collections.list.relations.hashing
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.associate
import dev.lounres.kone.collections.map.associateWith
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.mapsTo
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.copyTo
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.mapIndexed
import dev.lounres.kone.collections.utils.mapTo
import dev.lounres.kone.collections.utils.plusAssign
import dev.lounres.kone.collections.utils.single
import dev.lounres.kone.collections.utils.sum
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.combinatorics.enumerative.cartesianProduct
import dev.lounres.kone.combinatorics.enumerative.permutationsWithoutRepetitions
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.OwnedProviderRegistry
import dev.lounres.kone.registry.build
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor
import kotlin.apply


public fun Polytope.validate(): Boolean {
    TODO()
}

public inline fun Polytope.copyMapping(
    propertiesMapper: (currentPolytope: Polytope, polytopesMapping: KoneList<KoneMap<Polytope, Polytope>>) -> OwnedProviderRegistry<Polytope>
): Polytope {
    val faces = KoneArrayFixedCapacityList<KoneMap<Polytope, Polytope>>(this.dimension)
    for (dim in 0u ..< this.dimension) {
        faces += this.faces[dim].associateWith(
            keyEquality = Equality.absoluteFor(),
            keyHashing = Hashing.defaultFor(),
        ) { polytope ->
            Polytope(
                dimension = dim,
                faces = polytope.faces.mapIndexed { subdim, dimFaces ->
                    dimFaces.mapTo(
                        KoneMutableReifiedSet.of(
                            elementReification = Reification.defaultFor(),
                            elementEquality = Equality.absoluteFor(),
                            elementHashing = Hashing.defaultFor(),
                        )
                    ) { faces[subdim][it] }
                },
                properties = propertiesMapper(polytope, faces)
            )
        }
    }
    return Polytope(
        dimension = dimension,
        faces = faces.map { dimFaces ->
            dimFaces.valuesView.copyTo(
                KoneMutableReifiedSet.of(
                    elementReification = Reification.defaultFor(),
                    elementEquality = Equality.absoluteFor(),
                    elementHashing = Hashing.defaultFor(),
                )
            )
        },
        properties = propertiesMapper(this, faces)
    )
}

public inline fun Polytope.copyBuilding(
    propertiesMapper: MutableOwnedProviderRegistry<Polytope>.(currentPolytope: Polytope, polytopesMapping: KoneList<KoneMap<Polytope, Polytope>>) -> Unit
): Polytope = copyMapping { currentPolytope, polytopesMapping -> OwnedProviderRegistry.build { propertiesMapper(currentPolytope, polytopesMapping) } }

public fun simplexOn(vertices: KoneList<Polytope>): Polytope {
    require(vertices.isNotEmpty()) { TODO() }
    require(vertices.all { it.dimension == 0u }) { TODO() }
    val dimension = vertices.size - 1u
    if (dimension == 0u) return vertices[0u]
    
    val faces = KoneArrayFixedCapacityList<KoneMap<KoneList<UInt>, Polytope>>(dimension + 2u)
    faces.add(
        vertices.withIndex().associate(
            keyEquality = KoneList.equality(UInt.equality()),
            keyHashing = KoneList.hashing(UInt.hashing()),
        ) { (val index, val vertex = value) ->
            KoneList.generate(dimension + 1u) { if (it == index) 1u else 0u } mapsTo vertex
        }
    )
    for (dim in 1u .. dimension + 1u) faces.add(
        KoneMap.build(
            keyEquality = KoneList.equality(UInt.equality()),
            keyHashing = KoneList.hashing(UInt.hashing()),
        ) {
            for (flags in KoneList.generate(dimension + 1u) { if (it <= dim) 1u else 0u }.permutationsWithoutRepetitions(equality = UInt.equality()))
                this[flags] = Polytope(
                    dimension = dim,
                    faces = KoneList.generate(dim) {
                        KoneMutableReifiedSet.of<Polytope>(
                            elementEquality = Equality.absoluteFor(),
                            elementHashing = Hashing.defaultFor(),
                        )
                    }.apply {
                        for (subflags in cartesianProduct(flags.map { (0u .. it).toKoneList() })) if ((UInt.monoid()) { subflags.sum() } in 1u .. dim)
                            this[(UInt.monoid()) { subflags.sum() } - 1u].add(faces[(UInt.monoid()) { subflags.sum() } - 1u][subflags])
                    }
                )
        }
    )
    return faces[dimension].valuesView.single()
}