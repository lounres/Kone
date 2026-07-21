/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface PolynomialAlgebra<Number, Variable, Polynomial> : CommutativeAlgebra<Number, Polynomial> {
    @KoneContextInclude
    public val variablePlusInt: Plus<Variable, Int, Polynomial>
    @KoneContextInclude
    public val variableMinusInt: Minus<Variable, Int, Polynomial>
    @KoneContextInclude
    public val variableTimesInt: Times<Variable, Int, Polynomial>
    
    @KoneContextInclude
    public val variablePlusUInt: Plus<Variable, UInt, Polynomial>
    @KoneContextInclude
    public val variableMinusUInt: Minus<Variable, UInt, Polynomial>
    @KoneContextInclude
    public val variableTimesUInt: Times<Variable, UInt, Polynomial>
    
    @KoneContextInclude
    public val variablePlusLong: Plus<Variable, Long, Polynomial>
    @KoneContextInclude
    public val variableMinusLong: Minus<Variable, Long, Polynomial>
    @KoneContextInclude
    public val variableTimesLong: Times<Variable, Long, Polynomial>
    
    @KoneContextInclude
    public val variablePlusULong: Plus<Variable, ULong, Polynomial>
    @KoneContextInclude
    public val variableMinusULong: Minus<Variable, ULong, Polynomial>
    @KoneContextInclude
    public val variableTimesULong: Times<Variable, ULong, Polynomial>
    
    @KoneContextInclude
    public val variablePlusNumber: Plus<Variable, Number, Polynomial>
    @KoneContextInclude
    public val variableMinusNumber: Minus<Variable, Number, Polynomial>
    @KoneContextInclude
    public val variableTimesNumber: Times<Variable, Number, Polynomial>
    
    @KoneContextInclude
    public val intPlusVariable: Plus<Int, Variable, Polynomial>
    @KoneContextInclude
    public val intMinusVariable: Minus<Int, Variable, Polynomial>
    @KoneContextInclude
    public val intTimesVariable: Times<Int, Variable, Polynomial>
    
    @KoneContextInclude
    public val uIntPlusVariable: Plus<UInt, Variable, Polynomial>
    @KoneContextInclude
    public val uIntMinusVariable: Minus<UInt, Variable, Polynomial>
    @KoneContextInclude
    public val uIntTimesVariable: Times<UInt, Variable, Polynomial>
    
    @KoneContextInclude
    public val longPlusVariable: Plus<Long, Variable, Polynomial>
    @KoneContextInclude
    public val longMinusVariable: Minus<Long, Variable, Polynomial>
    @KoneContextInclude
    public val longTimesVariable: Times<Long, Variable, Polynomial>
    
    @KoneContextInclude
    public val uLongPlusVariable: Plus<ULong, Variable, Polynomial>
    @KoneContextInclude
    public val uLongMinusVariable: Minus<ULong, Variable, Polynomial>
    @KoneContextInclude
    public val uLongTimesVariable: Times<ULong, Variable, Polynomial>
    
    @KoneContextInclude
    public val numberPlusVariable: Plus<Number, Variable, Polynomial>
    @KoneContextInclude
    public val numberMinusVariable: Minus<Number, Variable, Polynomial>
    @KoneContextInclude
    public val numberTimesVariable: Times<Number, Variable, Polynomial>
    
    @KoneContextInclude
    public val variableUnaryPlus: UnaryPlus<Variable, Polynomial>
    @KoneContextInclude
    public val variableUnaryMinus: UnaryMinus<Variable, Polynomial>
    @KoneContextInclude
    public val variablePlusVariable: Plus<Variable, Variable, Polynomial>
    @KoneContextInclude
    public val variableMinusVariable: Minus<Variable, Variable, Polynomial>
    @KoneContextInclude
    public val variableTimesVariable: Times<Variable, Variable, Polynomial>
    
    @KoneContextInclude
    public val variablePlusPolynomial: Plus<Variable, Polynomial, Polynomial>
    @KoneContextInclude
    public val variableMinusPolynomial: Minus<Variable, Polynomial, Polynomial>
    @KoneContextInclude
    public val variableTimesPolynomial: Times<Variable, Polynomial, Polynomial>
    
    @KoneContextInclude
    public val polynomialPlusVariable: Plus<Polynomial, Variable, Polynomial>
    @KoneContextInclude
    public val polynomialMinusVariable: Minus<Polynomial, Variable, Polynomial>
    @KoneContextInclude
    public val polynomialTimesVariable: Times<Polynomial, Variable, Polynomial>
    
    @KoneContextInclude
    public val variablesAndDegree: VariablesAndDegree<Variable, Polynomial>
    
    public interface VariablesAndDegree<Variable, Polynomial> : KoneContext {
        public val Polynomial.variables: KoneSet<Variable>
        public val Polynomial.numberOfVariables: UInt get() = variables.size
        public fun Polynomial.degreeBy(variables: KoneSet<Variable>): UInt
        public fun Polynomial.degreeBy(variable: Variable): UInt
        public val Polynomial.degrees: KoneMap<Variable, UInt>
        public val Polynomial.degree: UInt get() = degreeBy(variables)
    }
    
    @Suppliable
    public class Key<@Supply Number, @Supply Variable, @Supply Polynomial> : SuppliedTypeRegistryKey<PolynomialAlgebra<Number, Variable, Polynomial>>() {
        override val impliedKeys: ImpliedKeysRegistry<PolynomialAlgebra<Number, Variable, Polynomial>> by lazy {
            ImpliedKeysRegistry {
                CommutativeAlgebra.Key<Number, Polynomial>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.PolynomialAlgebra.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Variable>()}, ${suppliedTypeOf<Polynomial>()}>"
    }
}

context(variablesAndDegree: PolynomialAlgebra.VariablesAndDegree<Variable, Polynomial>)
public val <Variable, Polynomial> Polynomial.variables: KoneSet<Variable> get() = with(variablesAndDegree) { this@variables.variables }
context(variablesAndDegree: PolynomialAlgebra.VariablesAndDegree<*, Polynomial>)
public val <Polynomial> Polynomial.numberOfVariables: UInt get() = with(variablesAndDegree) { this@numberOfVariables.numberOfVariables }
context(variablesAndDegree: PolynomialAlgebra.VariablesAndDegree<Variable, Polynomial>)
public fun <Variable, Polynomial> Polynomial.degreeBy(variables: KoneSet<Variable>): UInt = with(variablesAndDegree) { this@degreeBy.degreeBy(variables) }
context(variablesAndDegree: PolynomialAlgebra.VariablesAndDegree<Variable, Polynomial>)
public fun <Variable, Polynomial> Polynomial.degreeBy(variable: Variable): UInt = with(variablesAndDegree) { this@degreeBy.degreeBy(variable) }
context(variablesAndDegree: PolynomialAlgebra.VariablesAndDegree<Variable, Polynomial>)
public val <Variable, Polynomial> Polynomial.degrees: KoneMap<Variable, UInt> get() = with(variablesAndDegree) { this@degrees.degrees }
context(variablesAndDegree: PolynomialAlgebra.VariablesAndDegree<*, Polynomial>)
public val <Polynomial> Polynomial.degree: UInt get() = with(variablesAndDegree) { this@degree.degree }