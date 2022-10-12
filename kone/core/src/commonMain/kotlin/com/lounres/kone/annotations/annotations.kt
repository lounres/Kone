/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.annotations


@MustBeDocumented
@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This API is unstable. It may be changed in the future without notice."
)
@Retention(AnnotationRetention.BINARY)
public annotation class UnstableKoneAPI

@MustBeDocumented
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is a draft API. It may be changed or deleted in the future without notice."
)
@Retention(AnnotationRetention.BINARY)
public annotation class ExperimentalKoneAPI