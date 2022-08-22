package com.black.amoledwallpapers.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
public class RomUtil {

    private static final String TAG = "RomUtil";

    public static boolean isHuaweiRom() {
        if (!TextUtils.isEmpty(getEmuiVersion()) && !getEmuiVersion().equals("")) {
            Log.d(TAG, "isHuaweiRom: true");
            return true;
        }
        Log.d(TAG, "isHuaweiRom: false");
        return false;
    }

    public static boolean isMiuiRom() {
        if (!TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"))) {
            Log.d(TAG, "isMiuiRom: true");
            return true;
        }
        Log.d(TAG, "isMiuiRom: false");
        return false;
    }

    private static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    public static boolean isFlymeRom(Context context) {
        Object obj = isInstalledByPkgName(context, "com.meizu.flyme.update") ? Log.d(TAG, "isFlymeRom: true")
                : Log.d(TAG, "isFlymeRom: false");
        return isInstalledByPkgName(context, "com.meizu.flyme.update");
    }

    public static boolean isSmartisanRom(Context context) {
        Object obj = isInstalledByPkgName(context, "com.smartisanos.security") ? Log.d(TAG, "isSmartisanRom: true")
                : Log.d(TAG, "isSmartisanRom: false");
        return isInstalledByPkgName(context, "com.smartisanos.security");
    }


    public static boolean isInstalledByPkgName(Context context, String pkgName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    private static String getEmuiVersion() {
        String emuiVerion = "";
        Class<?>[] clsArray = new Class<?>[]{String.class};
        Object[] objArray = new Object[]{"ro.build.version.emui"};
        try {
            Class<?> SystemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method get = SystemPropertiesClass.getDeclaredMethod("get", clsArray);
            String version = (String) get.invoke(SystemPropertiesClass, objArray);
            Log.d(TAG, "get EMUI version is:" + version);
            if (!TextUtils.isEmpty(version)) {
                return version;
            }
        } catch (ClassNotFoundException e) {
            Log.e(TAG, " getEmuiVersion wrong, ClassNotFoundException");
        } catch (LinkageError e) {
            Log.e(TAG, " getEmuiVersion wrong, LinkageError");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, " getEmuiVersion wrong, NoSuchMethodException");
        } catch (NullPointerException e) {
            Log.e(TAG, " getEmuiVersion wrong, NullPointerException");
        } catch (Exception e) {
            Log.e(TAG, " getEmuiVersion wrong");
        }
        return emuiVerion;
    }
}