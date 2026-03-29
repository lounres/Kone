// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
interface Foo<@Supply T> {
    fun foo() {
        println(suppliedTypeOf<List<T>>())
    }
}

@Suppliable
class Bar : Foo<String>

fun box(): String {
    Bar().foo()
    return "OK"
}