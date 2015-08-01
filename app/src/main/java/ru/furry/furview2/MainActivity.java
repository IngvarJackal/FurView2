package ru.furry.furview2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ru.furry.furview2.UI.DataImageView;
import ru.furry.furview2.UI.OnSwipeAncClickTouchListener;
import ru.furry.furview2.drivers.Driver;
import ru.furry.furview2.drivers.Drivers;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.AsyncCounter;
import ru.furry.furview2.system.AsyncHandlerUI;
import ru.furry.furview2.system.ListlikeIterator;
import ru.furry.furview2.system.Utils;


public class MainActivity extends AppCompatActivity {

    public static final int NUM_OF_PICS = 4;
    public static String permanentStorage;
    public static String searchQuery = "";
    private static String previousQuery = null;
    public static boolean swf = false;
    public static ListlikeIterator<RemoteFurImage> remoteImagesIterator;
    public static List<FurImage> downloadedImages = new ArrayList<>();
    public List<FurImage> currtenlyDownloadedImages = (List<FurImage>) Utils.createAndFillList(NUM_OF_PICS, null);
    public static int cursor = -1;
    public static AtomicInteger shownImages = new AtomicInteger(0);

    private AsyncHandlerUI<Boolean> remoteImagesIteratorHandler = new AsyncHandlerUI<Boolean>() {
        @Override
        public void blockUI() {

        }

        @Override
        public void unblockUI() {

        }

        @Override
        public void retrieve(List<? extends Boolean> result) {
            if (result.get(0)) {
                if (procCounter.getVal() < NUM_OF_PICS) {
                    Log.d("fgsfds", "Recieved remote picture №" + procCounter.getVal());
                    setPicture(procCounter.getVal(), remoteImagesIterator.next());
                    procCounter.increase();
                    remoteImagesIterator.asyncLoad(this);
                } else
                    procCounter.reset();
            } else {
                int i = procCounter.getVal();
                while (i < NUM_OF_PICS) {
                    clearImage(i++);
                }
            }
        }
    };

    class RemoteImagesIterator implements ListlikeIterator<RemoteFurImage> {
        List<RemoteFurImage> downloadedRemoteImages = new ArrayList<>();
        String searchQuery;
        Driver driver;

        @Override
        public void init(Driver driver, String searchQuery) {
            this.searchQuery = searchQuery;
            this.driver = driver;
        }

        @Override
        public void asyncLoad(AsyncHandlerUI<Boolean> remoteImagesHandler) {
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
            if (downloadedRemoteImages.size()-1 > cursor) {
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
            return downloadedRemoteImages.get(++cursor);
        }

        @Override
        public RemoteFurImage previous() {
            return downloadedRemoteImages.get(--cursor);
        }
    }

    EditText mSearchField;
    ImageButton mSearchButton;
    DataImageView mImageView1, mImageView2, mImageView3, mImageView4;
    List<ImageViewAware> imageViewListeners;
    List<DataImageView> imageViews;
    ToggleButton sfwButton;
    LinearLayout picturesLayout;

    View.OnTouchListener mImageButtonSwitchListener;
    View.OnClickListener mImageButtonClickListener;
    View.OnClickListener mOnSearchButtonListener;

    Driver driver;
    Drivers driverEnum;

    private AsyncCounter procCounter = new AsyncCounter(0, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        JodaTimeAndroid.init(this);

        Collections.fill(currtenlyDownloadedImages, null);

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

        picturesLayout = (LinearLayout) findViewById(R.id.picturesLayout);

        mSearchField = (EditText) findViewById(R.id.searchField);
        mSearchButton = (ImageButton) findViewById(R.id.searchButton);

        mSearchField.setText(searchQuery);

        mImageView1 = (DataImageView) findViewById(R.id.imageView1);
        mImageView2 = (DataImageView) findViewById(R.id.imageView2);
        mImageView3 = (DataImageView) findViewById(R.id.imageView3);
        mImageView4 = (DataImageView) findViewById(R.id.imageView4);

        imageViews = new ArrayList<>(Arrays.asList(
                mImageView1,
                mImageView2,
                mImageView3,
                mImageView4
        ));
        imageViewListeners = new ArrayList<>(Arrays.asList(
                new ImageViewAware(mImageView1),
                new ImageViewAware(mImageView2),
                new ImageViewAware(mImageView3),
                new ImageViewAware(mImageView4)
        ));

        mImageButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (FurImage image: currtenlyDownloadedImages) {
                    if (image != null) {
                        downloadedImages.add(image);
                    }
                }
                Log.d("fgsfds", downloadedImages.toString());
                Intent intent = new Intent("ru.furry.furview2.fullscreen");
                intent.putExtra("imageIndex", ((DataImageView) view).getIndex());
                intent.putExtra("driver", getIntent().getStringExtra("driver"));
                startActivity(intent);
            }
        };

        mImageView1.setOnClickListener(mImageButtonClickListener);
        mImageView2.setOnClickListener(mImageButtonClickListener);
        mImageView3.setOnClickListener(mImageButtonClickListener);
        mImageView4.setOnClickListener(mImageButtonClickListener);

        mImageButtonSwitchListener = new OnSwipeAncClickTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                procCounter.reset();
                shownImages.set(0);
                remoteImagesIterator.asyncLoad(remoteImagesIteratorHandler);
            }

            @Override
            public void onSwipeRight() {

                if (procCounter.getVal() != 0)
                    cursor = Math.max(-1, cursor - (shownImages.get()) * 2 - 2);
                else
                    cursor = Math.max(-1, cursor - (shownImages.get()));
                procCounter.reset();
                remoteImagesIterator.asyncLoad(remoteImagesIteratorHandler);
            }
        };

        mImageView1.setOnTouchListener(mImageButtonSwitchListener);
        mImageView2.setOnTouchListener(mImageButtonSwitchListener);
        mImageView3.setOnTouchListener(mImageButtonSwitchListener);
        mImageView4.setOnTouchListener(mImageButtonSwitchListener);

        Log.d("fgsfds", "storage: " + permanentStorage);

        driverEnum = Drivers.getDriver(getIntent().getStringExtra("driver"));
        try {
            driver = driverEnum.driverclass.newInstance();
        } catch (Exception e) {
            Utils.printError(e);
        }
        driver.init(permanentStorage, getApplicationContext());

        mOnSearchButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchQuery = mSearchField.getText().toString();
                hideSoftKeyboard(MainActivity.this);
                searchDriver();
            }
        };

        mSearchButton.setOnClickListener(mOnSearchButtonListener);

        searchDriver();
    }

    private void searchDriver() {
        Log.d("fgsfds", "Search: " + searchQuery);
        procCounter.reset();
        cursor = -1;
        shownImages.set(0);
        previousQuery = searchQuery;
        mSearchField.setText(searchQuery);
        driver.setSfw(MainActivity.swf);
        downloadedImages = new ArrayList<>();
        remoteImagesIterator = new RemoteImagesIterator();
        remoteImagesIterator.init(driver, searchQuery);
        for (int i = 0; i < NUM_OF_PICS; i++) {
            clearImage(i);
        }
        remoteImagesIterator.asyncLoad(remoteImagesIteratorHandler);
    }

    private void setPicture(final int imageButtonIndex, RemoteFurImage image) {
        final int imageIndex = cursor;
        Log.d("fgsfds", "Setting image " + cursor + " to place " + imageButtonIndex);
        shownImages.incrementAndGet();
        ArrayList<RemoteFurImage> fImage = new ArrayList<>(Arrays.asList(image));
        driver.downloadPreviewFile(fImage,
                new ArrayList<>(Arrays.asList(imageViewListeners.get(imageButtonIndex))),
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
        if (cursor + NUM_OF_PICS*2 >= downloadedImages.size()) {
            driver.downloadFurImage(fImage, new ArrayList<AsyncHandlerUI<FurImage>>(Arrays.asList(new AsyncHandlerUI<FurImage>() {
                @Override
                public void blockUI() {
                }

                @Override
                public void unblockUI() {

                }

                @Override
                public void retrieve(List<? extends FurImage> images) {
                    currtenlyDownloadedImages.set(imageButtonIndex, images.get(0));
                    if (!currtenlyDownloadedImages.contains(null)) {
                        for (FurImage img : currtenlyDownloadedImages) {
                            if (!downloadedImages.contains(img)) {
                                downloadedImages.add(img);
                            }
                        }
                        currtenlyDownloadedImages = (List<FurImage>) Utils.createAndFillList(NUM_OF_PICS, null);
                    }
                    imageViews.get(imageButtonIndex).setImageIndex(imageIndex);
                }
            })));
        } else {
            imageViews.get(imageButtonIndex).setImageIndex(imageIndex);
        }
    }

    private void clearImage(int index) {
        Log.d("fgsfds", "Clearing image " + index);
        imageViews.get(index).setImageResource(android.R.color.transparent);
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void redrawImages() {
        int i = 0;
        while (i < NUM_OF_PICS && remoteImagesIterator.hasPrevious()) {
            i++;
            remoteImagesIterator.previous();
        }
        Log.d("fgsfds", "redraw index = " + i);
        if (i == NUM_OF_PICS) {
            remoteImagesIterator.asyncLoad(remoteImagesIteratorHandler);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("fgsfds", "Cursor: " + cursor);
        if (!MainActivity.searchQuery.equals(MainActivity.previousQuery)) {
            searchDriver();
        } else {
            redrawImages();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("fgsfds", "Clearing cache.");
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

        switch (id) {
            case (R.id.action_searchelp): {
                Intent intent = new Intent("ru.furry.furview2.HelpScreen");
                intent.putExtra("helptextId", driverEnum.searchHelpId);
                startActivity(intent);
                return true;
            }
            case (R.id.action_downloading): {
                Intent intent = new Intent("ru.furry.furview2.DownloadingActivity");
                intent.putExtra("drivername", driverEnum.drivername);
                startActivity(intent);
                return true;
            }
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
