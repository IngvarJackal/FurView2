package ru.furry.furview2.system;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class TempClassForTest implements ImageLoadingListener {
    @Override
    public void onLoadingStarted(String imageUri, View view) {
        Log.d("fgsfds", "onLoadingStarted " + imageUri);
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        Log.d("fgsfds", "onLoadingFailed " + imageUri);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        Log.d("fgsfds", "onLoadingComplete " + imageUri);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        Log.d("fgsfds", "onLoadingCancelled " + imageUri);
    }
}
