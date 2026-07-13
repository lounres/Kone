// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


class Foo : KoneContext {
    val bar = 57
}

context(foo: Foo)
val baz: Int get() = foo.bar

fun box(): String {
    KoneContext.useLocallyAsContexts(Foo())
    return if (baz == 57) "OK" else "INCORRECT"
}