import dev.lounres.kone.suppliedTypes.*


object Bar {
    @Suppliable
    fun <@Supply T> foo() = suppliedTypeOf<List<T>>().toString()
}

fun box(): String = if (Bar.foo<String>() == "kotlin.collections.List<out kotlin.String>") "OK" else "INCORRECT"