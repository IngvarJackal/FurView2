package ru.furry.furview2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.furry.furview2.UI.DataImageView;
import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.drivers.Driver;
import ru.furry.furview2.drivers.Drivers;
import ru.furry.furview2.drivers.e621.RemoteFurImageE621;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.AsyncDatabaseResponseHandlerGUI;
import ru.furry.furview2.system.AsyncRemoteImageHandlerGUI;
import ru.furry.furview2.system.Utils;



public class MainActivity extends Activity implements View.OnClickListener  {

    public static String searchQuery = "";
    private static String previousQuery = null;
    public static boolean swf = false;

    EditText mSearchField;
    ImageButton mSearchButton;
    DataImageView mImageView1, mImageView2, mImageView3, mImageView4;
    ToggleButton sfwButton;
    List<DataImageView> imageViews;
    View.OnTouchListener mOnTouchImageViewListener;
    View.OnTouchListener mOnSearchButtonListener;
    GlobalData appPath;
    List<ImageViewAware> imageViewListeners;
    List<RemoteFurImageE621> remoteImagesE621 = new ArrayList<>();
    List<FurImage> downloadedImages = new ArrayList<>();
    Driver driver;
    FurryDatabase database;
    AsyncRemoteImageHandlerGUI remoteImageHandler;
    AsyncDatabaseResponseHandlerGUI databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHandler = new AsyncDatabaseResponseHandlerGUI() {
            @Override
            public void blockInterfaceForDBResponse() {

            }

            @Override
            public void unblockInterfaceForDBResponse() {

            }

            @Override
            public void retrieveDBResponse(List<FurImage> images) {

            }
        };

        remoteImageHandler = new AsyncRemoteImageHandlerGUI() {
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
                int i = 0;
                for (FurImage img : driver.downloadPreview(e621Images.subList(0, Math.min(4, e621Images.size())), imageViewListeners)) {
                    downloadedImages.add(img);
                    imageViews.get(i++).setImage(img);
                }
            }
        };

        setContentView(R.layout.activity_main);

        JodaTimeAndroid.init(this);

        sfwButton = (ToggleButton)findViewById(R.id.sfwButton);
        if (MainActivity.swf) {
            sfwButton.setBackgroundColor(0xff63ec4f);
            sfwButton.setChecked(true);
        } else {
            sfwButton.setBackgroundColor(0xccb3b3b3);
            sfwButton.setChecked(false);
        }

        sfwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.swf = !MainActivity.swf;
                if (sfwButton.isChecked())
                    sfwButton.setBackgroundColor(0xff63ec4f);
                else
                    sfwButton.setBackgroundColor(0xccb3b3b3);
            }
        });

        mSearchField = (EditText)findViewById(R.id.searchField);
        mSearchButton = (ImageButton)findViewById(R.id.searchButton);

        mSearchField.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);

        mSearchField.setText(searchQuery);
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

        String permanentStorage = getApplicationContext().getExternalFilesDir(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .getAbsolutePath();

        try {
            driver = Drivers.drivers.get(getIntent().getStringExtra("driver")).newInstance();
        } catch (Exception e) {
            Utils.printError(e);
        }
        driver.init(permanentStorage, remoteImageHandler);

        database = new FurryDatabase(databaseHandler, this.getApplicationContext());

        try {
            Log.d("fgsfds", database.getDbHelper().isReady().toString()); // blocking
        } catch (ExecutionException | InterruptedException e) {
            Utils.printError(e);
        }

        mOnTouchImageViewListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    String str = getResources().getString(R.string.toast_text)+" webView1"+getResources().getString(R.string.toast_to_fullscreen);
                    Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent("ru.furry.furview2.fullscreen");
                    intent.putExtra("image", ((DataImageView) v).getImage());
                    intent.putExtra("driver", getIntent().getStringExtra("driver"));
                    startActivity(intent);
                }
                return false;
            }
        };

        mOnSearchButtonListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        searchQuery = mSearchField.getText().toString();
                        searchDriver();
                    }
                return false;
                }
        };

        mImageView1.setOnTouchListener(mOnTouchImageViewListener);
        mImageView2.setOnTouchListener(mOnTouchImageViewListener);
        mImageView3.setOnTouchListener(mOnTouchImageViewListener);
        mImageView4.setOnTouchListener(mOnTouchImageViewListener);

        mSearchButton.setOnTouchListener(mOnSearchButtonListener);

        searchDriver();
    }

    @Override
    protected void onResume() {
        super.onRestart();
        if (!MainActivity.searchQuery.equals(MainActivity.previousQuery)) {
            searchDriver();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!MainActivity.searchQuery.equals(MainActivity.previousQuery)) {
            searchDriver();
        }
    }

    private void searchDriver() {
        Log.d("fgsfds", "Search: " + searchQuery);
        previousQuery = searchQuery;
        mSearchField.setText(searchQuery);
        driver.setSfw(MainActivity.swf);
        driver.search(searchQuery);
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
            case R.id.searchField:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" SearchField",Toast.LENGTH_SHORT).show();
                break;
        }

    }

}
