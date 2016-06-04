package com.czm.imagecompressor;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by caizhiming on 2016/1/6.
 * App 全局工具类
 */
public class AppUtil {


    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * [获取应用程序版本名称信息]
     *
     * @return 当前应用的包名
     */
    public static String getAppPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDPath() {
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
        } else {
            return null;
        }
    }
    public static String getOutPutDir(){
        String sdDir = getSDPath();
        String output;
        if(sdDir == null){
            output= Environment.getDataDirectory().getPath();
        }else{
            output = sdDir;
        }
        output += "/image_compress";
        return output;

    }
    /**
     * 获取可用sd卡大小
     *
     * @return
     */
    public static long getAvailableSize() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = Environment.getExternalStorageDirectory();
            StatFs fs = new StatFs(file.getAbsolutePath());
            long blockSize = fs.getBlockSize();
            long blockCount = fs.getAvailableBlocks();
            return blockSize * blockCount;
        }
        return 0;
    }


    public static String getVersionCode(Context context) {
        String appVersion;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            int versionCode = pInfo.versionCode;
            appVersion = Integer.toString(versionCode);
        } catch (Exception e) {
            e.printStackTrace();
            appVersion = null;
        }
        return appVersion;
    }

    public static String getImei(Context context) {
        String imei;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            imei = null;
        }
        return imei;
    }


    /**
     * 获取设备标示
     */
    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String phone_imei = telephonyManager.getDeviceId();
        if (!TextUtils.isEmpty(phone_imei)) {
            return phone_imei;
        } else {
            String android_id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(android_id)) {
                return android_id;
            }

            return "6383hgdh56na4ff56786pkss";
        }
    }


    public static final boolean isGPSOpen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gps;
    }
    public static String getMetaDataValue(Context context,String name){
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        ApplicationInfo appInfo = null;
        String dataValue = null;
        try {
            appInfo = packageManager.getApplicationInfo(getAppPackageName(context),
                            PackageManager.GET_META_DATA);
            dataValue=appInfo.metaData.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return dataValue;
    }




    /**
     *Java文件操作 获取文件扩展名
     * Author: caizhiming
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
    /**
     * Java文件操作 获取不带扩展名的文件名
     *  Author: caizhiming
     */
    public static String getFileNameNoEx(String filePath) {
        String filename = null;
        if ((filePath != null) && (filePath.length() > 0)) {
            int dot = filePath.lastIndexOf('.');
            int line = filePath.lastIndexOf("/");
            if ((dot >-1) && (dot < (filePath.length()))) {
                filename = filePath.substring(line+1, dot);
            }
        }
        return filename;
    }
}
