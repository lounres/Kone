/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.util.collectionOperations

import kotlin.math.max
import kotlin.math.min


public inline fun <T> Array<T>.firstThat(predicate: (index: Int, T) -> Boolean): T {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun ByteArray.firstThat(predicate: (index: Int, Byte) -> Boolean): Byte {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun ShortArray.firstThat(predicate: (index: Int, Short) -> Boolean): Short {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun IntArray.firstThat(predicate: (index: Int, Int) -> Boolean): Int {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun LongArray.firstThat(predicate: (index: Int, Long) -> Boolean): Long {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun FloatArray.firstThat(predicate: (index: Int, Float) -> Boolean): Float {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun DoubleArray.firstThat(predicate: (index: Int, Double) -> Boolean): Double {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun BooleanArray.firstThat(predicate: (index: Int, Boolean) -> Boolean): Boolean {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun CharArray.firstThat(predicate: (index: Int, Char) -> Boolean): Char {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun <T> Array<T>.firstThatOrNull(predicate: (index: Int, T) -> Boolean): T? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

public inline fun ByteArray.firstThatOrNull(predicate: (index: Int, Byte) -> Boolean): Byte? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

public inline fun ShortArray.firstThatOrNull(predicate: (index: Int, Short) -> Boolean): Short? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

public inline fun IntArray.firstThatOrNull(predicate: (index: Int, Int) -> Boolean): Int? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

public inline fun LongArray.firstThatOrNull(predicate: (index: Int, Long) -> Boolean): Long? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

public inline fun FloatArray.firstThatOrNull(predicate: (index: Int, Float) -> Boolean): Float? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

public inline fun DoubleArray.firstThatOrNull(predicate: (index: Int, Double) -> Boolean): Double? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

public inline fun BooleanArray.firstThatOrNull(predicate: (index: Int, Boolean) -> Boolean): Boolean? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

public inline fun CharArray.firstThatOrNull(predicate: (index: Int, Char) -> Boolean): Char? {
    this.forEachIndexed { index, t -> if (predicate(index, t)) return t }
    return null
}

public inline fun <T> Array<T>.firstIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

public inline fun ByteArray.firstIndexThat(predicate: (index: Int, Byte) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

public inline fun ShortArray.firstIndexThat(predicate: (index: Int, Short) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

public inline fun IntArray.firstIndexThat(predicate: (index: Int, Int) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

public inline fun LongArray.firstIndexThat(predicate: (index: Int, Long) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

public inline fun FloatArray.firstIndexThat(predicate: (index: Int, Float) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

public inline fun DoubleArray.firstIndexThat(predicate: (index: Int, Double) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

public inline fun BooleanArray.firstIndexThat(predicate: (index: Int, Boolean) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

public inline fun CharArray.firstIndexThat(predicate: (index: Int, Char) -> Boolean): Int {
    forEachIndexed { index, t -> if (predicate(index, t)) return index }
    return -1
}

public inline fun <T> Array<T>.lastThat(predicate: (index: Int, T) -> Boolean): T {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun ByteArray.lastThat(predicate: (index: Int, Byte) -> Boolean): Byte {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun ShortArray.lastThat(predicate: (index: Int, Short) -> Boolean): Short {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun IntArray.lastThat(predicate: (index: Int, Int) -> Boolean): Int {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun LongArray.lastThat(predicate: (index: Int, Long) -> Boolean): Long {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun FloatArray.lastThat(predicate: (index: Int, Float) -> Boolean): Float {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun DoubleArray.lastThat(predicate: (index: Int, Double) -> Boolean): Double {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun BooleanArray.lastThat(predicate: (index: Int, Boolean) -> Boolean): Boolean {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun CharArray.lastThat(predicate: (index: Int, Char) -> Boolean): Char {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    throw NoSuchElementException("Array contains no element matching the predicate.")
}

public inline fun <T> Array<T>.lastThatOrNull(predicate: (index: Int, T) -> Boolean): T? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

public inline fun ByteArray.lastThatOrNull(predicate: (index: Int, Byte) -> Boolean): Byte? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

public inline fun ShortArray.lastThatOrNull(predicate: (index: Int, Short) -> Boolean): Short? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

public inline fun IntArray.lastThatOrNull(predicate: (index: Int, Int) -> Boolean): Int? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

public inline fun LongArray.lastThatOrNull(predicate: (index: Int, Long) -> Boolean): Long? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

public inline fun FloatArray.lastThatOrNull(predicate: (index: Int, Float) -> Boolean): Float? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

public inline fun DoubleArray.lastThatOrNull(predicate: (index: Int, Double) -> Boolean): Double? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

public inline fun BooleanArray.lastThatOrNull(predicate: (index: Int, Boolean) -> Boolean): Boolean? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

public inline fun CharArray.lastThatOrNull(predicate: (index: Int, Char) -> Boolean): Char? {
    for (index in this.indices.reversed()) {
        val element = this[index]
        if (predicate(index, element)) return element
    }
    return null
}

public inline fun <T> Array<T>.lastIndexThat(predicate: (index: Int, T) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

public inline fun ByteArray.lastIndexThat(predicate: (index: Int, Byte) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

public inline fun ShortArray.lastIndexThat(predicate: (index: Int, Short) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

public inline fun IntArray.lastIndexThat(predicate: (index: Int, Int) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

public inline fun LongArray.lastIndexThat(predicate: (index: Int, Long) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

public inline fun FloatArray.lastIndexThat(predicate: (index: Int, Float) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

public inline fun DoubleArray.lastIndexThat(predicate: (index: Int, Double) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

public inline fun BooleanArray.lastIndexThat(predicate: (index: Int, Boolean) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

public inline fun CharArray.lastIndexThat(predicate: (index: Int, Char) -> Boolean): Int {
    for (index in this.indices.reversed()) if (predicate(index, this[index])) return index
    return -1
}

//================================================================================================

public inline fun IntArray.count(from: Int = 0, to: Int = size, predicate: (Int) -> Boolean): Int {
    var count = 0
    for (i in max(0, from) .. min(size, to)) if (predicate(this[i])) count++
    return count
}

public inline fun BooleanArray.count(from: Int = 0, to: Int = size, predicate: (Boolean) -> Boolean): Int {
    var count = 0
    for (i in max(0, from) .. min(size, to)) if (predicate(this[i])) count++
    return count
}