package ru.furry.furview2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.io.IOException;

import ru.furry.furview2.drivers.e621.DriverE621;
import ru.furry.furview2.system.Utils;


public class FullscreenActivity extends Activity {

    ImageView mPictureImageView;

    private final static DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    private final static ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mPictureImageView = (ImageView)findViewById(R.id.pictureImageView);

        Bundle extras = getIntent().getExtras();
        String imageUrl = extras.getString("imageUrl");

        ImageView image = (ImageView) findViewById(R.id.pictureImageView);

        try {
            DriverE621.downloadImage(imageUrl, new ImageViewAware(image));
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
