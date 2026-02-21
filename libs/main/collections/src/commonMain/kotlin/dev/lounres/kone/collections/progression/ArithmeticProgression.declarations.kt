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