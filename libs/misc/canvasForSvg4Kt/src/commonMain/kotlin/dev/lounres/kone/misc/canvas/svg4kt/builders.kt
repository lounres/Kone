/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas.svg4kt

import dev.jamesyox.svg4kt.attr.attrs.ViewBox
import dev.jamesyox.svg4kt.attr.attrs.transform
import dev.jamesyox.svg4kt.attr.attrs.viewBox
import dev.jamesyox.svg4kt.consumers.SVGStreamTagConsumer
import dev.jamesyox.svg4kt.consumers.delayed
import dev.jamesyox.svg4kt.meta.RootContainer
import dev.jamesyox.svg4kt.tags.g
import dev.jamesyox.svg4kt.tags.svg
import dev.jamesyox.svg4kt.util.scale
import dev.lounres.kone.misc.canvas.KoneCanvasController
import dev.lounres.kone.misc.canvas.KoneCanvasData
import dev.lounres.kone.misc.canvas.KoneCanvasViewportKey
import dev.lounres.kone.registry.getOrNull
import dev.lounres.kone.scope


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
                    scope {
                        val viewport = canvasData.getOrNull(KoneCanvasViewportKey) ?: return@scope
                        viewBox = ViewBox(
                            minX = -viewport.x / 2,
                            minY = -viewport.y / 2,
                            width = viewport.x,
                            height = viewport.y,
                        )
                    }
                    g {
                        transform {
                            scale(1, -1)
                        }
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