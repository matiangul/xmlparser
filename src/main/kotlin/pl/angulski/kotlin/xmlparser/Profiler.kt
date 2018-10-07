package pl.angulski.kotlin.xmlparser

import org.slf4j.Logger

/**
 * @author Mateusz Angulski <mateusz@angulski.pl>.
 */
class Profiler(val logger: Logger) {
    private var startMillis: Long = 0L
    private var endMillis: Long = 0L

    fun start() {
        startMillis = System.currentTimeMillis()
    }

    fun stop() {
        endMillis = System.currentTimeMillis()
    }

    fun print() {
        val seconds = (endMillis - startMillis) / 1000
        val minutes = seconds / 60
        logger.info("Finished the job in $seconds seconds, which is around $minutes minutes")

        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory()
        val allocatedMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        logger.info("Allocated memory: ${(allocatedMemory / (1024 * 1024))}MB")
        logger.info("Memory available to allocate: ${(freeMemory + maxMemory) / (1024 * 1024)}MB")
    }
}
