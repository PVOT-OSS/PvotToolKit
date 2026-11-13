// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.vitals

import org.prauga.toolkit.core.model.Vitals

data class VitalsSample(val vitals: Vitals, val history: MetricHistory)
