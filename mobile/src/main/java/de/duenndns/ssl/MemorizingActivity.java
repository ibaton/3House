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
package de.duenndns.ssl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import treehou.se.habit.BaseActivity;
import treehou.se.habit.R;

public class MemorizingActivity extends BaseActivity {

	@BindView(R.id.title) TextView title;
	@BindView(R.id.message) TextView content;
	private Unbinder unbinder;

	private Handler handler = new Handler();

	private static final String TAG = MemorizingActivity.class.getSimpleName();
	int decisionId;
	byte[] cert;
	String hostname;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memorizing);
		unbinder = ButterKnife.bind(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		Intent i = getIntent();
		decisionId = i.getIntExtra(MemorizingTrustManager.DECISION_INTENT_ID, MTMDecision.DECISION_INVALID);
		int titleId = i.getIntExtra(MemorizingTrustManager.DECISION_TITLE_ID, R.string.mtm_accept_cert);
		hostname = i.getStringExtra(MemorizingTrustManager.DECISION_INTENT_HOST);
		String message = i.getStringExtra(MemorizingTrustManager.DECISION_INTENT_MESSAGE);
		cert = i.getByteArrayExtra(MemorizingTrustManager.DECISION_INTENT_CERT);

		title.setText(titleId);
		content.setText(message);
	}

	@Override
	protected void onDestroy() {
		unbinder.unbind();
		super.onDestroy();
	}

	void sendDecision(byte[] bytes, String hostname, int decision) {
		Log.d(TAG,  "Sending decision: " + decision);

		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			InputStream in = new ByteArrayInputStream(bytes);
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
			MemorizingTrustManager.interactResult(this, cert, hostname, decisionId, decision);
		} catch (CertificateException e) {
			e.printStackTrace();
		}

		finish();

		Intent i = getBaseContext().getPackageManager().
				getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	@OnClick(R.id.accept)
	void acceptAlways(){
		int decision = MTMDecision.DECISION_ALWAYS;
		sendDecision(cert, hostname, decision);
	}

	@OnClick(R.id.abort)
	void abort(){
		int decision = MTMDecision.DECISION_ABORT;
		sendDecision(cert, hostname, decision);
	}
}
