package com.duoniu.uploadmanager.task;

import android.graphics.Bitmap;

import com.duoniu.uploadmanager.policy.FilenamePolicy;
import com.duoniu.uploadmanager.manager.UploadManager;
import com.duoniu.uploadmanager.policy.BitmapCompressPolicy;

import java.io.ByteArrayOutputStream;

/**
 * Created by Huolongguo on 16/3/31.
 */
public class BitmapUploadAsyncTask extends UploadAsyncTask {

    BitmapCompressPolicy bitmapCompressPolicy;

    public BitmapUploadAsyncTask(UploadManager uploadManager, FilenamePolicy filenameGenerator) {
        super(uploadManager, filenameGenerator);
    }

    public void setBitmapCompressPolicy(BitmapCompressPolicy bitmapCompressPolicy) {
        this.bitmapCompressPolicy = bitmapCompressPolicy;
    }

    public BitmapCompressPolicy getBitmapCompressPolicy() {
        return bitmapCompressPolicy;
    }

    @Override
    protected UploadManager.UploadTask uploadFile(String imagePath, UploadManager.UploadListener uploadListener) {
        if (bitmapCompressPolicy == null){
            return super.uploadFile(imagePath, uploadListener);
        }
        Bitmap bitmap = bitmapCompressPolicy.compress(imagePath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return uploadManager.uploadFile(byteArrayOutputStream.toByteArray(), filenamePolicy.generatorFilename(imagePath),
                uploadListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bitmapCompressPolicy != null){
            bitmapCompressPolicy = null;
        }
    }
}
