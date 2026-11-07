package foo.bar

import dev.lounres.kone.contexts.*


class Foo<X> : KoneContext {
    @KoneContextInclude
    val bar: List<X> get() = emptyList()
    val baz: Set<X> get() = emptySet()
}

fun box(): String {
    KoneContext.localUnwrap(Foo<Int>())
    return if (contextOf<List<Int>>().isEmpty()) "OK" else "INCORRECT"
}