// MODULE: foo
import dev.lounres.kone.suppliedTypes.*


@Suppliable
interface Foo<@Supply T>

// MODULE: bar(foo)
import dev.lounres.kone.suppliedTypes.*


@Suppliable
interface Bar<@Supply U> : Foo<Map<out U, *>>