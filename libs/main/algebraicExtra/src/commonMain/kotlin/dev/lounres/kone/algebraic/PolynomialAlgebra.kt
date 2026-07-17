/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextHolderInclude
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public interface PolynomialAlgebra<Number, Variable, Polynomial> : CommutativeAlgebra<Number, Polynomial> {
    @KoneContextHolderInclude
    public val variablePlusInt: Plus<Variable, Int, Polynomial>
    @KoneContextHolderInclude
    public val variableMinusInt: Minus<Variable, Int, Polynomial>
    @KoneContextHolderInclude
    public val variableTimesInt: Times<Variable, Int, Polynomial>
    
    @KoneContextHolderInclude
    public val variablePlusUInt: Plus<Variable, UInt, Polynomial>
    @KoneContextHolderInclude
    public val variableMinusUInt: Minus<Variable, UInt, Polynomial>
    @KoneContextHolderInclude
    public val variableTimesUInt: Times<Variable, UInt, Polynomial>
    
    @KoneContextHolderInclude
    public val variablePlusLong: Plus<Variable, Long, Polynomial>
    @KoneContextHolderInclude
    public val variableMinusLong: Minus<Variable, Long, Polynomial>
    @KoneContextHolderInclude
    public val variableTimesLong: Times<Variable, Long, Polynomial>
    
    @KoneContextHolderInclude
    public val variablePlusULong: Plus<Variable, ULong, Polynomial>
    @KoneContextHolderInclude
    public val variableMinusULong: Minus<Variable, ULong, Polynomial>
    @KoneContextHolderInclude
    public val variableTimesULong: Times<Variable, ULong, Polynomial>
    
    @KoneContextHolderInclude
    public val variablePlusNumber: Plus<Variable, Number, Polynomial>
    @KoneContextHolderInclude
    public val variableMinusNumber: Minus<Variable, Number, Polynomial>
    @KoneContextHolderInclude
    public val variableTimesNumber: Times<Variable, Number, Polynomial>
    
    @KoneContextHolderInclude
    public val intPlusVariable: Plus<Int, Variable, Polynomial>
    @KoneContextHolderInclude
    public val intMinusVariable: Minus<Int, Variable, Polynomial>
    @KoneContextHolderInclude
    public val intTimesVariable: Times<Int, Variable, Polynomial>
    
    @KoneContextHolderInclude
    public val uIntPlusVariable: Plus<UInt, Variable, Polynomial>
    @KoneContextHolderInclude
    public val uIntMinusVariable: Minus<UInt, Variable, Polynomial>
    @KoneContextHolderInclude
    public val uIntTimesVariable: Times<UInt, Variable, Polynomial>
    
    @KoneContextHolderInclude
    public val longPlusVariable: Plus<Long, Variable, Polynomial>
    @KoneContextHolderInclude
    public val longMinusVariable: Minus<Long, Variable, Polynomial>
    @KoneContextHolderInclude
    public val longTimesVariable: Times<Long, Variable, Polynomial>
    
    @KoneContextHolderInclude
    public val uLongPlusVariable: Plus<ULong, Variable, Polynomial>
    @KoneContextHolderInclude
    public val uLongMinusVariable: Minus<ULong, Variable, Polynomial>
    @KoneContextHolderInclude
    public val uLongTimesVariable: Times<ULong, Variable, Polynomial>
    
    @KoneContextHolderInclude
    public val numberPlusVariable: Plus<Number, Variable, Polynomial>
    @KoneContextHolderInclude
    public val numberMinusVariable: Minus<Number, Variable, Polynomial>
    @KoneContextHolderInclude
    public val numberTimesVariable: Times<Number, Variable, Polynomial>
    
    @KoneContextHolderInclude
    public val variableUnaryPlus: UnaryPlus<Variable, Polynomial>
    @KoneContextHolderInclude
    public val variableUnaryMinus: UnaryMinus<Variable, Polynomial>
    @KoneContextHolderInclude
    public val variablePlusVariable: Plus<Variable, Variable, Polynomial>
    @KoneContextHolderInclude
    public val variableMinusVariable: Minus<Variable, Variable, Polynomial>
    @KoneContextHolderInclude
    public val variableTimesVariable: Times<Variable, Variable, Polynomial>
    
    @KoneContextHolderInclude
    public val variablePlusPolynomial: Plus<Variable, Polynomial, Polynomial>
    @KoneContextHolderInclude
    public val variableMinusPolynomial: Minus<Variable, Polynomial, Polynomial>
    @KoneContextHolderInclude
    public val variableTimesPolynomial: Times<Variable, Polynomial, Polynomial>
    
    @KoneContextHolderInclude
    public val polynomialPlusVariable: Plus<Polynomial, Variable, Polynomial>
    @KoneContextHolderInclude
    public val polynomialMinusVariable: Minus<Polynomial, Variable, Polynomial>
    @KoneContextHolderInclude
    public val polynomialTimesVariable: Times<Polynomial, Variable, Polynomial>
    
    @KoneContextHolderInclude
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