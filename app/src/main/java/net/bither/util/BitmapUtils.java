package net.bither.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class BitmapUtils {
	
	public static Bitmap getBitmapFromBitmap(InputStream is) {
		if (is == null) {
			return null;
		}
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		try {
			Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
			is.close();
			return bm;
		} catch (Throwable e) {
			return null;
		}
	}
	
//	/**
//	 * 按比例裁图片
//	 * @param activity
//	 * @param bitmap
//	 * @return
//	 */
//	public static Bitmap getScaledBitmap(Activity activity, Bitmap bitmap) {
//        if (bitmap == null) {
//            return null;
//        }
//        Bitmap bm_ret = null;
//
//        DisplayMetrics dm = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int statusBarHeight = DisplayUtils.getStatusBarHeight(activity); /* 状态栏高度 */
//        int dw = dm.widthPixels;
//        int dh = dm.heightPixels - statusBarHeight;
//
//        int w = bitmap.getWidth();
//        int h = bitmap.getHeight();
//
//        try {
//            if (h / w >= dh / dw) {
//                /* 原图高宽比大，即太“窄”了 */
//                bm_ret = Bitmap.createBitmap(bitmap, 0,
//                        h - Math.min(dh * w / dw, h), w,
//                        Math.min(dh * w / dw, h));
//            } else {
//                /* 原图高宽比小，即太“扁”了 */
//                bm_ret = Bitmap.createBitmap(bitmap,
//                        (w - Math.min(dw * h / dh, w)) / 2, 0,
//                        Math.min(dw * h / dh, w), h);
//            }
//
//            bm_ret = Bitmap.createScaledBitmap(bm_ret, dw, dh, true);
//        } catch (Throwable e) {
//            bm_ret = null;
//        }
//        return bm_ret;
//    }

	public static boolean scaleBitmapAndStore(Bitmap bmp, String target) {
		boolean result = false;
		if (bmp == null || TextUtils.isEmpty(target)) {
			return result;
		}

		int width = bmp.getWidth();
		int height = bmp.getHeight();

//		int dh = 640;
//		int dw = width * dh / height;

		try {
//			Bitmap bm_ret = Bitmap.createScaledBitmap(bmp, dw, dh, true);
			result = writeToFile(bmp, new File(target), 80, true);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 保存旋转后的图片文件
	 * @param srcFilePath 源文件
	 * @param destFilePath 目标存放文件
	 * @return
	 */
	public static boolean writeRotateFileByPath(String srcFilePath, String destFilePath) {
		Bitmap bitmap = getRotateBitmapByPath(srcFilePath);
		if (bitmap == null || bitmap.isRecycled()) {
			return false;
		}
		return writeAndRecycle(bitmap, new File(destFilePath));
	}
	
	/**
	 * 获取旋转后的图片
	 * @param path
	 * @return
	 */
	public static Bitmap getRotateBitmapByPath(String path) {
		return rotateBitmapByDegree(BitmapFactory.decodeFile(path), getBitmapDegree(path));
	}
	
	/**
	 * 将图片按照某个角度进行旋转
	 *
	 * @param bm
	 *            需要旋转的图片
	 * @param degree
	 *            旋转角度
	 * @return 旋转后的图片
	 */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
	    Bitmap returnBm = null;
	  
	    // 根据旋转角度，生成旋转矩阵
	    Matrix matrix = new Matrix();
	    matrix.postRotate(degree);
	    try {
			if (bm != null) {
				// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
				returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
			}
	    } catch (OutOfMemoryError e) {
	    }
	    if (returnBm == null) {
	        returnBm = bm;
	    }
	    if (bm != returnBm) {
	        bm.recycle();
	    }
	    return returnBm;
	}

	
	/**
	 * 读取图片的旋转的角度
	 *
	 * @param path
	 *            图片绝对路径
	 * @return 图片的旋转角度
	 */
	public static int getBitmapDegree(String path) {
	    int degree = 0;
	    try {
	        // 从指定路径下读取图片，并获取其EXIF信息
	        ExifInterface exifInterface = new ExifInterface(path);
	        // 获取图片的旋转信息
	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
	                ExifInterface.ORIENTATION_NORMAL);
	        switch (orientation) {
	        case ExifInterface.ORIENTATION_ROTATE_90:
	            degree = 90;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_180:
	            degree = 180;
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_270:
	            degree = 270;
	            break;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return degree;
	}


	//生成圆角图片
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float round) {
		try {
			Bitmap output = Bitmap.createBitmap(/*bitmap.getWidth()*/200,
					/*bitmap.getHeight()*/200, Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(output);
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, /*bitmap.getWidth()*/200,
					/*bitmap.getHeight()*/200);
			final RectF rectF = new RectF(new Rect(0, 0, 200/*bitmap.getWidth()*/,
					/*bitmap.getHeight()*/200));

			final float roundPx = round;
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.BLACK);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

			final Rect src = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());

			canvas.drawBitmap(bitmap, src, rect, paint);
			return output;
		} catch (Exception e) {
			return bitmap;
		}
	}

	public static Bitmap getblurBitmapSmile(Bitmap bkg, View view) {
	    float scaleFactor = 8;
	    Bitmap overlay = Bitmap.createBitmap((int)(view.getMeasuredWidth()/scaleFactor), (int)(view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(overlay);
	    canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
	    canvas.scale(1 / scaleFactor, 1 / scaleFactor);
	    Paint paint = new Paint();
	    paint.setFlags(Paint.FILTER_BITMAP_FLAG);
	    canvas.drawBitmap(bkg, 0, 0, paint);
	    return overlay;
	}
	
//	static Bitmap srcBitmap = BitmapFactory.decodeResource(AppApplication.getContext().getResources(), R.drawable.video_item_top);
	
	/**
	 * 根据原图和变长绘制圆形图片
	 * @param dstBitmap
	 * @param patch
	 * @param width
	 * @param height
	 * @return
	 */
//    public static Bitmap compositeImages(Bitmap dstBitmap, NinePatch patch, int width, int height){
//        Bitmap bmp = null;
//        //下面这个Bitmap中创建的函数就可以创建一个空的Bitmap
//        int dstheight = dstBitmap.getHeight();
//        bmp = Bitmap.createBitmap(width, height, dstBitmap.getConfig());
//        Paint paint = new Paint();
//        Canvas canvas = new Canvas(bmp);
//        //首先绘制第一张图片，很简单，就是和方法中getDstImage一样
//        Rect src = new Rect(0,0,srcBitmap.getWidth(), srcBitmap.getHeight());
//        Rect dst = new Rect(0,0,width, height);
//        canvas.drawBitmap(srcBitmap, src, dst, paint);
//
//        patch.draw(canvas, dst);
//        //在绘制第二张图片的时候，我们需要指定一个Xfermode
//        //这里采用Multiply模式，这个模式是将两张图片的对应的点的像素相乘
//        //，再除以255，然后以新的像素来重新绘制显示合成后的图像
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        Rect dsrc = new Rect(0,dstheight/4,width, height + dstheight/4);
//        canvas.drawBitmap(dstBitmap, dsrc, dst, paint);
//        return bmp;
//    }
    
    
    
    private static Bitmap veriV = null;
    private static Bitmap veriJ = null;
	private static Bitmap badgeB = null;
    /**
     * 获取认证bitmap
     * @param type
     */
//    public static Bitmap getVeriBitmap(int type) {
//    	switch(type) {
//    	case 1:
//    		if(veriV == null || veriV.isRecycled()) {
//    			veriV = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.icon_star_big_v);
//    		}
//    		return veriV;
//    	case 2:
//    		if(veriJ == null || veriJ.isRecycled()) {
//    			veriJ = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.icon_star_big_v_org);
//    		}
//    		return veriJ;
//    	}
//    	return null;
//    }

	/**
	 * 获取徽章bitmap
	 * @param
	 */
	/*public static Bitmap getBadgeBitmap(int step) {
		if(step == 0){
			return null;
		}
		int id_badge = BaseApplication.getContext().getResources().getIdentifier("badge_"+step,
				"drawable",BaseApplication.getContext().getPackageName());
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), id_badge);
		*//*}else if(step == 2){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.badge_2);
		}else if(step == 3){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.badge_3);
		}else if(step >= 50 && step < 60){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_6_circle);
		}else if(step >= 60 && step < 70){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_7_circle);
		}else if(step >= 70 && step < 80){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_8_circle);
		}else if(step >= 80 && step < 90){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_9_circle);
		}else if(step >= 90 && step < 100){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_10_circle);
		}else if(step >= 100 && step < 110){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_11_circle);
		}else if(step >= 110 && step < 120){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_12_circle);
		}else if(step >= 120 && step < 130){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_13_circle);
		}else if(step >= 130 && step < 140){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_14_circle);
		}else if(step >= 140 && step < 150){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_15_circle);
		}else if(step >= 150 && step < 160){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_16_circle);
		}else if(step >= 160 && step < 170){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_17_circle);
		}else if(step >= 170 && step < 180){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_18_circle);
		}else if(step >= 180 && step < 190){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_19_circle);
		}else if(step >= 190 && step < 200){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_20_circle);
		}else if(step >= 200 && step < 210){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_21_circle);
		}else if(step >= 210 && step < 220){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_22_circle);
		}else if(step >= 220 && step < 230){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_23_circle);
		}else if(step >= 230){
			badgeB = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.nlevel_24_circle);
		}else{
			return null;
		}
*//*
		return badgeB;
	}*/


	static public class BitmapWH {
		public int m_width;

		public int m_height;
	}

//	public static Bitmap DecodeFile(String str_path) {
//		FileInputStream is = null;
//		try {
//			is = new FileInputStream(str_path);
//		} catch (Throwable e) {
//			is = null;
//		}
//		if (is == null) {
//			return null;
//		}
//
//		Bitmap bitmap_ret = null;
//		bitmap_ret = DecodeInputStream(is);
//
//		int n_degree = GlobalFunctions.getExifOrientation(str_path);
//		com.huajiao.comm.im.Logger.v("Mainhhh", "n_degree==" + n_degree);
//
//		if (n_degree != 0) {
//			Matrix matrix = new Matrix();
//			matrix.postRotate(n_degree);
//			try {
//				bitmap_ret = Bitmap.createBitmap(bitmap_ret, 0, 0, bitmap_ret.getWidth(), bitmap_ret.getHeight(), matrix, true);
//			} catch (Throwable e) {
//				// TODO: handle exception
//			}
//		}
//
//		try {
//			is.close();
//			is = null;
//		} catch (Exception e) {
//			// TODO: handle exception
//			is = null;
//		}
//
//		return bitmap_ret;
//	}

//	public static Bitmap DecodeFile(String str_path, int opt_scale_time, Bitmap.Config config) {
//		FileInputStream is = null;
//		try {
//			is = new FileInputStream(str_path);
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			is = null;
//		}
//		if (is == null) {
//			return null;
//		}
//
//		Bitmap bitmap_ret = null;
//		bitmap_ret = DecodeInputStream(is, opt_scale_time, config);
//
//		int n_degree = GlobalFunctions.getExifOrientation(str_path);
//
//		if (n_degree != 0) {
//			Matrix matrix = new Matrix();
//			matrix.postRotate(n_degree);
//			try {
//				bitmap_ret = Bitmap.createBitmap(bitmap_ret, 0, 0, bitmap_ret.getWidth(), bitmap_ret.getHeight(), matrix, true);
//			} catch (Throwable e) {
//				// TODO: handle exception
//			}
//		}
//
//		try {
//			is.close();
//			is = null;
//		} catch (Exception e) {
//			// TODO: handle exception
//			is = null;
//		}
//
//		return bitmap_ret;
//	}


//	public static Bitmap DecodeFile(String str_path, int opt_scale_time) {
//		FileInputStream is = null;
//		try {
//			is = new FileInputStream(str_path);
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			is = null;
//		}
//		if (is == null) {
//			return null;
//		}
//
//		Bitmap bitmap_ret = null;
//		bitmap_ret = DecodeInputStream(is, opt_scale_time);
//
//		int n_degree = GlobalFunctions.getExifOrientation(str_path);
//
//		if (n_degree != 0) {
//			Matrix matrix = new Matrix();
//			matrix.postRotate(n_degree);
//			try {
//				bitmap_ret = Bitmap.createBitmap(bitmap_ret, 0, 0, bitmap_ret.getWidth(), bitmap_ret.getHeight(), matrix, true);
//			} catch (Throwable e) {
//				// TODO: handle exception
//			}
//		}
//
//		try {
//			is.close();
//			is = null;
//		} catch (Exception e) {
//			// TODO: handle exception
//			is = null;
//		}
//
//		return bitmap_ret;
//	}

	public static Bitmap DecodeResource(int n_id, Context context) {
		if (context == null) {
			return null;
		}
		InputStream is = null;
		try {
			is = context.getResources().openRawResource(n_id);
		} catch (Throwable e) {
			is = null;
		}
		Bitmap bitmap_ret = null;
		bitmap_ret = DecodeInputStream(is);

		try {
			if (is != null) {
				is.close();
			}
			is = null;
		} catch (Exception e) {
			// TODO: handle exception
			is = null;
		}

		return bitmap_ret;
	}

	public static Bitmap DecodeInputStream(InputStream is) {
		BitmapFactory.Options opt_decord = new BitmapFactory.Options();
		opt_decord.inPurgeable = true;
		opt_decord.inInputShareable = true;
		Bitmap bitmap_ret = null;
		try {
			bitmap_ret = BitmapFactory.decodeStream(is, null, opt_decord);
		} catch (Throwable e) {
			// TODO: handle exception
			bitmap_ret = null;
		}
		return bitmap_ret;
	}

	public static Bitmap DecodeInputStream(InputStream is, int opt_scale_time) {
		BitmapFactory.Options opt_decord = new BitmapFactory.Options();
		opt_decord.inPurgeable = true;
		opt_decord.inInputShareable = true;
		opt_decord.inSampleSize = opt_scale_time;
		Bitmap bitmap_ret = null;
		try {
			bitmap_ret = BitmapFactory.decodeStream(is, null, opt_decord);
		} catch (Throwable e) {
			// TODO: handle exception
			bitmap_ret = null;
		}
		return bitmap_ret;
	}

	public static Bitmap DecodeInputStream(InputStream is, int opt_scale_time, Bitmap.Config config) {
		BitmapFactory.Options opt_decord = new BitmapFactory.Options();
		opt_decord.inPurgeable = true;
		opt_decord.inInputShareable = true;
		opt_decord.inSampleSize = opt_scale_time;
		opt_decord.inPreferredConfig = config;
		Bitmap bitmap_ret = null;
		try {
			bitmap_ret = BitmapFactory.decodeStream(is, null, opt_decord);
		} catch (Throwable e) {
			// TODO: handle exception
			bitmap_ret = null;
		}
		return bitmap_ret;
	}


//	private static DisplayImageOptions pickerImageoptions = null;
//
//	/**
//	 * 小图片缩略图
//	 * @param path
//	 * @param width
//	 * @param height
//	 * @return
//	 */
//	public static synchronized DisplayImageOptions getPickerImageoptions(String path, int width, int height){
//		if(pickerImageoptions == null){
//
//			final BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inJustDecodeBounds = true;
//			BitmapFactory.decodeFile(path, options);
//			if (height == 0 || width == 0)
//			{
//				height = options.outHeight;
//				width = options.outWidth;
//			}
//			options.inSampleSize = calculateInSampleSize(options, width,
//					height);
//			pickerImageoptions =new DisplayImageOptions.Builder()
//
//					.showImageOnLoading(R.drawable.main_def_bg) //设置图片在下载期间显示的图片
//					.showImageForEmptyUri(R.drawable.main_def_bg)//设置图片Uri为空或是错误的时候显示的图片
//					.showImageOnFail(R.drawable.main_def_bg)  //设置图片加载/解码过程中错误时候显示的图片
//					.cacheInMemory(true)//设置下载的图片是否缓存在内存中
//					.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
////                    .decodingOptions(options)
//					.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
////                    .displayer(new FadeInBitmapDisplayer(500))//渐显动画
//					.considerExifParams(true)
//					.build();//构建完成
//		}
//		return pickerImageoptions;
//	}

	/*private static DisplayImageOptions videoImageoptions = null;

	public static synchronized DisplayImageOptions getVideoImageOptions(){
		if(videoImageoptions == null){
            videoImageoptions =new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.main_def_bg) //设置图片在下载期间显示的图片
					.showImageForEmptyUri(R.drawable.main_def_bg)//设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(R.drawable.main_def_bg)  //设置图片加载/解码过程中错误时候显示的图片
					.cacheInMemory(true)//设置下载的图片是否缓存在内存中
					.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
					.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
//    		.displayer(new FadeInBitmapDisplayer(100))//渐显动画
					.build();//构建完成
		}
		return videoImageoptions;
	}

	private static DisplayImageOptions watchesImageOptions = null;

	public static synchronized DisplayImageOptions getWatchesImageOptions(){
		if(watchesImageOptions == null){
			watchesImageOptions =new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.live_switch_default) //设置图片在下载期间显示的图片
					.showImageForEmptyUri(R.drawable.live_switch_default)//设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(R.drawable.live_switch_default)  //设置图片加载/解码过程中错误时候显示的图片
					.cacheInMemory(true)//设置下载的图片是否缓存在内存中
					.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
					.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
//    		.displayer(new FadeInBitmapDisplayer(100))//渐显动画
					.build();//构建完成
		}
		return watchesImageOptions;
	}


    public static DisplayImageOptions  twoColumnVideoImageOptions = null;
    public static synchronized DisplayImageOptions getTwoColumnVideoImageOptions(){
        if(twoColumnVideoImageOptions == null){
            twoColumnVideoImageOptions =new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.main_def_bg) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.main_def_bg)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.main_def_bg)  //设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
//    		.displayer(new FadeInBitmapDisplayer(100))//渐显动画
                    .build();//构建完成
        }
        return twoColumnVideoImageOptions;
    }

	private static DisplayImageOptions headerImageoptions = null;
	public static synchronized DisplayImageOptions getHeaderImageOptions(){
		if(headerImageoptions == null){
			headerImageoptions =new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.head_default) //设置图片在下载期间显示的图片
					.showImageForEmptyUri(R.drawable.head_default)//设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(R.drawable.head_default)  //设置图片加载/解码过程中错误时候显示的图片
					.cacheInMemory(true)//设置下载的图片是否缓存在内存中
					.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
					.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
//    		.displayer(new FadeInBitmapDisplayer(100))//渐显动画
					.build();//构建完成
		}
		return headerImageoptions;
	}

	private static DisplayImageOptions gifImageoptions = null;
	public static synchronized DisplayImageOptions getGifImageOptions(){
		if(gifImageoptions == null){
			gifImageoptions =new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.gift_default) //设置图片在下载期间显示的图片
					.showImageForEmptyUri(R.drawable.gift_default)//设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(R.drawable.gift_default)  //设置图片加载/解码过程中错误时候显示的图片
					.cacheInMemory(true)//设置下载的图片是否缓存在内存中
					.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
					.build();//构建完成
		}
		return gifImageoptions;
	}

	private static DisplayImageOptions cateNormalImageoptions = null;
	public static synchronized DisplayImageOptions getCateNormalImageOptions(){
		if(cateNormalImageoptions == null){
			cateNormalImageoptions =new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.cate_default_icon_normal) //设置图片在下载期间显示的图片
					.showImageForEmptyUri(R.drawable.cate_default_icon_normal)//设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(R.drawable.cate_default_icon_normal)  //设置图片加载/解码过程中错误时候显示的图片
					.cacheInMemory(true)//设置下载的图片是否缓存在内存中
					.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
					.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
//    		.displayer(new FadeInBitmapDisplayer(100))//渐显动画
					.build();//构建完成
		}
		return cateNormalImageoptions;
	}

	*//** 礼物列表 **//*
	private static DisplayImageOptions giftListImageOptions = null;

	public static synchronized DisplayImageOptions getGiftListImageoptions() {
		if (giftListImageOptions == null) {
			giftListImageOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(null) //设置图片在下载期间显示的图片
					.showImageForEmptyUri(null)//设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(null)  //设置图片加载/解码过程中错误时候显示的图片
					.cacheInMemory(true)//设置下载的图片是否缓存在内存中
					.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
					.bitmapConfig(Bitmap.Config.ARGB_8888)//设置图片的解码类型
    		        .displayer(new FadeInBitmapDisplayer(100))//渐显动画
					.build();//构建完成
		}
		return giftListImageOptions;
	}

	*//** 相册 **//*
	private static DisplayImageOptions galleryOptions = null;

	public static DisplayImageOptions galleryDefaultImageOptions() {
		if (galleryOptions == null) {
			BitmapFactory.Options decodeOption = new BitmapFactory.Options();
			decodeOption.inSampleSize = 8;

			galleryOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.color.gallery_default_color) //设置图片在下载期间显示的图片
					.showImageForEmptyUri(R.color.gallery_default_color)//设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(R.color.gallery_default_color)  //设置图片加载/解码过程中错误时候显示的图片
					.cacheInMemory(true)//设置下载的图片是否缓存在内存中
					.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
					.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
					.decodingOptions(decodeOption)
					.considerExifParams(true)
					.build();//构建完成
		}
		return galleryOptions;
	}

	*//**
	 * 开播图库图片参数
	 *//*
	private static DisplayImageOptions prepareGalleryOptions = null;
	public static DisplayImageOptions prepareGalleryDefaultImageOptions() {
		if (prepareGalleryOptions == null) {
			BitmapFactory.Options decodeOption = new BitmapFactory.Options();
			decodeOption.inSampleSize = 8;

			prepareGalleryOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.main_def_bg) //设置图片在下载期间显示的图片
					.showImageForEmptyUri(R.drawable.main_def_bg)//设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(R.drawable.main_def_bg)  //设置图片加载/解码过程中错误时候显示的图片
					.cacheInMemory(true)//设置下载的图片是否缓存在内存中
					.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
					.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
					.decodingOptions(decodeOption)
					.considerExifParams(true)
					.build();//构建完成
		}
		return prepareGalleryOptions;
	}


	*//**
	 * 图片预览
	 *//*
	private static DisplayImageOptions photoReviewOptions = null;

	public static DisplayImageOptions getPhotoReviewOptions() {
		if (photoReviewOptions == null) {
			BitmapFactory.Options decodeOption = new BitmapFactory.Options();
			decodeOption.inSampleSize = 4;

			photoReviewOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(android.R.color.transparent) //设置图片在下载期间显示的图片
					.showImageForEmptyUri(android.R.color.transparent)//设置图片Uri为空或是错误的时候显示的图片
					.showImageOnFail(android.R.color.transparent)  //设置图片加载/解码过程中错误时候显示的图片
					.cacheInMemory(true)//设置下载的图片是否缓存在内存中
					.cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
					.bitmapConfig(Bitmap.Config.ARGB_8888)//设置图片的解码类型
					.decodingOptions(decodeOption)
					.considerExifParams(true)
					.build();//构建完成
		}
		return photoReviewOptions;
	}
*/


	/**
	 * 计算inSampleSize，用于压缩图片
	 *
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
											 int reqWidth, int reqHeight)
	{
		// 源图片的宽度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;

		if (width > reqWidth && height > reqHeight)
		{
			// 计算出实际宽度和目标宽度的比率
			float widthRatio = (float) width / (float) reqWidth;
			float heightRatio = (float) height / (float) reqHeight;
			float max_times = Math.max(widthRatio, heightRatio);

			while (Float.compare((float) inSampleSize, max_times) < 0)
			{
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}


	public static boolean writeAndRecycle(Bitmap bitmap, File outBitmap) {
		boolean success = writeToFile(bitmap, outBitmap);
		bitmap.recycle();
		return success;
	}

	public static boolean writeAndRecycle(Bitmap bitmap, File outBitmap, int quality) {
		boolean success = writeToFile(bitmap, outBitmap, quality);
		bitmap.recycle();
		return success;
	}

	public static boolean writeToFile(Bitmap bitmap, File outBitmap) {
		boolean success = false;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outBitmap);
			success = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
			out.close();
		} catch (IOException e) {
			// success is already false
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	public static boolean writeToFile(Bitmap bitmap, File outBitmap, int quality) {
		return writeToFile(bitmap, outBitmap, quality, true);
	}

	public static boolean writeToFile(Bitmap bitmap, File outBitmap, int quality, boolean recycle) {
		boolean success = false;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outBitmap);
			success = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
			out.close();
		} catch (IOException e) {
			// success is already false
		} finally {
			try {
				if (out != null) {
					out.close();
					if (recycle) {
						bitmap.recycle();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	public static BitmapWH GetBitmapWH(String str_path) {
		BitmapWH b_wh_ret = new BitmapWH();
		b_wh_ret.m_height = 0;
		b_wh_ret.m_width = 0;

		FileInputStream is = null;
		try {
			is = new FileInputStream(str_path);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			is = null;
		}
		if (is != null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bm_tmp = BitmapFactory.decodeStream(is, null, options);

			try {
				is.close();
				is = null;
			} catch (Exception e) {
				// TODO: handle exception
				is = null;
			}
			b_wh_ret.m_height = options.outHeight;
			b_wh_ret.m_width = options.outWidth;
		}
		return b_wh_ret;
	}



	/**
	 * 合成分享图片，返回bitmap
	 * @param srcBitmap1
	 * @param srcBitmap2
	 * @return
	 */
	public static Bitmap toConformBitmap(Bitmap srcBitmap1, Bitmap srcBitmap2, Bitmap bgBitmap) {
		if (srcBitmap1 == null || srcBitmap2 == null || bgBitmap == null) {
			return null;
		}

		int dstWidth = bgBitmap.getWidth();
//		int dstHeight = DisplayUtils.getHeight();

//		if(dstHeight < srcBitmap1.getHeight()+srcBitmap2.getHeight()) {
		int dstHeight = bgBitmap.getHeight();
//		}

		Bitmap newbmp = Bitmap
				.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
		Canvas cv = new Canvas(newbmp);
		// draw bg into
		cv.drawBitmap(bgBitmap, 0, 0, null);
		cv.drawBitmap(srcBitmap1, 0, 0, null);
		// draw fg into

		cv.drawBitmap(srcBitmap2, 0, bgBitmap.getHeight()-srcBitmap2.getHeight() - 10, null);// 在
		// 0，0坐标开始画入fg
		// ，可以从任意位置画入
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储

		srcBitmap1.recycle();
		srcBitmap1 = null;
		srcBitmap2.recycle();
		bgBitmap.recycle();
		srcBitmap2 = null;

		return newbmp;
	}

	private static final int watermarkerTop = 56;
	private static final int watermarkerRight = 10;

//	/**
//	 * 组装截屏，并添加水印
//	 * @param bgBmp 720p的
//	 * @param fgBmp 屏幕截屏
//	 * @return
//	 */
//	public static Bitmap compoundCaptureBitmap(Bitmap bgBmp, Bitmap fgBmp, boolean addWater) {
//		if (bgBmp == null || fgBmp == null) {
//			return null;
//		}
//
//		int dstWidth = fgBmp.getWidth();
//		int dstHeight = fgBmp.getHeight();
//
//		int bgWidth = bgBmp.getWidth();
//		int bgHeight = bgBmp.getHeight();
//
//		Bitmap newbmp = Bitmap
//				.createBitmap(dstWidth, dstHeight, Bitmap.Config.RGB_565);
//		Canvas cv = new Canvas(newbmp);
//
//		int resultWidth = 0;
//		int resultHeight = 0;
//		if(dstWidth*bgHeight > dstHeight * bgWidth){
//			resultHeight = dstWidth * bgHeight / bgWidth;
//			resultWidth = dstWidth;
//		}else{
//			resultHeight = dstHeight;
//			resultWidth = dstHeight * bgWidth / bgHeight;
//		}
//
//		// draw bg into
//		cv.drawBitmap(bgBmp, new Rect(0, 0, bgWidth, bgHeight), new Rect(0, 0, resultWidth, resultHeight), null);
//		// draw fg into
//		cv.drawBitmap(fgBmp, 0, 0, null);
//
//		// draw watermark
//		Bitmap wmBmp = null;
//		if (addWater) {
//			try {
//				wmBmp = GlobalFunctions.getWatermark();
//			} catch (Exception e) {
//				e.printStackTrace();
//			} catch (OutOfMemoryError e) {
//				e.printStackTrace();
//			}
//			if (wmBmp != null) {
//				int left = dstWidth - wmBmp.getWidth() - DisplayUtils.dip2px(watermarkerRight);
//				int top = DisplayUtils.dip2px(watermarkerTop);
//				cv.drawBitmap(wmBmp, left, top, null);
//			}
//		}
//
//		// save all clip
//		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
//		// store
//		cv.restore();// 存储
//
//		bgBmp.recycle();
//		fgBmp.recycle();
//		if (wmBmp != null) {
//			wmBmp.recycle();
//		}
//
//		return newbmp;
//	}

	/**
	 * 将view 转换成 bitmap
	 * @param view
	 * @return
     */
	public static Bitmap convertViewToBitmap(View view){
		view.setDrawingCacheEnabled(true);//buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
}
