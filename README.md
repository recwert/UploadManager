# UploadManager
Android upload abstract module

[![](https://jitpack.io/v/recwert/UploadManager.svg)](https://jitpack.io/#recwert/UploadManager)

## Android API Require
**API 11**

## 项目介绍
将Android上的文件上传模块抽象出来，并在接口层提供文件上传的文件命名策略，文件压缩策略等

## Versions
### 1.0.1
* 提供默认文件名生成器
* 提供图片上传任务实现类BitmapUploadAsyncTask
* 提供阿里云OSS（AliyunUploadManager）文件上传实现

## 安装

在你的Application的build.gradle文件的`allprojects`节点添加`jitpack`库

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

```
在你的app的build.gradle文件的`dependencies`节点添加`RHWebApi`

```gradle
compile 'com.github.recwert:UploadManager:1.0.1'

```

## 用法介绍
### 简单用法

```
UploadManager uploadManager = new AliyunUploadManager(this, new OSSPlainTextAKSKCredentialProvider("<ACCESS-KEY>","<SCRECT-KEY>", "<bucket-name>);
BitmapUploadAsyncTask uploadAsyncTask = new BitmapUploadAsyncTask(uploadManager, null);
uploadAsyncTask.setUploadCallback(this);
uploadAsyncTask.execute("image-url");

```
### 记得在Activity onDestroy 调用uploadAsyncTask的onDestroy方法，防止内存泄漏
```
protected void onDestroy() {
      if(uploadAsyncTask != null){
          uploadAsyncTask.onDestroy();
      }
      super.onDestroy();
    }
```

