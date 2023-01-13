package com.lounres.kone.examples

import kotlin.reflect.KFunction0


public fun execute(vararg functions: KFunction0<*>) {
    for (func in functions) {
        println("### Executing \"${func.name}\"")
        func()
        println()
    }
}