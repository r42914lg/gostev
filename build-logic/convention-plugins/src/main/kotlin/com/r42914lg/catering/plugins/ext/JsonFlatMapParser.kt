package com.r42914lg.catering.plugins.ext

import kotlin.collections.iterator

class JsonFlatMapParser(private val json: String) {
    private var index = 0

    fun parse(): Map<String, String> {
        skipWhitespace()
        val obj = parseValue()
        return flattenToMap(obj as Map<String, Any?>)
    }

    private fun flattenToMap(map: Map<String, Any?>): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for ((key, value) in map) {
            result[key] = when (value) {
                is Map<*, *> -> toJsonString(value)
                is List<*> -> toJsonString(value)
                null -> "null"
                else -> value.toString()
            }
        }
        return result
    }

    private fun toJsonString(value: Any?): String {
        return when (value) {
            null -> "null"
            is String -> "\"${value.replace("\"", "\\\"")}\""
            is Number, is Boolean -> value.toString()
            is Map<*, *> -> value.entries.joinToString(prefix = "{", postfix = "}") {
                val k = it.key.toString()
                val v = toJsonString(it.value)
                "\"$k\":$v"
            }
            is List<*> -> value.joinToString(prefix = "[", postfix = "]") { toJsonString(it) }
            else -> "\"${value}\""
        }
    }

    private fun parseValue(): Any? {
        skipWhitespace()
        return when (peek()) {
            '{' -> parseObject()
            '[' -> parseArray()
            '"' -> parseString()
            't', 'f' -> parseBoolean()
            'n' -> parseNull()
            in "-0123456789" -> parseNumber()
            else -> error("Unexpected char: '${peek()}'")
        }
    }

    private fun parseObject(): Map<String, Any?> {
        consume('{')
        val result = mutableMapOf<String, Any?>()
        skipWhitespace()
        if (peek() == '}') {
            consume('}')
            return result
        }
        while (true) {
            skipWhitespace()
            val key = parseString()
            skipWhitespace()
            consume(':')
            val value = parseValue()
            result[key] = value
            skipWhitespace()
            if (peek() == '}') {
                consume('}')
                break
            }
            consume(',')
        }
        return result
    }

    private fun parseArray(): List<Any?> {
        consume('[')
        val result = mutableListOf<Any?>()
        skipWhitespace()
        if (peek() == ']') {
            consume(']')
            return result
        }
        while (true) {
            result.add(parseValue())
            skipWhitespace()
            if (peek() == ']') {
                consume(']')
                break
            }
            consume(',')
        }
        return result
    }

    private fun parseString(): String {
        consume('"')
        val sb = StringBuilder()
        while (peek() != '"') {
            val ch = next()
            if (ch == '\\') {
                val esc = next()
                sb.append(
                    when (esc) {
                        '"', '\\', '/' -> esc
                        'b' -> '\b'
                        'f' -> '\u000C'
                        'n' -> '\n'
                        'r' -> '\r'
                        't' -> '\t'
                        'u' -> {
                            val hex = (1..FOUR).map { next() }.joinToString("")
                            hex.toInt(HEX).toChar()
                        }
                        else -> error("Bad escape: \\$esc")
                    }
                )
            } else {
                sb.append(ch)
            }
        }
        consume('"')
        return sb.toString()
    }

    private fun parseNumber(): Number {
        val start = index
        while (peek() in "-+0123456789.eE") next()
        return json.substring(start, index).toDouble()
    }

    private fun parseBoolean(): Boolean {
        return if (json.startsWith("true", index)) {
            index += WORD_TRUE_LENGTH
            true
        } else if (json.startsWith("false", index)) {
            index += WORD_FALSE_LENGTH
            false
        } else error("Invalid boolean")
    }

    private fun parseNull(): Any? {
        if (json.startsWith("null", index)) {
            index += WORD_NULL_LENGTH
            return null
        }
        error("Invalid null")
    }

    private fun skipWhitespace() {
        while (index < json.length && json[index].isWhitespace()) index++
    }

    private fun peek(): Char = json.getOrNull(index) ?: error("Unexpected end")
    private fun next(): Char = json[index++]
    private fun consume(expected: Char) {
        if (peek() != expected) error("Expected '$expected', got '${peek()}'")
        index++
    }
    companion object {
        private const val WORD_NULL_LENGTH = 4
        private const val WORD_TRUE_LENGTH = 4
        private const val WORD_FALSE_LENGTH = 5
        private const val HEX = 16
        private const val FOUR = 4
    }
}