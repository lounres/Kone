/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.progression


public /*value*/ data class ClosedRangeArithmeticProgression<out Number>(
    public val startInclusive: Number,
    public val endInclusive: Number,
    public val step: Number,
)
public /*value*/ data class OpenEndRangeArithmeticProgression<out Number>(
    public val startInclusive: Number,
    public val endExclusive: Number,
    public val step: Number,
)