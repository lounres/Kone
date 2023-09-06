/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

import com.lounres.kone.algebraic.Rational
import com.lounres.kone.context.invoke
import com.lounres.kone.order.asOrder
import com.lounres.kone.polynomial.LabeledPolynomial
import com.lounres.kone.polynomial.LabeledPolynomialSpace
import com.lounres.kone.polynomial.manipulation.Ideal
import com.lounres.kone.polynomial.manipulation.groebnerBasisByBuchbergersAlgorithm
import space.kscience.kmath.expressions.symbol


fun main(): Unit = LabeledPolynomialSpace(Rational.field).invoke {
    val x by symbol
    val xPoly = +x
//    val xPoly = polynomialOf(
//        monomial(
//            signatureOf(
//                variablePower(x, 1u)
//            ), constantOne
//        )
//    )
//    val poly2 = polynomialOf(
//        monomial(
//            signatureOf(),
//            constantValueOf(2)
//        )
//    )

//    val ideal = Ideal(listOf(xPoly, poly2))
//    println(ideal)
//    println(groebnerBasisByBuchbergersAlgorithm(LabeledPolynomial.signatureComparator.lex.asOrder(), ideal))
}