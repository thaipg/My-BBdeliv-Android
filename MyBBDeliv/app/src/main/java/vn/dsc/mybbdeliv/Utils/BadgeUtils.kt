package dsc.vn.mybbdeliv.Utils

import android.content.Context
import me.leolin.shortcutbadger.ShortcutBadger
import org.jetbrains.annotations.NotNull

enum class BadgeType {
    ALL, WAIT_FOR_PICK
}

interface IBadgeInput {
    val context: Context
    val value: Int
    val type: BadgeType?
}

class BadgeInput(@NotNull override val context: Context,
                 override val value: Int = 0,
                 override val type: BadgeType? = null) : IBadgeInput {
}

interface IBadgeData {
    var value: Int
    val type: BadgeType?
}

class BadgeData(override var value: Int = 0, override val type: BadgeType? = null) : IBadgeData {
}

object BadgeUtils {
    private var badgeMap: MutableList<IBadgeData> = mutableListOf(
            BadgeData(0, BadgeType.ALL),
            BadgeData(0, BadgeType.WAIT_FOR_PICK)
    )

    fun getBadge(@NotNull type: BadgeType): Int {
        return this.badgeMap.firstOrNull { it.type == type }?.value ?: 0
    }

    fun addBadge(@NotNull mainContext: Context, @NotNull obj: IBadgeInput) {
        val badgeIndex: Int = this.badgeMap.indexOfFirst { it.type == obj.type }

        this.badgeMap[badgeIndex].value += obj.value
        ShortcutBadger.applyCount(obj.context, this.badgeMap[badgeIndex].value)

        if (obj.type != BadgeType.ALL) {
            val badgeAllIndex: Int = this.badgeMap.indexOfFirst { it.type == BadgeType.ALL }
            this.badgeMap[badgeAllIndex].value += obj.value
            ShortcutBadger.applyCount(mainContext, this.badgeMap[badgeAllIndex].value)
        }
    }

    fun subBadge(@NotNull mainContext: Context, obj: IBadgeInput) {
        val badgeIndex: Int = this.badgeMap.indexOfFirst { it.type == obj.type }

        this.badgeMap[badgeIndex].value -= obj.value
        ShortcutBadger.applyCount(obj.context, this.badgeMap[badgeIndex].value)

        if (obj.type != BadgeType.ALL) {
            val badgeAllIndex: Int = this.badgeMap.indexOfFirst { it.type == BadgeType.ALL }
            this.badgeMap[badgeAllIndex].value -= obj.value
            ShortcutBadger.applyCount(mainContext, this.badgeMap[badgeAllIndex].value)
        }
    }

    fun resetBadge() {
        badgeMap.forEach { data -> data.value = 0 }
    }

    fun clearBadgeContext(@NotNull list: List<Context>) {
        //Remove all count from list context
        list.forEach { data ->
            ShortcutBadger.removeCount(data)
        }
    }
}
