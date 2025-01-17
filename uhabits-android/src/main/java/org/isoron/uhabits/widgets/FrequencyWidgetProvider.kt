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

package org.isoron.uhabits.widgets

import android.content.Context

class FrequencyWidgetProvider : BaseWidgetProvider() {
    override fun getWidgetFromId(context: Context, id: Int): BaseWidget {
        val habits = getHabitsFromWidgetId(id)
        if (habits.isNotEmpty()) {
            return if (habits.size == 1) {
                FrequencyWidget(
                    context,
                    id,
                    habits[0],
                    preferences.firstWeekdayInt
                )
            } else {
                StackWidget(context, id, StackWidgetType.FREQUENCY, habits)
            }
        } else {
            val habitGroups = getHabitGroupsFromWidgetId(id)
            return if (habitGroups.size == 1) {
                FrequencyWidget(
                    context,
                    id,
                    habitGroups[0],
                    preferences.firstWeekdayInt
                )
            } else {
                StackWidget(context, id, StackWidgetType.FREQUENCY, habitGroups, true)
            }
        }
    }
}
