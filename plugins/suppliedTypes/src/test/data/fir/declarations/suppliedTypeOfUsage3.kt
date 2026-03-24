// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
class Foo<@Supply T> {
    fun foo() {
        println(suppliedTypeOf<List<T>>())
    }
}

fun box(): String {
    Foo<String>().foo()
    return "OK"
}