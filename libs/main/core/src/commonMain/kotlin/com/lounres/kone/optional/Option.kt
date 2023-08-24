/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.optional


public sealed interface Option<out E> {
    public data class Some<out E>(val value: E): Option<E>
    public data object None: Option<Nothing>
}