/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Module
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf
import kotlin.jvm.JvmName


@Suppress("INAPPLICABLE_JVM_NAME")
public interface AffineSpaceOverRing<Number, Vector, Point> : Module<Number, Vector> {
    @JvmName("plusPointVector")
    public operator fun Point.plus(other: Vector): Point
    @JvmName("minusPointVector")
    public operator fun Point.minus(other: Vector): Point
    @JvmName("plusVectorPoint")
    public operator fun Vector.plus(other: Point): Point
    @JvmName("minusPointPoint")
    public operator fun Point.minus(other: Point): Vector
    
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

@JvmName("pointPlusVector")
context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Point.plus(other: Vector): Point = with(affineSpace) { this@plus + other }

@JvmName("pointMinusVector")
context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Point.minus(other: Vector): Point = with(affineSpace) { this@minus - other }

@JvmName("vectorPlusPoint")
context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Vector.plus(other: Point): Point = with(affineSpace) { this@plus + other }

@JvmName("vectorMinusPoint")
context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Point.minus(other: Point): Vector = with(affineSpace) { this@minus - other }

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
    
    override fun PointWrapper<Vector>.plus(other: Vector): PointWrapper<Vector> =
        vectorSpace { PointWrapper(this.vector + other) }
    
    override fun PointWrapper<Vector>.minus(other: Vector): PointWrapper<Vector> =
        vectorSpace { PointWrapper(this.vector - other) }
    
    override fun Vector.plus(other: PointWrapper<Vector>): PointWrapper<Vector> =
        vectorSpace { PointWrapper(this + other.vector) }
    
    override fun PointWrapper<Vector>.minus(other: PointWrapper<Vector>): Vector =
        vectorSpace { this.vector - other.vector }
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