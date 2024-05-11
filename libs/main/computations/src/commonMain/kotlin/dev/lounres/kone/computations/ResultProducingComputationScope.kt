/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computations


public interface ResultProducingComputationScope<in R>: ComputationScope {
    public fun produce(result: R)
}