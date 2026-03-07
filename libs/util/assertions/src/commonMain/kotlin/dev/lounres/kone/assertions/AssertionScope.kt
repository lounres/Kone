package dev.lounres.kone.assertions


@AssertionScope.Dsl
public fun interface AssertionScope {
    public fun consumeAssertion(assertionResult: Assertion)
    
    public companion object : AssertionScope {
        override fun consumeAssertion(assertionResult: Assertion) {
            throw AssertionError("Assertion failed:\n${assertionResult.message}", assertionResult.cause)
        }
    }
    
    public data class Assertion(val message: String, val cause: Throwable? = null)
    
    @DslMarker
    public annotation class Dsl
}

public inline operator fun AssertionScope.invoke(block: context(AssertionScope) () -> Unit) {
    block(this)
}