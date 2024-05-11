/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computations

import kotlinx.coroutines.channels.ReceiveChannel


public interface ChannelComputation<out R>: Computation {
    public val resultsChannel: ReceiveChannel<R>
}