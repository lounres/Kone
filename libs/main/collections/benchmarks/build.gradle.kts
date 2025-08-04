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
        val start = 2

        @OptIn(ExperimentalStdlibApi::class)
        val numbers = buildList {
            addAll(1 ..< (1 shl start))
            for (i in start .. 29) {
                addAll((1 shl i) ..< (1 shl (i + 1)) step (1 shl (i - start)))
            }
        }

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
        register("arrayAllocationWithSizeFromSpecificNumbers") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.ArrayAllocationBenchmarks.*")
            param("size", value = numbers.toTypedArray())
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
        register("arrayAccessWithSizeFromFromSpecificNumbers") {
            mainConfiguration()
            include("dev.lounres.kone.benchmarks.collections.array.ArrayAccessBenchmarks.*")
            param("size", value = numbers.toTypedArray())
        }
    }
}