package com.black.amoledwallpapers.util;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;

public class SetWallpaper {

    public static void setWallpaper(Context context, String path, String authority) {
        if (context == null || TextUtils.isEmpty(path) || TextUtils.isEmpty(authority)) {
            return;
        }
        Uri uriPath = FileUtil.getUriWithPath(context, path, authority);
        Intent intent;
        if (RomUtil.isHuaweiRom()) {
            try {
                ComponentName componentName =
                    new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.Wallpaper");
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    WallpaperManager.getInstance(context.getApplicationContext())
                        .setBitmap(ImageUtil.INSTANCE.getImageBitmap(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (RomUtil.isMiuiRom()) {
            try {
                ComponentName componentName = new ComponentName("com.android.thememanager",
                    "com.android.thememanager.activity.WallpaperDetailActivity");
                intent = new Intent("miui.intent.action.START_WALLPAPER_DETAIL");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    WallpaperManager.getInstance(context.getApplicationContext())
                        .setBitmap(ImageUtil.INSTANCE.getImageBitmap(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent it = WallpaperManager.getInstance(context.getApplicationContext())
                        .getCropAndSetWallpaperIntent(FileUtil.getUriWithPath(context, path, authority));
                it.setDataAndType(uriPath, "image/*");
                it.putExtra("mimeType", "image/*");
                context.startActivity(it);
            } else {
                try {
                    WallpaperManager.getInstance(context.getApplicationContext())
                        .setBitmap(ImageUtil.INSTANCE.getImageBitmap(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}