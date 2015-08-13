package ru.furry.furview2.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Iterator;
import java.util.List;

import ru.furry.furview2.images.FurImage;

public class DataImageView extends ImageView {
    private int index;

    public DataImageView(Context context) {
        super(context);
    }

    public DataImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DataImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getIndex() {
        return index;
    }

    public void setImageIndex(int index) {
        this.index = index;
        this.setVisibility(VISIBLE);
    }
}
