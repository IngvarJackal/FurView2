package ru.furry.furview2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.io.IOException;

import ru.furry.furview2.drivers.e621.DriverE621;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.system.Utils;


public class FullscreenActivity extends Activity {


    ImageView mPictureImageView;
    ScrollView mScrollVew;
    Button mTagsButton;

    private final static DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    private final static ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mPictureImageView = (ImageView)findViewById(R.id.picImgView);
        mScrollVew = (ScrollView)findViewById(R.id.scrollView);
        mTagsButton = (Button)findViewById(R.id.tagsButton);

        FurImage fImage = (FurImage) getIntent().getParcelableExtra("image");

        mTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mScrollVew.isShown()) {
                    mScrollVew.setVisibility(View.GONE);
                    //mPictureImageView.setVisibility(View.VISIBLE);
                } else {
                    mScrollVew.setVisibility(View.VISIBLE);
                    //mPictureImageView.setVisibility(View.GONE);
                }
            }
        });

        try {
            DriverE621.downloadImage(fImage.getFileUrl(), new ImageViewAware(mPictureImageView));
        } catch (IOException e) {
            Utils.printError(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " action_settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_save:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " action_save", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_tags:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " action_tags", Toast.LENGTH_SHORT).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }


    }


}
