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
        register("arrayAllocationWithSizeOfPowersOfTwo") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.ArrayAllocationBenchmarks.*")
            param("size", value = (0 .. 30).map { 1 shl it }.toTypedArray())
        }
        register("arrayAllocationWithSizeFromZeroToNumber") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.ArrayAllocationBenchmarks.*")
            param("size", value = (0 .. 511).toList().toTypedArray())
        }
        register("arrayAccessWithSizeOfPowersOfTwo") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.ArrayAccessBenchmarks.*")
            param("size", value = (0 .. 30).map { 1 shl it }.toTypedArray())
        }
        register("arrayAccessWithSizeFromZeroToNumber") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.ArrayAccessBenchmarks.*")
            param("size", value = (0 .. 511).toList().toTypedArray())
        }
    }
}