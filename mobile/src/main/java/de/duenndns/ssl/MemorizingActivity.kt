/* MemorizingTrustManager - a TrustManager which asks the user about invalid
 *  certificates and memorizes their decision.
 *
 * Copyright (c) 2010 Georg Lukas <georg@op-co.de>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.duenndns.ssl

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.activity_memorizing.*
import treehou.se.habit.BaseActivity
import treehou.se.habit.R
import java.io.ByteArrayInputStream
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class MemorizingActivity : BaseActivity() {

    private val handler = Handler()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memorizing)

        val i = intent
        val decisionId = i.getIntExtra(MemorizingTrustManager.DECISION_INTENT_ID, MTMDecision.DECISION_INVALID)
        val titleId = i.getIntExtra(MemorizingTrustManager.DECISION_TITLE_ID, R.string.mtm_accept_cert)
        val hostname = i.getStringExtra(MemorizingTrustManager.DECISION_INTENT_HOST)
        val message = i.getStringExtra(MemorizingTrustManager.DECISION_INTENT_MESSAGE)
        val cert = i.getByteArrayExtra(MemorizingTrustManager.DECISION_INTENT_CERT)

        titleText.setText(titleId)
        messageText.text = message

        accept.setOnClickListener { acceptAlways(hostname, cert, decisionId) }
        abort.setOnClickListener { abort(hostname, cert, decisionId) }
    }

    public override fun onResume() {
        super.onResume()

    }

    private fun sendDecision(bytes: ByteArray, hostname: String, decision: Int, decisionId: Int) {
        Log.d(TAG, "Sending decision: $decision")

        try {
            val certFactory = CertificateFactory.getInstance("X.509")
            val `in` = ByteArrayInputStream(bytes)
            val cert = certFactory.generateCertificate(`in`) as X509Certificate
            MemorizingTrustManager.interactResult(this, cert, hostname, decisionId, decision)
        } catch (e: CertificateException) {
            e.printStackTrace()
        }

        finish()

        val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    private fun acceptAlways(hostname: String, cert: ByteArray, decisionId: Int) {
        val decision = MTMDecision.DECISION_ALWAYS
        sendDecision(cert, hostname, decision, decisionId)
    }

    private fun abort(hostname: String, cert: ByteArray, decisionId: Int) {
        val decision = MTMDecision.DECISION_ABORT
        sendDecision(cert, hostname, decision, decisionId)
    }

    companion object {

        private val TAG = MemorizingActivity::class.java.simpleName
    }
}
