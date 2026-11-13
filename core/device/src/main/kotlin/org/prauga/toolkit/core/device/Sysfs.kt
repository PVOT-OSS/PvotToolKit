// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import java.io.File
import java.io.IOException
import timber.log.Timber

internal const val CPU_ROOT = "/sys/devices/system/cpu"

private val CORE_DIR = Regex("cpu(\\d+)")
private val POLICY_DIR = Regex("policy(\\d+)")
private val CPU_LIST_SEPARATOR = Regex("[\\s,]+")

internal fun listCoreDirs(cpuRoot: File): Map<Int, File> = cpuRoot.numberedChildren(CORE_DIR)

internal fun listPolicyDirs(cpuRoot: File): Map<Int, File> =
    cpuRoot.resolve("cpufreq").numberedChildren(POLICY_DIR)

internal fun parseCpuList(text: String): List<Int> =
    text
        .trim()
        .split(CPU_LIST_SEPARATOR)
        .flatMap { token ->
            val bounds = token.split('-', limit = 2).map { it.toIntOrNull() }
            val first = bounds.firstOrNull() ?: return@flatMap emptyList()
            val last = if (bounds.size == 2) bounds[1] ?: return@flatMap emptyList() else first
            (first..last).toList()
        }
        .distinct()
        .sorted()

private fun File.numberedChildren(pattern: Regex): Map<Int, File> =
    listFiles()
        .orEmpty()
        .mapNotNull { file ->
            pattern.matchEntire(file.name)?.groupValues?.get(1)?.toIntOrNull()?.let { it to file }
        }
        .toMap()
        .toSortedMap()

internal fun File.readTextOrNull(): String? =
    try {
        takeIf { it.canRead() }?.readText()?.trim()?.takeIf { it.isNotEmpty() }
    } catch (e: IOException) {
        Timber.w(e, "Unreadable %s", path)
        null
    } catch (e: SecurityException) {
        Timber.w(e, "Denied %s", path)
        null
    }

internal fun File.readIntOrNull(): Int? = readTextOrNull()?.toIntOrNull()
