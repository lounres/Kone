/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computations


public interface CompletableComputation: Computation {
    // TODO: Think if it is OK to call them in `Paused`/`Pausing` state.
    public fun complete(): Boolean
    public fun completeExceptionally(exception: Throwable): Boolean
}