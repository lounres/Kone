/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("LocalVariableName", "NOTHING_TO_INLINE")

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.collections.utils.KoneIterableList
import dev.lounres.kone.collections.utils.koneIterableListOf
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.linearAlgebra.experiment1.Matrix
import dev.lounres.kone.linearAlgebra.experiment1.adjugate
import dev.lounres.kone.linearAlgebra.experiment1.det
import dev.lounres.kone.linearAlgebra.experiment1.minor
import dev.lounres.kone.polynomial.LabeledPolynomial
import space.kscience.kmath.expressions.Symbol
import kotlin.properties.ReadOnlyProperty


// region Finiteness
context(PlanimetricsCalculationContext<E, *>)
public val <E> Point<E>.isFinite : Boolean get() = calculate { z.isNotZero() }
//context(PlanimetricsCalculationContext<E, *>)
//public val <E> Line<E>.isFinite : Boolean get() = TODO("Finiteness test is not yet implemented")
// endregion

// region Lying and tangency
/**
 * Returns an expression which equality to zero is equivalent to condition of [P] lying on [l].
 *
 * Obviously, it's just value of polynomial of [l] on [P].
 *
 * @param P The considered point.
 * @param l The considered line.
 * @return The expression.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> lyingCondition(P: Point<E>, l: Line<E>): LabeledPolynomial<E> = calculate { P.x * l.x + P.y * l.y + P.z * l.z }

/**
 * Returns an expression which equality to zero is equivalent to condition of [P] lying on [q].
 *
 * Obviously, it's just value of polynomial of [q] on [P].
 *
 * @param P The considered point.
 * @param q The considered quadric.
 * @return The expression.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> lyingCondition(P: Point<E>, q: Quadric<E>): LabeledPolynomial<E> = calculate {
    P.x * P.x * q.xx +
            P.y * P.y * q.yy +
            P.z * P.z * q.zz +
            P.x * P.y * q.xy +
            P.x * P.z * q.xz +
            P.y * P.z * q.yz
}

/**
 * Returns an expression which equality to zero is equivalent to condition of [l] being tangent to [q].
 *
 * @param l The considered line.
 * @param q The considered quadric.
 * @return The expression.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> tangencyCondition(l: Line<E>, q: Quadric<E>): LabeledPolynomial<E> = calculate {
    l.x * l.x * (q.yz * q.yz - q.yy * q.zz * 4) +
            l.y * l.y * (q.xz * q.xz - q.xx * q.zz * 4) +
            l.z * l.z * (q.xy * q.xy - q.xx * q.yy * 4) +
            l.x * l.y * (q.xy * q.zz * 2 - q.xz * q.yz) * 2 +
            l.x * l.z * (q.xz * q.yy * 2 - q.xy * q.yz) * 2 +
            l.y * l.z * (q.xx * q.yz * 2 - q.xy * q.xz) * 2
}

/**
 * Checks if [this] point is lying on the line [l].
 *
 * @receiver The considered point.
 * @param l The considered line.
 * @return Boolean value of the statement.
 */
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Point<E>.isLyingOn(l: Line<E>): Boolean = calculate { lyingCondition(this, l).isZero() }

/**
 * Checks if [this] point is not lying on the line [l].
 *
 * @receiver The considered point.
 * @param l The considered line.
 * @return Boolean value of the statement.
 */
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Point<E>.isNotLyingOn(l: Line<E>): Boolean = calculate { lyingCondition(this, l).isNotZero() }

/**
 * Checks if [this] point is lying on the quadric [q].
 *
 * @receiver The considered point.
 * @param q The considered quadric.
 * @return Boolean value of the statement.
 */
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Point<E>.isLyingOn(q: Quadric<E>): Boolean = calculate { lyingCondition(this, q).isZero() }

/**
 * Checks if [this] point is not lying on the quadric [q].
 *
 * @receiver The considered point.
 * @param q The considered quadric.
 * @return Boolean value of the statement.
 */
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Point<E>.isNotLyingOn(q: Quadric<E>): Boolean = calculate { lyingCondition(this, q).isNotZero() }

/**
 * Checks if [this] line is lying through the point [P].
 *
 * @receiver The considered line.
 * @param P The considered point.
 * @return Boolean value of the statement.
 */
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Line<E>.isLyingThrough(P: Point<E>): Boolean = calculate { lyingCondition(P, this).isZero() }

/**
 * Checks if [this] line is not lying through the point [P].
 *
 * @receiver The considered line.
 * @param P The considered point.
 * @return Boolean value of the statement.
 */
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Line<E>.isNotLyingThrough(P: Point<E>): Boolean = calculate { lyingCondition(P, this).isNotZero() }

/**
 * Checks if [this] line is tangent to the quadric [q].
 *
 * @receiver The considered line.
 * @param q The considered quadric.
 * @return Boolean value of the statement.
 */
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Line<E>.isTangentTo(q: Quadric<E>): Boolean = calculate { tangencyCondition(this, q).isZero() }

/**
 * Checks if [this] line is not tangent to the quadric [q].
 *
 * @receiver The considered line.
 * @param q The considered quadric.
 * @return Boolean value of the statement.
 */
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Line<E>.isNotTangentTo(q: Quadric<E>): Boolean = calculate { tangencyCondition(this, q).isNotZero() }

/**
 * Checks if [this] quadric is lying through the point [P].
 *
 * @receiver The considered quadric.
 * @param P The considered point.
 * @return Boolean value of the statement.
 */
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Quadric<E>.isLyingThrough(P: Point<E>): Boolean = calculate { lyingCondition(P, this).isZero() }

/**
 * Checks if [this] quadric is not lying through the point [P].
 *
 * @receiver The considered quadric.
 * @param P The considered point.
 * @return Boolean value of the statement.
 */
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Quadric<E>.isNotLyingThrough(P: Point<E>): Boolean = calculate { lyingCondition(P, this).isNotZero() }

/**
 * Checks if [this] quadric is tangent to the line [l].
 *
 * @receiver The considered quadric.
 * @param l The considered line.
 * @return Boolean value of the statement.
 */
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Quadric<E>.isTangentTo(l: Line<E>): Boolean = calculate { tangencyCondition(l, this).isZero() }

/**
 * Checks if [this] quadric is not tangent to the line [l].
 *
 * @receiver The considered quadric.
 * @param l The considered line.
 * @return Boolean value of the statement.
 */
// FIXME: KT-5351
context(PlanimetricsCalculationContext<E, *>)
public infix fun <E> Quadric<E>.isNotTangentTo(l: Line<E>): Boolean = calculate { tangencyCondition(l, this).isNotZero() }
// endregion

// region Arbitrary points and lines with initial conditions
context(PlanimetricsCalculationContext<E, *>)
public fun <E> lineThrough(P: Point<E>) : ReadOnlyProperty<Any?, Line<E>> = ReadOnlyProperty { _, prop ->
    val xParameter = Symbol("${prop.name}\$param_x")
    val yParameter = Symbol("${prop.name}\$param_y")
    val zParameter = Symbol("${prop.name}\$param_z")
    calculate {
        Line(
            P.y * zParameter - P.z * yParameter,
            P.z * xParameter - P.x * zParameter,
            P.x * yParameter - P.y * xParameter,
        )
    }
}
context(PlanimetricsCalculationContext<E, *>)
public fun <E> pointOn(L: Line<E>) : ReadOnlyProperty<Any?, Point<E>> = ReadOnlyProperty { _, prop ->
    val xParameter = Symbol("${prop.name}\$param_x")
    val yParameter = Symbol("${prop.name}\$param_y")
    val zParameter = Symbol("${prop.name}\$param_z")
    calculate {
        Point(
            L.y * zParameter - L.z * yParameter,
            L.z * xParameter - L.x * zParameter,
            L.x * yParameter - L.y * xParameter,
        )
    }
}
// endregion

// region Lines and segments
/**
 * Constructs line that goes through the given points [A] and [B].
 *
 * Obviously it's vector multiplication operation.
 *
 * @param A The first point.
 * @param B The second point.
 * @return Line going through the points.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> lineThrough(A: Point<E>, B: Point<E>): Line<E> = calculate {
    Line(
        A.y * B.z - A.z * B.y,
        A.z * B.x - A.x * B.z,
        A.x * B.y - A.y * B.x
    )
}

/**
 * Constructs intersection of the given lines [l] and [m].
 *
 * Obviously it's vector multiplication operation.
 *
 * @param l The first line.
 * @param m The second line.
 * @return Intersection of the lines.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> intersectionOf(l: Line<E>, m: Line<E>): Point<E> = calculate {
    Point(
        l.y * m.z - l.z * m.y,
        l.z * m.x - l.x * m.z,
        l.x * m.y - l.y * m.x
    )
}

/**
 * Returns an expression which equality to zero is equivalent to collinearity of the points.
 *
 * Obviously, it's determinant of matrix constructed by row vectors of the points.
 *
 * @param A The first point.
 * @param B The second point.
 * @param C The third point.
 * @return The expression.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> collinearityCondition(A: Point<E>, B: Point<E>, C: Point<E>): LabeledPolynomial<E> =
    calculate { A.x * (B.y * C.z - B.z * C.y) + B.x * (C.y * A.z - C.z * A.y) + C.x * (A.y * B.z - A.z * B.y) }

/**
 * Tests if the points are collinear.
 *
 * @param A The first point.
 * @param B The second point.
 * @param C The third point.
 * @return The test result.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> collinearityTest(A: Point<E>, B: Point<E>, C: Point<E>): Boolean =
    calculate { collinearityCondition(A, B, C).isZero() }

/**
 * Returns an expression which equality to zero is equivalent to concurrency of the lines.
 *
 * Obviously, it's determinant of matrix constructed by row vectors of the lines.
 *
 * @param l The first line.
 * @param m The second line.
 * @param n The third line.
 * @return The expression.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> concurrencyCondition(l: Line<E>, m: Line<E>, n: Line<E>): LabeledPolynomial<E> =
    calculate { l.x * (m.y * n.z - m.z * n.y) + m.x * (n.y * l.z - n.z * l.y) + n.x * (l.y * m.z - l.z * m.y) }

/**
 * Tests if the lines are
 *
 * @param l The first line.
 * @param m The second line.
 * @param n The third line.
 * @return The test result.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> concurrencyTest(l: Line<E>, m: Line<E>, n: Line<E>): Boolean =
    calculate { concurrencyCondition(l, m, n).isZero() }

/**
 * Constructs a midpoint between [A] and [B]. The affine map is considered generated by [Point<E>.x] and [Point<E>.y]
 * coordinates.
 *
 * @param A The first point.
 * @param B The second point.
 * @return The midpoint.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> midpoint(A: Point<E>, B: Point<E>): Point<E> = calculate {
    Point(
        A.x * B.z + B.x * A.z,
        A.y * B.z + B.y * A.z,
        2 * A.z * B.z
    )
}

/**
 * Constructs a point P on the line through [A] and [B] that divides segment \([ AB ]\) in ratio lambda. It means that on
 * the affine map that is considered generated by [Point.x] and [Point.y] coordinates a point P such that
 * \(\overrightarrow{AP}/\overrightarrow{PB} = \lambda\), so P is returned.
 *
 * @param A The first point.
 * @param B The second point.
 * @param lambda The ration.
 * @return The constructed point P.
 * @usesMathJax
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> divideSegmentInRatio(A: Point<E>, B: Point<E>, lambda: E): Point<E> = calculate {
    Point(
        A.x * B.z + lambda * B.x * A.z,
        A.y * B.z + lambda * B.y * A.z,
        (lambda + 1) * A.z * B.z
    )
}

/**
 * Constructs a point P on the line through [A] and [B] that divides segment \([ AB ]\) in ratio \(a/b\).
 * It means that on the affine map that is considered generated by [Point.x] and [Point.y] coordinates a point P
 * such that \(\overrightarrow{AP}/\overrightarrow{PB} = a/b\), so P is returned.
 *
 * @param A The first point.
 * @param B The second point.
 * @param a Component of ratio \(a/b\).
 * @param b Component of ratio \(a/b\).
 * @return The constructed point P.
 * @usesMathJax
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> divideSegmentInRatio(A: Point<E>, B: Point<E>, a: E, b: E): Point<E> = calculate {
    Point(
        A.x * B.z * b + B.x * A.z * a,
        A.y * B.z * b + B.y * A.z * a,
        (a + b) * A.z * B.z
    )
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> parallelLine(l: Line<E>, A: Point<E>): Line<E> = calculate {
    Line(
        l.x * A.z,
        l.y * A.z,
        -(A.x * l.x + A.y * l.y)
    )
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Point<E>.parallelLineTo(l: Line<E>): Line<E> = parallelLine(l, this)

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Line<E>.parallelLineThrough(A: Point<E>): Line<E> = parallelLine(this, A)

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> parallelismCondition(l: Line<E>, m: Line<E>): LabeledPolynomial<E> = calculate {
    l.x * m.y - l.y * m.x
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> parallelismTest(l: Line<E>, m: Line<E>): Boolean = calculate {
    parallelismCondition(l, m).isZero()
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Line<E>.isParallelTo(other: Line<E>): Boolean = parallelismTest(this, other)

/**
 * Constructs perpendicular in terms of affine map that is considered generated by [Point<E>.x] and [Point<E>.y] coordinates
 * line to the given line [l] through the given point [A].
 *
 * @param l The line to which the perpendicular is being constructed.
 * @param A The point through which the perpendicular is being constructed.
 * @return The constructed perpendicular.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> perpendicular(l: Line<E>, A: Point<E>): Line<E> = calculate {
    Line(
        -l.y * A.z,
        l.x * A.z,
        A.x * l.y - A.y * l.x
    )
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Point<E>.perpendicularTo(l: Line<E>): Line<E> = perpendicular(l, this)

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Line<E>.perpendicularThrough(A: Point<E>): Line<E> = perpendicular(this, A)

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> perpendicularityCondition(l: Line<E>, m: Line<E>): LabeledPolynomial<E> = polynomialSpace {
    l.x * m.x + l.y * m.y
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> perpendicularityTest(l: Line<E>, m: Line<E>): Boolean = polynomialSpace {
    perpendicularityCondition(l, m).isZero()
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public inline infix fun <E> Line<E>.isPerpendicularTo(other: Line<E>): Boolean = perpendicularityTest(this, other)

/**
 * Construct a normal projection in terms of affine map that is considered generated by [Point.x] and [Point.y]
 * coordinates of [this] point to the given line [l].
 *
 * @receiver Projected point.
 * @param l Line projected on.
 * @return The projection.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> Point<E>.projectOn(l: Line<E>): Point<E> = calculate {
    Point(
        l.y * l.y * x - l.x * l.y * y - l.z * l.x * z,
        l.x * l.x * y - l.x * l.y * x - l.z * l.y * z,
        (l.x * l.x + l.y * l.y) * z
    )
}

/**
 * Reflects [this] point by the provided line [l] in terms of affine map
 * that is considered generated by [Point.x] and [Point.y] coordinates.
 *
 * @receiver Reflected point.
 * @param l Line reflected by.
 * @return The reflection.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> Point<E>.reflectThrough(l: Line<E>): Point<E> = calculate {
    Point(
        x * l.x * l.x - x * l.y * l.y + 2 * l.x * l.y * y + 2 * l.z * l.x * z,
        y * l.y * l.y - y * l.x * l.x + 2 * l.y * l.x * x + 2 * l.z * l.y * z,
        -(l.x * l.x + l.y * l.y) * z
    )
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> reflectionThrough(l: Line<E>): Transformation<E> = calculate {
    Transformation(
        Matrix(
            koneIterableListOf(l.x * l.x - l.y * l.y, 2 * l.x * l.y, 2 * l.z * l.x),
            koneIterableListOf(2 * l.x * l.y, l.y * l.y - l.x * l.x, 2 * l.z * l.y),
            koneIterableListOf(zero, zero, -(l.x * l.x + l.y * l.y)),
            context = defaultEquality() // TODO: Maybe, the contexts should be replaced
        )
    )
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> Point<E>.reflectThrough(P: Point<E>): Point<E> = calculate {
    Point(
        2 * P.x * z - x * P.z,
        2 * P.y * z - y * P.z,
        z * P.z,
    )
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> reflectionThrough(P: Point<E>): Transformation<E> = calculate {
    Transformation(
        Matrix(
            koneIterableListOf(-P.z, zero, 2 * P.x),
            koneIterableListOf(zero, -P.z, 2 * P.y),
            koneIterableListOf(zero, zero, P.z),
            context = defaultEquality() // TODO: Maybe, the contexts should be replaced
        )
    )
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> homothetyBy(P: Point<E>, k: E): Transformation<E> = calculate {
    Transformation(
        Matrix(
            koneIterableListOf(k * P.z, zero, (1 - k) * P.x),
            koneIterableListOf(zero, k * P.z, (1 - k) * P.y),
            koneIterableListOf(zero, zero, P.z),
            context = defaultEquality() // TODO: Maybe, the contexts should be replaced
        )
    )
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> segmentBisector(A: Point<E>, B: Point<E>): Line<E> = calculate {
    Line(
        -2 * (A.x * B.z - B.x * A.z) * A.z * B.z,
        -2 * (A.y * B.z - B.y * A.z) * A.z * B.z,
        (A.x * A.x + A.y * A.y) * B.z * B.z - (B.x * B.x + B.y * B.y) * A.z * A.z
    )
}
// endregion


// region Circles
/**
 * Checks if the given quadric is circle.
 *
 * @receiver The checked quadric.
 * @return Boolean value of the statement.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> Quadric<E>.isCircle(): Boolean = calculate { xy.isZero() && xx == yy }

/**
 * Constructs circle (as [Quadric]) by its center and point on it.
 *
 * @param O The center of constructed circle.
 * @param A The given point on the constructed circle.
 * @return The constructed circle as [Quadric].
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> circleByCenterAndPoint(O: Point<E>, A: Point<E>): Quadric<E> = calculate {
    Quadric(
        xx = O.z * O.z * A.z * A.z,
        yy = O.z * O.z * A.z * A.z,
        zz = (2 * O.x * A.z - O.z * A.x) * A.x * O.z + (2 * O.y * A.z - O.z * A.y) * A.y * O.z,
        xz = -2 * O.x * O.z * A.z * A.z,
        yz = -2 * O.y * O.z * A.z * A.z,
        xy = zero
    )
}

/**
 * Constructs circle (as [Quadric]) by the given opposite points on it.
 *
 * @param A The first of the opposite points.
 * @param B The second of the opposite points.
 * @return The constructed circle as [Quadric].
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> circleByDiameter(A: Point<E>, B: Point<E>): Quadric<E> = calculate {
    Quadric(
        A.z * B.z,
        A.z * B.z,
        A.y * B.y + A.x * B.x,
        zero,
        -(A.z * B.x + A.x * B.z),
        -(A.z * B.y + A.y * B.z)
    )
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> cocyclicityCondition(A: Point<E>, B: Point<E>, C: Point<E>, D: Point<E>): LabeledPolynomial<E> = calculate {
    Matrix(
        koneIterableListOf(A.x * A.x + A.y * A.y, A.x * A.z, A.y * A.z, A.z * A.z),
        koneIterableListOf(B.x * B.x + B.y * B.y, B.x * B.z, B.y * B.z, B.z * B.z),
        koneIterableListOf(C.x * C.x + C.y * C.y, C.x * C.z, C.y * C.z, C.z * C.z),
        koneIterableListOf(D.x * D.x + D.y * D.y, D.x * D.z, D.y * D.z, D.z * D.z),
        context = defaultEquality() // TODO: Maybe, the contexts should be replaced
    ).det
}

// TODO: Docs
context(PlanimetricsCalculationContext<E, *>)
public fun <E> cocyclicityTest(A: Point<E>, B: Point<E>, C: Point<E>, D: Point<E>): Boolean = calculate {
    cocyclicityCondition(A, B, C, D).isZero()
}
// endregion


// region Points, lines and quadrics of triangle
/**
 * Constructs centroid of triangle ABC.
 *
 * @param A The first vertex of the triangle.
 * @param B The second vertex of the triangle.
 * @param C The third vertex of the triangle.
 * @return The centroid.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> centroid(A: Point<E>, B: Point<E>, C: Point<E>): Point<E> = calculate {
    Point(
        A.x * B.z * C.z + B.x * C.z * A.z + C.x * A.z * B.z,
        A.y * B.z * C.z + B.y * C.z * A.z + C.y * A.z * B.z,
        3 * A.z * B.z * C.z
    )
}

/**
 * Constructs orthocenter of triangle ABC.
 *
 * @param A The first vertex of the triangle.
 * @param B The second vertex of the triangle.
 * @param C The third vertex of the triangle.
 * @return The orthocenter.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> orthocenter(A: Point<E>, B: Point<E>, C: Point<E>): Point<E> = calculate {
    Point(
        A.y * B.z * C.z * (A.x * (C.x * B.z - B.x * C.z) + A.y * (C.y * B.z - B.y * C.z)) + B.y * C.z * A.z * (B.x * (A.x * C.z - C.x * A.z) + B.y * (A.y * C.z - C.y * A.z)) + C.y * A.z * B.z * (C.x * (B.x * A.z - A.x * B.z) + C.y * (B.y * A.z - A.y * B.z)),
        A.x * B.z * C.z * (A.y * (B.y * C.z - C.y * B.z) + A.x * (B.x * C.z - C.x * B.z)) + B.x * C.z * A.z * (B.y * (C.y * A.z - A.y * C.z) + B.x * (C.x * A.z - A.x * C.z)) + C.x * A.z * B.z * (C.y * (A.y * B.z - B.y * A.z) + C.x * (A.x * B.z - B.x * A.z)),
        A.z * B.z * C.z * (A.x * B.y * C.z - A.x * B.z * C.y - A.y * B.x * C.z + A.y * B.z * C.x + A.z * B.x * C.y - A.z * B.y * C.x)
    )
}

/**
 * Constructs circumcenter of triangle ABC.
 *
 * @param A The first vertex of the triangle.
 * @param B The second vertex of the triangle.
 * @param C The third vertex of the triangle.
 * @return The circumcenter.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> circumcenter(A: Point<E>, B: Point<E>, C: Point<E>): Point<E> = calculate {
    Point(
        (A.x * A.x + A.y * A.y) * (B.y * C.z - C.y * B.z) * B.z * C.z
                + (B.x * B.x + B.y * B.y) * (C.y * A.z - A.y * C.z) * C.z * A.z
                + (C.x * C.x + C.y * C.y) * (A.y * B.z - B.y * A.z) * A.z * B.z,
        (A.x * A.x + A.y * A.y) * (C.x * B.z - B.x * C.z) * B.z * C.z
                + (B.x * B.x + B.y * B.y) * (A.x * C.z - C.x * A.z) * C.z * A.z
                + (C.x * C.x + C.y * C.y) * (B.x * A.z - A.x * B.z) * A.z * B.z,
        2 * (A.x * (B.y * C.z - C.y * B.z) + B.x * (C.y * A.z - A.y * C.z) + C.x * (A.y * B.z - B.y * A.z)) * A.z * B.z * C.z
    )
}

/**
 * Constructs circumcircle of triangle ABC as [Quadric].
 *
 * @param A The first vertex of the triangle.
 * @param B The second vertex of the triangle.
 * @param C The third vertex of the triangle.
 * @return The circumcircle as [Quadric].
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> circumcircle(A: Point<E>, B: Point<E>, C: Point<E>): Quadric<E> = calculate {
    Quadric(
        (A.x * B.y * C.z - A.x * B.z * C.y - A.y * B.x * C.z + A.y * B.z * C.x + A.z * B.x * C.y - A.z * B.y * C.x) * A.z * B.z * C.z,
        (A.x * B.y * C.z - A.x * B.z * C.y - A.y * B.x * C.z + A.y * B.z * C.x + A.z * B.x * C.y - A.z * B.y * C.x) * A.z * B.z * C.z,
        -((A.x * A.x + A.y * A.y) * B.x * B.z * C.y * C.z - (A.x * A.x + A.y * A.y) * B.y * B.z * C.x * C.z - A.x * A.z * (B.x * B.x + B.y * B.y) * C.y * C.z + A.x * A.z * B.y * B.z * (C.x * C.x + C.y * C.y) + A.y * A.z * (B.x * B.x + B.y * B.y) * C.x * C.z - A.y * A.z * B.x * B.z * (C.x * C.x + C.y * C.y)),
        zero,
        -((A.x * A.x + A.y * A.y) * B.y * B.z * C.z * C.z - (A.x * A.x + A.y * A.y) * B.z * B.z * C.y * C.z - A.y * A.z * (B.x * B.x + B.y * B.y) * C.z * C.z + A.y * A.z * B.z * B.z * (C.x * C.x + C.y * C.y) + A.z * A.z * (B.x * B.x + B.y * B.y) * C.y * C.z - A.z * A.z * B.y * B.z * (C.x * C.x + C.y * C.y)),
        (A.x * A.x + A.y * A.y) * B.x * B.z * C.z * C.z - (A.x * A.x + A.y * A.y) * B.z * B.z * C.x * C.z - A.x * A.z * (B.x * B.x + B.y * B.y) * C.z * C.z + A.x * A.z * B.z * B.z * (C.x * C.x + C.y * C.y) + A.z * A.z * (B.x * B.x + B.y * B.y) * C.x * C.z - A.z * A.z * B.x * B.z * (C.x * C.x + C.y * C.y)
    )
}

/**
 * Constructs Euler line of triangle ABC.
 *
 * @param A The first vertex of the triangle.
 * @param B The second vertex of the triangle.
 * @param C The third vertex of the triangle.
 * @return The Euler line.
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> eulerLine(A: Point<E>, B: Point<E>, C: Point<E>): Line<E> = calculate {
    Line(
        ((A.y * (A.x * (B.y * C.z - C.y * B.z) - A.y * (B.x * C.z - C.x * B.z)) - 3 * A.x * (A.x * (B.x * C.z - C.x * B.z) + A.y * (B.y * C.z - C.y * B.z))) * B.z * C.z +
                (B.y * (B.x * (C.y * A.z - A.y * C.z) - B.y * (C.x * A.z - A.x * C.z)) - 3 * B.x * (B.x * (C.x * A.z - A.x * C.z) + B.y * (C.y * A.z - A.y * C.z))) * C.z * A.z +
                (C.y * (C.x * (A.y * B.z - B.y * A.z) - C.y * (A.x * B.z - B.x * A.z)) - 3 * C.x * (C.x * (A.x * B.z - B.x * A.z) + C.y * (A.y * B.z - B.y * A.z))) * A.z * B.z) * A.z * B.z * C.z,
        ((A.x * (A.x * (C.y * B.z - B.y * C.z) - A.y * (C.x * B.z - B.x * C.z)) + 3 * A.y * (A.x * (C.x * B.z - B.x * C.z) + A.y * (C.y * B.z - B.y * C.z))) * B.z * C.z +
                (B.x * (B.x * (A.y * C.z - C.y * A.z) - B.y * (A.x * C.z - C.x * A.z)) + 3 * B.y * (B.x * (A.x * C.z - C.x * A.z) + B.y * (A.y * C.z - C.y * A.z))) * C.z * A.z +
                (C.x * (C.x * (B.y * A.z - A.y * B.z) - C.y * (B.x * A.z - A.x * B.z)) + 3 * C.y * (C.x * (B.x * A.z - A.x * B.z) + C.y * (B.y * A.z - A.y * B.z))) * A.z * B.z) * A.z * B.z * C.z,
        (A.x * A.x + A.y * A.y) * (A.x * (B.x * C.z - C.x * B.z) + A.y * (B.y * C.z - C.y * B.z)) * B.z * B.z * C.z * C.z +
                (B.x * B.x + B.y * B.y) * (B.x * (C.x * A.z - A.x * C.z) + B.y * (C.y * A.z - A.y * C.z)) * C.z * C.z * A.z * A.z +
                (C.x * C.x + C.y * C.y) * (C.x * (A.x * B.z - B.x * A.z) + C.y * (A.y * B.z - B.y * A.z)) * A.z * A.z * B.z * B.z
    )
}

/**
 * Constructs Euler's circle of triangle ABC as [Quadric].
 *
 * @param A The first vertex of the triangle.
 * @param B The second vertex of the triangle.
 * @param C The third vertex of the triangle.
 * @return The Euler's circle as [Quadric].
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> eulerCircle(A: Point<E>, B: Point<E>, C: Point<E>): Quadric<E> = calculate {
    Quadric(
        A.z * B.z * C.z * (A.x * (B.y * C.z - B.z * C.y)
                + A.y * (B.z * C.x - B.x * C.z)
                + A.z * (B.x * C.y - B.y * C.x)) * 2,
        A.z * B.z * C.z * (A.x * (B.y * C.z - B.z * C.y)
                + A.y * (B.z * C.x - B.x * C.z)
                + A.z * (B.x * C.y - B.y * C.x)) * 2,
        A.z * A.z * (B.x * C.x + B.y * C.y) * (B.x * C.y - B.y * C.x)
                + B.z * B.z * (C.x * A.x + C.y * A.y) * (C.x * A.y - C.y * A.x)
                + C.z * C.z * (A.x * B.x + A.y * B.y) * (A.x * B.y - A.y * B.x),
        zero,
        A.z * A.z * ((B.x * C.z + C.x * B.z) * (B.y * C.x - C.y * B.x) + (B.y * C.z - C.y * B.z) * (B.x * C.x + B.y * C.y))
                + B.z * B.z * ((C.x * A.z + A.x * C.z) * (C.y * A.x - A.y * C.x) + (C.y * A.z - A.y * C.z) * (C.x * A.x + C.y * A.y))
                + C.z * C.z * ((A.x * B.z + B.x * A.z) * (A.y * B.x - B.y * A.x) + (A.y * B.z - B.y * A.z) * (A.x * B.x + A.y * B.y)),
        A.z * A.z * ((B.y * C.z + C.y * B.z) * (C.x * B.y - B.x * C.y) + (C.x * B.z - B.x * C.z) * (B.y * C.y + B.x * C.x))
                + B.z * B.z * ((C.y * A.z + A.y * C.z) * (A.x * C.y - C.x * A.y) + (A.x * C.z - C.x * A.z) * (C.y * A.y + C.x * A.x))
                + C.z * C.z * ((A.y * B.z + B.y * A.z) * (B.x * A.y - A.x * B.y) + (B.x * A.z - A.x * B.z) * (A.y * B.y + A.x * B.x))
    )
}
// endregion


// region Pole and polar
context(PlanimetricsCalculationContext<E, *>)
public fun <E> Point<E>.polarBy(q: Quadric<E>): Line<E> = calculate { Line(rowVector * q.matrix) }

context(PlanimetricsCalculationContext<E, *>)
public fun <E> Line<E>.poleBy(q: Quadric<E>): Point<E> = calculate { Point(rowVector * q.matrix.adjugate) }

context(PlanimetricsCalculationContext<E, *>)
public fun <E> Quadric<E>.dualBy(q: Quadric<E>): Quadric<E> = calculate { with(q.matrix.adjugate) { Quadric(this * matrix * this) } }

context(PlanimetricsCalculationContext<E, *>)
public fun <E> Quadric<E>.center(): Point<E> = calculate {
    Point(
        2 * xz * yy - xy * yz,
        2 * xx * yz - xy * xz,
        xy * xy - 4 * xx * yy
    )
}
// endregion

// region Quadrics
/**
 * See also: [wiki](https://en.wikipedia.org/wiki/Five_points_determine_a_conic#Construction)
 */
context(PlanimetricsCalculationContext<E, *>)
public fun <E> quadricByPoints(P: Point<E>, Q: Point<E>, R: Point<E>, S: Point<E>, T: Point<E>): Quadric<E> = calculate {
    with(
        Matrix(
            KoneIterableList(6u) { zero },
            koneIterableListOf(P.x * P.x, P.x * P.y, P.x * P.z, P.y * P.y, P.y * P.z, P.z * P.z),
            koneIterableListOf(Q.x * Q.x, Q.x * Q.y, Q.x * Q.z, Q.y * Q.y, Q.y * Q.z, Q.z * Q.z),
            koneIterableListOf(R.x * R.x, R.x * R.y, R.x * R.z, R.y * R.y, R.y * R.z, R.z * R.z),
            koneIterableListOf(S.x * S.x, S.x * S.y, S.x * S.z, S.y * S.y, S.y * S.z, S.z * S.z),
            koneIterableListOf(T.x * T.x, T.x * T.y, T.x * T.z, T.y * T.y, T.y * T.z, T.z * T.z),
            context = defaultEquality() // TODO: Maybe, the contexts should be replaced
        )
    ) {
        Quadric(
            xx = minor.first(0u, 0u),
            xy = -minor.first(0u, 1u),
            xz = minor.first(0u, 2u),
            yy = -minor.first(0u, 3u),
            yz = minor.first(0u, 4u),
            zz = -minor.first(0u, 5u),
        )
    }
}

context(PlanimetricsCalculationContext<E, *>)
public fun <E> Line<E>.projectToQuadricBy(q: Quadric<E>, P: Point<E>): Point<E> =
    if (P.isNotLyingOn(this) || P.isNotLyingOn(q)) throw IllegalArgumentException("The point must lye on the line and the quadric.")
    else TODO("Not yet implemented")
//        Point(
//            q.xx * y * z * P.x + q.xy * y * z * P.y + q.xz * y * z * P.z - q.zz * x * y * P.z - q.yy * x * z * P.x,
//            (q.zz * x * x + q.xx * z * z - q.xz * x * z) * P.z,
//            (q.yy * x * x + q.xx * y * y - q.xy * x * y) * P.y
//        )

context(PlanimetricsCalculationContext<E, *>)
public fun <E> Point<E>.projectToQuadricBy(q: Quadric<E>, P: Point<E>): Point<E> =
    if (P.isNotLyingOn(q)) throw IllegalArgumentException("The point must lye on the quadric.")
    else TODO("Not yet implemented")
//        Point(
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial()
//        )

context(PlanimetricsCalculationContext<E, *>)
public fun <E> Point<E>.projectToQuadricBy(q: Quadric<E>, l: Line<E>): Line<E> =
    if (l.isNotTangentTo(q) || l.isNotLyingThrough(this)) throw IllegalArgumentException("The line must lye through the point and touch the quadric.")
    else TODO("Not yet implemented")
//        Point(
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial()
//        )

context(PlanimetricsCalculationContext<E, *>)
public fun <E> Line<E>.projectToQuadricBy(q: Quadric<E>, l: Line<E>): Line<E> =
    if (l.isNotTangentTo(q)) throw IllegalArgumentException("The line must touch the quadric.")
    else TODO("Not yet implemented")
//        Point(
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial()
//        )

// endregion

// region Transformations

context(PlanimetricsCalculationContext<E, *>)
public fun <E> involutionBy(A: Point<E>, l: Line<E>): Transformation<E> = calculate {
    Transformation(
        (l.rowVector * A.columnVector).let {
            Matrix(3u, 3u, context = defaultEquality() /* TODO: Maybe, the contexts should be replaced */) { rowIndex, columnIndex ->
                with(-2 * A.rowVector[rowIndex] * l.rowVector[columnIndex]) {
                    if (rowIndex == columnIndex) this + it else this
                }
            }
        }
    )
}

context(PlanimetricsCalculationContext<E, *>)
public fun <E> involutionBy(A: Point<E>, q: Quadric<E>): Transformation<E> = involutionBy(A, A.polarBy(q))

context(PlanimetricsCalculationContext<E, *>)
public fun <E> involutionBy(l: Line<E>, q: Quadric<E>): Transformation<E> = involutionBy(l.poleBy(q), l)

// endregion