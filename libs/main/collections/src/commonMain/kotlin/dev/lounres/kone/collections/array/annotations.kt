/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.array


@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION)
@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "Immutable array constructor just wraps Kotlin array. Be sure not to mutate underlying Kotlin array."
)
public annotation class DelicateImmutableArrayConstructor