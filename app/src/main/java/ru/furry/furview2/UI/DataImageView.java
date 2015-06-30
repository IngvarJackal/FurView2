package ru.furry.furview2.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import ru.furry.furview2.images.FurImage;

public class DataImageView extends ImageView {
    private FurImage image;

    public DataImageView(Context context) {
        super(context);
    }

    public DataImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DataImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FurImage getImage() {
        return image;
    }

    public void setImage(FurImage image) {
        this.image = image;
    }
}
