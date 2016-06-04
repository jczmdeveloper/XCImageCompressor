/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bither.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import com.czm.imagecompressor.BitmapUtil;

import java.io.File;


/**
 * jpeg图片压缩工具类
 */
public class NativeBitmapUtil {
    private static int DEFAULT_QUALITY = 100;

    public static interface BitmapCompressListener {
        void onSuccess(Bitmap outBitmap, String outFilePath);

        void onFail(String message);
    }


    /**
     * 异步压缩,默认启用图片优化、采用默认的图片质量
     *
     * @param inFilePath  压缩前的图片路径
     * @param outFilePath 压缩后的图片路径
     * @param listener    压缩图片的回调
     */
    public static void asynCompressBitmap(String inFilePath, String outFilePath, BitmapCompressListener listener) {
        asynCompressBitmap(inFilePath, DEFAULT_QUALITY, outFilePath, true, listener);
    }

    /**
     * 异步压缩,默认启用图片优化
     *
     * @param inFilePath  压缩前的图片路径
     * @param quality     压缩质量
     * @param outFilePath 压缩后的图片路径
     * @param listener    压缩图片的回调
     */
    public static void asynCompressBitmap(String inFilePath, int quality, String outFilePath, BitmapCompressListener listener) {
        asynCompressBitmap(inFilePath, quality, outFilePath, true, listener);

    }


    /**
     * 异步压缩
     *
     * @param inFilePath  压缩前的图片路径
     * @param quality     压缩质量
     * @param outFilePath 压缩后的图片路径
     * @param optimize    是否启用图片优化，会耗时，但图片质量和大小会较高
     * @param listener    压缩图片的回调
     */
    public static void asynCompressBitmap(final String inFilePath, final int quality, final String outFilePath, final boolean optimize, final BitmapCompressListener listener) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                File inFile = new File(inFilePath);
                if (inFile != null && inFile.exists()) {
                    Bitmap inBitmap = BitmapUtil.getRotateBitmapByPath(inFilePath);//获得旋转后的图片
                    return compressBitmap(inBitmap, quality, outFilePath, optimize);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    File outFile = new File(outFilePath);
                    if (outFile != null && outFile.exists()) {
                        Bitmap outBitmap = BitmapFactory.decodeFile(outFilePath);
                        if (listener != null) {
                            listener.onSuccess(outBitmap, outFilePath);
                        }
                    } else {
                        if (listener != null) {
                            listener.onFail("未找到压缩后的图片！！！");
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.onFail("图片压缩失败！！！");
                    }
                }

            }
        }.execute();

    }

    /**
     * 同步压缩，默认启用图片优化
     * @return 是否压缩成功
     */
    public static boolean syncCompressBitmap(String inFilePath, String outFilePath) {
        return syncCompressBitmap(inFilePath, DEFAULT_QUALITY, outFilePath, true);

    }
    /**
     * 同步压缩，默认启用图片优化
     *
     * @param quality     压缩质量
     * @param outFilePath 压缩后的图片路径
     * @return 是否压缩成功
     */
    public static boolean syncCompressBitmap(String inFilePath, int quality, String outFilePath) {
        return syncCompressBitmap(inFilePath, quality, outFilePath, true);

    }

    /**
     * 同步压缩
     *
     * @param inFilePath  图片路径
     * @param quality     压缩质量
     * @param outFilePath 压缩后的图片路径
     * @param optimize    是否启用图片优化，会耗时，但图片质量和大小会较高
     * @return 是否压缩成功
     */
    public static boolean syncCompressBitmap(String inFilePath, int quality, String outFilePath, boolean optimize) {
        File inFile = new File(inFilePath);
        if (inFile != null && inFile.exists()) {
            Bitmap inBitmap = BitmapUtil.getRotateBitmapByPath(inFilePath,BitmapUtil.MAX_SIZE);//获得旋转后的图片
            return compressBitmap(inBitmap, quality, outFilePath, optimize);
        }
        return false;
    }

    /**
     * 同步压缩
     *
     * @param bit         图片bitmap
     * @param quality     压缩质量
     * @param outFilePath 压缩后的图片路径
     * @param optimize    是否启用图片优化，会耗时，但图片质量和大小会较高
     * @return 是否压缩成功
     */
    public static boolean syncCompressBitmap(Bitmap bit, int quality, String outFilePath, boolean optimize) {
        return compressBitmap(bit, quality, outFilePath, optimize);
    }


    /**
     * @param bit         图片bitmap
     * @param quality     压缩质量
     * @param outFilePath 压缩后的图片路径
     * @param optimize    是否启用图片优化，会耗时，但图片质量和大小会较高
     * @return 是否压缩成功
     */
    private static boolean compressBitmap(Bitmap bit, int quality, String outFilePath,
                                          boolean optimize) {
//		Log.d("native", "compress of native");
        if (bit.getConfig() != Config.ARGB_8888) {
            Bitmap result  = null;
            result = Bitmap.createBitmap(bit.getWidth(), bit.getHeight(),
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Rect rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());
            canvas.drawBitmap(bit, null, rect, null);
            boolean comResult = saveBitmap(result, quality, outFilePath, optimize);
            result.recycle();
            return comResult;
        } else {
            return saveBitmap(bit, quality, outFilePath, optimize);
        }

    }

    private static boolean saveBitmap(Bitmap bit, int quality, String outFilePath,
                                      boolean optimize) {
        long start = System.currentTimeMillis();
        String result = compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality,
                outFilePath.getBytes(), optimize);
        long end = System.currentTimeMillis();
        long time = end - start;
        Log.v("czm","time="+time);
        bit.recycle();
        if ("1".equals(result)) {
            return true;
        } else {
            return false;
        }

    }

    private static native String compressBitmap(Bitmap bit, int w, int h,
                                                int quality, byte[] fileNameBytes, boolean optimize);

    static {
        System.loadLibrary("jpegbither");
        System.loadLibrary("bitherjni");

    }

}
