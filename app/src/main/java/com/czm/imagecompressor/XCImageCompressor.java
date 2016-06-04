package com.czm.imagecompressor;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import net.bither.util.NativeBitmapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by caizhiming on 2016/6/2.
 * 图片压缩器
 */
public class XCImageCompressor {

    public static long MAX_FILE_SIZE = 1024 * 1024;
    /**
     * 是否压缩图片：大于 1280*1280的 图片 需要压缩 或者 大小大于1M
     * @param inFilePath
     * @return isNeedCompress
     */
    public static boolean isNeedCompress(String inFilePath){
        boolean ret = true;
        File file = new File(inFilePath);
        if(file != null && file.exists()){
            BitmapFactory.Options  options= new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(inFilePath,options);
            if(options.outWidth <= BitmapUtil.MAX_SIZE
                    && options.outHeight <= BitmapUtil.MAX_SIZE){
                ret = false;
            }
            if(file.length() > MAX_FILE_SIZE){
                ret = true;
            }
        }
        return ret;
    }

    /**
     * 异步压缩多张图片
     * @param inFilePathList ：需要压缩的图片的路径列表
     * @param listener：压缩监听器
     */
    public static void compress(final List<String> inFilePathList
            , final ImageCompressListener listener) {
        compress(inFilePathList,null,listener);
    }
    /**
     * 异步压缩多张图片
     * @param inFilePathList：需要压缩的图片的路径列表
     * @param outList：压缩后输出的新图片的路径列表
     * @param listener：压缩监听器
     */
    public static void compress(final List<String> inFilePathList, final List<String> outList
            , final ImageCompressListener listener) {
        final List<String> outFilePathList = (outList == null) ? createOutFilePathList(inFilePathList) : outList;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean isSuccess = true;
                CountDownLatch singal = new CountDownLatch(inFilePathList.size());
                List<Boolean> retList = new ArrayList<>();
                for (int i = 0; i < inFilePathList.size(); i++) {
                    retList.add(i, Boolean.TRUE);
                    boolean isNeedCompress = isNeedCompress(inFilePathList.get(i));
                    if(!isNeedCompress){
                        outFilePathList.set(i,inFilePathList.get(i));
                    }
                    CompressTask compressTask = new CompressTask(singal, retList.get(i)
                            , inFilePathList.get(i), outFilePathList.get(i),isNeedCompress);
                    compressTask.start();
                }
                try {
                    singal.await();
                    for (Boolean ret : retList) {
                        if(!ret) isSuccess = false;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isSuccess = false;
                }finally {
                    if(!isSuccess) {
                        String failErrorMsg = "";
                        for(int i =0;i < retList.size();i++){
                            if(!retList.get(i)){
                                failErrorMsg += "\nCompress error: "+inFilePathList.get(i);
                            }
                        }
                    }
                    return isSuccess;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if(listener != null){
                    if(result) {
                        listener.onSuccess(outFilePathList);
                        for(String path :outFilePathList){
                            Log.v("czm","outFilePath="+path);
                        }
                    }else{
                        listener.onFailure("图片压缩失败！！");
                    }
                }
            }
        }.execute();
    }

    /**
     * 异步压缩单张图片
     * @param inFilePath ：需要压缩的图片的路径
     * @param listener：压缩监听器
     */
    public static void compress(String inFilePath
            , final ImageCompressListener listener) {
        compress(inFilePath,null,listener);
    }
    /**
     * 异步压缩单张图片
     * @param inFilePath：需要压缩的图片的路径列表
     * @param outPath：压缩后输出的新图片的路径列表
     * @param listener：压缩监听器
     */
    public static void compress(final String inFilePath, final String outPath
            , final ImageCompressListener listener) {
        final String outFilePath = (outPath == null) ? createOutFilePath(inFilePath): outPath;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean isSuccess = true;
                if(isNeedCompress(inFilePath)){
                    isSuccess =  NativeBitmapUtil.syncCompressBitmap(inFilePath,outFilePath);
                    if(!isSuccess){
                        String failErrorMsg = "";
                        failErrorMsg += "\nCompress error: "+inFilePath;
                    }
                }else{
                    isSuccess = true;
                }
                return isSuccess;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if(listener != null){
                    if(result) {
                        List<String> retList = new ArrayList<>();
                        if(isNeedCompress(inFilePath)){
                            retList.add(outFilePath);
                            listener.onSuccess(retList);
                        }else{
                            retList.add(inFilePath);
                            listener.onSuccess(retList);
                        }
                        Log.v("czm","outFilePath="+retList.get(0));
                    }else{
                        listener.onFailure("图片压缩失败！！");
                    }
                }
            }
        }.execute();
    }

    /**
     * 创建压缩输出新文件路径
     * @param inPath
     * @return
     */
    public static String createOutFilePath(String inPath){
        String outPath = AppUtil.getOutPutDir() + "/"+inPath+"_new.jpg";
        return outPath;
    }
    /**
     * 创建压缩输出新文件路径列表
     * @param inList
     * @return
     */
    public static List<String> createOutFilePathList(List<String> inList){
        List<String> outList = new ArrayList<>();
        for(String inFilePath : inList){
            outList.add(AppUtil.getOutPutDir() + "/"+AppUtil.getFileNameNoEx(inFilePath)+"_new.jpg");
        }
        return outList;
    }
    /**
     * 图片压缩监听器
     */
    public interface ImageCompressListener {
        void onSuccess(List<String> outFilePathList);

        void onFailure(String message);
    }
}
