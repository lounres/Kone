package foo.bar

import dev.lounres.kone.contexts.*


fun interface CoolContext<X> {
    fun makeItCool(): X
}

context(coolContext: CoolContext<X>)
fun <X> makeItCool(): X = coolContext.makeItCool()

object Foo : KoneContext {
    @KoneContextInclude
    val bar = CoolContext { 57 }
    val baz = CoolContext { 179 }
}

fun box(): String {
    KoneContext.localUnwrap(Foo)
    return if (makeItCool() == 57) "OK" else "INCORRECT"
}