package org.isoron.uhabits.core.models

import org.isoron.uhabits.core.utils.DateUtils
import java.util.UUID

data class HabitGroup(
    var color: PaletteColor = PaletteColor(8),
    var description: String = "",
    var id: Long? = null,
    var isArchived: Boolean = false,
    var name: String = "",
    var position: Int = 0,
    var question: String = "",
    var reminder: Reminder? = null,
    var unit: String = "",
    var uuid: String? = null,
    var habitList: HabitList,
    var habitGroupList: HabitGroupList,
    val scores: ScoreList,
    val streaks: StreakList
) {
    init {
        if (uuid == null) this.uuid = UUID.randomUUID().toString().replace("-", "")
    }

    var observable = ModelObservable()

    val uriString: String
        get() = "content://org.isoron.uhabits/habit/$id"

    fun hasReminder(): Boolean = reminder != null

    fun isCompletedToday(): Boolean {
        return habitList.all { it.isCompletedToday() } && habitGroupList.all { it.isCompletedToday() }
    }

    fun isEnteredToday(): Boolean {
        return habitList.all { it.isEnteredToday() } && habitGroupList.all { it.isEnteredToday() }
    }

    fun firstEntryDate(): Timestamp {
        val today = DateUtils.getTodayWithOffset()
        var earliest = today
        for (h in habitList) {
            val first = h.firstEntryDate()
            if (earliest.isNewerThan(first)) earliest = first
        }
        for (hgr in habitGroupList) {
            val first = hgr.firstEntryDate()
            if (earliest.isNewerThan(first)) earliest = first
        }
        return earliest
    }

    fun recompute() {
        for (h in habitList) h.recompute()
        for (hgr in habitGroupList) hgr.recompute()

        val today = DateUtils.getTodayWithOffset()
        val to = today.plus(30)
        var from = firstEntryDate()
        if (from.isNewerThan(to)) from = to

        scores.combineFrom(
            habitList = habitList,
            habitGroupList = habitGroupList,
            from = from,
            to = to
        )

        streaks.combineFrom(
            habitList = habitList,
            habitGroupList = habitGroupList,
            from = from,
            to = to
        )
    }

    fun copyFrom(other: Habit) {
        this.color = other.color
        this.description = other.description
        // this.id should not be copied
        this.isArchived = other.isArchived
        this.name = other.name
        this.position = other.position
        this.question = other.question
        this.reminder = other.reminder
        this.unit = other.unit
        this.uuid = other.uuid
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Habit) return false

        if (color != other.color) return false
        if (description != other.description) return false
        if (id != other.id) return false
        if (isArchived != other.isArchived) return false
        if (name != other.name) return false
        if (position != other.position) return false
        if (question != other.question) return false
        if (reminder != other.reminder) return false
        if (unit != other.unit) return false
        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + isArchived.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + position
        result = 31 * result + question.hashCode()
        result = 31 * result + (reminder?.hashCode() ?: 0)
        result = 31 * result + unit.hashCode()
        result = 31 * result + (uuid?.hashCode() ?: 0)
        return result
    }
}