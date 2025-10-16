/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import kotlin.jvm.JvmInline


@JvmInline
public value class KoneCanvasEllipse(
    public val transformationMatrix: KoneCanvasTransformationMatrix,
)

internal fun KoneCanvasEllipse.transform(matrix: KoneCanvasTransformationMatrix): KoneCanvasEllipse =
    KoneCanvasEllipse(transformationMatrix = transformationMatrix.transform(matrix))