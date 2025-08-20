/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import androidx.compose.ui.graphics.Path
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.list.toKoneList
import dev.lounres.kone.collections.utils.last
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.computationalGeometry.PointWrapper
import dev.lounres.kone.computationalGeometry.VectorWrapper
import dev.lounres.kone.computationalGeometry.plus
import dev.lounres.kone.multidimensionalCollections.MDList1
import kotlin.jvm.JvmInline


@JvmInline
public value class KoneCanvasPath internal constructor(
    internal val paths: KoneList<Subpath>,
) {
    internal class Subpath(
        val start: PointWrapper<MDList1<Double>>,
        val parts: KoneList<Part>,
    ) {
        class Builder(
            val start: PointWrapper<MDList1<Double>>,
        ) {
            private val parts: KoneMutableList<Part> = KoneMutableList.of()
            var end: PointWrapper<MDList1<Double>> = start
                private set
            
            fun add(part: Part) {
                parts.add(part)
                end = part.end
            }
            
            fun build(): Subpath =
                Subpath(
                    start = start,
                    parts = parts.toKoneList(),
                )
        }
    }
    
    internal sealed interface Part {
        val end: PointWrapper<MDList1<Double>>
        
        data class LineTo(override val end: PointWrapper<MDList1<Double>>) : Part
    }
    
    @JvmInline
    public value class Builder internal constructor(
        private val paths: KoneMutableList<Subpath.Builder>,
    ) {
        internal fun build(): KoneCanvasPath = KoneCanvasPath(paths.map { it.build() })
        
        public fun moveTo(start: PointWrapper<MDList1<Double>>) {
            paths.add(Subpath.Builder(start))
        }
        
        public fun relativeMoveTo(shift: VectorWrapper<MDList1<Double>>) {
            paths.add(
                Subpath.Builder(
                    inKoneCanvasEuclideanSpace {
                        paths.last().end + shift
                    }
                )
            )
        }
        
        public fun lineTo(end: PointWrapper<MDList1<Double>>) {
            paths.last().add(Part.LineTo(end))
        }
        
        public fun relativeLineTo(shift: VectorWrapper<MDList1<Double>>) {
            paths.last().add(
                Part.LineTo(
                    inKoneCanvasEuclideanSpace {
                        paths.last().end + shift
                    }
                )
            )
        }
    }
}

public fun KoneCanvasPath(builder: KoneCanvasPath.Builder.() -> Unit): KoneCanvasPath =
    KoneCanvasPath.Builder(KoneMutableList.of()).apply(builder).build()

internal fun KoneCanvasPath.toComposePath(): Path =
    Path().apply {
        for (subpath in paths) {
            moveTo(subpath.start.vector[0u].toFloat(), subpath.start.vector[1u].toFloat())
            for (part in subpath.parts)
                when (part) {
                    is KoneCanvasPath.Part.LineTo -> lineTo(part.end.vector[0u].toFloat(), part.end.vector[1u].toFloat())
                }
        }
    }