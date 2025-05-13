package benchmark

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import xyz.xenondevs.cbf.CBF
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun main() {
    fun randomString(length: Int): String {
        val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
    val random = Random(42)
    val a: Map<String, Int> = (0..<1_000_000).associate {
        randomString(random.nextInt(1, 20)) to random.nextInt()
    }
    
    CBF.write(a)
}

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
open class MapSerializerBenchmark {
    
    private lateinit var map10: Map<String, Int>
    private lateinit var map1k: Map<String, Int>
    private lateinit var map1m: Map<String, Int>
    
    private lateinit var nestedMap10: Map<String, Map<String, Int>>
    private lateinit var nestedMap1k: Map<String, Map<String, Int>>
    
    @Setup
    fun setup() {
        fun randomString(length: Int): String {
            val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            return (1..length)
                .map { chars.random() }
                .joinToString("")
        }
        
        val random = Random(42)
        map10 = (0..<10).associate { 
            randomString(random.nextInt(1, 20)) to random.nextInt()
        }
        map1k = (0..<1000).associate { 
            randomString(random.nextInt(1, 20)) to random.nextInt()
        }
        map1m = (0..<1_000_000).associate { 
            randomString(random.nextInt(1, 20)) to random.nextInt()
        }
        
        nestedMap10 = (0..<10).associate { 
            randomString(random.nextInt(1, 20)) to (0..<10).associate { 
                randomString(random.nextInt(1, 20)) to random.nextInt()
            }
        }
        
        nestedMap1k = (0..<1000).associate { 
            randomString(random.nextInt(1, 20)) to (0..<10).associate { 
                randomString(random.nextInt(1, 20)) to random.nextInt()
            }
        }
    }
    
    @Benchmark
    fun serializeMap10() {
        CBF.write(map10)
    }
    
    @Benchmark
    fun serializeMap1k() {
        CBF.write(map1k)
    }
    
    @Benchmark
    fun serializeMap1m() {
        CBF.write(map1m)
    }
    
    @Benchmark
    fun serializeNestedMap10() {
        CBF.write(nestedMap10)
    }
    
    @Benchmark
    fun serializeNestedMap1k() {
        CBF.write(nestedMap1k)
    }
    
}