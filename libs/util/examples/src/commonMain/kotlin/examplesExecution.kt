/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.examples

import kotlin.reflect.KFunction0


public fun execute(vararg functions: KFunction0<*>) {
    for (func in functions) {
        println("### Executing \"${func.name}\"")
        func()
        println()
    }
}