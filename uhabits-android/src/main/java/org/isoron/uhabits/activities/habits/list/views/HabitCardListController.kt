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

package org.isoron.uhabits.activities.habits.list.views

import dagger.Lazy
import org.isoron.uhabits.activities.habits.list.ListHabitsSelectionMenu
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.ModelObservable
import org.isoron.uhabits.core.ui.screens.habits.list.ListHabitsBehavior
import org.isoron.uhabits.inject.ActivityScope
import javax.inject.Inject

/**
 * Controller responsible for receiving and processing the events generated by a
 * HabitListView. These include selecting and reordering items, toggling
 * checkmarks and clicking habits.
 */
@ActivityScope
class HabitCardListController @Inject constructor(
    private val adapter: HabitCardListAdapter,
    private val behavior: ListHabitsBehavior,
    private val selectionMenu: Lazy<ListHabitsSelectionMenu>
) : HabitCardListView.Controller, ModelObservable.Listener {

    private var activeMode: Mode

    init {
        this.activeMode = NormalMode()
        adapter.observable.addListener(this)
    }

    override fun drop(from: Int, to: Int) {
        if (from == to) return
        cancelSelection()

        val habitFrom = adapter.getHabit(from)
        val habitTo = adapter.getHabit(to)
        if (habitFrom != null) {
            if (habitTo != null) {
                adapter.performReorder(from, to)
                behavior.onReorderHabit(habitFrom, habitTo)
            }
            return
        }

        val hgrFrom = adapter.getHabitGroup(from)!!
        val hgrTo = adapter.getHabitGroup(to) ?: return
        adapter.performReorder(from, to)
        behavior.onReorderHabitGroup(hgrFrom, hgrTo)
    }

    override fun onItemClick(position: Int) {
        activeMode.onItemClick(position)
    }

    override fun onItemLongClick(position: Int) {
        activeMode.onItemLongClick(position)
    }

    override fun onModelChange() {
        if (adapter.isSelectionEmpty) {
            activeMode = NormalMode()
            selectionMenu.get().onSelectionFinish()
        }
    }

    fun onSelectionFinished() {
        cancelSelection()
    }

    override fun startDrag(position: Int) {
        activeMode.startDrag(position)
    }

    private fun toggleSelection(position: Int) {
        adapter.toggleSelection(position)
        activeMode = if (adapter.isSelectionEmpty) NormalMode() else SelectionMode()
    }

    private fun cancelSelection() {
        adapter.clearSelection()
        activeMode = NormalMode()
        selectionMenu.get().onSelectionFinish()
    }

    interface HabitListener {
        fun onHabitClick(habit: Habit)
        fun onHabitReorder(from: Habit, to: Habit)
    }

    /**
     * A Mode describes the behavior of the list upon clicking, long clicking
     * and dragging an item. This depends on whether some items are already
     * selected or not.
     */
    private interface Mode {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int): Boolean
        fun startDrag(position: Int)
    }

    /**
     * Mode activated when there are no items selected. Clicks trigger habit
     * click. Long clicks start selection.
     */
    internal inner class NormalMode : Mode {
        override fun onItemClick(position: Int) {
            val habit = adapter.getHabit(position)
            if (habit != null) {
                behavior.onClickHabit(habit)
            } else {
                val hgr = adapter.getHabitGroup(position)
                if (hgr != null) {
                    behavior.onClickHabitGroup(hgr)
                }
            }
        }

        override fun onItemLongClick(position: Int): Boolean {
            startSelection(position)
            return true
        }

        override fun startDrag(position: Int) {
            startSelection(position)
        }

        private fun startSelection(position: Int) {
            toggleSelection(position)
            activeMode = SelectionMode()
            selectionMenu.get().onSelectionStart()
        }
    }

    /**
     * Mode activated when some items are already selected. Clicks toggle
     * item selection. Long clicks select more items.
     */
    internal inner class SelectionMode : Mode {
        override fun onItemClick(position: Int) {
            toggleSelection(position)
            notifyListener()
        }

        override fun onItemLongClick(position: Int): Boolean {
            toggleSelection(position)
            notifyListener()
            return true
        }

        override fun startDrag(position: Int) {
            toggleSelection(position)
            notifyListener()
        }

        private fun notifyListener() {
            if (activeMode is SelectionMode) {
                selectionMenu.get().onSelectionChange()
            } else {
                selectionMenu.get().onSelectionFinish()
            }
        }
    }
}
