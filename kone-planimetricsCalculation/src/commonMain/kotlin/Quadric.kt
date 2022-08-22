package math.varia.planimetricsCalculation

import math.ringsAndFields.*
import math.polynomials.*
import math.linear.*
import kotlin.reflect.KProperty


data class Quadric (
    val xx: LabeledPolynomial<Rational>,
    val yy: LabeledPolynomial<Rational>,
    val zz: LabeledPolynomial<Rational>,
    val xy: LabeledPolynomial<Rational>,
    val xz: LabeledPolynomial<Rational>,
    val yz: LabeledPolynomial<Rational>
) {
    constructor(name: String) : this(
        Variable(name + "_xx").toLabeledPolynomial(1.toRational()),
        Variable(name + "_yy").toLabeledPolynomial(1.toRational()),
        Variable(name + "_zz").toLabeledPolynomial(1.toRational()),
        Variable(name + "_xy").toLabeledPolynomial(1.toRational()),
        Variable(name + "_xz").toLabeledPolynomial(1.toRational()),
        Variable(name + "_yz").toLabeledPolynomial(1.toRational())
    )
    constructor(matrix: SquareMatrix<LabeledPolynomial<Rational>>) : this(
        xx = matrix[0, 0].also { if (matrix.countOfRows != 3) throw IllegalArgumentException("Defining matrix should have sizes 3×3.")},
        yy = matrix[1, 1],
        zz = matrix[2, 2],
        xy = 2 * matrix[0, 1].also { if (matrix[1, 0] != it) throw IllegalArgumentException("Defining matrix should be symmetric.") },
        xz = 2 * matrix[0, 2].also { if (matrix[2, 0] != it) throw IllegalArgumentException("Defining matrix should be symmetric.") },
        yz = 2 * matrix[1, 2].also { if (matrix[2, 1] != it) throw IllegalArgumentException("Defining matrix should be symmetric.") }
    )

    val matrix
        get() =
            SquareMatrix(
                listOf(2 * xx, xy, xz),
                listOf(xy, 2 * yy, yz),
                listOf(xz, yz, 2 * zz)
            )

    override fun equals(other: Any?): Boolean =
        if (other !is Quadric) false
        else
            xx * other.yy == yy * other.xx &&
                    xx * other.zz == zz * other.xx &&
                    xx * other.xy == xy * other.xx &&
                    xx * other.xz == xz * other.xx &&
                    xx * other.yz == yz * other.xx &&
                    yy * other.zz == zz * other.yy &&
                    yy * other.xy == xy * other.yy &&
                    yy * other.xz == xz * other.yy &&
                    yy * other.yz == yz * other.yy &&
                    zz * other.xy == xy * other.zz &&
                    zz * other.xz == xz * other.zz &&
                    zz * other.yz == yz * other.zz &&
                    xy * other.xz == xz * other.xy &&
                    xy * other.yz == yz * other.xy &&
                    xz * other.yz == yz * other.xz

    override fun hashCode(): Int {
        var result = xx.hashCode()
        result = 31 * result + yy.hashCode()
        result = 31 * result + zz.hashCode()
        result = 31 * result + xy.hashCode()
        result = 31 * result + xz.hashCode()
        result = 31 * result + yz.hashCode()
        return result
    }

    companion object {
        operator fun getValue(thisRef: Any?, property: KProperty<*>) : Quadric = Quadric(property.name)
    }
}