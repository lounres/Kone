package io.heapy.ktc.plugins.kover

import org.jetbrains.amper.plugins.Configurable

@Configurable
interface KoverSettings {
    /** Minimum percentage of covered executable lines, from 0 to 100 inclusive. */
    val minimumLineCoverage: Int get() = 0
    
    /** Kover class-name wildcards; an empty list includes all classes in this module. */
    val includes: List<String> get() = emptyList()
    val excludes: List<String> get() = emptyList()
    
    /** Optional JUnit tag expressions, passed to the toolchain's test runner. */
    val includeTags: List<String> get() = emptyList()
    val excludeTags: List<String> get() = emptyList()
    
    /** Timeout for the instrumented test build, including compilation. */
    val testTimeoutSeconds: Int get() = 600
}
