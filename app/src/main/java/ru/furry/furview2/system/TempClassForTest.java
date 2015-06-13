package ru.furry.furview2.system;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class TempClassForTest implements ImageAware {

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public ViewScaleType getScaleType() {
        return null;
    }

    @Override
    public View getWrappedView() {
        return null;
    }

    @Override
    public boolean isCollected() {
        return false;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public boolean setImageDrawable(Drawable drawable) {
        return false;
    }

    @Override
    public boolean setImageBitmap(Bitmap bitmap) {
        return false;
    }
}
