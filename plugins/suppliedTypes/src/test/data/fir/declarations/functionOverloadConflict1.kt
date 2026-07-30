package foo.bar

import dev.lounres.kone.suppliedTypes.*


@Suppliable
fun <@Supply Gee> baz(): Gee? = null

@Suppliable
fun <Gee> baz(suppliedTypeParameterForGee: SuppliedType): Gee? = null
