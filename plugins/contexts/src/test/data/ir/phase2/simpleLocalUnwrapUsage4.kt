package foo.bar

import dev.lounres.kone.contexts.*


interface Bar : KoneContext {
    @KoneContextInclude
    val bar: Int get() = 57
}

class Baz : Bar {
    @KoneContextInclude
    override val bar: Int get() = super.bar
    val baz = 179
}

fun box(): String {
    KoneContext.localUnwrap(Baz())
    return if (contextOf<Int>() == 57) "OK" else "INCORRECT"
}