/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computations

import dev.lounres.kone.collections.KoneIterableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


public interface FlowComputation<out R>: Computation {
    public val resultsFlow: Flow<R>
    public val resultsCollectionFlow: StateFlow<KoneIterableList<R>>
}