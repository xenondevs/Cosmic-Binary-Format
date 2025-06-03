package xyz.xenondevs.cbf.util

import java.util.concurrent.locks.Lock

internal inline fun <T> withLocksOrdered(
    lock1: Lock,
    lock2: Lock,
    action: () -> T
): T {
    val min: Lock
    val max: Lock
    if (System.identityHashCode(lock1) < System.identityHashCode(lock2)) {
        min = lock1
        max = lock2
    } else {
        min = lock2
        max = lock1
    }
    
    min.lock()
    max.lock()
    try {
        return action()
    } finally {
        max.unlock()
        min.unlock()
    }
}