/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.associateWith
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.copyTo
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.mapIndexed
import dev.lounres.kone.collections.utils.mapTo
import dev.lounres.kone.collections.utils.plusAssign
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.build
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor


public fun Polytope.validate(): Boolean {
    TODO()
}

public inline fun Polytope.copyMapping(
    propertiesMapper: (currentPolytope: Polytope, polytopesMapping: KoneList<KoneMap<Polytope, Polytope>>) -> Registry
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
    propertiesMapper: RegistryBuilder<Polytope>.(currentPolytope: Polytope, polytopesMapping: KoneList<KoneMap<Polytope, Polytope>>) -> Unit
): Polytope = copyMapping { currentPolytope, polytopesMapping ->  Registry.build { propertiesMapper(currentPolytope, polytopesMapping) } }