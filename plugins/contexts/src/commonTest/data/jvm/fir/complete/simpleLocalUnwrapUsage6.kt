package foo.bar

import dev.lounres.kone.contexts.*


interface Bar : KoneContext {
    @KoneContextInclude
    val bar: Int get() = 179
}

class Baz : Bar {
    @KoneContextExclude
    override val bar: Int get() = super.bar
    @KoneContextInclude
    val baz = 57
}

fun box(): String {
    KoneContext.localUnwrap(Baz())
    return if (contextOf<Int>() == 57) "OK" else "INCORRECT"
}