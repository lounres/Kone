import dev.lounres.kone.buildSrc.benchmarks.denseListOfPowersOfTwo
import dev.lounres.kone.buildSrc.benchmarks.mainConfiguration

plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(versions.kotlinx.serialization.json)
            }
        }
    }
}

benchmark {
    configurations {
        register("koneArrayAllocationWithSizeOfPowersOfTwo") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.KoneArrayAllocationBenchmarks.*")
            param("size", value = (0 .. 30).map { 1 shl it }.toTypedArray())
        }
        register("koneArrayAllocationWithSizeFromZeroToNumber") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.KoneArrayAllocationBenchmarks.*")
            param("size", value = (0 .. 511).toList().toTypedArray())
        }
        register("koneArrayAllocationWithSizeFromDenseListOfPowersOfTwo") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.KoneArrayAllocationBenchmarks.*")
            param("size", value = denseListOfPowersOfTwo(start = 2, lastPower = 29).toTypedArray())
        }
        register("koneArrayAccessWithSizeOfPowersOfTwo") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.KoneArrayAccessBenchmarks.*")
            param("size", value = (0 .. 30).map { 1 shl it }.toTypedArray())
        }
        register("koneArrayAccessWithSizeFromZeroToNumber") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.KoneArrayAccessBenchmarks.*")
            param("size", value = (0 .. 511).toList().toTypedArray())
        }
        register("koneArrayAccessWithSizeFromDenseListOfPowersOfTwo") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.KoneArrayAccessBenchmarks.*")
            param("size", value = denseListOfPowersOfTwo(start = 2, lastPower = 29).toTypedArray())
        }
    }
}