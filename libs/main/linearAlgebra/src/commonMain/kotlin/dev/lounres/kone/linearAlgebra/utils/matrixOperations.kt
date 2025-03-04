/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.algebraic.zero
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.foldIndexed
import dev.lounres.kone.collections.utils.hasDuplicates
import dev.lounres.kone.combinatorics.enumerative.permutations
import dev.lounres.kone.comparison.neq
import dev.lounres.kone.linearAlgebra.Matrix
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.scope
import kotlin.sequences.fold


context(_: Ring<N>)
public val <N> Matrix<N>.isSymmetric: Boolean
    get() {
        if (rowNumber != columnNumber) return false
        
        for (row in 0u ..< rowNumber) for (column in row + 1u ..< columnNumber) if (this[row, column] neq this[column, row]) return false
        
        return true
    }

context(_: Ring<N>)
public val <N> Matrix<N>.isAntisymmetric: Boolean
    get() {
        if (rowNumber != columnNumber) return false
        
        for (row in 0u ..< rowNumber) {
            if (this[row, row].isNotZero()) return false
            for (column in row + 1u..<columnNumber) if ((this[row, column] + this[column, row]).isNotZero()) return false
        }
        
        return true
    }

context(_: A, _: VectorKategory<N>)
public val <N, A> Matrix<N>.transpose: Matrix<N> where A : Ring<N>
    get() = Matrix(this.columnNumber, this.rowNumber) { row, column -> this[column, row] }

//context(_: VectorSpace<N>)
//public val <N, A> Matrix<N>.reciprocal: Matrix<N> where A : Ring<N>
//    get() = (this.getFeature<_, InvertibleMatrixFeature<N>>() ?: throw IllegalArgumentException("Could not compute reciprocal matrix")).inverseMatrix