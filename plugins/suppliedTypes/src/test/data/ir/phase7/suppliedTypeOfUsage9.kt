import dev.lounres.kone.suppliedTypes.*


@Suppliable
interface Foo<@Supply T> {
    fun foo() = suppliedTypeOf<T?>().toString()
}

@Suppliable
class Bar : Foo<String>

fun box(): String = if (Bar().foo() == "kotlin.String?") "OK" else "INCORRECT"