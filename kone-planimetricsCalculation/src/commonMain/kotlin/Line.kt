package math.varia.planimetricsCalculation

import math.ringsAndFields.*
import math.polynomials.*
import math.linear.*
import kotlin.reflect.KProperty

/**
 * Stores Line on projective plane as triplet of coefficients of its equation. The coefficients are determined with
 * accuracy to multiplication all of them by any constant.
 *
 * @property x Coefficient before 'x' variable.
 * @property y Coefficient before 'y' variable.
 * @property z Coefficient before 'z' variable.
 *
 * @constructor Constructs line with given values. Just stores them and does nothing.
 * @param x Coefficient before 'x' variable.
 * @param y Coefficient before 'y' variable.
 * @param z Coefficient before 'z' variable.
 */
data class Line (
    val x: LabeledPolynomial<Rational>,
    val y: LabeledPolynomial<Rational>,
    val z: LabeledPolynomial<Rational>
) {
    /**
     * Constructs arbitrary line with given [name]. Hence it's coefficients are just variables that get names after
     * [name]: coefficient before variable x will be variable [name]_x, and the same goes for y and z.
     *
     * @param name The name of the line.
     */
    constructor(name: String) : this(
        Variable(name + "_x").toLabeledPolynomial(1.toRational()),
        Variable(name + "_y").toLabeledPolynomial(1.toRational()),
        Variable(name + "_z").toLabeledPolynomial(1.toRational())
    )

    /**
     * Constructs line from the tuple. Hence the [rowVector] must contain exactly 3 elements (must have size 3⨯1).
     *
     * @param rowVector The input row vector.
//     * @throws
     */
    constructor(rowVector: RowVector<LabeledPolynomial<Rational>>) : this(
        rowVector[0],
        rowVector[1],
        rowVector[2]
    )

    /**
     * Constructs line from the tuple. Hence the [columnVector] must contain exactly 3 elements (must have size 1⨯3).
     *
     * @param columnVector The input column vector.
//     * @throws
     */
    constructor(columnVector: ColumnVector<LabeledPolynomial<Rational>>) : this(
        columnVector[0],
        columnVector[1],
        columnVector[2]
    )

    /**
     * Row vector representation.
     */
    val rowVector
        get() = RowVector(x, y, z)
    /**
     * Column vector representation.
     */
    val columnVector
        get() = ColumnVector(x, y, z)

    /**
     * Checks equality of the line to [other] object. True is returned if and only if [other] object is [Line] and has
     * proportional coefficients (i.e. [this] line and [other] line has equivalent equations).
     */
    override fun equals(other: Any?): Boolean =
        if (other !is Line) false
        else x * other.y == y * other.x && y * other.z == z * other.y && z * other.x == x * other.z

    /**
     * Trivial hash code generator.
     */
    override fun hashCode(): Int = 1

    companion object {
        operator fun getValue(thisRef: Any?, property: KProperty<*>) : Line = Line(property.name)
    }
}