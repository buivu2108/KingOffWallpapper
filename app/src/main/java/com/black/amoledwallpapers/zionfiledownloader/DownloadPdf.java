package com.black.amoledwallpapers.zionfiledownloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import com.black.amoledwallpapers.R;

/**
 * Created by caner on 18.08.2017.
 */

public final class DownloadPdf implements DownloadFile {

    private long downloadReference;
    private BroadcastReceiver receiverDownloadComplete;
    DownloadManager downloadManager;
    String downloadFileUrl;
    private Context context;
    private String fileName;

    public DownloadPdf(Context context, String downloadFileUrl, String fileName) {
        this.downloadFileUrl = downloadFileUrl;
        this.context = context;
        this.fileName = fileName;
    }


    @Override
    public void start(final ZionDownloadListener downloadListener) {
        downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(downloadFileUrl);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDescription(context.getString(R.string.status_downloading_dw)).setTitle(context.getString(R.string.pdf_file_title_dw));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, fileName + ".pdf");
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(View.VISIBLE);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

        downloadReference = downloadManager.enqueue(request);


        /**
         *
         * filter for download - on completion
         *
         */
        IntentFilter intentFilter = new IntentFilter(downloadManager.ACTION_DOWNLOAD_COMPLETE);

        receiverDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long refernce = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadReference == refernce) {
                    //TODO do something with download file..

                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(refernce);
                    Cursor cursor = downloadManager.query(query);

                    /**
                     * get the status of download.
                     */
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);

                    if (cursor.moveToFirst()) {

                        int status = cursor.getInt(columnIndex);

                        int fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);

                        String savedFilePath = cursor.getString(fileNameIndex);
                        savedFilePath = savedFilePath.replace("file://", "");
                        /**
                         * get the reason-more detail on the status.
                         */
                        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                        int reason = cursor.getInt(columnReason);

                        switch (status) {
                            case DownloadManager.STATUS_SUCCESSFUL:
                                if (downloadListener != null) {
                                    downloadListener.onSuccess(savedFilePath);
                                }
                                break;

                            case DownloadManager.STATUS_FAILED:
                                if (downloadListener != null) {
                                    downloadListener.onFailed(String.valueOf(reason));
                                }
                                break;

                            case DownloadManager.STATUS_PAUSED:
                                if (downloadListener != null) {
                                    downloadListener.onPaused(String.valueOf(reason));
                                }

                                break;

                            case DownloadManager.STATUS_PENDING:
                                if (downloadListener != null) {
                                    downloadListener.onPending(String.valueOf(reason));
                                }

                                break;
                        }

                        cursor.close();

                    }

                }
            }
        };

        context.registerReceiver(receiverDownloadComplete, intentFilter);

    }
}
