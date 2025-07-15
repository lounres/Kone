/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.planimetricsCalculus

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.eq
import dev.lounres.kone.polynomial.LabeledPolynomial


internal class QuadricEquality<Number>(val polynomialRing: Ring<LabeledPolynomial<Number>>) : Equality<Quadric<Number>> {
    override fun Quadric<Number>.equalsTo(other: Quadric<Number>): Boolean {
        if (this === other) return true
        
        return context(polynomialRing) {
            this.xx * other.yy eq this.yy * other.xx &&
                    this.xx * other.zz eq this.zz * other.xx &&
                    this.xx * other.xy eq this.xy * other.xx &&
                    this.xx * other.xz eq this.xz * other.xx &&
                    this.xx * other.yz eq this.yz * other.xx &&
                    this.yy * other.zz eq this.zz * other.yy &&
                    this.yy * other.xy eq this.xy * other.yy &&
                    this.yy * other.xz eq this.xz * other.yy &&
                    this.yy * other.yz eq this.yz * other.yy &&
                    this.zz * other.xy eq this.xy * other.zz &&
                    this.zz * other.xz eq this.xz * other.zz &&
                    this.zz * other.yz eq this.yz * other.zz &&
                    this.xy * other.xz eq this.xz * other.xy &&
                    this.xy * other.yz eq this.yz * other.xy &&
                    this.xz * other.yz eq this.yz * other.xz
        }
    }
}

public fun <Number> quadricEquality(polynomialRing: Ring<LabeledPolynomial<Number>>): Equality<Quadric<Number>> =
    QuadricEquality(polynomialRing)