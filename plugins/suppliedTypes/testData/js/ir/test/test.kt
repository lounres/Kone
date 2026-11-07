// MODULE: foo
// FILE: IntX.kt

context(_: List<String>)
fun <T> Int.foo(): Set<T> = emptySet<T>().also { println("Int-hey-ho!") }

// FILE: LongX.kt

context(_: List<String>)
fun <T> Long.foo(): Set<T> = emptySet<T>().also { println("Long-hey-ho!") }

// MODULE: bar(foo)
import dev.lounres.kone.suppliedTypes.*


fun box(): String {
    val result = context(listOf("compile", "me", "please")) {
        57.foo<Double>()
    }
    return if (result === emptySet<Double>()) "OK" else "INCORRECT"
}