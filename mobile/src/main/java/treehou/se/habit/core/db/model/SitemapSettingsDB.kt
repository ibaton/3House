package treehou.se.habit.core.db.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SitemapSettingsDB : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var display = true
}
