/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computations


// TODO: Think about cancellation and maybe implement in the same way as in kotlinx.coroutines
public actual typealias CancellationException = kotlin.coroutines.cancellation.CancellationException

public actual fun CancellationException(message: String?, cause: Throwable?) : CancellationException =
    kotlin.coroutines.cancellation.CancellationException(message, cause)