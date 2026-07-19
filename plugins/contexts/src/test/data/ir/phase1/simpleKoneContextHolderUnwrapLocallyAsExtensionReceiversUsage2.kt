// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


fun interface CoolContext<X> {
    fun makeItCool(): X
}

context(coolContext: CoolContext<X>)
fun <X> makeItCool(): X = coolContext.makeItCool()

object Foo : KoneContext {
    @KoneContextHolderInclude
    val bar = CoolContext { 57 }
    val baz = CoolContext { 179 }
}

fun box(): String {
    KoneContext.unwrap(Foo)
    return if (makeItCool() == 57) "OK" else "INCORRECT"
}