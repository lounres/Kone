// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

// MODULE: foo
// FILE: fooContent.kt
import dev.lounres.kone.suppliedTypes.*


@Suppliable
interface Foo<@Supply T>

// MODULE: bar(foo)
// FILE: barContent.kt
import dev.lounres.kone.suppliedTypes.*


@Suppliable
interface Bar<@Supply U> : Foo<Map<out U, *>>