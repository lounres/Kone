/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import kotlinx.serialization.Serializable


@Serializable
public data class KoneCanvasSize(
    public val width: Double,
    public val height: Double,
)