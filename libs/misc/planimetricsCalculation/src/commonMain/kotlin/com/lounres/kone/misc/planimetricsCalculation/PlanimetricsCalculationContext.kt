/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package com.lounres.kone.misc.planimetricsCalculation

import com.lounres.kone.computationalContext.ComputationalContext
import com.lounres.kone.computationalContext.invoke
import com.lounres.kone.algebraic.Ring
import com.lounres.kone.linearAlgebra.MatrixSpace
import com.lounres.kone.linearAlgebra.SquareMatrix
import com.lounres.kone.polynomial.LabeledPolynomial
import com.lounres.kone.polynomial.LabeledPolynomialSpace
import kotlin.contracts.InvocationKind.*
import kotlin.reflect.KProperty


public class PlanimetricsCalculationContext<C, out A : Ring<C>>(
    public val ring: A,
) : ComputationalContext {
    public val polynomialSpace: LabeledPolynomialSpace<C, A> by lazy { LabeledPolynomialSpace(ring) }
    public val matrixSpace: MatrixSpace<LabeledPolynomial<C>, LabeledPolynomialSpace<C, A>> by lazy { MatrixSpace(polynomialSpace) }

    public operator fun Point.Companion.getValue(thisRef: Any?, property: KProperty<*>) : Point<C> = Point(property.name)
    public operator fun Line.Companion.getValue(thisRef: Any?, property: KProperty<*>) : Line<C> = Line(property.name)
    public operator fun Quadric.Companion.getValue(thisRef: Any?, property: KProperty<*>) : Quadric<C> = Quadric(property.name)

    public infix fun Point<C>.equalsTo(other: Point<C>): Boolean = this === other || polynomialSpace {
        x * other.y eq y * other.x && y * other.z eq z * other.y && z * other.x eq x * other.z
    }
    // FIXME: KT-5351
    public inline infix fun Point<C>.notEqualsTo(other: Point<C>): Boolean = !(this equalsTo other)
    public inline infix fun Point<C>.eq(other: Point<C>): Boolean = this equalsTo other
    // FIXME: KT-5351
    public inline infix fun Point<C>.neq(other: Point<C>): Boolean = !(this equalsTo other)

    public infix fun Line<C>.equalsTo(other: Line<C>): Boolean = this === other || polynomialSpace {
        x * other.y eq y * other.x &&
                y * other.z eq z * other.y &&
                z * other.x eq x * other.z
    }
    // FIXME: KT-5351
    public inline infix fun Line<C>.notEqualsTo(other: Line<C>): Boolean = !(this equalsTo other)
    public inline infix fun Line<C>.eq(other: Line<C>): Boolean = this equalsTo other
    // FIXME: KT-5351
    public inline infix fun Line<C>.neq(other: Line<C>): Boolean = !(this equalsTo other)

    public infix fun Quadric<C>.equalsTo(other: Quadric<C>): Boolean = this === other || polynomialSpace {
        xx * other.yy eq yy * other.xx &&
                xx * other.zz eq zz * other.xx &&
                xx * other.xy eq xy * other.xx &&
                xx * other.xz eq xz * other.xx &&
                xx * other.yz eq yz * other.xx &&
                yy * other.zz eq zz * other.yy &&
                yy * other.xy eq xy * other.yy &&
                yy * other.xz eq xz * other.yy &&
                yy * other.yz eq yz * other.yy &&
                zz * other.xy eq xy * other.zz &&
                zz * other.xz eq xz * other.zz &&
                zz * other.yz eq yz * other.zz &&
                xy * other.xz eq xz * other.xy &&
                xy * other.yz eq yz * other.xy &&
                xz * other.yz eq yz * other.xz
    }
    // FIXME: KT-5351
    public inline infix fun Quadric<C>.notEqualsTo(other: Quadric<C>): Boolean = !(this equalsTo other)
    public inline infix fun Quadric<C>.eq(other: Quadric<C>): Boolean = this equalsTo other
    // FIXME: KT-5351
    public inline infix fun Quadric<C>.neq(other: Quadric<C>): Boolean = !(this equalsTo other)

    public val Quadric<C>.matrix: SquareMatrix<LabeledPolynomial<C>>
        get() = polynomialSpace {
            SquareMatrix(
                listOf(2 * xx, xy, xz),
                listOf(xy, 2 * yy, yz),
                listOf(xz, yz, 2 * zz)
            )
        }

    public inline infix fun Transformation<C>.equalsTo(other: Transformation<C>): Boolean = this === other || polynomialSpace {
        for (i1 in matrix.rowIndices) {
            for (i2 in 0 until i1) for (j1 in matrix.columnIndices) for (j2 in matrix.columnIndices)
                if (matrix[i1, j1] * other.matrix[i2, j2] neq matrix[i2, j2] * other.matrix[i1, j1]) return@polynomialSpace false
            val i2 = i1
            for (j1 in matrix.columnIndices) for (j2 in 0 until j1)
                if (matrix[i1, j1] * other.matrix[i2, j2] neq matrix[i2, j2] * other.matrix[i1, j1]) return@polynomialSpace false
        }
        true
    }
    // FIXME: KT-5351
    public inline infix fun Transformation<C>.notEqualsTo(other: Transformation<C>): Boolean = !(this equalsTo other)
    public inline infix fun Transformation<C>.eq(other: Transformation<C>): Boolean = this equalsTo other
    // FIXME: KT-5351
    public inline infix fun Transformation<C>.neq(other: Transformation<C>): Boolean = !(this equalsTo other)

    public operator fun Transformation<C>.invoke(P: Point<C>): Point<C> = matrixSpace { Point(matrix * P.columnVector) }
    public operator fun Transformation<C>.invoke(l: Line<C>): Line<C> = matrixSpace { Line(l.rowVector * matrix.adjugate()) }
    public operator fun Transformation<C>.invoke(q: Quadric<C>): Quadric<C> = matrixSpace { (matrix.adjugate()).let { Quadric(it.transposed() * q.matrix * it) } }

    // FIXME: Make the following functions extensions when context receivers will be available

    /**
     * Checks if [this] point is lying on the line [l].
     *
     * @receiver The considered point.
     * @param l The considered line.
     * @return Boolean value of the statement.
     */
    public infix fun Point<C>.isLyingOn(l: Line<C>): Boolean = polynomialSpace { lyingCondition(this@isLyingOn, l).isZero() }

    /**
     * Checks if [this] point is not lying on the line [l].
     *
     * @receiver The considered point.
     * @param l The considered line.
     * @return Boolean value of the statement.
     */
    // FIXME: KT-5351
    public infix fun Point<C>.isNotLyingOn(l: Line<C>): Boolean = polynomialSpace { lyingCondition(this@isNotLyingOn, l).isNotZero() }

    /**
     * Checks if [this] point is lying on the quadric [q].
     *
     * @receiver The considered point.
     * @param q The considered quadric.
     * @return Boolean value of the statement.
     */
    public infix fun Point<C>.isLyingOn(q: Quadric<C>): Boolean = polynomialSpace { lyingCondition(this@isLyingOn, q).isZero() }

    /**
     * Checks if [this] point is not lying on the quadric [q].
     *
     * @receiver The considered point.
     * @param q The considered quadric.
     * @return Boolean value of the statement.
     */
    // FIXME: KT-5351
    public infix fun Point<C>.isNotLyingOn(q: Quadric<C>): Boolean = polynomialSpace { lyingCondition(this@isNotLyingOn, q).isNotZero() }

    /**
     * Checks if [this] line is lying through the point [P].
     *
     * @receiver The considered line.
     * @param P The considered point.
     * @return Boolean value of the statement.
     */
    public infix fun Line<C>.isLyingThrough(P: Point<C>): Boolean = polynomialSpace { lyingCondition(P, this@isLyingThrough).isZero() }

    /**
     * Checks if [this] line is not lying through the point [P].
     *
     * @receiver The considered line.
     * @param P The considered point.
     * @return Boolean value of the statement.
     */
    // FIXME: KT-5351
    public infix fun Line<C>.isNotLyingThrough(P: Point<C>): Boolean = polynomialSpace { lyingCondition(P, this@isNotLyingThrough).isNotZero() }

    /**
     * Checks if [this] line is tangent to the quadric [q].
     *
     * @receiver The considered line.
     * @param q The considered quadric.
     * @return Boolean value of the statement.
     */
    public infix fun Line<C>.isTangentTo(q: Quadric<C>): Boolean = polynomialSpace { tangencyCondition(this@isTangentTo, q).isZero() }

    /**
     * Checks if [this] line is not tangent to the quadric [q].
     *
     * @receiver The considered line.
     * @param q The considered quadric.
     * @return Boolean value of the statement.
     */
    // FIXME: KT-5351
    public infix fun Line<C>.isNotTangentTo(q: Quadric<C>): Boolean = polynomialSpace { tangencyCondition(this@isNotTangentTo, q).isNotZero() }

    /**
     * Checks if [this] quadric is lying through the point [P].
     *
     * @receiver The considered quadric.
     * @param P The considered point.
     * @return Boolean value of the statement.
     */
    public infix fun Quadric<C>.isLyingThrough(P: Point<C>): Boolean = polynomialSpace { lyingCondition(P, this@isLyingThrough).isZero() }

    /**
     * Checks if [this] quadric is not lying through the point [P].
     *
     * @receiver The considered quadric.
     * @param P The considered point.
     * @return Boolean value of the statement.
     */
    // FIXME: KT-5351
    public infix fun Quadric<C>.isNotLyingThrough(P: Point<C>): Boolean = polynomialSpace { lyingCondition(P, this@isNotLyingThrough).isNotZero() }

    /**
     * Checks if [this] quadric is tangent to the line [l].
     *
     * @receiver The considered quadric.
     * @param l The considered line.
     * @return Boolean value of the statement.
     */
    public infix fun Quadric<C>.isTangentTo(l: Line<C>): Boolean = polynomialSpace { tangencyCondition(l, this@isTangentTo).isZero() }

    /**
     * Checks if [this] quadric is not tangent to the line [l].
     *
     * @receiver The considered quadric.
     * @param l The considered line.
     * @return Boolean value of the statement.
     */
    // FIXME: KT-5351
    public infix fun Quadric<C>.isNotTangentTo(l: Line<C>): Boolean = polynomialSpace { tangencyCondition(l, this@isNotTangentTo).isNotZero() }

    /**
     * Construct a normal projection in terms of affine map that is considered generated by [Point<C>.x] and [Point<C>.y]
     * coordinates of [this] point to the given line [l].
     *
     * @receiver Projected point.
     * @param l Line projected on.
     * @return The projection.
     */
    public fun Point<C>.projectTo(l: Line<C>): Point<C> = intersectionOf(l, perpendicular(l, this))

    /**
     * Checks if the given quadric is circle.
     *
     * @receiver The checked quadric.
     * @return Boolean value of the statement.
     */
    public fun Quadric<C>.isCircle(): Boolean = polynomialSpace { xy.isZero() && xx == yy }

    public fun Point<C>.polarBy(q: Quadric<C>): Line<C> = matrixSpace { Line(rowVector * q.matrix) }

    public fun Line<C>.poleBy(q: Quadric<C>): Point<C> = matrixSpace { Point(rowVector * q.matrix.adjugate()) }

    public fun Quadric<C>.dualBy(q: Quadric<C>): Quadric<C> = matrixSpace { with(q.matrix.adjugate()) { Quadric(this * matrix * this) } }

    public fun Quadric<C>.center(): Point<C> = polynomialSpace {
        Point(
            2 * xz * yy - xy * yz,
            2 * xx * yz - xy * xz,
            xy * xy - 4 * xx * yy
        )
    }

    public fun Line<C>.projectToQuadricBy(q: Quadric<C>, P: Point<C>): Point<C> {
        require(P.isLyingOn(this) && P.isLyingOn(q)) { "The point must lye on the line and the quadric." }
        //        Point(
        //            q.xx * y * z * P.x + q.xy * y * z * P.y + q.xz * y * z * P.z - q.zz * x * y * P.z - q.yy * x * z * P.x,
        //            (q.zz * x * x + q.xx * z * z - q.xz * x * z) * P.z,
        //            (q.yy * x * x + q.xx * y * y - q.xy * x * y) * P.y
        //        )
        TODO("Not yet implemented")
    }

    public fun Point<C>.projectToQuadricBy(q: Quadric<C>, P: Point<C>): Point<C> {
        require(P.isLyingOn(q)) { "The point must lye on the quadric." }
        //        Point(
        //            0.toRational().toLabeledPolynomial(),
        //            0.toRational().toLabeledPolynomial(),
        //            0.toRational().toLabeledPolynomial()
        //        )
        TODO("Not yet implemented")
    }

    public fun Point<C>.projectToQuadricBy(q: Quadric<C>, l: Line<C>): Line<C> {
        require(l.isTangentTo(q) && l.isLyingThrough(this)) { "The line must lye through the point and touch the quadric." }
        //        Point(
        //            0.toRational().toLabeledPolynomial(),
        //            0.toRational().toLabeledPolynomial(),
        //            0.toRational().toLabeledPolynomial()
        //        )
        TODO("Not yet implemented")
    }

    public fun Line<C>.projectToQuadricBy(q: Quadric<C>, l: Line<C>): Line<C> {
        require(l.isTangentTo(q)) { "The line must touch the quadric." }
        //        Point(
        //            0.toRational().toLabeledPolynomial(),
        //            0.toRational().toLabeledPolynomial(),
        //            0.toRational().toLabeledPolynomial()
        //        )
        TODO("Not yet implemented")
    }

    public fun involutionBy(A: Point<C>, q: Quadric<C>): Transformation<C> = involutionBy(A, A.polarBy(q))

    public fun involutionBy(l: Line<C>, q: Quadric<C>): Transformation<C> = involutionBy(l.poleBy(q), l)
}

public inline val <C, A: Ring<C>> A.planimetricsCalculationContext: PlanimetricsCalculationContext<C, A> get() = PlanimetricsCalculationContext(this)

// Waiting for context receivers :( FIXME: Uncomment when context receivers will be available

//public inline operator fun <C, A: Ring<C>, PC: PlanimetricsCalculationContext<C, A>, R> PC.invoke(block: context(A, LabeledPolynomialSpace<C, A>, MatrixSpace<LabeledPolynomial<C>, LabeledPolynomialSpace<C, A>>, PC) () -> R): R {
////    FIXME: KT-32313
////    contract {
////        callsInPlace(block, EXACTLY_ONCE)
////    }
//    return block(ring, polynomialSpace, matrixSpace, this)
//}