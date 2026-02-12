package dev.lounres.kone.graphs

import dev.lounres.kone.collections.list.KoneList


//@JvmInline
public /*value*/ data class Path<out Weight>(
    public val weight: Weight,
    public val vertices: KoneList<HypergraphVertex>,
    public val edges: KoneList<HypergraphEdge>,
)