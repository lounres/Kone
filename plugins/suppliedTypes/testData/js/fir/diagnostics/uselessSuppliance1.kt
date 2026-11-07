import dev.lounres.kone.suppliedTypes.*


interface A<@Supply <!USELESS_SUPPLIANCE!>T<!>>
interface B<@Supply <!USELESS_SUPPLIANCE!>S<!>> : A<List<S>>
