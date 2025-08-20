/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline


@Serializable
@JvmInline
public value class VectorWrapper<out Vector>(public val vector: Vector)

@Serializable
@JvmInline
public value class PointWrapper<out Vector>(public val vector: Vector)