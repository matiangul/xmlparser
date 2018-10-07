package pl.angulski.kotlin.xmlparser

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Mateusz Angulski <mateusz@angulski.pl>.
 */
class DataRunnable(private val dataList: List<XmlSingleData>): Runnable {
    private val logger: Logger = LoggerFactory.getLogger("Data runnable")

    override fun run() {
        logger.debug("First contributor in this batch ${dataList.firstOrNull()}")
    }
}
