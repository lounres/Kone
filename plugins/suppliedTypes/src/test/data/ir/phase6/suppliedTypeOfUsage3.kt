// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
class Foo<@Supply T> {
    fun foo() = suppliedTypeOf<List<T>>().toString()
}

fun box(): String = if (Foo<String>().foo() == "kotlin.collections.List<out kotlin.String>") "OK" else "INCORRECT"