/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas.svg4kt

import dev.jamesyox.svg4kt.consumers.SVGStreamTagConsumer
import dev.jamesyox.svg4kt.consumers.delayed
import dev.jamesyox.svg4kt.meta.RootContainer
import dev.jamesyox.svg4kt.tags.g
import dev.jamesyox.svg4kt.tags.svg
import dev.lounres.kone.misc.canvas.KoneCanvasController
import dev.lounres.kone.misc.canvas.KoneCanvasData
import dev.lounres.kone.registry.getOrNull


public fun <T : Appendable> T.appendKoneCanvasViaSvg4kt(
    canvasController: KoneCanvasController,
    canvasData: KoneCanvasData,
    isPrettyPrint: Boolean = false,
): T =
    SVGStreamTagConsumer(
        isPrettyPrint = isPrettyPrint,
        appendable = this,
    )
        .delayed()
        .apply {
            context(RootContainer) {
                svg {
                    g {
                        canvasController.getOrNull(KoneCanvasSvg4ktDrawer.Key)?.draw(canvasData)
                    }
                }
            }
        }
        .output()

public fun koneCanvasSvgStringViaSvg4kt(
    canvasController: KoneCanvasController,
    canvasData: KoneCanvasData,
    isPrettyPrint: Boolean = false,
): String = StringBuilder().appendKoneCanvasViaSvg4kt(
    canvasController = canvasController,
    canvasData = canvasData,
    isPrettyPrint = isPrettyPrint
).toString()