/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.benchmarks.kotlinxBenchmarkSupport.ir

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter


data class KotlinxBenchmarkClass(
    val theClass: IrClass,
    val theConstructor: IrConstructor,
    val setupMethods: List<IrSimpleFunction>,
    val tearDownMethods: List<IrSimpleFunction>,
    val benchmarkMethods: List<KotlinxBenchmarkMethod>,
    val benchmarkModes: List<KotlinxBenchmarkMode>,
    val outputTimeUnit: KotlinxBenchmarkTimeUnit,
    val warmup: KotlinxBenchmarkWarmupOrMeasurement,
    val measurement: KotlinxBenchmarkWarmupOrMeasurement,
    val params: Map<IrProperty, KotlinxBenchmarkParamDescription>,
)

data class KotlinxBenchmarkMethod(
    val theMethod: IrSimpleFunction,
    val dispatchReceiverValueParameter: IrValueParameter,
    val blackholeValueParameter: IrValueParameter?,
)

sealed interface KotlinxBenchmarkMode {
    val name: String
    
    sealed interface Jvm : KotlinxBenchmarkMode
    sealed interface NonJvm : KotlinxBenchmarkMode
    
    data object Throughput : Jvm, NonJvm {
        override val name: String get() = "Throughput"
    }
    data object AverageTime : Jvm, NonJvm {
        override val name: String get() = "AverageTime"
    }
    data object SampleTime : Jvm {
        override val name: String get() = "SampleTime"
    }
    data object SingleShotTime : Jvm {
        override val name: String get() = "SingleShotTime"
    }
    data object All : Jvm {
        override val name: String get() = "All"
    }
    
    companion object {
        val entries = listOf<KotlinxBenchmarkMode>(
            Throughput,
            AverageTime,
            SampleTime,
            SingleShotTime,
            All,
        )
    }
}

sealed interface KotlinxBenchmarkTimeUnit {
    val name: String
    
    sealed interface Jvm : KotlinxBenchmarkTimeUnit
    sealed interface NonJvm : KotlinxBenchmarkTimeUnit
    
    data object NANOSECONDS : Jvm, NonJvm {
        override val name: String get() = "NANOSECONDS"
    }
    data object MICROSECONDS : Jvm, NonJvm {
        override val name: String get() = "MICROSECONDS"
    }
    data object MILLISECONDS : Jvm, NonJvm {
        override val name: String get() = "MILLISECONDS"
    }
    data object SECONDS : Jvm, NonJvm {
        override val name: String get() = "SECONDS"
    }
    data object MINUTES : Jvm, NonJvm {
        override val name: String get() = "MINUTES"
    }
    data object HOURS : Jvm {
        override val name: String get() = "HOURS"
    }
    data object DAYS : Jvm {
        override val name: String get() = "DAYS"
    }
    
    companion object {
        val entries = listOf<KotlinxBenchmarkTimeUnit>(
            NANOSECONDS,
            MICROSECONDS,
            MILLISECONDS,
            SECONDS,
            MINUTES,
            HOURS,
            DAYS,
        )
    }
}

data class KotlinxBenchmarkWarmupOrMeasurement(
    val iterations: UInt,
    val time: ULong,
    val timeUnit: KotlinxBenchmarkTimeUnit,
    val batchSize: UInt,
) {
    companion object {
        val default = KotlinxBenchmarkWarmupOrMeasurement(
            iterations = 5u,
            time = 10u,
            timeUnit = SECONDS,
            batchSize = 0u,
        )
    }
}

data class KotlinxBenchmarkParamDescription(
    val setter: IrSimpleFunction,
    val defaultValues: List<String>,
    val kind: Kind,
) {
    enum class Kind {
        Byte, Short, Int, Long,
        UByte, UShort, UInt, ULong,
        Float, Double,
        Boolean,
        Char,
        String,
    }
}