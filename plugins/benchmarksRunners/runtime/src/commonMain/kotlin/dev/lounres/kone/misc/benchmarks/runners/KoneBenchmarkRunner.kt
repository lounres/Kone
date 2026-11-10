/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.runners


@Target(FUNCTION)
public annotation class KoneBenchmarkRunner(val name: String = "")