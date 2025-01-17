/*
 * Copyright (C) 2016-2021 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isoron.uhabits.core.models

/**
 * If [isSkipDays] is enabled, then the selected [days] of the week are regularly skipped, and
 * the score is automatically calculated based on that. For measurable weekly tasks and weekly
 * boolean tasks with frequency < 1, the target is not changed but distributed over the rest of the
 * days of the week.
 */
data class SkipDays(
    val isSkipDays: Boolean,
    val days: WeekdayList
) {

    fun isDaySkipped(day: Timestamp): Boolean {
        return isSkipDays && days.isDayTrue(day.weekday)
    }

    fun numDaysSkipped(): Int {
        return if (isSkipDays) days.numDays() else 0
    }

    companion object {
        @JvmField
        val NONE = SkipDays(false, WeekdayList(0))
    }
}
