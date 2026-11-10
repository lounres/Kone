/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.jmh

import dev.lounres.kone.misc.benchmarks.support.jmh.internal.JmhSupportInternal


public class JmhBenchmark @JmhSupportInternal constructor(
    public val name: String,
    public val parameters: Map<String, String>,
)