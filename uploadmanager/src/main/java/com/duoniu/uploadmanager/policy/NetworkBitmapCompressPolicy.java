package com.duoniu.uploadmanager.policy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Huolongguo on 16/3/31.
 */
public class NetworkBitmapCompressPolicy implements BitmapCompressPolicy {

    private final int CompressThresholdWifi  = (int)(1024 * 1024 * 2.0 );  //2MB
    private final int CompressThreshold4G    = (int)(1024 * 1024 * 1.0);   //1MB
    private final int CompressThreshold3G    = (int)(1024 * 1024 * 0.5);   //0.5MB
    private final int CompressThreshold2G    = (int)(1024 * 1024 * 0.5);   //0.5MB
    private final int CompressThresholdNo    = (int)(1024 * 1024 * 0.5);   //0.5MB

    private WeakReference<Context> weakContext;

    NetworkStateUtil networkStateUtil;

    public NetworkBitmapCompressPolicy(Context context) {
        this.weakContext = new WeakReference<>(context);
        networkStateUtil = new NetworkStateUtil();
    }

    @Override
    public Bitmap compress(Bitmap src) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressToByteArray(src));
        return BitmapFactory.decodeStream(byteArrayInputStream, null, null);
    }

    public byte[] compressToByteArray(Bitmap src){
        int compressThreshold = getCompressThreshold();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        int options = (int)(compressThreshold * 1.0 / byteArrayOutputStream.toByteArray().length * 100);
        while(byteArrayOutputStream.toByteArray().length > compressThreshold){
            byteArrayOutputStream.reset();
            src.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream);
            options -= 10;
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Bitmap compress(String bitmapPath) {
        int compressThreshold = getCompressThreshold();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bitmapPath, options);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int bitmapSrcWidth = options.outWidth;
        int bitmapSrcHeight = options.outHeight;
        if ( bitmapSrcWidth * bitmapSrcHeight * 2 > compressThreshold){
            options.inSampleSize = (int)Math.round(Math.sqrt(bitmapSrcWidth * bitmapSrcHeight * 2 * 1.0 / compressThreshold ));
            if (options.inSampleSize <= 0 ){
                options.inSampleSize = 1;
            }
        }
        return BitmapFactory.decodeFile(bitmapPath, options);
    }


    private int getCompressThreshold(){
        int compressThreshold = CompressThresholdNo;
        switch (networkStateUtil.getNetworkState()){
            case NetworkStateWifi:
                compressThreshold = CompressThresholdWifi;
                break;
            case NetworkState4G:
                compressThreshold = CompressThreshold4G;
                break;
            case NetworkState3G:
                compressThreshold = CompressThreshold3G;
                break;
            case NetworkState2G:
                compressThreshold = CompressThreshold2G;
            case NetworkStateWap:
            case NetworkStateNo:
                break;
            default:
                break;
        }
        return compressThreshold;
    }
    private class NetworkStateUtil{
        NetworkState getNetworkState(){
            ConnectivityManager connectivityManager = (ConnectivityManager) weakContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null){
                return NetworkState.NetworkStateNo;
            }
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable()){
                return NetworkState.NetworkStateNo;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                return NetworkState.NetworkStateWifi;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                switch (networkInfo.getSubtype()){
                    case TelephonyManager.NETWORK_TYPE_GPRS:  //联通2G
                    case TelephonyManager.NETWORK_TYPE_CDMA:  //电信2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:  //移动2G
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NetworkState.NetworkState2G;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NetworkState.NetworkState3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NetworkState.NetworkState4G;
                    default:
                        if (networkInfo.getSubtypeName().equalsIgnoreCase("TD-SCDMA") ||
                                networkInfo.getSubtypeName().equalsIgnoreCase("WCDMA") ||
                                networkInfo.getSubtypeName().equalsIgnoreCase("CDMA2000")){
                            return NetworkState.NetworkState3G;
                        }
                        return NetworkState.NetworkStateWap;
                }
            }
            return NetworkState.NetworkStateWap;
        }

    }

    private enum NetworkState{
        NetworkStateNo, NetworkStateWap, NetworkState2G, NetworkState3G,
        NetworkState4G, NetworkStateWifi
    }





}
