package com.duoniu.uploadmanager.policy;

import android.graphics.Bitmap;

/**
 * Created by Huolongguo on 16/3/31.
 */
public interface BitmapCompressPolicy {
    Bitmap compress(Bitmap src);

    byte[] compress(byte[] bytes);

    Bitmap compress(String bitmapPath);
}
