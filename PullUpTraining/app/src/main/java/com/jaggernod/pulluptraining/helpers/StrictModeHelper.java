package com.jaggernod.pulluptraining.helpers;

import com.jaggernod.pulluptraining.BuildConfig;

import android.app.Activity;
import android.os.Debug;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Pawel Polanski on 14/10/14.
 */
public class StrictModeHelper {

    private static final String TAG = StrictModeHelper.class.getSimpleName();

    public static final boolean STRICT_POLICY_ENABLED = true && BuildConfig.DEBUG;
    public static final boolean CREATE_HEAP_BUMP = true && STRICT_POLICY_ENABLED;

    private Set<Class<? extends Activity>> registeredActivities = new CopyOnWriteArraySet<>();

    private boolean started = false;

    private static class SingletonHolder {
        public static final StrictModeHelper INSTANCE;

        static {
            INSTANCE = new StrictModeHelper();
        }
    }

    private StrictModeHelper() { }

    public static StrictModeHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

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

    public void startStrictMode() {
        if (!STRICT_POLICY_ENABLED) {
            Log.w(TAG, "Strict policy hasn't been enabled via STRICT_POLICY_ENABLED");
            return;
        }

        synchronized (this) {
            if (started) {
                Log.i(TAG, "Strict policy has been started");
                return;
            }

            try {
                StrictMode.setVmPolicy(getStrictVmPolicy());

                if (CREATE_HEAP_BUMP) {
                    // Replace System.err with one that will monitor for StrictMode
                    // killing us and perform a hprof heap dump just before it does.
                    System.setErr(new PrintStreamThatDumpsHprofWhenStrictModeKillsUs(System.err));
                }
                started = true;
            } catch (Exception e) {
                Log.e(TAG, "Unable to register Strict Mode");
            }
        }
    }

    public static boolean registerActivity(@NonNull Activity activity) {
        if (!STRICT_POLICY_ENABLED) {
            return false;
        }

        final Class<? extends Activity> activityClass = activity.getClass();

        if (!getInstance().isStarted()) {
            throw new RuntimeException("Strict polity not yet started");
        }

        if (getInstance().isRegistered(activityClass)) {
            Log.v(TAG, "Activity already registered: " + activityClass);
            return false;
        }

        try {
            Method m = StrictMode.class.getMethod("incrementExpectedActivityCount", Class.class);
            m.invoke(null, activityClass);
            getInstance().registeredActivities.add(activityClass);
            Log.v(TAG, "New activity registered: " + activityClass);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Log.e(TAG, "Unable to increase the limit of activities", e);
            return false;
        }
        return true;
    }

    private boolean isStarted() {
        return started;
    }

    private boolean isRegistered(@NonNull Class<? extends Activity> activity) {
        return registeredActivities.contains(activity);
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
                // We don't want trash in our HPROF
                System.gc();
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