package com.czm.imagecompressor;

import net.bither.util.NativeBitmapUtil;

import java.util.concurrent.CountDownLatch;

/**
 * Created by caizhiming on 2016/6/2.
 * 图片压缩任务线程
 */
public class CompressTask extends Thread{
    CountDownLatch mLatch;
    Boolean mRet;
    String mInFilePath;
    String mOutFilePath;
    boolean mIsNeedCompress;
    public CompressTask(CountDownLatch latch,Boolean ret,String inFilePath,String outFilePath,boolean isNeedCompress){
        mLatch = latch;
        mRet = ret;
        mInFilePath = inFilePath;
        mOutFilePath = outFilePath;
        mIsNeedCompress = isNeedCompress;
    }
    @Override
    public void run() {
        if(mIsNeedCompress) {
            mRet = NativeBitmapUtil.syncCompressBitmap(mInFilePath,mOutFilePath);
            mLatch.countDown();
        }else{
            mRet = true;
            mLatch.countDown();
        }
    }
}
