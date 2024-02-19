package xyz.xenondevs.cbf.instancecreator.impl

import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertIs

class EnumMapInstanceCreatorTest {
    
    @Test
    fun testCreateEnumMapInstance() {
        assertIs<EnumMap<*, *>>(EnumMapInstanceCreator.createInstance(typeOf<EnumMap<TimeUnit, String>>()))
    }
    
}