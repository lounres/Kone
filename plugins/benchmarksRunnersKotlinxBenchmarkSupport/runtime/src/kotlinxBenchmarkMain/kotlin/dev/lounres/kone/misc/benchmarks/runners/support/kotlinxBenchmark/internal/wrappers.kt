/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.internal

import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Mode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmInline


@Serializable
@JvmInline
public value class BenchmarkTimeUnitWrapper(
    @Serializable(BenchmarkTimeUnitSerializer::class)
    public val value: BenchmarkTimeUnit
) {
    private object BenchmarkTimeUnitSerializer : KSerializer<BenchmarkTimeUnit> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BenchmarkTimeUnit", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: BenchmarkTimeUnit) {
            encoder.encodeString(value.name)
        }
        override fun deserialize(decoder: Decoder): BenchmarkTimeUnit =
            BenchmarkTimeUnit.valueOf(decoder.decodeString())
    }
}

@Serializable
@JvmInline
public value class ModeWrapper(
    @Serializable(ModeSerializer::class)
    public val value: Mode
) {
    private object ModeSerializer : KSerializer<Mode> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Mode", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: Mode) {
            encoder.encodeString(value.name)
        }
        override fun deserialize(decoder: Decoder): Mode =
            Mode.valueOf(decoder.decodeString())
    }
}