import dev.lounres.kone.suppliedTypes.*


object Bar {
    @Suppliable
    fun <@Supply T> foo() = SuppliedType.of<T & Any>().toString()
}

fun box(): String = if (Bar.foo<String>() == "kotlin.String") "OK" else "INCORRECT"