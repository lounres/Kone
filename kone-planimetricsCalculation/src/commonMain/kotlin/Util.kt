package math.varia.planimetricsCalculation

import math.ringsAndFields.*
import math.polynomials.*
import math.linear.*


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
fun lyingCondition(P: Point, l: Line) = P.x * l.x + P.y * l.y + P.z * l.z

/**
 * Returns an expression which equality to zero is equivalent to condition of [P] lying on [q].
 *
 * Obviously, it's just value of polynomial of [q] on [P].
 *
 * @param P The considered point.
 * @param q The considered quadric.
 * @return The expression.
 */
fun lyingCondition(P: Point, q: Quadric) =
    P.x * P.x * q.xx +
            P.y * P.y * q.yy +
            P.z * P.z * q.zz +
            P.x * P.y * q.xy +
            P.x * P.z * q.xz +
            P.y * P.z * q.yz

/**
 * Returns an expression which equality to zero is equivalent to condition of [l] being tangent to [q].
 *
 * @param l The considered line.
 * @param q The considered quadric.
 * @return The expression.
 */
fun tangencyCondition(l: Line, q: Quadric) =
    l.x * l.x * (q.yz * q.yz - q.yy * q.zz * 4) +
            l.y * l.y * (q.xz * q.xz - q.xx * q.zz * 4) +
            l.z * l.z * (q.xy * q.xy - q.xx * q.yy * 4) +
            l.x * l.y * (q.xy * q.zz * 2 - q.xz * q.yz) * 2 +
            l.x * l.z * (q.xz * q.yy * 2 - q.xy * q.yz) * 2 +
            l.y * l.z * (q.xx * q.yz * 2 - q.xy * q.xz) * 2

/**
 * Checks if [this] point is lying on the line [l].
 *
 * @receiver The considered point.
 * @param l The considered line.
 * @return Boolean value of the statement.
 */
fun Point.isLyingOn(l: Line) = lyingCondition(this, l).isZero()

/**
 * Checks if [this] point is not lying on the line [l].
 *
 * @receiver The considered point.
 * @param l The considered line.
 * @return Boolean value of the statement.
 */
fun Point.isNotLyingOn(l: Line) = lyingCondition(this, l).isNotZero()

/**
 * Checks if [this] point is lying on the quadric [q].
 *
 * @receiver The considered point.
 * @param q The considered quadric.
 * @return Boolean value of the statement.
 */
fun Point.isLyingOn(q: Quadric) = lyingCondition(this, q).isZero()

/**
 * Checks if [this] point is not lying on the quadric [q].
 *
 * @receiver The considered point.
 * @param q The considered quadric.
 * @return Boolean value of the statement.
 */
fun Point.isNotLyingOn(q: Quadric) = lyingCondition(this, q).isNotZero()

/**
 * Checks if [this] line is lying through the point [P].
 *
 * @receiver The considered line.
 * @param P The considered point.
 * @return Boolean value of the statement.
 */
fun Line.isLyingThrough(P: Point) = lyingCondition(P, this).isZero()

/**
 * Checks if [this] line is not lying through the point [P].
 *
 * @receiver The considered line.
 * @param P The considered point.
 * @return Boolean value of the statement.
 */
fun Line.isNotLyingThrough(P: Point) = lyingCondition(P, this).isNotZero()

/**
 * Checks if [this] line is tangent to the quadric [q].
 *
 * @receiver The considered line.
 * @param q The considered quadric.
 * @return Boolean value of the statement.
 */
fun Line.isTangentTo(q: Quadric): Boolean = tangencyCondition(this, q).isZero()

/**
 * Checks if [this] line is not tangent to the quadric [q].
 *
 * @receiver The considered line.
 * @param q The considered quadric.
 * @return Boolean value of the statement.
 */
fun Line.isNotTangentTo(q: Quadric): Boolean = tangencyCondition(this, q).isNotZero()

/**
 * Checks if [this] quadric is lying through the point [P].
 *
 * @receiver The considered quadric.
 * @param P The considered point.
 * @return Boolean value of the statement.
 */
fun Quadric.isLyingThrough(P: Point) = lyingCondition(P, this).isZero()

/**
 * Checks if [this] quadric is not lying through the point [P].
 *
 * @receiver The considered quadric.
 * @param P The considered point.
 * @return Boolean value of the statement.
 */
fun Quadric.isNotLyingThrough(P: Point) = lyingCondition(P, this).isNotZero()

/**
 * Checks if [this] quadric is tangent to the line [l].
 *
 * @receiver The considered quadric.
 * @param l The considered line.
 * @return Boolean value of the statement.
 */
fun Quadric.isTangentTo(l: Line): Boolean = tangencyCondition(l, this).isZero()

/**
 * Checks if [this] quadric is not tangent to the line [l].
 *
 * @receiver The considered quadric.
 * @param l The considered line.
 * @return Boolean value of the statement.
 */
fun Quadric.isNotTangentTo(l: Line): Boolean = tangencyCondition(l, this).isNotZero()
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
fun lineThrough(A: Point, B: Point) =
    Line(
        A.y * B.z - A.z * B.y,
        A.z * B.x - A.x * B.z,
        A.x * B.y - A.y * B.x
    )

/**
 * Constructs intersection of the given lines [l] and [m].
 *
 * Obviously it's vector multiplication operation.
 *
 * @param l The first line.
 * @param m The second line.
 * @return Intersection of the lines.
 */
fun intersectionOf(l: Line, m: Line) =
    Point(
        l.y * m.z - l.z * m.y,
        l.z * m.x - l.x * m.z,
        l.x * m.y - l.y * m.x
    )

/**
 * Returns an expression which equality to zero is equivalent to collinearity of the points.
 *
 * Obviously it's determinant of matrix constructed by row vectors of the points.
 *
 * @param A The first point.
 * @param B The second point.
 * @param C The third point.
 * @return The expression.
 */
fun collinearityCondition(A: Point, B: Point, C: Point) = A.x * (B.y * C.z - B.z * C.y) + B.x * (C.y * A.z - C.z * A.y) + C.x * (A.y * B.z - A.z * B.y)

/**
 * Returns an expression which equality to zero is equivalent to concurrency of the lines.
 *
 * Obviously it's determinant of matrix constructed by row vectors of the lines.
 *
 * @param l The first line.
 * @param m The second line.
 * @param n The third line.
 * @return The expression.
 */
fun concurrencyCondition(l: Line, m: Line, n: Line) = l.x * (m.y * n.z - m.z * n.y) + m.x * (n.y * l.z - n.z * l.y) + n.x * (l.y * m.z - l.z * m.y)

/**
 * Constructs a midpoint between [A] and [B]. The affine map is considered generated by [Point.x] and [Point.y]
 * coordinates.
 *
 * @param A The first point.
 * @param B The second point.
 * @return The midpoint.
 */
fun midpoint(A: Point, B: Point) =
    Point(
        A.x * B.z + B.x * A.z,
        A.y * B.z + B.y * A.z,
        2 * A.z * B.z
    )

/**
 * Constructs a point P on the line through [A] and [B] that divides segment `[AB]` in ratio lambda. It means that on
 * the affine map that is considered generated by [Point.x] and [Point.y] coordinates a point P such that
 * `\overrightarrow{AP}/\overrightarrow{PB} = lambda`, so P is returned.
 *
 * @param A The first point.
 * @param B The second point.
 * @param lambda The ration.
 * @return The constructed point P.
 */
fun divideSegmentInRatio(A: Point, B: Point, lambda: Rational) =
    Point(
        A.x * B.z + lambda * B.x * A.z,
        A.y * B.z + lambda * B.y * A.z,
        (lambda + 1) * A.z * B.z
    )

/**
 * Constructs perpendicular in terms of affine map that is considered generated by [Point.x] and [Point.y] coordinates
 * line to the given line [l] through the given point [A].
 *
 * @param l The line to which the perpendicular is being constructed.
 * @param A The point through which the perpendicular is being constructed.
 * @return The constructed perpendicular.
 */
fun perpendicular(l: Line, A: Point) =
    Line(
        -l.y * A.z,
        l.x * A.z,
        A.x * l.y - A.y * l.x
    )

/**
 * Construct a normal projection in terms of affine map that is considered generated by [Point.x] and [Point.y]
 * coordinates of [this] point to the given line [l].
 *
 * @receiver Projected point.
 * @param l Line projected on.
 * @return The projection.
 */
fun Point.projectTo(l: Line) = intersectionOf(l, perpendicular(l, this))
// endregion


// region Circles
/**
 * Checks if the given quadric is circle.
 *
 * @receiver The checked quadric.
 * @return Boolean value of the statement.
 */
fun Quadric.isCircle() = xy.isZero() && xx == yy

/**
 * Constructs circle (as [Quadric]) by its center and point on it.
 *
 * @param O The center of constructed circle.
 * @param A The given point on the constructed circle.
 * @return The constructed circle as [Quadric].
 */
fun circleByCenterAndPoint(O: Point, A: Point) =
    Quadric(
        xx = O.z * O.z * A.z * A.z,
        yy = O.z * O.z * A.z * A.z,
        zz = (2 * O.x * A.z - O.z * A.x) * A.x * O.z + (2 * O.y * A.z - O.z * A.y) * A.y * O.z,
        xz = -2 * O.x * O.z * A.z * A.z,
        yz = -2 * O.y * O.z * A.z * A.z,
        xy = 0.toRational().toLabeledPolynomial()
    )

/**
 * Constructs circle (as [Quadric]) by the given opposite points on it.
 *
 * @param A The first of the opposite points.
 * @param B The second of the opposite points.
 * @return The constructed circle as [Quadric].
 */
fun circleByDiameter(A: Point, B: Point) =
    Quadric(
        A.z * B.z,
        A.z * B.z,
        A.y * B.y + A.x * B.x,
        0.toRational().toLabeledPolynomial(),
        -(A.z * B.x + A.x * B.z),
        -(A.z * B.y + A.y * B.z)
    )
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
fun centroid(A: Point, B: Point, C: Point) =
    Point(
        A.x * B.z * C.z + B.x * C.z * A.z + C.x * A.z * B.z,
        A.y * B.z * C.z + B.y * C.z * A.z + C.y * A.z * B.z,
        3 * A.z * B.z * C.z
    )

/**
 * Constructs orthocenter of triangle ABC.
 *
 * @param A The first vertex of the triangle.
 * @param B The second vertex of the triangle.
 * @param C The third vertex of the triangle.
 * @return The orthocenter.
 */
fun orthocenter(A: Point, B: Point, C: Point) =
    Point(
        A.y * B.z * C.z * (A.x * (C.x * B.z - B.x * C.z) + A.y * (C.y * B.z - B.y * C.z)) + B.y * C.z * A.z * (B.x * (A.x * C.z - C.x * A.z) + B.y * (A.y * C.z - C.y * A.z)) + C.y * A.z * B.z * (C.x * (B.x * A.z - A.x * B.z) + C.y * (B.y * A.z - A.y * B.z)),
        A.x * B.z * C.z * (A.y * (B.y * C.z - C.y * B.z) + A.x * (B.x * C.z - C.x * B.z)) + B.x * C.z * A.z * (B.y * (C.y * A.z - A.y * C.z) + B.x * (C.x * A.z - A.x * C.z)) + C.x * A.z * B.z * (C.y * (A.y * B.z - B.y * A.z) + C.x * (A.x * B.z - B.x * A.z)),
        A.z * B.z * C.z * (A.x * B.y * C.z - A.x * B.z * C.y - A.y * B.x * C.z + A.y * B.z * C.x + A.z * B.x * C.y - A.z * B.y * C.x)
    )

/**
 * Constructs circumcenter of triangle ABC.
 *
 * @param A The first vertex of the triangle.
 * @param B The second vertex of the triangle.
 * @param C The third vertex of the triangle.
 * @return The circumcenter.
 */
fun circumcenter(A: Point, B: Point, C: Point) =
    Point(
        (A.x * A.x + A.y * A.y) * (B.y * C.z - C.y * B.z) * B.z * C.z
                + (B.x * B.x + B.y * B.y) * (C.y * A.z - A.y * C.z) * C.z * A.z
                + (C.x * C.x + C.y * C.y) * (A.y * B.z - B.y * A.z) * A.z * B.z,
        (A.x * A.x + A.y * A.y) * (C.x * B.z - B.x * C.z) * B.z * C.z
                + (B.x * B.x + B.y * B.y) * (A.x * C.z - C.x * A.z) * C.z * A.z
                + (C.x * C.x + C.y * C.y) * (B.x * A.z - A.x * B.z) * A.z * B.z,
        2 * (A.x * (B.y * C.z - C.y * B.z) + B.x * (C.y * A.z - A.y * C.z) + C.x * (A.y * B.z - B.y * A.z)) * A.z * B.z * C.z
    )

/**
 * Constructs circumcircle of triangle ABC as [Quadric].
 *
 * @param A The first vertex of the triangle.
 * @param B The second vertex of the triangle.
 * @param C The third vertex of the triangle.
 * @return The circumcircle as [Quadric].
 */
fun circumcircle(A: Point, B: Point, C: Point) =
    Quadric(
        (A.x * B.y * C.z - A.x * B.z * C.y - A.y * B.x * C.z + A.y * B.z * C.x + A.z * B.x * C.y - A.z * B.y * C.x) * A.z * B.z * C.z,
        (A.x * B.y * C.z - A.x * B.z * C.y - A.y * B.x * C.z + A.y * B.z * C.x + A.z * B.x * C.y - A.z * B.y * C.x) * A.z * B.z * C.z,
        -((A.x * A.x + A.y * A.y) * B.x * B.z * C.y * C.z - (A.x * A.x + A.y * A.y) * B.y * B.z * C.x * C.z - A.x * A.z * (B.x * B.x + B.y * B.y) * C.y * C.z + A.x * A.z * B.y * B.z * (C.x * C.x + C.y * C.y) + A.y * A.z * (B.x * B.x + B.y * B.y) * C.x * C.z - A.y * A.z * B.x * B.z * (C.x * C.x + C.y * C.y)),
        0.toRational().toLabeledPolynomial(),
        -((A.x * A.x + A.y * A.y) * B.y * B.z * C.z * C.z - (A.x * A.x + A.y * A.y) * B.z * B.z * C.y * C.z - A.y * A.z * (B.x * B.x + B.y * B.y) * C.z * C.z + A.y * A.z * B.z * B.z * (C.x * C.x + C.y * C.y) + A.z * A.z * (B.x * B.x + B.y * B.y) * C.y * C.z - A.z * A.z * B.y * B.z * (C.x * C.x + C.y * C.y)),
        (A.x * A.x + A.y * A.y) * B.x * B.z * C.z * C.z - (A.x * A.x + A.y * A.y) * B.z * B.z * C.x * C.z - A.x * A.z * (B.x * B.x + B.y * B.y) * C.z * C.z + A.x * A.z * B.z * B.z * (C.x * C.x + C.y * C.y) + A.z * A.z * (B.x * B.x + B.y * B.y) * C.x * C.z - A.z * A.z * B.x * B.z * (C.x * C.x + C.y * C.y)
    )

/**
 * Constructs Euler line of triangle ABC.
 *
 * @param A The first vertex of the triangle.
 * @param B The second vertex of the triangle.
 * @param C The third vertex of the triangle.
 * @return The Euler line.
 */
fun eulerLine(A: Point, B: Point, C: Point) =
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

/**
 * Constructs Euler's circle of triangle ABC as [Quadric].
 *
 * @param A The first vertex of the triangle.
 * @param B The second vertex of the triangle.
 * @param C The third vertex of the triangle.
 * @return The Euler's circle as [Quadric].
 */
fun eulersCircle(A: Point, B: Point, C: Point) =
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
        0.toRational().toLabeledPolynomial(),
        A.z * A.z * ((B.x * C.z + C.x * B.z) * (B.y * C.x - C.y * B.x) + (B.y * C.z - C.y * B.z) * (B.x * C.x + B.y * C.y))
                + B.z * B.z * ((C.x * A.z + A.x * C.z) * (C.y * A.x - A.y * C.x) + (C.y * A.z - A.y * C.z) * (C.x * A.x + C.y * A.y))
                + C.z * C.z * ((A.x * B.z + B.x * A.z) * (A.y * B.x - B.y * A.x) + (A.y * B.z - B.y * A.z) * (A.x * B.x + A.y * B.y)),
        A.z * A.z * ((B.y * C.z + C.y * B.z) * (C.x * B.y - B.x * C.y) + (C.x * B.z - B.x * C.z) * (B.y * C.y + B.x * C.x))
                + B.z * B.z * ((C.y * A.z + A.y * C.z) * (A.x * C.y - C.x * A.y) + (A.x * C.z - C.x * A.z) * (C.y * A.y + C.x * A.x))
                + C.z * C.z * ((A.y * B.z + B.y * A.z) * (B.x * A.y - A.x * B.y) + (B.x * A.z - A.x * B.z) * (A.y * B.y + A.x * B.x))
    )
// endregion


// region Pole and polar
fun Point.polarBy(q: Quadric) = Line(rowVector * q.matrix)

fun Line.poleBy(q: Quadric) = Point(rowVector * q.matrix.adjugate())

fun Quadric.dualBy(q: Quadric) = with(q.matrix.adjugate()) { Quadric(this * matrix * this) }

fun Quadric.center() =
    Point(
        2 * xz * yy - xy * yz,
        2 * xx * yz - xy * xz,
        xy * xy - 4 * xx * yy
    )
// endregion

// region Quadrics
/**
 * See also: [wiki](https://en.wikipedia.org/wiki/Five_points_determine_a_conic#Construction)
 */
fun quadricByPoints(A: Point, B: Point, C: Point, D: Point, E: Point): Quadric =
    with(
        SquareMatrix(
            List(6) { 0.toRational().toLabeledPolynomial() },
            listOf(A.x * A.x, A.x * A.y, A.x * A.z, A.y * A.y, A.y * A.z, A.z * A.z),
            listOf(B.x * B.x, B.x * B.y, B.x * B.z, B.y * B.y, B.y * B.z, B.z * B.z),
            listOf(C.x * C.x, C.x * C.y, C.x * C.z, C.y * C.y, C.y * C.z, C.z * C.z),
            listOf(D.x * D.x, D.x * D.y, D.x * D.z, D.y * D.y, D.y * D.z, D.z * D.z),
            listOf(E.x * E.x, E.x * E.y, E.x * E.z, E.y * E.y, E.y * E.z, E.z * E.z)
        )
    ) {
        Quadric(
            xx =  minor(0, 0),
            xy = -minor(0, 1),
            xz =  minor(0, 2),
            yy = -minor(0, 3),
            yz =  minor(0, 4),
            zz = -minor(0, 5),
        )
    }

fun Line.projectToQuadricBy(q: Quadric, P: Point): Point =
    if (P.isNotLyingOn(this) || P.isNotLyingOn(q)) throw IllegalArgumentException("The point must lye on the line and the quadric.")
    else TODO("Not yet implemented")
//        Point(
//            q.xx * y * z * P.x + q.xy * y * z * P.y + q.xz * y * z * P.z - q.zz * x * y * P.z - q.yy * x * z * P.x,
//            (q.zz * x * x + q.xx * z * z - q.xz * x * z) * P.z,
//            (q.yy * x * x + q.xx * y * y - q.xy * x * y) * P.y
//        )

fun Point.projectToQuadricBy(q: Quadric, P: Point): Point =
    if (P.isNotLyingOn(q)) throw IllegalArgumentException("The point must lye on the quadric.")
    else TODO("Not yet implemented")
//        Point(
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial()
//        )

fun Point.projectToQuadricBy(q: Quadric, l: Line): Line =
    if (l.isNotTangentTo(q) || l.isNotLyingThrough(this)) throw IllegalArgumentException("The line must lye through the point and touch the quadric.")
    else TODO("Not yet implemented")
//        Point(
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial()
//        )

fun Line.projectToQuadricBy(q: Quadric, l: Line): Line =
    if (l.isNotTangentTo(q)) throw IllegalArgumentException("The line must touch the quadric.")
    else TODO("Not yet implemented")
//        Point(
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial(),
//            0.toRational().toLabeledPolynomial()
//        )

// endregion

// region Transformations

fun involutionBy(A: Point, l: Line): Transformation =
    Transformation(
        ((l.rowVector * A.columnVector)[0, 0]).let {
            SquareMatrix(3) { rowIndex, columnIndex ->
                with(-2 * A.rowVector[rowIndex] * l.rowVector[columnIndex]) {
                    if (rowIndex == columnIndex) this + it else this
                }
            }
        }
    )

fun involutionBy(A: Point, q: Quadric): Transformation = involutionBy(A, A.polarBy(q))

fun involutionBy(l: Line, q: Quadric): Transformation = involutionBy(l.poleBy(q), l)

// endregion