// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


object Bar {
    @Suppliable
    fun <@Supply T> foo() = suppliedTypeOf<T & Any>().toString()
}

fun box(): String = if (Bar.foo<String?>() == "kotlin.String") "OK" else "INCORRECT"