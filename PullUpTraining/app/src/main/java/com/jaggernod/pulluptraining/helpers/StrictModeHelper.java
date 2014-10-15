package com.jaggernod.pulluptraining.helpers;

import com.jaggernod.pulluptraining.BuildConfig;

import android.os.Debug;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by Pawel Polanski on 14/10/14.
 */
public class StrictModeHelper {

    private static final String TAG = StrictModeHelper.class.getSimpleName();

    private StrictModeHelper() { }

    public static final boolean STRICT_POLICY_ENABLED = true && BuildConfig.DEBUG;
    public static final boolean CREATE_HEAP_BUMP = false && STRICT_POLICY_ENABLED;

    private StrictMode.VmPolicy getStrictVmPolicy() {
        return new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .detectLeakedClosableObjects()
                .detectLeakedRegistrationObjects()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build();
    }

    public static void startStrictMode() {
        if (!STRICT_POLICY_ENABLED) {
            Log.w(TAG, "Strict policy hasn't been enabled via STRICT_POLICY_ENABLED");
            return;
        }

        final StrictModeHelper strictMode = new StrictModeHelper();
        try {
            StrictMode.setVmPolicy(strictMode.getStrictVmPolicy());

            if (CREATE_HEAP_BUMP) {
                // Replace System.err with one that'll monitor for StrictMode
                // killing us and perform a hprof heap dump just before it does.
                System.setErr(new PrintStreamThatDumpsHprofWhenStrictModeKillsUs(System.err));
            }
        } catch(Exception e) {
            Log.e(TAG, "Unable to register Strict Mode");
        }
    }

    public static void activityRecreation() {
        if (STRICT_POLICY_ENABLED) {
            // Just in case previous Activity is hasn't yet been collected
            System.gc();
        }
    }

    private static class PrintStreamThatDumpsHprofWhenStrictModeKillsUs extends PrintStream {
        public PrintStreamThatDumpsHprofWhenStrictModeKillsUs(OutputStream outs) {
            super (outs);
        }

        @Override
        public synchronized void println(String str) {
            super.println(str);
            if (str.startsWith("StrictMode VmPolicy violation with POLICY_DEATH;")) {
                // StrictMode is about to terminate us... do a heap dump!
                try {
                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir, "strict-mode-violation.hprof");
                    super.println("Dumping HPROF to: " + file);
                    Debug.dumpHprofData(file.getAbsolutePath());
                } catch (Exception e) {
                    super.println("Dumping HPROF exception: " + e);
                }
            }
        }
    }

}