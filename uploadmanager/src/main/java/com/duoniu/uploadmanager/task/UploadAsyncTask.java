package com.duoniu.uploadmanager.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.duoniu.uploadmanager.policy.DefaultFilenamePolicy;
import com.duoniu.uploadmanager.policy.FilenamePolicy;
import com.duoniu.uploadmanager.manager.UploadManager;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Huolongguo on 16/3/31.
 */
public class UploadAsyncTask<Params> extends AsyncTask<Params, Integer, List<String>> {

    protected UploadManager uploadManager;

    protected CopyOnWriteArrayList<String> copyOnWriteResults;

    protected FilenamePolicy filenamePolicy;

    protected UploadCallback uploadCallback;

    private CopyOnWriteArrayList<UploadManager.UploadTask> uploadTasks;

    public UploadAsyncTask(UploadManager uploadManager, FilenamePolicy filenamePolicy) {
        this.uploadManager = uploadManager;
        this.filenamePolicy = filenamePolicy;
        if (this.filenamePolicy == null){
            this.filenamePolicy = new DefaultFilenamePolicy();
        }
    }

    public void setUploadCallback(UploadCallback uploadCallback) {
        this.uploadCallback = uploadCallback;
    }

    @Override
    protected List<String> doInBackground(Params... params) {
        copyOnWriteResults = new CopyOnWriteArrayList<>();
        uploadTasks = new CopyOnWriteArrayList<>();
        for (int i = 0; i < params.length; i++) {
            copyOnWriteResults.add(new String(""));
        }
        if (params[0] instanceof String) {
            for (int i = 0; i < params.length; i++) {
                String imagePath = (String) params[i];
                if (imagePath.startsWith("file://")) {
                    imagePath = imagePath.substring("file://".length());
                }
                uploadTasks.add(uploadFile(imagePath, new UploadTaskListener(i)));
            }
        }
        else if (params[0] instanceof Bitmap) {
            for (int i = 0; i < params.length; i++) {
                Bitmap bitmap = (Bitmap) params[i];
                ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] bitmapBytes = byteArrayOutputStream.toByteArray();
                uploadTasks.add(uploadFile(bitmapBytes, new UploadTaskListener(i)));
            }
        }

        while (!isUploadFinished() && !isCancelled()) {

        }
        return copyOnWriteResults;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        if (uploadCallback != null){
            uploadCallback.onCompleted(strings);
        }
    }

    protected UploadManager.UploadTask uploadFile(String imagePath, UploadManager.UploadListener uploadListener) {
        return uploadManager.uploadFile(imagePath, filenamePolicy.generatorFilename(imagePath),
                uploadListener);
    }

    protected UploadManager.UploadTask uploadFile(byte[] bytes, UploadManager.UploadListener uploadListener) {
        return uploadManager.uploadFile(bytes, filenamePolicy.generatorFilename(""), uploadListener);
    }

    private boolean isUploadFinished(){
        Iterator<String> iterator = copyOnWriteResults.iterator();
        while (iterator.hasNext()){
            if (TextUtils.isEmpty(iterator.next())) {
                return false;
            }
        }
        return true;
    }


    public void cancelAllUploadTask(){
        if (uploadTasks == null || uploadTasks.isEmpty()){
            return;
        }
        Iterator<UploadManager.UploadTask> iterator = uploadTasks.iterator();
        while (iterator.hasNext()){
            UploadManager.UploadTask uploadTask = iterator.next();
            if (!uploadTask.isCompleted()){
                uploadTask.cancel();
            }
        }
        this.cancel(true);
    }

    public void onDestroy(){
        uploadManager = null;
        copyOnWriteResults = null;
        uploadCallback = null;
        filenamePolicy = null;
        if (uploadTasks != null){
            uploadTasks.clear();
        }
        uploadTasks = null;

    }
    protected class UploadTaskListener implements UploadManager.UploadListener {

        private int position;

        public UploadTaskListener(int position) {
            this.position = position;
        }

        @Override
        public void onProgress(long currentSize, long totalSize) {
            if (uploadCallback != null){
                uploadCallback.onProgress(position, currentSize, totalSize);
            }
        }

        @Override
        public void onCompleted(String fileUrl) {
            if (TextUtils.isEmpty(fileUrl)) {
                return;
            }
            copyOnWriteResults.set(position, fileUrl);
            if (uploadCallback != null){
                uploadCallback.onCompleted(position, fileUrl);
            }
        }

        @Override
        public void onFailure(Exception e) {
            e.printStackTrace();
            if (uploadCallback != null){
                uploadCallback.onFailure(e);
            }
        }
    }

    public interface UploadCallback{
        void onProgress(int index, long currentSize, long totalSize);
        void onCompleted(int index, String fileUrl);
        void onCompleted(List<String> fileUrls);
        void onFailure(Exception e);
    }



}
