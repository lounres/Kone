/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.misc.planimetricsCalculation

import com.lounres.kone.algebraic.invoke
import com.lounres.kone.linearAlgebra.SquareMatrix
import com.lounres.kone.polynomial.LabeledPolynomial
import space.kscience.kmath.expressions.Symbol
import kotlin.properties.ReadOnlyProperty


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
public fun <C> PlanimetricsCalculationContext<C, *>.lyingCondition(P: Point<C>, l: Line<C>): LabeledPolynomial<C> = polynomialSpace { P.x * l.x + P.y * l.y + P.z * l.z }

/**
 * Returns an expression which equality to zero is equivalent to condition of [P] lying on [q].
 *
 * Obviously, it's just value of polynomial of [q] on [P].
 *
 * @param P The considered point.
 * @param q The considered quadric.
 * @return The expression.
 */
public fun <C> PlanimetricsCalculationContext<C, *>.lyingCondition(P: Point<C>, q: Quadric<C>): LabeledPolynomial<C> = polynomialSpace {
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
public fun <C> PlanimetricsCalculationContext<C, *>.tangencyCondition(l: Line<C>, q: Quadric<C>): LabeledPolynomial<C> = polynomialSpace {
    l.x * l.x * (q.yz * q.yz - q.yy * q.zz * 4) +
            l.y * l.y * (q.xz * q.xz - q.xx * q.zz * 4) +
            l.z * l.z * (q.xy * q.xy - q.xx * q.yy * 4) +
            l.x * l.y * (q.xy * q.zz * 2 - q.xz * q.yz) * 2 +
            l.x * l.z * (q.xz * q.yy * 2 - q.xy * q.yz) * 2 +
            l.y * l.z * (q.xx * q.yz * 2 - q.xy * q.xz) * 2
}

///**
// * Checks if [this] point is lying on the line [l].
// *
// * @receiver The considered point.
// * @param l The considered line.
// * @return Boolean value of the statement.
// */
//public infix fun <C> Point<C>.isLyingOn(l: Line<C>) = lyingCondition(this, l).isZero()
//
///**
// * Checks if [this] point is not lying on the line [l].
// *
// * @receiver The considered point.
// * @param l The considered line.
// * @return Boolean value of the statement.
// */
//// FIXME: KT-5351
//public infix fun <C> Point<C>.isNotLyingOn(l: Line<C>) = lyingCondition(this, l).isNotZero()
//
///**
// * Checks if [this] point is lying on the quadric [q].
// *
// * @receiver The considered point.
// * @param q The considered quadric.
// * @return Boolean value of the statement.
// */
//public infix fun <C> Point<C>.isLyingOn(q: Quadric<C>) = lyingCondition(this, q).isZero()
//
///**
// * Checks if [this] point is not lying on the quadric [q].
// *
// * @receiver The considered point.
// * @param q The considered quadric.
// * @return Boolean value of the statement.
// */
//// FIXME: KT-5351
//public infix fun <C> Point<C>.isNotLyingOn(q: Quadric<C>) = lyingCondition(this, q).isNotZero()
//
///**
// * Checks if [this] line is lying through the point [P].
// *
// * @receiver The considered line.
// * @param P The considered point.
// * @return Boolean value of the statement.
// */
//public infix fun <C> Line<C>.isLyingThrough(P: Point<C>) = lyingCondition(P, this).isZero()
//
///**
// * Checks if [this] line is not lying through the point [P].
// *
// * @receiver The considered line.
// * @param P The considered point.
// * @return Boolean value of the statement.
// */
//// FIXME: KT-5351
//public infix fun <C> Line<C>.isNotLyingThrough(P: Point<C>) = lyingCondition(P, this).isNotZero()
//
///**
// * Checks if [this] line is tangent to the quadric [q].
// *
// * @receiver The considered line.
// * @param q The considered quadric.
// * @return Boolean value of the statement.
// */
//public infix fun <C> Line<C>.isTangentTo(q: Quadric<C>): Boolean = tangencyCondition(this, q).isZero()
//
///**
// * Checks if [this] line is not tangent to the quadric [q].
// *
// * @receiver The considered line.
// * @param q The considered quadric.
// * @return Boolean value of the statement.
// */
//// FIXME: KT-5351
//public infix fun <C> Line<C>.isNotTangentTo(q: Quadric<C>): Boolean = tangencyCondition(this, q).isNotZero()
//
///**
// * Checks if [this] quadric is lying through the point [P].
// *
// * @receiver The considered quadric.
// * @param P The considered point.
// * @return Boolean value of the statement.
// */
//public infix fun <C> Quadric<C>.isLyingThrough(P: Point<C>) = lyingCondition(P, this).isZero()
//
///**
// * Checks if [this] quadric is not lying through the point [P].
// *
// * @receiver The considered quadric.
// * @param P The considered point.
// * @return Boolean value of the statement.
// */
//// FIXME: KT-5351
//public infix fun <C> Quadric<C>.isNotLyingThrough(P: Point<C>) = lyingCondition(P, this).isNotZero()
//
///**
// * Checks if [this] quadric is tangent to the line [l].
// *
// * @receiver The considered quadric.
// * @param l The considered line.
// * @return Boolean value of the statement.
// */
//public infix fun <C> Quadric<C>.isTangentTo(l: Line<C>): Boolean = tangencyCondition(l, this).isZero()
//
///**
// * Checks if [this] quadric is not tangent to the line [l].
// *
// * @receiver The considered quadric.
// * @param l The considered line.
// * @return Boolean value of the statement.
// */
//// FIXME: KT-5351
//public infix fun <C> Quadric<C>.isNotTangentTo(l: Line<C>): Boolean = tangencyCondition(l, this).isNotZero()
// endregion

// region Arbitrary points and lines with initial conditions

public fun <C> PlanimetricsCalculationContext<C, *>.lineThrough(P: Point<C>) : ReadOnlyProperty<Any?, Line<C>> = ReadOnlyProperty { _, prop ->
    val xParameter = Symbol("${prop.name}\$param_x")
    val yParameter = Symbol("${prop.name}\$param_y")
    val zParameter = Symbol("${prop.name}\$param_z")
    polynomialSpace {
        Line(
            P.y * zParameter - P.z * yParameter,
            P.z * xParameter - P.x * zParameter,
            P.x * yParameter - P.y * xParameter,
        )
    }
}
public fun <C> PlanimetricsCalculationContext<C, *>.pointOn(L: Line<C>) : ReadOnlyProperty<Any?, Point<C>> = ReadOnlyProperty { _, prop ->
    val xParameter = Symbol("${prop.name}\$param_x")
    val yParameter = Symbol("${prop.name}\$param_y")
    val zParameter = Symbol("${prop.name}\$param_z")
    polynomialSpace {
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
public fun <C> PlanimetricsCalculationContext<C, *>.lineThrough(A: Point<C>, B: Point<C>): Line<C> = polynomialSpace {
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
public fun <C> PlanimetricsCalculationContext<C, *>.intersectionOf(l: Line<C>, m: Line<C>): Point<C> = polynomialSpace {
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
public fun <CT> PlanimetricsCalculationContext<CT, *>.collinearityCondition(A: Point<CT>, B: Point<CT>, C: Point<CT>): LabeledPolynomial<CT> = polynomialSpace { A.x * (B.y * C.z - B.z * C.y) + B.x * (C.y * A.z - C.z * A.y) + C.x * (A.y * B.z - A.z * B.y) }

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
public fun <C> PlanimetricsCalculationContext<C, *>.concurrencyCondition(l: Line<C>, m: Line<C>, n: Line<C>): LabeledPolynomial<C> = polynomialSpace { l.x * (m.y * n.z - m.z * n.y) + m.x * (n.y * l.z - n.z * l.y) + n.x * (l.y * m.z - l.z * m.y) }

/**
 * Constructs a midpoint between [A] and [B]. The affine map is considered generated by [Point<C>.x] and [Point<C>.y]
 * coordinates.
 *
 * @param A The first point.
 * @param B The second point.
 * @return The midpoint.
 */
public fun <C> PlanimetricsCalculationContext<C, *>.midpoint(A: Point<C>, B: Point<C>): Point<C> = polynomialSpace {
    Point(
        A.x * B.z + B.x * A.z,
        A.y * B.z + B.y * A.z,
        2 * A.z * B.z
    )
}

/**
 * Constructs a point P on the line through [A] and [B] that divides segment \([AB]\) in ratio lambda. It means that on
 * the affine map that is considered generated by [Point.x] and [Point.y] coordinates a point P such that
 * \(\overrightarrow{AP}/\overrightarrow{PB} = lambda\), so P is returned.
 *
 * @param A The first point.
 * @param B The second point.
 * @param lambda The ration.
 * @return The constructed point P.
 */
public fun <C> PlanimetricsCalculationContext<C, *>.divideSegmentInRatio(A: Point<C>, B: Point<C>, lambda: C): Point<C> = polynomialSpace {
    Point(
        A.x * B.z + lambda * B.x * A.z,
        A.y * B.z + lambda * B.y * A.z,
        (lambda + 1) * A.z * B.z
    )
}

// TODO: Docs
public fun <C> PlanimetricsCalculationContext<C, *>.parallelLine(l: Line<C>, A: Point<C>): Line<C> = polynomialSpace {
    Line(
        l.x * A.z,
        l.y * A.z,
        -(A.x * l.x + A.y * l.y)
    )
}

//// TODO: Docs
//public inline infix fun <C> Point<C>.parallelLineTo(l: Line<C>): Line<C> = parallelLine(l, this)

//// TODO: Docs
//public inline infix fun <C> Line<C>.parallelLineThrough(A: Point<C>): Line<C> = parallelLine(this, A)

/**
 * Constructs perpendicular in terms of affine map that is considered generated by [Point<C>.x] and [Point<C>.y] coordinates
 * line to the given line [l] through the given point [A].
 *
 * @param l The line to which the perpendicular is being constructed.
 * @param A The point through which the perpendicular is being constructed.
 * @return The constructed perpendicular.
 */
public fun <C> PlanimetricsCalculationContext<C, *>.perpendicular(l: Line<C>, A: Point<C>): Line<C> = polynomialSpace {
    Line(
        -l.y * A.z,
        l.x * A.z,
        A.x * l.y - A.y * l.x
    )
}

//// TODO: Docs
//public inline infix fun <C> Point<C>.perpendicularTo(l: Line<C>): Line<C> = perpendicular(l, this)

//// TODO: Docs
//public inline infix fun <C> Line<C>.perpendicularThrough(A: Point<C>): Line<C> = perpendicular(this, A)

///**
// * Construct a normal projection in terms of affine map that is considered generated by [Point<C>.x] and [Point<C>.y]
// * coordinates of [this] point to the given line [l].
// *
// * @receiver Projected point.
// * @param l Line projected on.
// * @return The projection.
// */
//public fun <C> Point<C>.projectOn(l: Line<C>) = intersectionOf(l, perpendicular(l, this))

// TODO: Docs
public fun <C> PlanimetricsCalculationContext<C, *>.segmentBisector(A: Point<C>, B: Point<C>): Line<C> = polynomialSpace {
    Line(
        -2 * (A.x * B.z - B.x * A.z) * A.z * B.z,
        -2 * (A.y * B.z - B.y * A.z) * A.z * B.z,
        (A.x * A.x + A.y * A.y) * B.z * B.z - (B.x * B.x + B.y * B.y) * A.z * A.z
    )
}
// endregion


// region Circles
///**
// * Checks if the given quadric is circle.
// *
// * @receiver The checked quadric.
// * @return Boolean value of the statement.
// */
//public fun <C> Quadric<C>.isCircle() = xy.isZero() && xx == yy

/**
 * Constructs circle (as [Quadric]) by its center and point on it.
 *
 * @param O The center of constructed circle.
 * @param A The given point on the constructed circle.
 * @return The constructed circle as [Quadric].
 */
public fun <C> PlanimetricsCalculationContext<C, *>.circleByCenterAndPoint(O: Point<C>, A: Point<C>): Quadric<C> = polynomialSpace {
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
public fun <C> PlanimetricsCalculationContext<C, *>.circleByDiameter(A: Point<C>, B: Point<C>): Quadric<C> = polynomialSpace {
    Quadric(
        A.z * B.z,
        A.z * B.z,
        A.y * B.y + A.x * B.x,
        zero,
        -(A.z * B.x + A.x * B.z),
        -(A.z * B.y + A.y * B.z)
    )
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
public fun <CT> PlanimetricsCalculationContext<CT, *>.centroid(A: Point<CT>, B: Point<CT>, C: Point<CT>): Point<CT> = polynomialSpace {
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
public fun <CT> PlanimetricsCalculationContext<CT, *>.orthocenter(A: Point<CT>, B: Point<CT>, C: Point<CT>): Point<CT> = polynomialSpace {
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
public fun <CT> PlanimetricsCalculationContext<CT, *>.circumcenter(A: Point<CT>, B: Point<CT>, C: Point<CT>): Point<CT> = polynomialSpace {
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
public fun <CT> PlanimetricsCalculationContext<CT, *>.circumcircle(A: Point<CT>, B: Point<CT>, C: Point<CT>): Quadric<CT> = polynomialSpace {
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
public fun <CT> PlanimetricsCalculationContext<CT, *>.eulerLine(A: Point<CT>, B: Point<CT>, C: Point<CT>): Line<CT> = polynomialSpace {
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
public fun <CT> PlanimetricsCalculationContext<CT, *>.eulerCircle(A: Point<CT>, B: Point<CT>, C: Point<CT>): Quadric<CT> = polynomialSpace {
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
//public fun <C> Point<C>.polarBy(q: Quadric<C>) = Line(rowVector * q.matrix)

//public fun <C> Line<C>.poleBy(q: Quadric<C>) = Point(rowVector * q.matrix.adjugate())

//public fun <C> Quadric<C>.dualBy(q: Quadric<C>) = with(q.matrix.adjugate()) { Quadric(this * matrix * this) }

//public fun <C> Quadric<C>.center() =
//    Point(
//        2 * xz * yy - xy * yz,
//        2 * xx * yz - xy * xz,
//        xy * xy - 4 * xx * yy
//    )
// endregion

// region Quadrics
/**
 * See also: [wiki](https://en.wikipedia.org/wiki/Five_points_determine_a_conic#Construction)
 */
public fun <CT> PlanimetricsCalculationContext<CT, *>.quadricByPoints(A: Point<CT>, B: Point<CT>, C: Point<CT>, D: Point<CT>, E: Point<CT>): Quadric<CT> = polynomialSpace {
    with(
        SquareMatrix(
            List(6) { zero },
            listOf(A.x * A.x, A.x * A.y, A.x * A.z, A.y * A.y, A.y * A.z, A.z * A.z),
            listOf(B.x * B.x, B.x * B.y, B.x * B.z, B.y * B.y, B.y * B.z, B.z * B.z),
            listOf(C.x * C.x, C.x * C.y, C.x * C.z, C.y * C.y, C.y * C.z, C.z * C.z),
            listOf(D.x * D.x, D.x * D.y, D.x * D.z, D.y * D.y, D.y * D.z, D.z * D.z),
            listOf(E.x * E.x, E.x * E.y, E.x * E.z, E.y * E.y, E.y * E.z, E.z * E.z)
        )
    ) {
        matrixSpace {
            Quadric(
                xx = minor(0, 0),
                xy = -minor(0, 1),
                xz = minor(0, 2),
                yy = -minor(0, 3),
                yz = minor(0, 4),
                zz = -minor(0, 5),
            )
        }
    }
}

//public fun <C> Line<C>.projectToQuadricBy(q: Quadric<C>, P: Point<C>): Point<C> =
//    if (P.isNotLyingOn(this) || P.isNotLyingOn(q)) throw IllegalArgumentException("The point must lye on the line and the quadric.")
//    else TODO("Not yet implemented")
////        Point(
////            q.xx * y * z * P.x + q.xy * y * z * P.y + q.xz * y * z * P.z - q.zz * x * y * P.z - q.yy * x * z * P.x,
////            (q.zz * x * x + q.xx * z * z - q.xz * x * z) * P.z,
////            (q.yy * x * x + q.xx * y * y - q.xy * x * y) * P.y
////        )

//public fun <C> Point<C>.projectToQuadricBy(q: Quadric<C>, P: Point<C>): Point<C> =
//    if (P.isNotLyingOn(q)) throw IllegalArgumentException("The point must lye on the quadric.")
//    else TODO("Not yet implemented")
////        Point(
////            0.toRational().toLabeledPolynomial(),
////            0.toRational().toLabeledPolynomial(),
////            0.toRational().toLabeledPolynomial()
////        )

//public fun <C> Point<C>.projectToQuadricBy(q: Quadric<C>, l: Line<C>): Line<C> =
//    if (l.isNotTangentTo(q) || l.isNotLyingThrough(this)) throw IllegalArgumentException("The line must lye through the point and touch the quadric.")
//    else TODO("Not yet implemented")
////        Point(
////            0.toRational().toLabeledPolynomial(),
////            0.toRational().toLabeledPolynomial(),
////            0.toRational().toLabeledPolynomial()
////        )

//public fun <C> Line<C>.projectToQuadricBy(q: Quadric<C>, l: Line<C>): Line<C> =
//    if (l.isNotTangentTo(q)) throw IllegalArgumentException("The line must touch the quadric.")
//    else TODO("Not yet implemented")
////        Point(
////            0.toRational().toLabeledPolynomial(),
////            0.toRational().toLabeledPolynomial(),
////            0.toRational().toLabeledPolynomial()
////        )

// endregion

// region Transformations

public fun <C> PlanimetricsCalculationContext<C, *>.involutionBy(A: Point<C>, l: Line<C>): Transformation<C> = polynomialSpace {
    Transformation(
        ((matrixSpace { l.rowVector * A.columnVector })[0, 0]).let {
            SquareMatrix(3) { rowIndex, columnIndex ->
                with(-2 * A.rowVector[rowIndex] * l.rowVector[columnIndex]) {
                    if (rowIndex == columnIndex) this + it else this
                }
            }
        }
    )
}

//public fun <C> PlanimetricsCalculationContext<C, *>.involutionBy(A: Point<C>, q: Quadric<C>): Transformation<C> = involutionBy(A, A.polarBy(q))

//public fun <C> PlanimetricsCalculationContext<C, *>.involutionBy(l: Line<C>, q: Quadric<C>): Transformation<C> = involutionBy(l.poleBy(q), l)

// endregion