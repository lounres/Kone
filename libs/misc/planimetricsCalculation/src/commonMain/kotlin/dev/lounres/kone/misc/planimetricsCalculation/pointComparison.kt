/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.context
import dev.lounres.kone.polynomial.LabeledPolynomial


internal class PointEquality<Number>(val polynomialRing: Ring<LabeledPolynomial<Number>>) : Equality<Point<Number>> {
    override fun Point<Number>.equalsTo(other: Point<Number>): Boolean {
        if (this === other) return true
        
        return context(polynomialRing) {
            this.x * other.y eq this.y * other.x
                    && this.y * other.z eq this.z * other.y
                    && this.z * other.x eq this.x * other.z
        }
    }
}

public fun <Number> pointEquality(polynomialRing: Ring<LabeledPolynomial<Number>>): Equality<Point<Number>> =
    PointEquality(polynomialRing)