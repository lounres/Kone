/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import androidx.compose.ui.graphics.ImageBitmap


public data class KoneCanvasImage(
    public val image: ImageBitmap,
    public val transformationMatrix: KoneCanvasTransformationMatrix,
)