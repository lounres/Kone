// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
abstract class Foo<@Supply T> {
    fun foo() = suppliedTypeOf<T?>().toString()
}

@Suppliable
object Bar : Foo<String>()

fun box(): String = if (Bar.foo() == "kotlin.String?") "OK" else "INCORRECT"