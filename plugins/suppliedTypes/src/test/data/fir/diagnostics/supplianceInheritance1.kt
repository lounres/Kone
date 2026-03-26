// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
interface <!USELESS_SUPPLIABILITY!>Foo<!><T>

interface <!SUPPLIABLE_INHERITED_BY_NON_SUPPLIABLE!>Bar<!><T> : Foo<List<T>>
