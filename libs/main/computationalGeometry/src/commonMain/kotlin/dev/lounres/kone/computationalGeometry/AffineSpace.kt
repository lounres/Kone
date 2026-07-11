/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Minus
import dev.lounres.kone.algebraic.Module
import dev.lounres.kone.algebraic.Plus
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.contexts.KoneContextHolderContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface AffineSpaceOverRing<Number, Vector, Point> : Module<Number, Vector> {
    @KoneContextHolderContext
    public val pointPlusVector: Plus<Point, Vector, Point>
    @KoneContextHolderContext
    public val pointMinusVector: Minus<Point, Vector, Point>
    @KoneContextHolderContext
    public val vectorPlusPoint: Plus<Vector, Point, Point>
    @KoneContextHolderContext
    public val pointMinusPoint: Minus<Point, Point, Vector>
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector, @Supply Point> : SuppliedTypeRegistryKey<AffineSpaceOverRing<Number, Vector, Point>>() {
        override val impliedKeys: ImpliedKeysRegistry<AffineSpaceOverRing<Number, Vector, Point>> by lazy {
            ImpliedKeysRegistry {
                Module.Key<Number, Vector>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.AffineSpaceOverRing.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}, ${suppliedTypeOf<Point>()}>"
    }
}

public interface AffineSpaceOverField<Number, Vector, Point> : VectorSpace<Number, Vector>, AffineSpaceOverRing<Number, Vector, Point> {
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Vector, @Supply Point> : SuppliedTypeRegistryKey<AffineSpaceOverField<Number, Vector, Point>>() {
        override val impliedKeys: ImpliedKeysRegistry<AffineSpaceOverField<Number, Vector, Point>> by lazy {
            ImpliedKeysRegistry {
                VectorSpace.Key<Number, Vector>().impliesSame()
                AffineSpaceOverRing.Key<Number, Vector, Point>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.AffineSpaceOverField.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Vector>()}, ${suppliedTypeOf<Point>()}>"
    }
}

private class AffineSpaceOverFieldViaVectorSpace<Number, Vector>(
    private val vectorSpace: VectorSpace<Number, Vector>,
) : AffineSpaceOverField<Number, Vector, PointWrapper<Vector>>, VectorSpace<Number, Vector> by vectorSpace {
    override val pointPlusVector: Plus<PointWrapper<Vector>, Vector, PointWrapper<Vector>> = Plus { other ->
        vectorSpace.numberPlusNumber { PointWrapper(this.vector + other) }
    }
    override val pointMinusVector: Minus<PointWrapper<Vector>, Vector, PointWrapper<Vector>> = Minus { other ->
        vectorSpace.numberMinusNumber { PointWrapper(this.vector - other) }
    }
    override val vectorPlusPoint: Plus<Vector, PointWrapper<Vector>, PointWrapper<Vector>> = Plus { other ->
        vectorSpace.numberPlusNumber { PointWrapper(this + other.vector) }
    }
    override val pointMinusPoint: Minus<PointWrapper<Vector>, PointWrapper<Vector>, Vector> = Minus { other ->
        vectorSpace.numberMinusNumber { this.vector - other.vector }
    }
}

public fun <Number, Vector> AffineSpaceOverField.Companion.viaVectorSpace(vectorSpace: VectorSpace<Number, Vector>): AffineSpaceOverField<Number, Vector, PointWrapper<Vector>> =
    AffineSpaceOverFieldViaVectorSpace(vectorSpace)

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number, @Supply Vector> AffineSpaceOverField.Companion.setViaVectorSpaceFor() {
    AffineSpaceOverField.Key<Number, Vector, PointWrapper<Vector>>() correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        viaVectorSpace(koneContextRegistry[VectorSpace.Key<Number, Vector>()])
    }
}