package com.czm.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.czm.imagecompressor.R;
import com.czm.imagecompressor.XCImageCompressor;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compressImages();
    }
    private void compressImages() {

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
    }
}
