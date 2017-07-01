package com.duoniu.uploadmanager.policy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Huolongguo on 16/3/31.
 */
public class DefaultBitmapCompressPolicy implements BitmapCompressPolicy {

    @Override
    public Bitmap compress(Bitmap src) {
        return src;
    }

    @Override
    public byte[] compress(byte[] bytes) {
        return bytes;
    }

    @Override
    public Bitmap compress(String bitmapPath) {
        return BitmapFactory.decodeFile(bitmapPath);
    }
}
