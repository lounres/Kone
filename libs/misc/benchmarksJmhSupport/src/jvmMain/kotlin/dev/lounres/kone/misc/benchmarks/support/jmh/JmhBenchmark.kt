/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.jmh


public class JmhBenchmark(
    public val include: List<String>,
    public val exclude: List<String>,
    public val parameters: Map<String, String>,
)