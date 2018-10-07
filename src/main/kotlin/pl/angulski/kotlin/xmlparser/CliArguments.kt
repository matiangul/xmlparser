package pl.angulski.kotlin.xmlparser

import java.lang.IllegalArgumentException

/**
 * @author Mateusz Angulski <mateusz@angulski.pl>.
 */
class CliArguments(args: Array<String>, defaults: Map<String, String>) {

    val arguments = mutableMapOf<String, String>()

    init {
        for (arg in args) {
            val (name, value) = arg.split(delimiters = '=', ignoreCase = true)
            val argName = name.trim().toLowerCase()
            val argValue = value.trim()
            arguments[argName] = argValue
        }
        for ((name, value) in defaults) {
            val argName = name.trim().toLowerCase()
            val argValue = value.trim()
            if (arguments[argName].isNullOrBlank()) {
                arguments[argName] = argValue
            }
        }
    }

    fun get(name: String):Int = arguments[name]?.toInt() ?: throw IllegalArgumentException("$name not specified!")

    fun getString(name: String):String = arguments[name] ?: throw IllegalArgumentException("$name not specified!")
}
