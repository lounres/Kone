// MODULE: foo
// FILE: IntX.kt
import dev.lounres.kone.suppliedTypes.*


@Suppliable
context(_: List<String>)
fun <@Supply T> Int.foo(): Set<T> = emptySet<T>().also { println(suppliedTypeOf<T>()) }

// FILE: LongX.kt
import dev.lounres.kone.suppliedTypes.*


@Suppliable
context(_: List<String>)
fun <@Supply T> Long.foo(): Set<T> = emptySet<T>().also { println(suppliedTypeOf<T>()) }

// MODULE: bar(foo)
import dev.lounres.kone.suppliedTypes.*


fun box(): String {
    val result = context(listOf("compile", "me", "please")) {
        57.foo<Double>()
    }
    return if (result === emptySet<Double>()) "OK" else "INCORRECT"
}