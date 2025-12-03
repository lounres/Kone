/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.operations


//public interface SymmetricityComputer<in Matrix> : KoneContext {
//    public fun Matrix.isSymmetric(): Boolean
//
//    public companion object;
//
//    public class Key<Matrix>(
//        matrixType: SuppliedType,
//    ) : RegistryKey<SymmetricityComputer<Matrix>> {
//        public val typeKey: SuppliedType.Regular =
//            @OptIn(DelicateSuppliedTypeConstructor::class)
//            SuppliedType.Regular(
//                fullyQualifiedName = "dev.lounres.kone.linearAlgebra.operations.SymmetricityComputer",
//                typeArguments = listOf(
//                    SuppliedProjection.Regular(
//                        variance = IN,
//                        type = matrixType
//                    ),
//                ),
//                isNullable = false
//            )
//        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
//        override fun hashCode(): Int = typeKey.hashCode()
//    }
//}
//
//context(symmetricityComputer: SymmetricityComputer<Matrix>)
//public fun <Matrix> Matrix.isSymmetric(): Boolean = with(symmetricityComputer) { this@isSymmetric.isSymmetric() }
//
//private class DefaultSymmetricityComputer<Number>(
//    private val ring: Ring<Number>,
//) : SymmetricityComputer<MDList2<Number>> {
//    override fun MDList2<Number>.isSymmetric(): Boolean {
//        if (rowNumber != columnNumber) return false
//
//        for (row in 0u ..< rowNumber) for (column in row + 1u ..< columnNumber) if (ring { this[row, column] neq this[column, row] }) return false
//
//        return true
//    }
//}
//
//public fun <Number> SymmetricityComputer.Companion.default(
//    ring: Ring<Number>,
//): SymmetricityComputer<Number, MDList2<Number>> = DefaultSymmetricityComputer(ring)
//
//public fun <Number, Content2: MDList2<Number>> RegistryBuilder<KoneContextRegistry>.setDefaultSymmetricityComputerFor(
//    numberType: SuppliedType,
//    content2Type: SuppliedType,
//) {
//    val ring = this[Ring.Key<Number>(numberType)]
//    SymmetricityComputer.Key<Number, Content2>(numberType, content2Type) correspondsTo SymmetricityComputer.default(ring)
//}