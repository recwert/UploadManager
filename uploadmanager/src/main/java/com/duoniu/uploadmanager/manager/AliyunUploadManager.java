package com.duoniu.uploadmanager.manager;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

/**
 * Created by Huolongguo on 16/3/24.
 */
public class AliyunUploadManager implements UploadManager {

    private OSS oss;

    private String endPointPrefix = "http://";

    private String endPointSuffix = "oss-cn-hangzhou.aliyuncs.com";

    private String endpoint = endPointPrefix + endPointSuffix;

    private String bucketName;

    public AliyunUploadManager(Context context, OSSCredentialProvider credentialProvider, String bucketName){
        this.bucketName = bucketName;
        oss = new OSSClient(context, this.endpoint, credentialProvider);
    }

    public AliyunUploadManager(Context context, OSSCredentialProvider credentialProvider, String bucketName, String endPointPrefix,
                               String endPointSuffix){
        this.endPointPrefix = endPointPrefix;
        this.endPointSuffix = endPointSuffix;
        this.endpoint = this.endPointPrefix + this.endPointSuffix;
        this.bucketName = bucketName;
        oss = new OSSClient(context, this.endpoint, credentialProvider);
    }



    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public UploadTask uploadFile(String path, String imageName, final UploadListener uploadListener) {
        PutObjectRequest put = new PutObjectRequest(bucketName, imageName, path);
        return putObject(put, uploadListener);


    }

    @Override
    public UploadTask uploadFile(byte[] data, String imageName, UploadListener uploadListener) {
        PutObjectRequest put = new PutObjectRequest(bucketName, imageName, data);
        return putObject(put, uploadListener);
    }


    public UploadTask putObject(PutObjectRequest put, final UploadListener uploadListener){
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest putObjectRequest, long currentSize, long totalSize) {
                if (uploadListener != null){
                    uploadListener.onProgress(currentSize, totalSize);
                }
            }
        });
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                if (uploadListener != null){
                    uploadListener.onCompleted(endPointPrefix  + bucketName + "." + endPointSuffix + "/"
                            + putObjectRequest.getObjectKey());
                }
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException clientException, ServiceException serviceException) {
                if (uploadListener == null){
                    return;
                }
                if (clientException != null ){
                    uploadListener.onFailure(clientException);
                }
                else if (serviceException != null ){
                    uploadListener.onFailure(serviceException);
                }
                else{
                    uploadListener.onFailure(new RuntimeException("Unknown Upload Exception!"));
                }
            }
        });
        return new AliyunUploadTask(task);
    }

    class AliyunUploadTask implements UploadManager.UploadTask{

        OSSAsyncTask task ;
        public AliyunUploadTask(OSSAsyncTask task){
            this.task = task;
        }
        @Override
        public void cancel() {

        }

        @Override
        public void waitUntilFinished() {
            task.waitUntilFinished();
        }

        @Override
        public boolean isCompleted() {
            return task.isCompleted();
        }
    }
}
