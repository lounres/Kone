/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computations


// TODO: Think about cancellation and maybe implement in the same way as in kotlinx.coroutines
public expect open class CancellationException(message: String?) : IllegalStateException

public expect fun CancellationException(message: String?, cause: Throwable?) : CancellationException