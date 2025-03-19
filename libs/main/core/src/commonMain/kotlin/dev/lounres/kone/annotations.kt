/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone


/**
 * Marks parts of Kone API that have more or less certain shape but which final form is not yet finished.
 */
@MustBeDocumented
@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This API is unstable. It may be changed in the future without notice."
)
@Retention(AnnotationRetention.BINARY)
public annotation class UnstableKoneAPI

/**
 * Marks parts of Kone API that are just sketches of possible API.
 * Their main purpose is only to gather ideas of the API and check which parts of it should be rewritten from scratch.
 */
@MustBeDocumented
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is a draft API. It may be changed or deleted in the future without notice."
)
@Retention(AnnotationRetention.BINARY)
public annotation class ExperimentalKoneAPI