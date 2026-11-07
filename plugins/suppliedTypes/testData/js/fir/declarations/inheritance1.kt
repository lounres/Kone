import dev.lounres.kone.suppliedTypes.*


@Suppliable
interface Foo<@Supply T>

@Suppliable
interface Bar<@Supply U> : Foo<Map<out U, *>>