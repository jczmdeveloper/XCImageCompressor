# XCImageCompressor
An Android Image Compressor which use bither-android-lib - Android 图片压缩器

使用方法：


        List<String> srcFilePathList = new ArrayList<>();
        List<String> outputFilePathList = new ArrayList<>();
        XCImageCompressor.compress(srcFilePathList, new XCImageCompressor.ImageCompressListener() {
            @Override
            public void onSuccess(List<String> outFilePathList) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
        XCImageCompressor.compress(srcFilePathList, outputFilePathList,new XCImageCompressor.ImageCompressListener() {
            @Override
            public void onSuccess(List<String> outFilePathList) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
