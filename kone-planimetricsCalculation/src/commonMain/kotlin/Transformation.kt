package math.varia.planimetricsCalculation

import math.ringsAndFields.*
import math.polynomials.*
import math.linear.*


class Transformation(
    val matrix: SquareMatrix<LabeledPolynomial<Rational>>
) {
    init {
        if (matrix.countOfRows != 3) throw IllegalArgumentException("Transformation should be defined by 3⨉3 matrix. No other sizes are compatible.")
    }

    operator fun invoke(P: Point): Point = Point(matrix * P.columnVector)

    operator fun invoke(l: Line): Line = Line(l.rowVector * matrix.adjugate())

    operator fun invoke(q: Quadric): Quadric = (matrix.adjugate()).let { Quadric(it.transposed() * q.matrix * it) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Transformation) return false

        val indices = matrix.indices

        for (num1 in 0 .. indices.lastIndex) for (num2 in num1+1 .. indices.lastIndex) {
            val index1 = indices[num1]
            val index2 = indices[num2]
            if (matrix[index1] * other.matrix[index2] != matrix[index2] * other.matrix[index1]) return false
        }

        return true
    }

    override fun hashCode(): Int {
        return matrix.hashCode()
    }
}