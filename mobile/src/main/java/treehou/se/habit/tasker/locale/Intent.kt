package treehou.se.habit.tasker.locale

import android.os.Parcelable

class Intent private constructor() {
    init {
        throw UnsupportedOperationException("This class is non-instantiable") //$NON-NLS-1$
    }

    companion object {

        /**
         * Ordered broadcast result code indicating that a plug-in condition's state is satisfied (true).
         *
         * @see Intent.ACTION_QUERY_CONDITION
         */
        val RESULT_CONDITION_SATISFIED = 16

        /**
         * Ordered broadcast result code indicating that a plug-in condition's state is not satisfied (false).
         *
         * @see Intent.ACTION_QUERY_CONDITION
         */
        val RESULT_CONDITION_UNSATISFIED = 17

        /**
         * Ordered broadcast result code indicating that a plug-in condition's state is unknown (neither true nor
         * false).
         *
         *
         * If a condition returns UNKNOWN, then Locale will use the last known return value on a best-effort
         * basis. Best-effort means that Locale may not persist known values forever (e.g. last known values could
         * hypothetically be cleared after a device reboot or a restart of the Locale process. If
         * there is no last known return value, then unknown is treated as not satisfied (false).
         *
         *
         * The purpose of an UNKNOWN result is to allow a plug-in condition more than 10 seconds to process a
         * requery. A `BroadcastReceiver` must return within 10 seconds, otherwise it will be killed by
         * Android. A plug-in that needs more than 10 seconds might initially return
         * [.RESULT_CONDITION_UNKNOWN], subsequently request a requery, and then return either
         * [.RESULT_CONDITION_SATISFIED] or [.RESULT_CONDITION_UNSATISFIED].
         *
         * @see Intent.ACTION_QUERY_CONDITION
         */
        val RESULT_CONDITION_UNKNOWN = 18

        /**
         * `Intent` action `String` broadcast by Locale to create or edit a plug-in setting. When
         * Locale broadcasts this `Intent`, it will be sent directly to the package and class of the
         * plug-in's `Activity`. The `Intent` may contain a [.EXTRA_BUNDLE] that was previously
         * set by the `Activity` result of [.ACTION_EDIT_SETTING].
         *
         *
         * There SHOULD be only one `Activity` per APK that implements this `Intent`. If a single APK
         * wishes to export multiple plug-ins, it MAY implement multiple Activity instances that implement this
         * `Intent`, however there must only be a single [.ACTION_FIRE_SETTING] receiver. In this
         * scenario, it is the responsibility of the Activities to store enough data in [.EXTRA_BUNDLE] to
         * allow this receiver to disambiguate which "plug-in" is being fired. To avoid user confusion, it is
         * recommended that only a single plug-in be implemented per APK.
         *
         * @see Intent.EXTRA_BUNDLE
         *
         * @see Intent.EXTRA_STRING_BREADCRUMB
         */
        val ACTION_EDIT_SETTING = "com.twofortyfouram.locale.intent.action.EDIT_SETTING" //$NON-NLS-1$

        /**
         * `Intent` action `String` broadcast by Locale to fire a plug-in setting. When Locale
         * broadcasts this `Intent`, it will be sent directly to the package and class of the plug-in's
         * `BroadcastReceiver`. The `Intent` will contain a [.EXTRA_BUNDLE] that was previously
         * set by the `Activity` result of [.ACTION_EDIT_SETTING].
         *
         *
         * There MUST be only one `BroadcastReceiver` per APK that implements this `Intent`.
         *
         * @see Intent.EXTRA_BUNDLE
         */
        val ACTION_FIRE_SETTING = "com.twofortyfouram.locale.intent.action.FIRE_SETTING" //$NON-NLS-1$

        /**
         * `Intent` action `String` broadcast by Locale to create or edit a plug-in condition. When
         * Locale broadcasts this `Intent`, it will be sent directly to the package and class of the
         * plug-in's `Activity`. The `Intent` may contain a store-and-forward [.EXTRA_BUNDLE]
         * that was previously set by the `Activity` result of [.ACTION_EDIT_CONDITION].
         *
         *
         * There SHOULD be only one `Activity` per APK that implements this `Intent`. If a single APK
         * wishes to export multiple plug-ins, it MAY implement multiple Activity instances that implement this
         * `Intent`, however there must only be a single [.ACTION_QUERY_CONDITION] receiver. In this
         * scenario, it is the responsibility of the Activities to store enough data in [.EXTRA_BUNDLE] to
         * allow this receiver to disambiguate which "plug-in" is being queried. To avoid user confusion, it is
         * recommended that only a single plug-in be implemented per APK.
         *
         * @see Intent.EXTRA_BUNDLE
         *
         * @see Intent.EXTRA_STRING_BREADCRUMB
         */
        val ACTION_EDIT_CONDITION = "com.twofortyfouram.locale.intent.action.EDIT_CONDITION" //$NON-NLS-1$

        /**
         * Ordered `Intent` action `String` broadcast by Locale to query a plug-in condition. When
         * Locale broadcasts this `Intent`, it will be sent directly to the package and class of the
         * plug-in's `BroadcastReceiver`. The `Intent` will contain a [.EXTRA_BUNDLE] that was
         * previously set by the `Activity` result of [.ACTION_EDIT_CONDITION].
         *
         *
         * Since this is an ordered broadcast, the receiver is expected to set an appropriate result code from
         * [.RESULT_CONDITION_SATISFIED], [.RESULT_CONDITION_UNSATISFIED], and
         * [.RESULT_CONDITION_UNKNOWN].
         *
         *
         * There MUST be only one `BroadcastReceiver` per APK that implements this `Intent`.
         *
         * @see Intent.EXTRA_BUNDLE
         *
         * @see Intent.RESULT_CONDITION_SATISFIED
         *
         * @see Intent.RESULT_CONDITION_UNSATISFIED
         *
         * @see Intent.RESULT_CONDITION_UNKNOWN
         */
        val ACTION_QUERY_CONDITION = "com.twofortyfouram.locale.intent.action.QUERY_CONDITION" //$NON-NLS-1$

        /**
         * `Intent` action `String` to notify Locale that a plug-in condition is requesting that
         * Locale query it via [.ACTION_QUERY_CONDITION]. This merely serves as a hint to Locale that a
         * condition wants to be queried. There is no guarantee as to when or if the plug-in will be queried after
         * this `Intent` is broadcast. If Locale does not respond to the plug-in condition after a
         * [.ACTION_REQUEST_QUERY] Intent is sent, the plug-in SHOULD shut itself down and stop requesting
         * requeries. A lack of response from Locale indicates that Locale is not currently interested in this
         * plug-in. When Locale becomes interested in the plug-in again, Locale will send
         * [.ACTION_QUERY_CONDITION].
         *
         *
         * The extra [.EXTRA_ACTIVITY] MUST be included, otherwise Locale will ignore this `Intent`.
         *
         *
         * Plug-in conditions SHOULD NOT use this unless there is some sort of asynchronous event that has
         * occurred, such as a broadcast `Intent` being received by the plug-in. Plug-ins SHOULD NOT
         * periodically request a requery as a way of implementing polling behavior.
         *
         * @see Intent.EXTRA_ACTIVITY
         */
        val ACTION_REQUEST_QUERY = "com.twofortyfouram.locale.intent.action.REQUEST_QUERY" //$NON-NLS-1$

        /**
         * Type: `String`.
         *
         *
         * Maps to a `String` that represents the `Activity` bread crumb path.
         *
         * @see BreadCrumber
         */
        val EXTRA_STRING_BREADCRUMB = "com.twofortyfouram.locale.intent.extra.BREADCRUMB" //$NON-NLS-1$

        /**
         * Type: `String`.
         *
         *
         * Maps to a `String` that represents a blurb. This is returned as an `Activity` result extra
         * from [.ACTION_EDIT_CONDITION] or [.ACTION_EDIT_SETTING].
         *
         *
         * The blurb is a concise description displayed to the user of what the plug-in is configured to do.
         */
        val EXTRA_STRING_BLURB = "com.twofortyfouram.locale.intent.extra.BLURB" //$NON-NLS-1$

        /**
         * Type: `Bundle`.
         *
         *
         * Maps to a `Bundle` that contains all of a plug-in's extras.
         *
         *
         * Plug-ins MUST NOT store [Parcelable] objects in this `Bundle`, because `Parcelable`
         * is not a long-term storage format. Also, plug-ins MUST NOT store any serializable object that is not
         * exposed by the Android SDK.
         *
         *
         * The maximum size of a Bundle that can be sent across process boundaries is on the order of 500
         * kilobytes (base-10), while Locale further limits plug-in Bundles to about 100 kilobytes (base-10).
         * Although the maximum size is about 100 kilobytes, plug-ins SHOULD keep Bundles much smaller for
         * performance and memory usage reasons.
         */
        val EXTRA_BUNDLE = "com.twofortyfouram.locale.intent.extra.BUNDLE" //$NON-NLS-1$

        /**
         * Type: `String`.
         *
         *
         * Maps to a `String` that represents the name of a plug-in's `Activity`.
         *
         * @see Intent.ACTION_REQUEST_QUERY
         */
        val EXTRA_ACTIVITY = "com.twofortyfouram.locale.intent.extra.ACTIVITY" //$NON-NLS-1$
    }
}