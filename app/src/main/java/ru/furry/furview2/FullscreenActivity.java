package ru.furry.furview2;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.furry.furview2.UI.OnSwipeAncClickTouchListener;
import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.drivers.Driver;
import ru.furry.furview2.drivers.Drivers;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.system.AsyncHandlerUI;
import ru.furry.furview2.system.DefaultCreator;
import ru.furry.furview2.system.ExtendableWDef;
import ru.furry.furview2.system.Utils;


public class FullscreenActivity extends AppCompatActivity {

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final int LEN_OF_TAGS_ROW = 5;

    ImageView mPictureImageView;
    ScrollView mScrollVew;
    TableLayout mTagsTable;
    Button mTagsButton;
    ProgressBar mProgress;
    ImageButton mRatingImageButton;
    EditText mScoreEditText;
    EditText mArtistEditText;
    EditText mDateEditText;
    EditText mTagsEditText;
    ImageButton mSearchButton;
    ImageButton mSaveButton;
    ProgressBar mSaveButtonProgress;
    FurryDatabase database;
    FurImage fImage;
    Driver driver;
    Drivers driverEnum;
    int fIndex;

    private AsyncHandlerUI<Boolean> remoteImagesIteratorHandler = new AsyncHandlerUI<Boolean>() {
        @Override
        public void blockUI() {

        }

        @Override
        public void unblockUI() {

        }

        @Override
        public void retrieve(List<? extends Boolean> result) {
            Log.d("fgsfds", result.toString());
            if (result.get(0)) {
                driver.downloadFurImage(new ArrayList<>(Arrays.asList(MainActivity.remoteImagesIterator.next())),
                        new ArrayList<AsyncHandlerUI<FurImage>>(Arrays.asList(new AsyncHandlerUI<FurImage>() {
                            @Override
                            public void blockUI() {

                            }

                            @Override
                            public void unblockUI() {

                            }

                            @Override
                            public void retrieve(List<? extends FurImage> images) {
                                if (MainActivity.downloadedImages.size() == fIndex)
                                    MainActivity.downloadedImages.addAll(images);
                                Intent intent = new Intent("ru.furry.furview2.fullscreen");
                                intent.putExtra("imageIndex", fIndex);
                                intent.putExtra("driver", getIntent().getStringExtra("driver"));
                                startActivity(intent);
                                finish();
                            }
                        })));
            }
        }
    };

    class Labelled6Row {
        public List<TextView> items = new ArrayList<>();

        public Labelled6Row(TableLayout table, Context context) {
            LinearLayout linLay = new LinearLayout(context);
            linLay.setOrientation(LinearLayout.HORIZONTAL);
            linLay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1));
            for (int i = 0; i < LEN_OF_TAGS_ROW; i++) {
                TextView t = new TextView(context);
                t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                t.setGravity(Gravity.CENTER);
                t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                int padding = (int) (1.5 * getResources().getDisplayMetrics().density + 0.5f);
                t.setPadding(padding, 0, padding, 0);
                items.add(t);
                linLay.addView(t);
            }
            row.addView(linLay);
            table.addView(row);
        }
    }

    class Labelled6RowCreator extends DefaultCreator<Labelled6Row> {
        @Override
        public Labelled6Row getDefaultValue(Object... params) {
            return new Labelled6Row((TableLayout) params[0], (Context) params[1]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        Log.d("fgsfds", "Fulscreen cur. cursor = " + MainActivity.cursor);

        mPictureImageView = (ImageView) findViewById(R.id.picImgView);
        mScrollVew = (ScrollView) findViewById(R.id.scrollView);
        mTagsTable = (TableLayout) findViewById(R.id.tagsTableLayout);
        mTagsButton = (Button) findViewById(R.id.tagsButton);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mRatingImageButton = (ImageButton) findViewById(R.id.ratingImageButton);
        mScoreEditText = (EditText) findViewById(R.id.scoreEditText);
        mArtistEditText = (EditText) findViewById(R.id.artistEditText);
        mDateEditText = (EditText) findViewById(R.id.dateEditText);
        mTagsEditText = (EditText) findViewById(R.id.tagsEditText);
        mSearchButton = (ImageButton) findViewById(R.id.searchImageButton);
        mSaveButton = (ImageButton) findViewById(R.id.saveButton);
        mSaveButton.setEnabled(false);
        mSaveButtonProgress = (ProgressBar) findViewById(R.id.saveImageButtonProgressBar);

        fIndex = getIntent().getIntExtra("imageIndex", 0);
        fImage = MainActivity.downloadedImages.get(fIndex);
        Log.d("fgsfds", fImage.toString());
        driverEnum = Drivers.getDriver(getIntent().getStringExtra("driver"));
        try {
            driver = driverEnum.driverclass.newInstance();
        } catch (Exception e) {
            Utils.printError(e);
        }
        driver.setSfw(MainActivity.swf);
        driver.init(MainActivity.permanentStorage, getApplicationContext());

        database = new FurryDatabase(getApplicationContext());

        Log.d("fgsfds database", database.getTableAsString("images"));

        ExtendableWDef<Labelled6Row> tagsLinesHandler = new ExtendableWDef<Labelled6Row>(new Labelled6RowCreator()) {
            @Override
            public void ensureCapacity(int index) {
                if (entries.size() <= index) {
                    for (int i = 0; i < ((index - entries.size()) * 3 / 2 + 1); i++) {
                        entries.add(creator.getDefaultValue(mTagsTable, getApplicationContext()));
                    }
                }
            }
        };

        for (int row = 0; row < Math.ceil(fImage.getTags().size() * 1.0 / LEN_OF_TAGS_ROW); row++) {
            for (int column = 0; (column < LEN_OF_TAGS_ROW) && (row * LEN_OF_TAGS_ROW + column < fImage.getTags().size()); column++) {
                tagsLinesHandler.get(row).items.get(column).setText(Utils.unescapeUnicode(fImage.getTags().get(row * LEN_OF_TAGS_ROW + column)));
            }
        }

        switch (fImage.getRating()) {
            case SAFE:
                mRatingImageButton.setBackgroundColor(0xCC31c128);
                break;
            case QUESTIONABLE:
                mRatingImageButton.setBackgroundColor(0xCCe07b0a);
                break;
            case EXPLICIT:
                mRatingImageButton.setBackgroundColor(0xCCe01d0a);
                break;
            case NA:
                mRatingImageButton.setBackgroundColor(0xCCa2b6b5);
                break;
        }

        mTagsEditText.setText(MainActivity.searchQuery);
        mScoreEditText.setText(Integer.toString(fImage.getScore()));
        mScoreEditText.setText(Integer.toString(fImage.getScore()));
        mScoreEditText.setText(Integer.toString(fImage.getScore()));
        mArtistEditText.setText(Utils.unescapeUnicode(Utils.joinList(fImage.getArtists(), ", ")));
        mDateEditText.setText(DATETIME_FORMAT.print(fImage.getDownloadedAt()));

        mTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTagsTable.isShown()) {
                    mTagsTable.setVisibility(View.GONE);
                } else {
                    mTagsTable.setVisibility(View.VISIBLE);
                }
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.searchQuery = mTagsEditText.getText().toString();
                finish();
            }
        });


        driver.downloadImageFile(fImage, new ImageViewAware(mPictureImageView), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                imageLoaded();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageLoaded();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                imageLoaded();
            }
        });

        mPictureImageView.setOnTouchListener(new OnSwipeAncClickTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                fIndex += 1;
                MainActivity.remoteImagesIterator.asyncLoad(remoteImagesIteratorHandler);
            }

            @Override
            public void onSwipeRight() {
                boolean needLoadNext = fIndex > 0;
                if (needLoadNext) {
                    MainActivity.cursor = fIndex - 1;
                    fIndex = MainActivity.cursor;
                    Log.d("fgsfds", "MainActivity.cursor " + MainActivity.cursor + " fIndex " + fIndex);
                    MainActivity.remoteImagesIterator.asyncLoad(remoteImagesIteratorHandler);
                }
            }
        });
    }

    private void enableDeleteMode() {
        mSaveButton.setImageResource(android.R.drawable.ic_menu_delete);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driver.deleteFromDBandStorage(fImage, database);
                enableDownloadMode();
            }
        });
    }

    private void enableDownloadMode() {
        mSaveButton.setImageResource(android.R.drawable.ic_menu_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driver.saveToDBandStorage(fImage, database);
                Log.d("fgsfds", fImage.toString());
                enableDeleteMode();
            }
        });
    }

    private void imageLoaded() {
        mProgress.setVisibility(View.GONE);
        database.searchByMD5(fImage.getMd5(), new AsyncHandlerUI<FurImage>() {
            @Override
            public void blockUI() {

            }

            @Override
            public void unblockUI() {
                mSaveButtonProgress.setVisibility(View.GONE);
                mSaveButton.setEnabled(true);
            }

            @Override
            public void retrieve(List<? extends FurImage> images) {
                if (images.size() > 0) {
                    enableDeleteMode();
                } else {
                    enableDownloadMode();
                }
            }
        });
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

        switch (id) {
            case (R.id.action_searchelp): {
                Intent intent = new Intent("ru.furry.furview2.HelpScreen");
                intent.putExtra("helptextId", driverEnum.searchHelpId);
                startActivity(intent);
                return true;
            }
            case R.id.action_settings:
                return true;
            case R.id.action_save:
                return true;
            case R.id.action_tags:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


}
