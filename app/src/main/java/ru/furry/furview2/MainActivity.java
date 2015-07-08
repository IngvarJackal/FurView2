package ru.furry.furview2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.furry.furview2.UI.DataImageView;
import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.drivers.Driver;
import ru.furry.furview2.drivers.Drivers;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.AsyncCounter;
import ru.furry.furview2.system.AsyncHandlerUI;
import ru.furry.furview2.system.ListlikeIterator;
import ru.furry.furview2.system.Utils;


public class MainActivity extends Activity implements View.OnClickListener {

    public static String searchQuery = "";
    private static String previousQuery = null;
    public static boolean swf = false;
    public static ListlikeIterator<RemoteFurImage> remoteImagesIterator;

    private final static int NUM_OF_PICS = 4;

    class RemoteImagesIterator implements ListlikeIterator<RemoteFurImage> {
        List<RemoteFurImage> downloadedRemoteImages = new ArrayList<>();
        String searchQuery;
        Driver driver;
        int cursor = 0;

        @Override
        public void init(Driver driver, String searchQuery) {
            this.searchQuery = searchQuery;
            this.driver = driver;
        }

        @Override
        public void asyncHasNext(AsyncHandlerUI<Boolean> remoteImagesHandler) {
            if (downloadedRemoteImages.size() == 0) {
                final AsyncHandlerUI<Boolean> remoteImagesHandler2 = remoteImagesHandler;
                driver.search(searchQuery, new AsyncHandlerUI<RemoteFurImage>() {
                    @Override
                    public void blockUI() {
                        remoteImagesHandler2.blockUI();
                    }

                    @Override
                    public void unblockUI() {
                        remoteImagesHandler2.unblockUI();
                    }

                    @Override
                    public void retrieve(List<? extends RemoteFurImage> images) {
                        downloadedRemoteImages.addAll(images);
                        check(remoteImagesHandler2);
                    }
                });
            } else {
                check(remoteImagesHandler);
            }
        }

        private void check(AsyncHandlerUI<Boolean> remoteImagesHandler) {
            if (downloadedRemoteImages.size() > cursor) {
                remoteImagesHandler.retrieve(new ArrayList<>(Arrays.asList(true)));
            } else {
                if (driver.hasNext()) {
                    downloadImages(remoteImagesHandler);
                } else {
                    remoteImagesHandler.retrieve(new ArrayList<>(Arrays.asList(false)));
                }
            }
        }

        private void downloadImages(AsyncHandlerUI<Boolean> remoteImagesHandler) {
            final AsyncHandlerUI<Boolean> remoteImagesHandler2 = remoteImagesHandler;
            driver.getNext(new AsyncHandlerUI<RemoteFurImage>() {

                @Override
                public void blockUI() {
                    remoteImagesHandler2.blockUI();
                }

                @Override
                public void unblockUI() {
                    remoteImagesHandler2.unblockUI();
                }

                @Override
                public void retrieve(List<? extends RemoteFurImage> images) {
                    downloadedRemoteImages.addAll(images);
                    remoteImagesHandler2.retrieve(new ArrayList<>(Arrays.asList(driver.hasNext())));
                }
            });
        }

        @Override
        public boolean hasPrevious() {
            return (cursor > 0);
        }

        @Override
        public RemoteFurImage next() {
            return downloadedRemoteImages.get(cursor++);
        }

        @Override
        public RemoteFurImage previous() {
            return downloadedRemoteImages.get(cursor--);
        }
    }

    EditText mSearchField;
    ImageButton mSearchButton;
    DataImageView mImageView1, mImageView2, mImageView3, mImageView4;
    List<ImageViewAware> imageViewListeners;
    List<DataImageView> imageViews;
    ToggleButton sfwButton;

    View.OnTouchListener mOnTouchImageViewListener;
    View.OnTouchListener mOnSearchButtonListener;

    GlobalData appPath;
    Driver driver;
    FurryDatabase database;
    AsyncHandlerUI<FurImage> databaseHandler;

    private AsyncCounter procCounter = new AsyncCounter(0, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHandler = new AsyncHandlerUI<FurImage>() {
            @Override
            public void blockUI() {

            }

            @Override
            public void unblockUI() {

            }

            @Override
            public void retrieve(List<? extends FurImage> images) {

            }
        };

        setContentView(R.layout.activity_main);

        JodaTimeAndroid.init(this);

        sfwButton = (ToggleButton) findViewById(R.id.sfwButton);
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

        mSearchField = (EditText) findViewById(R.id.searchField);
        mSearchButton = (ImageButton) findViewById(R.id.searchButton);

        mSearchField.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);

        mSearchField.setText(searchQuery);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_to_mainscreen), Toast.LENGTH_SHORT).show();

        mImageView1 = (DataImageView) findViewById(R.id.imageView1);
        mImageView2 = (DataImageView) findViewById(R.id.imageView2);
        mImageView3 = (DataImageView) findViewById(R.id.imageView3);
        mImageView4 = (DataImageView) findViewById(R.id.imageView4);

        imageViews = new ArrayList<DataImageView>(Arrays.asList(
                mImageView1,
                mImageView2,
                mImageView3,
                mImageView4
        ));

        imageViewListeners = new ArrayList<ImageViewAware>(Arrays.asList(
                new ImageViewAware(mImageView1),
                new ImageViewAware(mImageView2),
                new ImageViewAware(mImageView3),
                new ImageViewAware(mImageView4)
        ));

        appPath = ((GlobalData) getApplicationContext());
        appPath.setState("/mnt/sdcard/furview2");   //Example path

        String permanentStorage = getApplicationContext().getExternalFilesDir(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .getAbsolutePath();

        try {
            driver = Drivers.drivers.get(getIntent().getStringExtra("driver")).newInstance();
        } catch (Exception e) {
            Utils.printError(e);
        }
        driver.init(permanentStorage);

        database = new FurryDatabase(databaseHandler, this.getApplicationContext());

        try {
            Log.d("fgsfds", database.getDbHelper().isReady().toString()); // blocking
        } catch (ExecutionException | InterruptedException e) {
            Utils.printError(e);
        }

        mOnTouchImageViewListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    String str = getResources().getString(R.string.toast_text) + " webView1" + getResources().getString(R.string.toast_to_fullscreen);
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
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
        remoteImagesIterator = new RemoteImagesIterator();
        remoteImagesIterator.init(driver, searchQuery);
        remoteImagesIterator.asyncHasNext(new AsyncHandlerUI<Boolean>() {
            @Override
            public void blockUI() {

            }

            @Override
            public void unblockUI() {

            }

            @Override
            public void retrieve(List<? extends Boolean> result) {
                Log.d("fgsfds", "Recieved remote pictures; " + procCounter.getVal());
                if (result.get(0)) {
                    if (procCounter.getVal() < NUM_OF_PICS) {
                        final int index = procCounter.getVal();
                        ArrayList<RemoteFurImage> fImage = new ArrayList<>(Arrays.asList(remoteImagesIterator.next()));
                        driver.downloadPreviewFile(fImage,
                                new ArrayList<>(Arrays.asList(imageViewListeners.get(index))),
                                new ArrayList<ImageLoadingListener>(Arrays.asList(new ImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String imageUri, View view) {

                                    }

                                    @Override
                                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                                    }

                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    }

                                    @Override
                                    public void onLoadingCancelled(String imageUri, View view) {

                                    }
                                })));
                        driver.downloadFurImage(fImage, new ArrayList<AsyncHandlerUI<FurImage>>(Arrays.asList(new AsyncHandlerUI<FurImage>() {
                            @Override
                            public void blockUI() {
                            }

                            @Override
                            public void unblockUI() {

                            }

                            @Override
                            public void retrieve(List<? extends FurImage> images) {
                                imageViews.get(index).setImage(images.get(0));
                            }
                        })));
                        procCounter.increase();
                    }
                    if (procCounter.getVal() >= NUM_OF_PICS) {
                        procCounter.reset();
                    } else {
                        remoteImagesIterator.asyncHasNext(this);
                    }
                }
            }
        });
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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " SearchField", Toast.LENGTH_SHORT).show();
                break;
        }

    }

}
