package xyz.xenondevs.cbf

import kotlin.test.assertEquals

fun <K, V> assertContentEquals(expected: Map<K, V>, actual: Map<K, V>) {
    assertEquals(expected.size, actual.size)
    expected.forEach { (key, value) -> assertEquals(value, actual[key]) }
}