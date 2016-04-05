package com.duoniu.uploadmanager.manager;

/**
 * Created by Huolongguo on 16/3/24.
 */
public interface UploadManager {
    UploadTask uploadFile(String path, String imageName, UploadListener uploadListener);

    UploadTask uploadFile(byte[] data, String imageName, UploadListener uploadListener);


    /**
     * 该监听会在非UI线程执行
     */
    interface UploadListener{
        void onProgress(long currentSize, long totalSize);

        void onCompleted(String fileUrl);

        void onFailure(Exception e);
    }

    interface UploadTask{
        void cancel();

        void waitUntilFinished();

        boolean isCompleted();

    }
}
