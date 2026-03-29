// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
abstract class Foo<@Supply T> {
    fun foo() = suppliedTypeOf<List<T>>().toString()
}

@Suppliable
object Bar : Foo<String>()

fun box(): String = if (Bar.foo() == "kotlin.collections.List<out kotlin.String>") "OK" else "INCORRECT"