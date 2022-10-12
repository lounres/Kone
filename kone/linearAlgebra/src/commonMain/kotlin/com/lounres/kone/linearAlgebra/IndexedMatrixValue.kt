/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.linearAlgebra


///**
// * Data class representing a value from a collection or sequence, along with its index in that collection or sequence.
// *
// * @property value the underlying value.
// * @property index the index of the value in the collection or sequence.
// */
public data class IndexedMatrixValue<out T>(val index: MatrixIndex, val value: T)