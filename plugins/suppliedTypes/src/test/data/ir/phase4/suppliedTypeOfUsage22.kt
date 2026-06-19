// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


interface Foo {
    @Suppliable
    fun <@Supply T> foo(): String
}

object Bar : Foo {
    @Suppliable
    override fun <@Supply T> foo(): String = suppliedTypeOf<T & Any>().toString()
}

fun box(): String = if ((Bar as Foo).foo<String?>() == "kotlin.String") "OK" else "INCORRECT"