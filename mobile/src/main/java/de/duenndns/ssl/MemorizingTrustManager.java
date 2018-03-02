/* MemorizingTrustManager - a TrustManager which asks the user about invalid
 *  certificates and memorizes their decision.
 *
 * Copyright (c) 2010 Georg Lukas <georg@op-co.de>
 *
 * MemorizingTrustManager.java contains the actual trust manager and interface
 * code to create a MemorizingActivity and obtain the results.
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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import treehou.se.habit.R;
import treehou.se.habit.util.NotificationUtil;

/**
 * A X509 trust manager implementation which asks the user about invalid
 * certificates and memorizes their decision.
 * <p>
 * The certificate validity is checked using the system default X509
 * TrustManager, creating a query Dialog if the check fails.
 * <p>
 * <b>WARNING:</b> This only works if a dedicated thread is used for
 * opening sockets!
 */
public class MemorizingTrustManager implements X509TrustManager {
	final static String TAG = MemorizingTrustManager.class.getSimpleName();

	final static String DECISION_INTENT = "de.duenndns.ssl.DECISION";
	final static String DECISION_INTENT_ID     = DECISION_INTENT + ".decisionId";
	final static String DECISION_INTENT_CERT   = DECISION_INTENT + ".cert";
	final static String DECISION_INTENT_HOST   = DECISION_INTENT + ".host";
	final static String DECISION_INTENT_MESSAGE   = DECISION_INTENT + ".message";

	final static String DECISION_TITLE_ID      = DECISION_INTENT + ".titleId";
	private final static int NOTIFICATION_ID = 100509;

	static String KEYSTORE_FILE = "KeyStore.bks";
	static String KEYSTORE_DIR = "private";


	Context master;
	public static Activity foregroundAct;
	NotificationManagerCompat notificationManager;
	private static int decisionId = 0;

	Handler masterHandler;
	private File keyStoreFile;
	private KeyStore appKeyStore;
	private X509TrustManager defaultTrustManager;
	private X509TrustManager appTrustManager;
	private static boolean updated = false;

	/** Creates an instance of the MemorizingTrustManager class that falls back to a custom TrustManager.
	 *
	 * You need to supply the application context. This has to be one of:
	 *    - Application
	 *    - Activity
	 *    - Service
	 *
	 * The context is used for file management, to display the dialog /
	 * notification and for obtaining translated strings.
	 *
	 * @param m Context for the application.
	 * @param defaultTrustManager Delegate trust management to this TM. If null, the user must accept every certificate.
	 */
	public MemorizingTrustManager(Context m, X509TrustManager defaultTrustManager) {
		init(m);
		this.appTrustManager = getTrustManager(appKeyStore);
		this.defaultTrustManager = defaultTrustManager;
	}

	/** Creates an instance of the MemorizingTrustManager class using the system X509TrustManager.
	 *
	 * You need to supply the application context. This has to be one of:
	 *    - Application
	 *    - Activity
	 *    - Service
	 *
	 * The context is used for file management, to display the dialog /
	 * notification and for obtaining translated strings.
	 *
	 * @param m Context for the application.
	 */
	public MemorizingTrustManager(Context m) {
		init(m);
		this.appTrustManager = getTrustManager(appKeyStore);
		this.defaultTrustManager = getTrustManager(null);
	}

	void init(Context m) {
		master = m;
		masterHandler = new Handler(m.getMainLooper());
		notificationManager = NotificationManagerCompat.from(master);

		Application app;
		if (m instanceof Application) {
			app = (Application)m;
		} else if (m instanceof Service) {
			app = ((Service)m).getApplication();
		} else if (m instanceof Activity) {
			app = ((Activity)m).getApplication();
		} else throw new ClassCastException("MemorizingTrustManager context must be either Activity or Service!");

		keyStoreFile = createKeystoreFile(app);
		appKeyStore = loadAppKeyStore(keyStoreFile);
	}

	private static File createKeystoreFile(Context context){
        File dir = context.getDir(KEYSTORE_DIR, Context.MODE_PRIVATE);
        return new File(dir + File.separator + KEYSTORE_FILE);
    }


	/**
	 * Returns a X509TrustManager list containing a new instance of
	 * TrustManagerFactory.
	 *
	 * This function is meant for convenience only. You can use it
	 * as follows to integrate TrustManagerFactory for HTTPS sockets:
	 *
	 * <pre>
	 *     SSLContext sc = SSLContext.getInstance("TLS");
	 *     sc.init(null, MemorizingTrustManager.getInstanceList(this),
	 *         new java.security.SecureRandom());
	 *     HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	 * </pre>
	 * @param c Activity or Service to show the Dialog / Notification
	 */
	public static X509TrustManager[] getInstanceList(Context c) {
		return new X509TrustManager[] { new MemorizingTrustManager(c) };
	}

	/**
	 * Binds an Activity to the MTM for displaying the query dialog.
	 *
	 * This is useful if your connection is run from a service that is
	 * triggered by user interaction -- in such cases the view is
	 * visible and the user tends to ignore the service notification.
	 *
	 * You should never have a hidden view bound to MTM! Use this
	 * function in onResume() and @see unbindDisplayActivity in onPause().
	 *
	 * @param act Activity to be bound
	 */
	public void bindDisplayActivity(Activity act) {
		foregroundAct = act;
	}

	/**
	 * Removes an Activity from the MTM display stack.
	 *
	 * Always call this function when the Activity added with
	 * {@link #bindDisplayActivity(Activity)} is hidden.
	 *
	 * @param act Activity to be unbound
	 */
	public void unbindDisplayActivity(Activity act) {
		// do not remove if it was overridden by a different view
		if (foregroundAct == act)
			foregroundAct = null;
	}

	/**
	 * Changes the path for the KeyStore file.
	 *
	 * The actual filename relative to the app's directory will be
	 * <code>app_<i>dirname</i>/<i>filename</i></code>.
	 *
	 * @param dirname directory to store the KeyStore.
	 * @param filename file name for the KeyStore.
	 */
	public static void setKeyStoreFile(String dirname, String filename) {
		KEYSTORE_DIR = dirname;
		KEYSTORE_FILE = filename;
	}

	/**
	 * Get a list of all certificate aliases stored in MTM.
	 *
	 * @return an {@link Enumeration} of all certificates
	 */
	public Enumeration<String> getCertificates() {
		try {
			return appKeyStore.aliases();
		} catch (KeyStoreException e) {
			// this should never happen, however...
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get a certificate for a given alias.
	 *
	 * @param alias the certificate's alias as returned by {@link #getCertificates()}.
	 *
	 * @return the certificate associated with the alias or <tt>null</tt> if none found.
	 */
	public Certificate getCertificate(String alias) {
		try {
			return appKeyStore.getCertificate(alias);
		} catch (KeyStoreException e) {
			// this should never happen, however...
			throw new RuntimeException(e);
		}
	}

	/**
	 * Removes the given certificate from MTMs key store.
	 *
	 * <p>
	 * <b>WARNING</b>: this does not immediately invalidate the certificate. It is
	 * well possible that (a) data is transmitted over still existing connections or
	 * (b) new connections are created using TLS renegotiation, without a new cert
	 * check.
	 * </p>
	 * @param alias the certificate's alias as returned by {@link #getCertificates()}.
	 *
	 * @throws KeyStoreException if the certificate could not be deleted.
	 */
	public void deleteCertificate(String alias) throws KeyStoreException {
		appKeyStore.deleteEntry(alias);
		keyStoreUpdated();
	}

	/**
	 * Creates a new hostname verifier supporting user interaction.
	 *
	 * <p>This method creates a new {@link HostnameVerifier} that is bound to
	 * the given instance of {@link MemorizingTrustManager}, and leverages an
	 * existing {@link HostnameVerifier}. The returned verifier performs the
	 * following steps, returning as soon as one of them succeeds:
	 *  </p>
	 *  <ol>
	 *  <li>Success, if the wrapped defaultVerifier accepts the certificate.</li>
	 *  <li>Success, if the server certificate is stored in the keystore under the given hostname.</li>
	 *  <li>Ask the user and return accordingly.</li>
	 *  <li>Failure on exception.</li>
	 *  </ol>
	 *
	 * @param defaultVerifier the {@link HostnameVerifier} that should perform the actual check
	 * @return a new hostname verifier using the MTM's key store
	 *
	 * @throws IllegalArgumentException if the defaultVerifier parameter is null
	 */
	public HostnameVerifier wrapHostnameVerifier(final HostnameVerifier defaultVerifier) {
		if (defaultVerifier == null)
			throw new IllegalArgumentException("The default verifier may not be null");

		return new MemorizingHostnameVerifier(defaultVerifier);
	}

	static X509TrustManager getTrustManager(KeyStore ks) {
		try {
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
			tmf.init(ks);
			for (TrustManager t : tmf.getTrustManagers()) {
				if (t instanceof X509TrustManager) {
					return (X509TrustManager)t;
				}
			}
		} catch (Exception e) {
			// Here, we are covering up errors. It might be more useful
			// however to throw them out of the constructor so the
			// embedding app knows something went wrong.
			Log.e(TAG,"getTrustManager(" + ks + ")", e);
		}
		return null;
	}

    static KeyStore loadAppKeyStore(Context context) {
        File keystoreFile = createKeystoreFile(context);
        return loadAppKeyStore(keystoreFile);
    }

	static KeyStore loadAppKeyStore(File keyStoreFile) {
		KeyStore ks;
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
		} catch (KeyStoreException e) {
			Log.e(TAG,"getAppKeyStore()", e);
			return null;
		}
		try {
			ks.load(null, null);
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			Log.e(TAG,"getAppKeyStore(" + keyStoreFile + ")", e);
		}
		InputStream is = null;
		try {
			is = new java.io.FileInputStream(keyStoreFile);
			ks.load(is, "MTM".toCharArray());
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e(TAG, "getAppKeyStore(" + keyStoreFile + ") - exception closing file key store input stream", e);
				}
			}
		}
		return ks;
	}

    static void storeCert(Context context, String alias, Certificate cert) {
        KeyStore appKeyStore = loadAppKeyStore(context);
        try {
            Log.d(TAG,"storeCertUpdateKeystore(" + cert + ")");
            appKeyStore.setCertificateEntry(alias, cert);
        } catch (KeyStoreException e) {
            Log.e(TAG,"storeCertUpdateKeystore(" + cert + ")", e);
            return;
        }
        saveKeyStore(context, appKeyStore);
    }

	void keyStoreUpdated() {
        appTrustManager = getTrustManager(appKeyStore);

        // store KeyStore to file
        java.io.FileOutputStream fos = null;
        try {
            fos = new java.io.FileOutputStream(keyStoreFile);
            appKeyStore.store(fos, "MTM".toCharArray());
        } catch (Exception e) {
            Log.e(TAG,"storeCertUpdateKeystore(" + keyStoreFile + ")", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG,"storeCertUpdateKeystore(" + keyStoreFile + ")", e);
                }
            }
        }
    }

    void reloadKeystore(){
	    Log.d(TAG, "Reloading keystore");
	    keyStoreFile = createKeystoreFile(master);
		appKeyStore = loadAppKeyStore(keyStoreFile);
		appTrustManager = getTrustManager(appKeyStore);
	}

    static void saveKeyStore(Context context, KeyStore appKeyStore) {
        // reload appTrustManager

        // store KeyStore to file
        java.io.FileOutputStream fos = null;
        File keyStoreFile = createKeystoreFile(context);
        try {
            fos = new java.io.FileOutputStream(keyStoreFile);
            appKeyStore.store(fos, "MTM".toCharArray());
			updated = true;
            Log.d(TAG,"saveKeyStore(" + keyStoreFile.getAbsolutePath() + ")");
        } catch (Exception e) {
            Log.e(TAG,"saveKeyStore(" + keyStoreFile + ")", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG,"saveKeyStore(" + keyStoreFile + ")", e);
                }
            }
        }
    }

	// if the certificate is stored in the app key store, it is considered "known"
	private boolean isCertKnown(X509Certificate cert) {
		try {
			return appKeyStore.getCertificateAlias(cert) != null;
		} catch (KeyStoreException e) {
			return false;
		}
	}

	private static boolean isExpiredException(Throwable e) {
		do {
			if (e instanceof CertificateExpiredException)
				return true;
			e = e.getCause();
		} while (e != null);
		return false;
	}

	private static boolean isPathException(Throwable e) {
		do {
			if (e instanceof CertPathValidatorException)
				return true;
			e = e.getCause();
		} while (e != null);
		return false;
	}

	public void checkCertTrusted(X509Certificate[] chain, String authType, boolean isServer)
		throws CertificateException
	{
		Log.d(TAG, "checkCertTrusted(" + chain + ", " + authType + ", " + isServer + ")");
		try {
			Log.d(TAG, "checkCertTrusted: trying appTrustManager");
			if (isServer)
				appTrustManager.checkServerTrusted(chain, authType);
			else
				appTrustManager.checkClientTrusted(chain, authType);
		} catch (CertificateException ae) {
			Log.d(TAG,  "checkCertTrusted: appTrustManager did not verify certificate. Will fall back to secondary verification mechanisms (if any).");
			// if the cert is stored in our appTrustManager, we ignore expiredness
			if (isExpiredException(ae)) {
				Log.d(TAG, "checkCertTrusted: accepting expired certificate from keystore");
				return;
			}
			if (isCertKnown(chain[0])) {
				Log.d(TAG,  "checkCertTrusted: accepting cert already stored in keystore");
				return;
			}

			try {
				if (defaultTrustManager == null) {
					Log.d(TAG, "No defaultTrustManager set. Verification failed, throwing " + ae);
					throw ae;
				}
				Log.d(TAG, "checkCertTrusted: trying defaultTrustManager");
				if (isServer) {
					defaultTrustManager.checkServerTrusted(chain, authType);
				} else {
					defaultTrustManager.checkClientTrusted(chain, authType);
				}
			} catch (Exception e){

            }
		}
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType)
		throws CertificateException
	{
		checkCertTrusted(chain, authType, false);
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType)
		throws CertificateException
	{
		checkCertTrusted(chain, authType, true);
	}

	public X509Certificate[] getAcceptedIssuers()
	{
		Log.d(TAG, "getAcceptedIssuers()");
		return defaultTrustManager.getAcceptedIssuers();
	}

	private static int createDecisionId(MTMDecision d) {
		int myId = decisionId;
		decisionId += 1;
		return myId;
	}

	private static String hexString(byte[] data) {
		StringBuilder si = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			si.append(String.format("%02x", data[i]));
			if (i < data.length - 1)
				si.append(":");
		}
		return si.toString();
	}

	private static String certHash(final X509Certificate cert, String digest) {
		try {
			MessageDigest md = MessageDigest.getInstance(digest);
			md.update(cert.getEncoded());
			return hexString(md.digest());
		} catch (CertificateEncodingException e) {
			return e.getMessage();
		} catch (NoSuchAlgorithmException e) {
			return e.getMessage();
		}
	}

	private static void certDetails(StringBuilder si, X509Certificate c) {
		SimpleDateFormat validityDateFormater = new SimpleDateFormat("yyyy-MM-dd");
		si.append("\n");
		si.append(c.getSubjectDN().toString());
		si.append("\n");
		si.append(validityDateFormater.format(c.getNotBefore()));
		si.append(" - ");
		si.append(validityDateFormater.format(c.getNotAfter()));
		si.append("\nSHA-256: ");
		si.append(certHash(c, "SHA-256"));
		si.append("\nSHA-1: ");
		si.append(certHash(c, "SHA-1"));
		si.append("\nSigned by: ");
		si.append(c.getIssuerDN().toString());
		si.append("\n");
	}

	private String certChainMessage(final X509Certificate[] chain, CertificateException cause) {
		Throwable e = cause;
		Log.d(TAG, "certChainMessage for " + e);
		StringBuilder si = new StringBuilder();
		if (isPathException(e))
			si.append(master.getString(R.string.mtm_trust_anchor));
		else if (isExpiredException(e))
			si.append(master.getString(R.string.mtm_cert_expired));
		else {
			// get to the cause
			while (e.getCause() != null)
				e = e.getCause();
			si.append(e.getLocalizedMessage());
		}
		si.append("\n\n");
		si.append(master.getString(R.string.mtm_connect_anyway));
		si.append("\n\n");
		si.append(master.getString(R.string.mtm_cert_details));
		for (X509Certificate c : chain) {
			certDetails(si, c);
		}
		return si.toString();
	}

	private String hostNameMessage(X509Certificate cert, String hostname) {
		StringBuilder si = new StringBuilder();

		si.append(master.getString(R.string.mtm_hostname_mismatch, hostname));
		si.append("\n\n");
		try {
			Collection<List<?>> sans = cert.getSubjectAlternativeNames();
			if (sans == null) {
				si.append(cert.getSubjectDN());
				si.append("\n");
			} else for (List<?> altName : sans) {
				Object name = altName.get(1);
				if (name instanceof String) {
					si.append("[");
					si.append(altName.get(0));
					si.append("] ");
					si.append(name);
					si.append("\n");
				}
			}
		} catch (CertificateParsingException e) {
			e.printStackTrace();
			si.append("<Parsing error: ");
			si.append(e.getLocalizedMessage());
			si.append(">\n");
		}
		si.append("\n");
		si.append(master.getString(R.string.mtm_connect_anyway));
		si.append("\n\n");
		si.append(master.getString(R.string.mtm_cert_details));
		certDetails(si, cert);
		return si.toString();
	}

	/**
	 * Reflectively call
	 * <code>Notification.setLatestEventInfo(Context, CharSequence, CharSequence, PendingIntent)</code>
	 * since it was remove in Android API level 23.
	 *
	 * @param notification
	 * @param context
	 * @param mtmNotification
	 * @param certName
	 * @param call
	 */
	private static void setLatestEventInfoReflective(Notification notification,
			Context context, CharSequence mtmNotification,
			CharSequence certName, PendingIntent call) {
		Method setLatestEventInfo;
		try {
			setLatestEventInfo = notification.getClass().getMethod(
					"setLatestEventInfo", Context.class, CharSequence.class,
					CharSequence.class, PendingIntent.class);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}

		try {
			setLatestEventInfo.invoke(notification, context, mtmNotification,
					certName, call);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	void startActivityNotification(Intent intent, String certName) {
		final PendingIntent call = PendingIntent.getActivity(master, 0, intent,
				0);
		final String mtmNotification = master.getString(R.string.mtm_notification);
		final long currentMillis = System.currentTimeMillis();
		final Context context = master.getApplicationContext();

		Notification notification = new NotificationCompat.Builder(master, NotificationUtil.CHANNEL_ID_TRUST_MANAGER)
				.setContentTitle(mtmNotification)
				.setContentText(certName)
				.setTicker(certName)
				.setSmallIcon(android.R.drawable.ic_lock_lock)
				.setWhen(currentMillis)
				.setContentIntent(call)
				.setAutoCancel(true)
				.build();

		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	/**
	 * Returns the top-most entry of the view stack.
	 *
	 * @return the Context of the currently bound UI or the master context if none is bound
	 */
	Context getUI() {
		return (foregroundAct != null) ? foregroundAct : master;
	}

	void interact(X509Certificate cert, final String hostname, final String message, final int titleId) {
		/* prepare the MTMDecision blocker object */

		masterHandler.post(() -> {
            Intent ni = createIntent(cert, hostname, message, titleId);
			startActivityNotification(ni, message);
        });
	}

	public Intent createIntent(X509Certificate cert, final String hostname, final String message, final int titleId){
		MTMDecision choice = new MTMDecision();
		final int myId = createDecisionId(choice);

		Intent ni = new Intent(master, MemorizingActivity.class);
		ni.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ni.setData(Uri.parse(MemorizingTrustManager.class.getName() + "/" + myId));
		ni.putExtra(DECISION_INTENT_ID, myId);
		ni.putExtra(DECISION_INTENT_HOST, hostname);
		ni.putExtra(DECISION_INTENT_MESSAGE, message);
		try {
			ni.putExtra(DECISION_INTENT_CERT, cert.getEncoded());
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
		ni.putExtra(DECISION_TITLE_ID, titleId);
		return ni;
	}

	void launchCertInstaller(X509Certificate cert, String hostname) {
		interact(cert, hostname, hostNameMessage(cert, hostname), R.string.mtm_accept_servername);
	}

	protected static void interactResult(Context context, X509Certificate cert, String host, int decisionId, int choice) {
		if(choice == MTMDecision.DECISION_ALWAYS) {
			NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
			storeCert(context, host, cert);
		}
	}

	class MemorizingHostnameVerifier implements HostnameVerifier {
		private HostnameVerifier defaultVerifier;

		public MemorizingHostnameVerifier(HostnameVerifier wrapped) {
			defaultVerifier = wrapped;
		}

		@Override
		public boolean verify(String hostname, SSLSession session) {

			if(updated){
				updated = false;
				reloadKeystore();
			}

			// if the default verifier accepts the hostname, we are done
			if (defaultVerifier.verify(hostname, session)) {
				Log.d(TAG, "default verifier accepted " + hostname);
				return true;
			}
			// otherwise, we check if the hostname is an alias for this cert in our keystore
			try {
				X509Certificate cert = (X509Certificate)session.getPeerCertificates()[0];
				//Log.d(TAG, "cert: " + cert);
				if (cert.equals(appKeyStore.getCertificate(hostname.toLowerCase(Locale.US)))) {
					Log.d(TAG, "certificate for " + hostname + " is in our keystore. accepting.");
					return true;
				} else {
					Log.d(TAG, "server " + hostname + " provided wrong certificate, asking user.");
                    new Thread(() -> launchCertInstaller(cert, hostname)).start();
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
}
