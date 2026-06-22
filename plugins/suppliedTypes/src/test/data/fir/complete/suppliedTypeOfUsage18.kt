// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
class Foo<@Supply T> {
    fun foo() = suppliedTypeOf<T & Any>().toString()
}

fun box(): String = if (Foo<String?>().foo() == "kotlin.String") "OK" else "INCORRECT"