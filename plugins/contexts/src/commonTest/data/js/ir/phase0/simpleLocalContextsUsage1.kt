package foo.bar

import dev.lounres.kone.contexts.*


class Foo : KoneContext {
    val bar = 57
}

context(foo: Foo)
val baz: Int get() = foo.bar

fun box(): String {
    localContexts(Foo())
    return if (baz == 57) "OK" else "INCORRECT"
}