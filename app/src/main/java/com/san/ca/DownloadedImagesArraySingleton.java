package com.san.ca;

import android.graphics.Bitmap;

public class DownloadedImagesArraySingleton {

    private static volatile Bitmap[] downloadedImages = new Bitmap[20];

    private DownloadedImagesArraySingleton(){}

    public static Bitmap[] getInstance() {
        return downloadedImages;
    }

}
