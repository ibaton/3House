package treehou.se.habit.core.db.model

import android.net.Uri

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SitemapDB : RealmObject() {

    @PrimaryKey
    var id: Long = 0

    var name: String? = null
    var label: String? = null
    var link: String? = null
    var server: ServerDB? = null
    var homepage: LinkedPageDB? = null

    var settingsDB: SitemapSettingsDB? = null

    companion object {

        fun isLocal(sitemap: SitemapDB): Boolean {
            val uri = Uri.parse(sitemap.link)

            try {
                return uri.host == Uri.parse(sitemap.server!!.localurl).host
            } catch (e: Exception) {
            }

            return false
        }

        /**
         * Generate a unique id for realm object
         * @param realm
         * @return
         */
        fun getUniqueId(realm: Realm): Long {
            var id: Long = 1
            val num = realm.where(SitemapDB::class.java).max("id")
            if (num != null) id = num.toLong() + 1

            return id
        }
    }
}
