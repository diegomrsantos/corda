@file:DeleteForDJVM

package net.corda.core.internal

import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import net.corda.core.DeleteForDJVM
import java.security.AccessController.doPrivileged
import java.security.PrivilegedAction
import java.security.PrivilegedExceptionAction
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private val pooledScanMutex = ReentrantLock()

/**
 * Use this rather than the built in implementation of [scan] on [ClassGraph].  The built in implementation of [scan] creates
 * a thread pool every time resulting in too many threads.  This one uses a mutex to restrict concurrency.
 */
fun ClassGraph.pooledScan(): ScanResult {
    return pooledScanMutex.withLock {
        doPrivileged(PrivilegedExceptionAction {
            this@pooledScan.scan()
        })
    }
}

fun privilegedCreateClassGraph(): ClassGraph {
    return doPrivileged(PrivilegedAction {
        ClassGraph()
    })
}