/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks.collections.array

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.array.KoneBooleanArray
import dev.lounres.kone.collections.array.KoneByteArray
import dev.lounres.kone.collections.array.KoneCharArray
import dev.lounres.kone.collections.array.KoneDoubleArray
import dev.lounres.kone.collections.array.KoneFloatArray
import dev.lounres.kone.collections.array.KoneIntArray
import dev.lounres.kone.collections.array.KoneLongArray
import dev.lounres.kone.collections.array.KoneShortArray
import dev.lounres.kone.collections.array.KoneUByteArray
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.KoneULongArray
import dev.lounres.kone.collections.array.KoneUShortArray
import dev.lounres.kone.collections.array.generate
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State


@State(Scope.Benchmark)
class KoneArrayAllocationBenchmarks {
    @Param("0")
    final var size: UInt = 0u
    
    @Benchmark
    fun kone_null_generic(blackhole: Blackhole) {
        blackhole.consume(KoneArray.generate(size) { null })
    }
    
    @Benchmark
    fun kotlin_null_generic(blackhole: Blackhole) {
        blackhole.consume(Array<Void?>(size.toInt()) { null })
    }
    
    @Benchmark
    fun kone_uint_generic(blackhole: Blackhole) {
        blackhole.consume(KoneArray.generate(size) { it })
    }
    
    @Benchmark
    fun kotlin_uint_generic(blackhole: Blackhole) {
        blackhole.consume(Array(size.toInt()) { it })
    }
    
    @Benchmark
    fun kone_boolean(blackhole: Blackhole) {
        blackhole.consume(KoneBooleanArray.generate(size) { it % 2u == 0u })
    }
    
    @Benchmark
    fun kotlin_boolean(blackhole: Blackhole) {
        blackhole.consume(BooleanArray(size.toInt()) { it % 2 == 0 })
    }
    
    @Benchmark
    fun kone_char(blackhole: Blackhole) {
        blackhole.consume(KoneCharArray.generate(size) { it.toInt().toChar() })
    }
    
    @Benchmark
    fun kotlin_char(blackhole: Blackhole) {
        blackhole.consume(CharArray(size.toInt()) { it.toChar() })
    }
    
    @Benchmark
    fun kone_byte(blackhole: Blackhole) {
        blackhole.consume(KoneByteArray.generate(size) { it.toInt().toByte() })
    }
    
    @Benchmark
    fun kotlin_byte(blackhole: Blackhole) {
        blackhole.consume(ByteArray(size.toInt()) { it.toByte() })
    }
    
    @Benchmark
    fun kone_short(blackhole: Blackhole) {
        blackhole.consume(KoneShortArray.generate(size) { it.toInt().toShort() })
    }
    
    @Benchmark
    fun kotlin_short(blackhole: Blackhole) {
        blackhole.consume(ShortArray(size.toInt()) { it.toShort() })
    }
    
    @Benchmark
    fun kone_int(blackhole: Blackhole) {
        blackhole.consume(KoneIntArray.generate(size) { it.toInt() })
    }
    
    @Benchmark
    fun kotlin_int(blackhole: Blackhole) {
        blackhole.consume(IntArray(size.toInt()) { it })
    }
    
    @Benchmark
    fun kone_long(blackhole: Blackhole) {
        blackhole.consume(KoneLongArray.generate(size) { it.toInt().toLong() })
    }
    
    @Benchmark
    fun kotlin_long(blackhole: Blackhole) {
        blackhole.consume(LongArray(size.toInt()) { it.toLong() })
    }
    
    @Benchmark
    fun kone_float(blackhole: Blackhole) {
        blackhole.consume(KoneFloatArray.generate(size) { it.toInt().toFloat() })
    }
    
    @Benchmark
    fun kotlin_float(blackhole: Blackhole) {
        blackhole.consume(FloatArray(size.toInt()) { it.toFloat() })
    }
    
    @Benchmark
    fun kone_double(blackhole: Blackhole) {
        blackhole.consume(KoneDoubleArray.generate(size) { it.toInt().toDouble() })
    }
    
    @Benchmark
    fun kotlin_double(blackhole: Blackhole) {
        blackhole.consume(DoubleArray(size.toInt()) { it.toDouble() })
    }
    
    @Benchmark
    fun kone_ubyte(blackhole: Blackhole) {
        blackhole.consume(KoneUByteArray.generate(size) { it.toUByte() })
    }
    
    @Benchmark
    fun kotlin_ubyte(blackhole: Blackhole) {
        blackhole.consume(UByteArray(size.toInt()) { it.toUByte() })
    }
    
    @Benchmark
    fun kone_ushort(blackhole: Blackhole) {
        blackhole.consume(KoneUShortArray.generate(size) { it.toUShort() })
    }
    
    @Benchmark
    fun kotlin_ushort(blackhole: Blackhole) {
        blackhole.consume(UShortArray(size.toInt()) { it.toUShort() })
    }
    
    @Benchmark
    fun kone_uint(blackhole: Blackhole) {
        blackhole.consume(KoneUIntArray.generate(size) { it })
    }
    
    @Benchmark
    fun kotlin_uint(blackhole: Blackhole) {
        blackhole.consume(UIntArray(size.toInt()) { it.toUInt() })
    }
    
    @Benchmark
    fun kone_ulong(blackhole: Blackhole) {
        blackhole.consume(KoneULongArray.generate(size) { it.toULong() })
    }
    
    @Benchmark
    fun kotlin_ulong(blackhole: Blackhole) {
        blackhole.consume(ULongArray(size.toInt()) { it.toULong() })
    }
}