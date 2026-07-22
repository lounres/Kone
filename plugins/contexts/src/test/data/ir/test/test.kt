// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


class Foo : KoneContext {
    val bar = 57
}

context(foo: Foo)
val baz: Int get() = foo.bar

fun box(): String {
    contexts(Foo(), 1, "2", 3.0) {
        return if (baz == 57) "OK" else "INCORRECT"
    }
}