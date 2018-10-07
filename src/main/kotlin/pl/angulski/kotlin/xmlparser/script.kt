package pl.angulski.kotlin.xmlparser

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPInputStream
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import kotlin.concurrent.thread

typealias XmlSingleData = String

/**
 * @author Mateusz Angulski <mateusz@angulski.pl>.
 */
fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("XML Parser")
    val debugger = Profiler(logger).apply { start() }
    val arguments = CliArguments(args, mapOf("job-chunk-size" to "800", "concurrent-jobs" to "3"))
    val concurrentJobs = arguments.get("concurrent-jobs")
    val jobChunkSize = arguments.get("job-chunk-size")
    val filePath = arguments.getString("file-path")

    logger.info("Launching XML Parser with parameters:")
    logger.info("job-chunk-size set to $jobChunkSize")
    logger.info("concurrent-jobs set to $concurrentJobs")
    logger.info("file-path set to $filePath")
    logger.info("You can set those by passing parameters like 'java -jar xmlparser.jar job-chunk-size=500 concurrent-jobs=6'")

    val dataList = LinkedBlockingQueue<XmlSingleData>(jobChunkSize)
    val todoList = LinkedBlockingQueue<DataRunnable>(concurrentJobs * 2 + 1)

    /** Thread that reads xml file and fills data list */
    val readerThread = thread(start = true) {
        val fileStream = FileInputStream(File(filePath))
        val gzipStream = GZIPInputStream(fileStream)
        val xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(gzipStream)

        while (xmlReader.hasNext()) {
            xmlReader.next()
            if (xmlReader.eventType == XMLStreamConstants.START_ELEMENT && xmlReader.localName == "username") {
                dataList.put(xmlReader.elementText)
            }
        }
    }

    /** Thread that creates tasks to do (runnables) */
    thread(start = true) {
        while (readerThread.isAlive) {
            if (dataList.remainingCapacity() == 0) {
                logger.debug("Take ${dataList.size} elements. I have now ${todoList.size} tasks to do")
                val tmpDataList = mutableListOf<XmlSingleData>()
                dataList.drainTo(tmpDataList, dataList.size)
                todoList.put(DataRunnable(tmpDataList))
            }
        }
        if (dataList.size > 0) {
            logger.debug("Take ${dataList.size} elements. I have now ${todoList.size} tasks to do")
            val tmpDataList = mutableListOf<XmlSingleData>()
            dataList.drainTo(tmpDataList, dataList.size)
            todoList.put(DataRunnable(tmpDataList))
        }
    }

    /** Run pool of data executors */
    @Suppress("UNCHECKED_CAST")
    val executor = ThreadPoolExecutor(
        concurrentJobs,
        concurrentJobs,
        0L,
        TimeUnit.MILLISECONDS,
        todoList as BlockingQueue<Runnable>
    )
    executor.prestartAllCoreThreads()

    /** Kill the thread pool when finished and show debug info */
    thread(start = true) {
        Thread.sleep(1000)
        while (executor.activeCount > 0 || readerThread.isAlive) {
            continue
        }
        executor.shutdownNow()
        debugger.stop()
        debugger.print()
    }
}
