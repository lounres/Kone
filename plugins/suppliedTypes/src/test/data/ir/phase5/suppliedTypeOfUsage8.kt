import dev.lounres.kone.suppliedTypes.*


@Suppliable
class Foo<@Supply T> {
    fun foo() = SuppliedType.of<T?>().toString()
}

fun box(): String = if (Foo<String>().foo() == "kotlin.String?") "OK" else "INCORRECT"