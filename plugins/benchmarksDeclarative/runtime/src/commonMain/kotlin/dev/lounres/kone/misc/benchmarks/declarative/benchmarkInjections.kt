/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.declarative


public interface KoneBenchmarkInjectionContext<out Injection>

@Target(FUNCTION, PROPERTY)
public annotation class KoneBenchmarkInjectable

@KoneBenchmarkInjectable
public val <Injection> KoneBenchmarkInjectionContext<Injection>.injection: Injection get() = error("... intrinsic ...")

@KoneBenchmarkInjectable
context(_: KoneBenchmarkInjectionContext<Injection>)
public val <Injection> injection: Injection get() = error("... intrinsic ...")