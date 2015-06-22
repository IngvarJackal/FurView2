package ru.furry.furview2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.drivers.e926.DriverE926;
import ru.furry.furview2.drivers.e926.RemoteFurImageE926;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.system.ProxySettings;
import ru.furry.furview2.system.Utils;



public class MainActivity extends Activity implements View.OnClickListener{

    EditText mSearchField;
    ImageButton mSearchButton;
    ImageView mImageView1, mImageView2, mImageView3, mImageView4;
    String mSearchQuery;
    String mProxy;
    View.OnTouchListener mOnTouchImageViewListener;
    View.OnTouchListener mOnSearchButtonListener;
    GlobalData appPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d("fgsfds", FurryDatabase.getDbHelper().isReady().toString());
        } catch (ExecutionException | InterruptedException e) {
            Utils.printError(e);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        JodaTimeAndroid.init(this);

        mSearchField = (EditText)findViewById(R.id.SearchField);
        mSearchButton = (ImageButton)findViewById(R.id.SearchButton);

        mSearchField.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);

        mSearchQuery = getIntent().getExtras().getString("SearchQuery");
        mProxy = getIntent().getExtras().getString("Proxy");

        mSearchField.setText(mSearchQuery);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_to_mainscreen), Toast.LENGTH_SHORT).show();

        mImageView1 = (ImageView) findViewById(R.id.imageView1);
        mImageView2 = (ImageView) findViewById(R.id.imageView2);
        mImageView3 = (ImageView) findViewById(R.id.imageView3);
        mImageView4 = (ImageView) findViewById(R.id.imageView4);

        final DriverE926 driver;
        final List<ImageViewAware> imageViewListeners = new ArrayList<ImageViewAware>(Arrays.asList(
                new ImageViewAware(mImageView1),
                new ImageViewAware(mImageView2),
                new ImageViewAware(mImageView3),
                new ImageViewAware(mImageView4)
        ));

        appPath = ((GlobalData)getApplicationContext());
        appPath.setState("/mnt/sdcard/furview2");   //Example path

        String permanentStorage = getApplicationContext().getExternalFilesDir(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .getAbsolutePath();

        driver = new DriverE926(permanentStorage, 150, 150);
        if (mProxy != null) {
            Proxy proxy = ProxySettings.getLastProxy();
            Log.d("fgsfds", "Use proxy: " + mProxy);
            driver.setProxy(proxy);
        }

        searchE926(driver, mSearchField.getText().toString(), imageViewListeners);

        mOnTouchImageViewListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    String str = getResources().getString(R.string.toast_text)+" webView1"+getResources().getString(R.string.toast_to_fullscreen);
                    Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent("ru.furry.furview2.fullscreen");
                    Bitmap bitmap = ((BitmapDrawable)((ImageView)v).getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();
                    intent.putExtra("image", bitmapdata);
                    startActivity(intent);
                }
                return false;
            }
        };

        mOnSearchButtonListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        searchE926(driver, mSearchField.getText().toString(), imageViewListeners);
                    }
                return false;
                }
        };

        mImageView1.setOnTouchListener(mOnTouchImageViewListener);
        mImageView2.setOnTouchListener(mOnTouchImageViewListener);
        mImageView3.setOnTouchListener(mOnTouchImageViewListener);
        mImageView4.setOnTouchListener(mOnTouchImageViewListener);

        mSearchButton.setOnTouchListener(mOnSearchButtonListener);

    }

    private List<FurImage> searchE926(DriverE926 driver, String query, List<ImageViewAware> listeners) {
        List<FurImage> result = null;
        try {
            Log.d("fgsfds", "Searching " + query);
            Iterator<RemoteFurImageE926> posts = driver.search(query);
            List<RemoteFurImageE926> images = new ArrayList<>();

            for (int i = 0; i < 4 && posts.hasNext(); i++) {
                images.add(posts.next());
            }

            result = driver.download(images, listeners);

            for (FurImage image : result) {
                Log.d("fgsfds", image.toString());
            }

            FurryDatabase.create(result.get(0));
            Log.d("fgsfds", "------------------------------------------------------------");
            Log.d("fgsfds", FurryDatabase.searchByMD5(result.get(0).getMd5()).get().toString());

        } catch (Exception e) {
            Utils.printError(e);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_to_OnDestroy), Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " action_settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.SearchField:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" SearchField",Toast.LENGTH_SHORT).show();
                break;
            /**
            case R.id.SearchButton:
                 Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " SearchButton", Toast.LENGTH_SHORT).show();
                 break;
            case R.id.webView1:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView1",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("comingvarjackalfurview2.github.furview2.fullscreen");
                startActivity(intent);
                break;
            case R.id.webView2:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView2",Toast.LENGTH_SHORT).show();
                break;
            case R.id.webView3:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView3",Toast.LENGTH_SHORT).show();
                break;
            case R.id.webView4:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView4",Toast.LENGTH_SHORT).show();
                break;
             **/
        }

    }


}
