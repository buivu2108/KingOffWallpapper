package com.black.amoledwallpapers.zionfiledownloader;

/**
 * Created by caner on 18.08.2017.
 */

public interface ZionDownloadListener {

    void onSuccess(String dataPath);

    void onFailed(String message);

    void onPaused(String message);

    void onPending(String message);

    void onBusy();
}
