package com.xclub.feature.notes.util

object NoteLinkParser {
    private val linkPattern = Regex("""\[\[(.+?)]]""")

    fun extractLinks(content: String): List<String> = linkPattern.findAll(content).map { it.groupValues[1] }.toList()

    fun replaceLinkNames(content: String, nameMapping: Map<String, String>): String = linkPattern.replace(content) { matchResult ->
        val name = matchResult.groupValues[1]
        val newName = nameMapping[name] ?: name
        "[[$newName]]"
    }
}
