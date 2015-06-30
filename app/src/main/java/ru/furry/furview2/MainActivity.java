package ru.furry.furview2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.furry.furview2.UI.DataImageView;
import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.drivers.e621.DriverE621;
import ru.furry.furview2.drivers.e621.RemoteFurImageE621;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.AsyncDatabaseResponseHandlerGUI;
import ru.furry.furview2.system.AsyncRemoteImageHandlerGUI;
import ru.furry.furview2.system.ProxySettings;
import ru.furry.furview2.system.Utils;



public class MainActivity extends Activity implements View.OnClickListener, AsyncRemoteImageHandlerGUI, AsyncDatabaseResponseHandlerGUI {

    EditText mSearchField;
    ImageButton mSearchButton;
    DataImageView mImageView1, mImageView2, mImageView3, mImageView4;
    List<DataImageView> imageViews;
    String mSearchQuery;
    String mProxy="";
    View.OnTouchListener mOnTouchImageViewListener;
    View.OnTouchListener mOnSearchButtonListener;
    GlobalData appPath;
    List<ImageViewAware> imageViewListeners;
    List<RemoteFurImageE621> remoteImagesE621 = new ArrayList<>();
    List<FurImage> downloadedImages = new ArrayList<>();
    DriverE621 driver;
    FurryDatabase database;
    int mPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = new FurryDatabase(this, this.getApplicationContext());

        try {
            Log.d("fgsfds", database.getDbHelper().isReady().toString()); // blocking
        } catch (ExecutionException | InterruptedException e) {
            Utils.printError(e);
        }

        /*
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        */

        setContentView(R.layout.activity_main);

        JodaTimeAndroid.init(this);

        mSearchField = (EditText)findViewById(R.id.SearchField);
        mSearchButton = (ImageButton)findViewById(R.id.SearchButton);

        mSearchField.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);

        mSearchQuery = getIntent().getExtras().getString("SearchQuery");
        mProxy = getIntent().getExtras().getString("mProxy");
        mPort = getIntent().getExtras().getInt("mPort");

        mSearchField.setText(mSearchQuery);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_to_mainscreen), Toast.LENGTH_SHORT).show();

        mImageView1 = (DataImageView) findViewById(R.id.imageView1);
        mImageView2 = (DataImageView) findViewById(R.id.imageView2);
        mImageView3 = (DataImageView) findViewById(R.id.imageView3);
        mImageView4 = (DataImageView) findViewById(R.id.imageView4);

        imageViews= new ArrayList<DataImageView>(Arrays.asList(
                mImageView1,
                mImageView2,
                mImageView3,
                mImageView4
        ));

        imageViewListeners= new ArrayList<ImageViewAware>(Arrays.asList(
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

        driver = new DriverE621(permanentStorage, 150, 150, this);
        if (mProxy.equals("")) {
            Log.d("fgsfds", "Proxy is not using");
        } else {
            //Proxy proxy = ProxySettings.getLastProxy();
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(mProxy, mPort));
            Log.d("fgsfds", "Set proxy "+mProxy+" port "+mPort);
            driver.setProxy(proxy);
        }

        mOnTouchImageViewListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    String str = getResources().getString(R.string.toast_text)+" webView1"+getResources().getString(R.string.toast_to_fullscreen);
                    Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent("ru.furry.furview2.fullscreen");
                    intent.putExtra("imageUrl", ((DataImageView)v).getImage().getFileUrl());
                    startActivity(intent);
                }
                return false;
            }
        };

        mOnSearchButtonListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        searchE926(mSearchField.getText().toString());
                    }
                return false;
                }
        };

        mImageView1.setOnTouchListener(mOnTouchImageViewListener);
        mImageView2.setOnTouchListener(mOnTouchImageViewListener);
        mImageView3.setOnTouchListener(mOnTouchImageViewListener);
        mImageView4.setOnTouchListener(mOnTouchImageViewListener);

        mSearchButton.setOnTouchListener(mOnSearchButtonListener);





        searchE926(mSearchField.getText().toString());
    }

    private void searchE926(String query) {
        Log.d("fgsfds", "Search: " + query);
        try {
            driver.search(query);
        } catch (IOException e) {
            Utils.printError(e);
        }
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
        }

    }

    @Override
    public void blockInterfaceForRemoteImages() {

    }

    @Override
    public void unblockInterfaceForRemoteImages() {

    }

    @Override
    public void retrieveRemoteImages(List<? extends RemoteFurImage> images) {
        Log.d("fgsfds", "Recieved remote pictures");
        List<RemoteFurImageE621> e621Images = (List<RemoteFurImageE621>)images;
        remoteImagesE621.addAll(e621Images);
        try {
            //downloadedImages.addAll(driver.download(e621Images.subList(0,4), imageViewListeners));
            int i = 0;
            for (FurImage img : driver.downloadPreview(e621Images.subList(0,4), imageViewListeners)) {
                downloadedImages.add(img);
                imageViews.get(i++).setImage(img);
            }
        } catch (IOException e) {
            Utils.printError(e);
        }
    }

    @Override
    public void blockInterfaceForDBResponse() {

    }

    @Override
    public void unblockInterfaceForDBResponse() {

    }

    @Override
    public void retrieveDBResponse(List<FurImage> images) {

    }
}
