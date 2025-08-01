/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks.collections.array

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.array.KoneBooleanArray
import dev.lounres.kone.collections.array.KoneByteArray
import dev.lounres.kone.collections.array.KoneCharArray
import dev.lounres.kone.collections.array.KoneDoubleArray
import dev.lounres.kone.collections.array.KoneFloatArray
import dev.lounres.kone.collections.array.KoneIntArray
import dev.lounres.kone.collections.array.KoneLongArray
import dev.lounres.kone.collections.array.KoneShortArray
import dev.lounres.kone.collections.array.KoneUByteArray
import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.KoneULongArray
import dev.lounres.kone.collections.array.KoneUShortArray
import dev.lounres.kone.collections.array.of
import dev.lounres.kone.collections.array.serializers.serializer
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import java.io.File


@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class ArrayAllocationBenchmarks {
    @Param(
        "1", "2", "4", "8", "16", "32", "64", "128",
        "256", "512", "1024", "2048", "4096", "8192", "16384", "32768",
        "65536", "131072", "262144", "524288", "1048576", "2097152", "4194304", "8388608",
        "16777216", "33554432", "67108864", "134217728", "268435456", "536870912", "1073741824",

//        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
//        "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
//        "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
//        "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63",
//        "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
//        "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95",
//        "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111",
//        "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127",
//        "128", "129", "130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "140", "141", "142", "143",
//        "144", "145", "146", "147", "148", "149", "150", "151", "152", "153", "154", "155", "156", "157", "158", "159",
//        "160", "161", "162", "163", "164", "165", "166", "167", "168", "169", "170", "171", "172", "173", "174", "175",
//        "176", "177", "178", "179", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189", "190", "191",
//        "192", "193", "194", "195", "196", "197", "198", "199", "200", "201", "202", "203", "204", "205", "206", "207",
//        "208", "209", "210", "211", "212", "213", "214", "215", "216", "217", "218", "219", "220", "221", "222", "223",
//        "224", "225", "226", "227", "228", "229", "230", "231", "232", "233", "234", "235", "236", "237", "238", "239",
//        "240", "241", "242", "243", "244", "245", "246", "247", "248", "249", "250", "251", "252", "253", "254", "255",
//        "256", "257", "258", "259", "260", "261", "262", "263", "264", "265", "266", "267", "268", "269", "270", "271",
//        "272", "273", "274", "275", "276", "277", "278", "279", "280", "281", "282", "283", "284", "285", "286", "287",
//        "288", "289", "290", "291", "292", "293", "294", "295", "296", "297", "298", "299", "300", "301", "302", "303",
//        "304", "305", "306", "307", "308", "309", "310", "311", "312", "313", "314", "315", "316", "317", "318", "319",
//        "320", "321", "322", "323", "324", "325", "326", "327", "328", "329", "330", "331", "332", "333", "334", "335",
//        "336", "337", "338", "339", "340", "341", "342", "343", "344", "345", "346", "347", "348", "349", "350", "351",
//        "352", "353", "354", "355", "356", "357", "358", "359", "360", "361", "362", "363", "364", "365", "366", "367",
//        "368", "369", "370", "371", "372", "373", "374", "375", "376", "377", "378", "379", "380", "381", "382", "383",
//        "384", "385", "386", "387", "388", "389", "390", "391", "392", "393", "394", "395", "396", "397", "398", "399",
//        "400", "401", "402", "403", "404", "405", "406", "407", "408", "409", "410", "411", "412", "413", "414", "415",
//        "416", "417", "418", "419", "420", "421", "422", "423", "424", "425", "426", "427", "428", "429", "430", "431",
//        "432", "433", "434", "435", "436", "437", "438", "439", "440", "441", "442", "443", "444", "445", "446", "447",
//        "448", "449", "450", "451", "452", "453", "454", "455", "456", "457", "458", "459", "460", "461", "462", "463",
//        "464", "465", "466", "467", "468", "469", "470", "471", "472", "473", "474", "475", "476", "477", "478", "479",
//        "480", "481", "482", "483", "484", "485", "486", "487", "488", "489", "490", "491", "492", "493", "494", "495",
//        "496", "497", "498", "499", "500", "501", "502", "503", "504", "505", "506", "507", "508", "509", "510", "511",
    )
    final var size: UInt = 0u

    @Benchmark
    fun kone_null_generic(blackhole: Blackhole) {
        blackhole.consume(KoneArray(size) { null })
        val a = KoneArray(size) { null }
    }
    
    @Benchmark
    fun kotlin_null_generic(blackhole: Blackhole) {
        blackhole.consume(Array<Void?>(size.toInt()) { null })
    }

    @Benchmark
    fun kone_uint_generic(blackhole: Blackhole) {
        blackhole.consume(KoneArray(size) { it })
    }
    
    @Benchmark
    fun kotlin_uint_generic(blackhole: Blackhole) {
        blackhole.consume(Array(size.toInt()) { it })
    }
    
    @Benchmark
    fun kone_boolean(blackhole: Blackhole) {
        blackhole.consume(KoneBooleanArray(size) { it % 2u == 0u })
    }
    
    @Benchmark
    fun kotlin_boolean(blackhole: Blackhole) {
        blackhole.consume(BooleanArray(size.toInt()) { it % 2 == 0 })
    }
    
    @Benchmark
    fun kone_char(blackhole: Blackhole) {
        blackhole.consume(KoneCharArray(size) { it.toInt().toChar() })
    }
    
    @Benchmark
    fun kotlin_char(blackhole: Blackhole) {
        blackhole.consume(CharArray(size.toInt()) { it.toChar() })
    }

    @Benchmark
    fun kone_byte(blackhole: Blackhole) {
        blackhole.consume(KoneByteArray(size) { it.toInt().toByte() })
    }
    
    @Benchmark
    fun kotlin_byte(blackhole: Blackhole) {
        blackhole.consume(ByteArray(size.toInt()) { it.toByte() })
    }

    @Benchmark
    fun kone_short(blackhole: Blackhole) {
        blackhole.consume(KoneShortArray(size) { it.toInt().toShort() })
    }
    
    @Benchmark
    fun kotlin_short(blackhole: Blackhole) {
        blackhole.consume(ShortArray(size.toInt()) { it.toShort() })
    }

    @Benchmark
    fun kone_int(blackhole: Blackhole) {
        blackhole.consume(KoneIntArray(size) { it.toInt() })
    }
    
    @Benchmark
    fun kotlin_int(blackhole: Blackhole) {
        blackhole.consume(IntArray(size.toInt()) { it })
    }

    @Benchmark
    fun kone_long(blackhole: Blackhole) {
        blackhole.consume(KoneLongArray(size) { it.toInt().toLong() })
    }
    
    @Benchmark
    fun kotlin_long(blackhole: Blackhole) {
        blackhole.consume(LongArray(size.toInt()) { it.toLong() })
    }

    @Benchmark
    fun kone_float(blackhole: Blackhole) {
        blackhole.consume(KoneFloatArray(size) { it.toInt().toFloat() })
    }
    
    @Benchmark
    fun kotlin_float(blackhole: Blackhole) {
        blackhole.consume(FloatArray(size.toInt()) { it.toFloat() })
    }

    @Benchmark
    fun kone_double(blackhole: Blackhole) {
        blackhole.consume(KoneDoubleArray(size) { it.toInt().toDouble() })
    }
    
    @Benchmark
    fun kotlin_double(blackhole: Blackhole) {
        blackhole.consume(DoubleArray(size.toInt()) { it.toDouble() })
    }

    @Benchmark
    fun kone_ubyte(blackhole: Blackhole) {
        blackhole.consume(KoneUByteArray(size) { it.toUByte() })
    }
    
    @Benchmark
    fun kotlin_ubyte(blackhole: Blackhole) {
        blackhole.consume(UByteArray(size.toInt()) { it.toUByte() })
    }

    @Benchmark
    fun kone_ushort(blackhole: Blackhole) {
        blackhole.consume(KoneUShortArray(size) { it.toUShort() })
    }
    
    @Benchmark
    fun kotlin_ushort(blackhole: Blackhole) {
        blackhole.consume(UShortArray(size.toInt()) { it.toUShort() })
    }

    @Benchmark
    fun kone_uint(blackhole: Blackhole) {
        blackhole.consume(KoneUIntArray(size) { it })
    }
    
    @Benchmark
    fun kotlin_uint(blackhole: Blackhole) {
        blackhole.consume(UIntArray(size.toInt()) { it.toUInt() })
    }

    @Benchmark
    fun kone_ulong(blackhole: Blackhole) {
        blackhole.consume(KoneULongArray(size) { it.toULong() })
    }
    
    @Benchmark
    fun kotlin_ulong(blackhole: Blackhole) {
        blackhole.consume(ULongArray(size.toInt()) { it.toULong() })
    }
}

//@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
//@State(Scope.Benchmark)
//class ArrayAccessBenchmarks {
//    @Param(
////        "1", "2", "4", "8", "16", "32", "64", "128",
////        "256", "512", "1024", "2048", "4096", "8192", "16384", "32768",
////        "65536", "131072", "262144", "524288", "1048576", "2097152", "4194304", "8388608",
////        "16777216", "33554432", "67108864", "134217728", "268435456", "536870912", "1073741824",
//
//        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
////        "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32",
////        "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48",
////        "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64",
////        "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80",
////        "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96",
////        "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112",
////        "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127", "128",
////        "129", "130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "140", "141", "142", "143", "144",
////        "145", "146", "147", "148", "149", "150", "151", "152", "153", "154", "155", "156", "157", "158", "159", "160",
////        "161", "162", "163", "164", "165", "166", "167", "168", "169", "170", "171", "172", "173", "174", "175", "176",
////        "177", "178", "179", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189", "190", "191", "192",
////        "193", "194", "195", "196", "197", "198", "199", "200", "201", "202", "203", "204", "205", "206", "207", "208",
////        "209", "210", "211", "212", "213", "214", "215", "216", "217", "218", "219", "220", "221", "222", "223", "224",
////        "225", "226", "227", "228", "229", "230", "231", "232", "233", "234", "235", "236", "237", "238", "239", "240",
////        "241", "242", "243", "244", "245", "246", "247", "248", "249", "250", "251", "252", "253", "254", "255", "256",
////        "257", "258", "259", "260", "261", "262", "263", "264", "265", "266", "267", "268", "269", "270", "271", "272",
////        "273", "274", "275", "276", "277", "278", "279", "280", "281", "282", "283", "284", "285", "286", "287", "288",
////        "289", "290", "291", "292", "293", "294", "295", "296", "297", "298", "299", "300", "301", "302", "303", "304",
////        "305", "306", "307", "308", "309", "310", "311", "312", "313", "314", "315", "316", "317", "318", "319", "320",
////        "321", "322", "323", "324", "325", "326", "327", "328", "329", "330", "331", "332", "333", "334", "335", "336",
////        "337", "338", "339", "340", "341", "342", "343", "344", "345", "346", "347", "348", "349", "350", "351", "352",
////        "353", "354", "355", "356", "357", "358", "359", "360", "361", "362", "363", "364", "365", "366", "367", "368",
////        "369", "370", "371", "372", "373", "374", "375", "376", "377", "378", "379", "380", "381", "382", "383", "384",
////        "385", "386", "387", "388", "389", "390", "391", "392", "393", "394", "395", "396", "397", "398", "399", "400",
////        "401", "402", "403", "404", "405", "406", "407", "408", "409", "410", "411", "412", "413", "414", "415", "416",
////        "417", "418", "419", "420", "421", "422", "423", "424", "425", "426", "427", "428", "429", "430", "431", "432",
////        "433", "434", "435", "436", "437", "438", "439", "440", "441", "442", "443", "444", "445", "446", "447", "448",
////        "449", "450", "451", "452", "453", "454", "455", "456", "457", "458", "459", "460", "461", "462", "463", "464",
////        "465", "466", "467", "468", "469", "470", "471", "472", "473", "474", "475", "476", "477", "478", "479", "480",
////        "481", "482", "483", "484", "485", "486", "487", "488", "489", "490", "491", "492", "493", "494", "495", "496",
////        "497", "498", "499", "500", "501", "502", "503", "504", "505", "506", "507", "508", "509", "510", "511", "512",
//    )
//    final var size: UInt = 0u
//
//    class KoneArrayHolder(val array: KoneArray<Int>)
//    final var arrayHolder: KoneArrayHolder = KoneArrayHolder(KoneArray.of())
//
//    final var middleIndex: UInt = 0u
//    final var lastIndex: UInt = 0u
//
//    @Setup
//    fun setup() {
//        arrayHolder = KoneArrayHolder(Json.decodeFromStream(KoneArray.serializer<Int, Int>(serializer()), File("src/jvmMain/resources/array/$size.json").inputStream()))
//        check(arrayHolder.array.size == size)
//        middleIndex = size / 2u
//        lastIndex = size - 1u
//    }
//
//    @Benchmark
//    fun firstElement(blackhole: Blackhole) {
//        blackhole.consume(arrayHolder.array[0u])
//    }
//
//    @Benchmark
//    fun middleElement(blackhole: Blackhole) {
//        blackhole.consume(arrayHolder.array[middleIndex])
//    }
//
//    @Benchmark
//    fun lastElement(blackhole: Blackhole) {
//        blackhole.consume(arrayHolder.array[lastIndex])
//    }
//}