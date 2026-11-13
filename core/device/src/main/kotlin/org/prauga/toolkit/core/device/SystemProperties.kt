// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import java.io.IOException
import timber.log.Timber

class SystemProperties internal constructor(private val values: Map<String, String>) {
    internal operator fun get(key: String): String? = values[key]?.takeIf { it.isNotBlank() }

    companion object {
        private val LINE = Regex("""^\[(.+?)]: \[(.*)]$""")

        fun read(): SystemProperties {
            var process: Process? = null
            return try {
                val started = ProcessBuilder("getprop").redirectErrorStream(true).start()
                process = started
                SystemProperties(started.inputStream.bufferedReader().useLines(::parse))
            } catch (e: IOException) {
                Timber.w(e, "getprop failed")
                SystemProperties(emptyMap())
            } finally {
                process?.destroy()
            }
        }

        internal fun parse(lines: Sequence<String>): Map<String, String> =
            lines
                .mapNotNull { line ->
                    LINE.matchEntire(line.trim())?.let { match ->
                        match.groupValues[1] to match.groupValues[2]
                    }
                }
                .toMap()
    }
}
