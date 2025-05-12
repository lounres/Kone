/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.array.isNotEmpty
import dev.lounres.kone.collections.array.koneULongArrayOf
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.utils.last
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.equalsTo
import dev.lounres.kone.relations.neq
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream


fun validate(number: UBigLong) {
    if (number.magnitude.isNotEmpty() && number.magnitude.last() == 0uL) fail("number is invalid")
}

context(_: Equality<E>)
infix fun <E> E.shouldBeEqualTo(expected: E): E = this.also { this should beEqualTo(expected) }

context(_: Equality<E>)
infix fun <E> E.shouldNotBeEqualTo(expected: E): E = this.also { this shouldNot beEqualTo(expected) }

context(equality: Equality<E>)
fun <E> beEqualTo(expected: E): Matcher<E> = object : Matcher<E> {
    override fun test(value: E) = MatcherResult(
        value equalsTo expected,
        { "$value should be equal to $expected" },
        { "$value should not be equal to $expected" }
    )
}

fun UBigLong.toBinaryString() =
    when (magnitude.size) {
        0u -> "0"
        1u -> magnitude[0u].toString(2)
        else -> buildString {
            append(magnitude.last().toString(2))
            for (index in magnitude.lastIndex - 1u downTo 0u) {
                append(magnitude[index].toString(2).padStart(64, '0'))
            }
        }
    }

val ComparisonResult.reversed: ComparisonResult
    get() = when (this) {
        ComparisonResult.LeftIsGreaterThanRight -> ComparisonResult.LeftIsLessThanRight
        ComparisonResult.LeftIsLessThanRight -> ComparisonResult.LeftIsGreaterThanRight
        ComparisonResult.Equal -> ComparisonResult.Equal
    }
    
@OptIn(ExperimentalSerializationApi::class)
class UBigLongTest : FunSpec({
    with(UBigLong.context) {
        context("test 'compareWith'") {
            @Serializable
            data class Case(
                val left: UBigLong,
                val leftName: String,
                val right: UBigLong,
                val rightName: String,
                val result: ComparisonResult,
            )
            withData(
                nameFn = { (_, leftName, _, rightName, _) -> "Compare $leftName with $rightName" },
                Json.decodeFromStream<List<Case>>(UBigLongTest::class.java.getResourceAsStream("/dev/lounres/kone/algebraic/UBigLongTest-compareWith.json")!!)
            ) { (left, _, right, _, result) ->
                (left compareWith right) shouldBe result
                (right compareWith left) shouldBe result.reversed
            }
        }
        
        context("test 'plus'") {
            @Serializable
            data class Case(
                val left: UBigLong,
                val leftName: String,
                val right: UBigLong,
                val rightName: String,
                val result: UBigLong,
            )
            withData(
                nameFn = { (_, leftName, _, rightName, _) -> "Plus $leftName with $rightName" },
                Json.decodeFromStream<List<Case>>(UBigLongTest::class.java.getResourceAsStream("/dev/lounres/kone/algebraic/UBigLongTest-plus.json")!!)
            ) { (left, _, right, _, result) ->
                (left + right).also { validate(it) } shouldBeEqualTo result
                (right + left).also { validate(it) } shouldBeEqualTo result
            }
        }
        
        context("test 'minus'") {
            @Serializable
            data class Case(
                val left: UBigLong,
                val leftName: String,
                val right: UBigLong,
                val rightName: String,
                val result: UBigLong,
            )
            withData(
                nameFn = { (_, leftName, _, rightName, _) -> "Minus $leftName with $rightName" },
                Json.decodeFromStream<List<Case>>(UBigLongTest::class.java.getResourceAsStream("/dev/lounres/kone/algebraic/UBigLongTest-minus.json")!!)
            ) { (left, _, right, _, result) ->
                (left - right).also { validate(it) } shouldBeEqualTo result
                if (left neq right) shouldThrow<ArithmeticException> { right - left }
            }
        }
        
        context("test 'times'") {
            @Serializable
            data class Case(
                val left: UBigLong,
                val leftName: String,
                val right: UBigLong,
                val rightName: String,
                val result: UBigLong,
            )
            withData(
                nameFn = { (_, leftName, _, rightName, _) -> "Times $leftName with $rightName" },
                Json.decodeFromStream<List<Case>>(UBigLongTest::class.java.getResourceAsStream("/dev/lounres/kone/algebraic/UBigLongTest-times.json")!!)
            ) { (left, _, right, _, result) ->
                (left * right).also { validate(it) } shouldBeEqualTo result
                (right * left).also { validate(it) } shouldBeEqualTo result
            }
        }
        
        context("test 'divrem'") {
            @Serializable
            data class Case(
                val left: UBigLong,
                val leftName: String,
                val right: UBigLong,
                val rightName: String,
                val result: EuclideanDivisionResult<UBigLong>,
            )
            withData(
                nameFn = { (_, leftName, _, rightName, _) -> "Divrem $leftName with $rightName" },
                Json.decodeFromStream<List<Case>>(UBigLongTest::class.java.getResourceAsStream("/dev/lounres/kone/algebraic/UBigLongTest-divrem.json")!!)
            ) { (left, _, right, _, expected) ->
                val result = left divrem right
                validate(result.quotient)
                validate(result.remainder)
                result.quotient shouldBeEqualTo expected.quotient
                result.remainder shouldBeEqualTo expected.remainder
            }
        }
        
        context("test 'div'") {
            @Serializable
            data class Case(
                val left: UBigLong,
                val leftName: String,
                val right: UBigLong,
                val rightName: String,
                val result: EuclideanDivisionResult<UBigLong>,
            )
            withData(
                nameFn = { (_, leftName, _, rightName, _) -> "Divide $leftName with $rightName" },
                Json.decodeFromStream<List<Case>>(UBigLongTest::class.java.getResourceAsStream("/dev/lounres/kone/algebraic/UBigLongTest-divrem.json")!!)
            ) { (left, _, right, _, expected) ->
                val result = left / right
                validate(result)
                result shouldBeEqualTo expected.quotient
            }
        }
        
        context("test 'rem'") {
            @Serializable
            data class Case(
                val left: UBigLong,
                val leftName: String,
                val right: UBigLong,
                val rightName: String,
                val result: EuclideanDivisionResult<UBigLong>,
            )
            withData(
                nameFn = { (_, leftName, _, rightName, _) -> "Remainder $leftName with $rightName" },
                Json.decodeFromStream<List<Case>>(UBigLongTest::class.java.getResourceAsStream("/dev/lounres/kone/algebraic/UBigLongTest-divrem.json")!!)
            ) { (left, _, right, _, expected) ->
                val result = left % right
                validate(result)
                result shouldBeEqualTo expected.remainder
            }
        }
        
        context("test 'shr'") {
            @Serializable
            data class CaseProducer(
                val number: UBigLong,
                val name: String,
            )
            val inputs = listOf<CaseProducer>(
                CaseProducer(
                    number = UBigLong(koneULongArrayOf(12079987231103497637uL, 18185429751149502341uL, 15524878586025400633uL, 7336060013147343010uL)),
                    name = "46049195039428022913422970774947675843853370420699389623804568776985996539301",
                )
            )
            data class Case(
                val number: UBigLong,
                val name: String,
                val shift: UInt,
                val binaryStringResult: String,
            )
            withData(
                nameFn = { (_, name, shift, _) -> "$name shr ${shift}u" },
                inputs.flatMap { caseProducer ->
                    val binaryString = caseProducer.number.toBinaryString()
                    val bitLength = binaryString.length
                    List(bitLength + 129) { shift ->
                        Case(
                            number = caseProducer.number,
                            name = caseProducer.name,
                            shift = shift.toUInt(),
                            binaryStringResult = if (shift < bitLength) binaryString.substring(0, bitLength - shift) else "0"
                        )
                    }
                }
            ) { (number, _, shift, binaryStringResult) ->
                val result = number shr shift
                validate(result)
                result.toBinaryString() shouldBe binaryStringResult
            }
        }
        
        context("test 'shl'") {
            @Serializable
            data class CaseProducer(
                val number: UBigLong,
                val name: String,
            )
            val inputs = listOf<CaseProducer>(
                CaseProducer(
                    number = UBigLong(koneULongArrayOf(12079987231103497637uL, 15uL)),
                    name = "288781148336746771877",
                ),
                CaseProducer(
                    number = UBigLong(koneULongArrayOf(12079987231103497637uL, 18185429751149502341uL, 15524878586025400633uL, 7336060013147343010uL)),
                    name = "46049195039428022913422970774947675843853370420699389623804568776985996539301",
                ),
            )
            data class Case(
                val number: UBigLong,
                val name: String,
                val shift: UInt,
                val binaryStringResult: String,
            )
            withData(
                nameFn = { (_, name, shift, _) -> "$name shr ${shift}u" },
                inputs.flatMap { caseProducer ->
                    val binaryString = caseProducer.number.toBinaryString()
                    val bitLength = binaryString.length
                    List(bitLength + 129) { shift ->
                        Case(
                            number = caseProducer.number,
                            name = caseProducer.name,
                            shift = shift.toUInt(),
                            binaryStringResult = binaryString + "0".repeat(shift)
                        )
                    }
                }
            ) { (number, _, shift, binaryStringResult) ->
                val result = number shl shift
                validate(result)
                result.toBinaryString() shouldBe binaryStringResult
            }
        }
        
        // TODO: Add tests on `and`, `or`, `xor`.
    }
})