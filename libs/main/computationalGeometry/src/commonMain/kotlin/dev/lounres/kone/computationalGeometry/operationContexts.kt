/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey


@GenerateKoneContextKey
public interface Dot<in Left, in Right, out Result> : KoneContext {
    public infix fun Left.dot(other: Right): Result
}

public inline fun <Left, Right, Result> Dot(crossinline block: (left: Left, right: Right) -> Result): Dot<Left, Right, Result> = object : Dot<Left, Right, Result> {
    override fun Left.dot(other: Right): Result = block(this, other)
}

context(dot: Dot<Left, Right, Result>)
public infix fun <Left, Right, Result> Left.dot(other: Right): Result = with(dot) { this@dot dot other }

context(dot: Dot<Input, Input, Result>)
public fun <Input, Result> Input.lengthSquared(): Result = this dot this