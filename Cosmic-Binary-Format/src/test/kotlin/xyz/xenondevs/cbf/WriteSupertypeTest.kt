package xyz.xenondevs.cbf

import org.junit.jupiter.api.Test

// CBF should not fail to serialize if a user (accidentally) requests to serialize to a supertype of an object
// Instead, CBF should always choose the most specific adapter for the given type
class WriteSupertypeTest {
    
    @Test
    fun testCannotWriteSupertype() {
        val str = "abc"
        assert(CBF.read<String>(CBF.write<Any>(str)) == str)
    }
    
}