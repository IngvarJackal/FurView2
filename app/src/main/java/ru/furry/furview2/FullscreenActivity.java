package ru.furry.furview2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.imageaware.ViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.furry.furview2.UI.OnSwipeAncClickTouchListener;
import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.drivers.Driver;
import ru.furry.furview2.drivers.Drivers;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.system.AsyncHandlerUI;
import ru.furry.furview2.system.BlockUnblockUI;
import ru.furry.furview2.system.DefaultCreator;
import ru.furry.furview2.system.ExtendableWDef;
import ru.furry.furview2.system.Utils;


public class FullscreenActivity extends AppCompatActivity {

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final int LEN_OF_TAGS_ROW = 5;

    SubsamplingScaleImageView mPictureImageView;
    ScrollView mScrollVew;
    TableLayout mTagsTable;
    Button mTagsButton;
    Button mDescriptionButton;
    ProgressBar mProgress;
    ImageButton mRatingImageButton;
    EditText mScoreEditText;
    EditText mArtistEditText;
    EditText mDateEditText;
    EditText mTagsEditText;
    TextView mDescriptionText;
    ImageButton mSearchButton;
    ImageButton mButtonSaveDelInDB;
    ProgressBar mButtonSaveDelInDBProgress;
    ImageButton mFullscreenButton;
    ImageButton mFullscreenButton2;
    FurryDatabase database;
    FurImage fImage;
    Driver driver;
    Drivers driverEnum;
    int fIndex;
    RelativeLayout mRelativeLayout;
    BlockUnblockUI blocking;
    LinearLayout mLayoutSearchBar;
    LinearLayout mLayoutInfoBar;
    LinearLayout mLayoutTagBar;
    LinearLayout mLayoutFullscreenOut;

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_FULLSCREEN = "setFullscreen";
    private SharedPreferences mSettings;

    private boolean inFulscreenMode;

    class SubsamplingScaleImageViewAware implements ImageAware {

        public static final String WARN_CANT_SET_DRAWABLE = "Can't set a drawable into view. You should call ImageLoader on UI thread for it.";
        public static final String WARN_CANT_SET_BITMAP = "Can't set a bitmap into view. You should call ImageLoader on UI thread for it.";

        protected Reference<SubsamplingScaleImageView> viewRef;

        public SubsamplingScaleImageViewAware(SubsamplingScaleImageView view) {
            this(view, true);
        }

        public SubsamplingScaleImageViewAware(SubsamplingScaleImageView view, boolean checkActualViewSize) {
            this.viewRef = new WeakReference<>(view);
        }

        @Override
        public int getWidth() {
            SubsamplingScaleImageView view = viewRef.get();
            if (view != null) {
                return view.getWidth();
            }
            return 0;
        }

        @Override
        public int getHeight() {
            SubsamplingScaleImageView view = viewRef.get();
            if (view != null) {
                return view.getHeight();
            }
            return 0;
        }

        @Override
        public ViewScaleType getScaleType() {
            return ViewScaleType.CROP;
        }

        @Override
        public SubsamplingScaleImageView getWrappedView() {
            return viewRef.get();
        }

        @Override
        public boolean isCollected() {
            return viewRef.get() == null;
        }

        @Override
        public int getId() {
            SubsamplingScaleImageView view = viewRef.get();
            return view == null ? super.hashCode() : view.hashCode();
        }

        @Override
        public boolean setImageDrawable(Drawable drawable) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                SubsamplingScaleImageView view = viewRef.get();
                if (view != null) {
                    setImageDrawableInto(drawable, view);
                    return true;
                }
            } else {
                L.w(WARN_CANT_SET_DRAWABLE);
            }
            return false;
        }

        @Override
        public boolean setImageBitmap(Bitmap bitmap) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                SubsamplingScaleImageView view = viewRef.get();
                if (view != null) {
                    setImageBitmapInto(bitmap, view);
                    return true;
                }
            } else {
                L.w(WARN_CANT_SET_BITMAP);
            }
            return false;
        }

        protected void setImageDrawableInto(Drawable drawable, SubsamplingScaleImageView view) {
            view.setImage(ImageSource.bitmap(((BitmapDrawable) drawable).getBitmap()));
        }

        protected void setImageBitmapInto(Bitmap bitmap, SubsamplingScaleImageView view) {
            view.setImage(ImageSource.bitmap(bitmap));
        }
    }

    private AsyncHandlerUI<Boolean> remoteImagesIteratorHandler = new AsyncHandlerUI<Boolean>() {
        @Override
        public void blockUI() {
            blocking.blockUI();
        }

        @Override
        public void unblockUI() {
            blocking.unblockUI();
        }

        @Override
        public void retrieve(List<? extends Boolean> result) {
            if (result.get(0)) {
                driver.downloadFurImage(new ArrayList<>(Arrays.asList(MainActivity.remoteImagesIterator.next())),
                        new ArrayList<AsyncHandlerUI<FurImage>>(Arrays.asList(new AsyncHandlerUI<FurImage>() {
                            @Override
                            public void blockUI() {
                                blocking.blockUI();
                            }

                            @Override
                            public void unblockUI() {
                                blocking.unblockUI();
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
        if (!InitialScreen.isStarted) {
            Intent intent = new Intent("ru.furry.furview2.InitialScreen");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // won't work on 10 API
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_fullscreen);

            mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

            Log.d("fgsfds", "Fulscreen cur. cursor = " + MainActivity.cursor);

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            mPictureImageView = (SubsamplingScaleImageView) findViewById(R.id.picImgView);
            mScrollVew = (ScrollView) findViewById(R.id.scrollView);
            mTagsTable = (TableLayout) findViewById(R.id.tagsTableLayout);
            mTagsButton = (Button) findViewById(R.id.tagsButton);
            mDescriptionButton = (Button) findViewById(R.id.descriptionButton);
            mProgress = (ProgressBar) findViewById(R.id.progressBar);
            mRatingImageButton = (ImageButton) findViewById(R.id.ratingImageButton);
            mScoreEditText = (EditText) findViewById(R.id.scoreEditText);
            mArtistEditText = (EditText) findViewById(R.id.artistEditText);
            mDateEditText = (EditText) findViewById(R.id.dateEditText);
            mTagsEditText = (EditText) findViewById(R.id.tagsEditText);
            mSearchButton = (ImageButton) findViewById(R.id.searchImageButton);
            mButtonSaveDelInDB = (ImageButton) findViewById(R.id.buttonSaveDelInDB);
            mButtonSaveDelInDBProgress = (ProgressBar) findViewById(R.id.saveImageButtonProgressBar);
            mFullscreenButton = (ImageButton) findViewById(R.id.fullscreenButton);
            mFullscreenButton2 = (ImageButton) findViewById(R.id.fullscreenButton2);
            mRelativeLayout = (RelativeLayout) findViewById(R.id.fullscreenLayout);
            mDescriptionText = (TextView) findViewById(R.id.descriptionText);
            mLayoutSearchBar = (LinearLayout) findViewById(R.id.layoutSearchBar);
            mLayoutInfoBar = (LinearLayout) findViewById(R.id.layoutInfoBar);
            mLayoutTagBar = (LinearLayout) findViewById(R.id.layoutTagBar);
            mLayoutFullscreenOut = (LinearLayout) findViewById(R.id.layoutFullscreenOut);

            blocking = new BlockUnblockUI(mRelativeLayout);

            fIndex = getIntent().getIntExtra("imageIndex", 0);
            fImage = MainActivity.downloadedImages.get(fIndex);
            driverEnum = Drivers.getDriver(getIntent().getStringExtra("driver"));
            try {
                driver = driverEnum.driverclass.newInstance();
            } catch (Exception e) {
                Utils.printError(e);
            }
            driver.setSfw(MainActivity.swf);
            driver.init(MainActivity.permanentStorage, getApplicationContext());

            database = new FurryDatabase(getApplicationContext());

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

            View.OnClickListener setTagToSearch = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = (TextView) v;
                    mTagsEditText.setText(textView.getText());
                }
            };

            View.OnLongClickListener addTagToSearch = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    TextView textView = (TextView) v;
                    mTagsEditText.setText(mTagsEditText.getText() + " " + textView.getText());
                    return true;
                }
            };

            for (int row = 0; row < Math.ceil(fImage.getTags().size() * 1.0 / LEN_OF_TAGS_ROW); row++) {
                for (int column = 0; (column < LEN_OF_TAGS_ROW) && (row * LEN_OF_TAGS_ROW + column < fImage.getTags().size()); column++) {
                    tagsLinesHandler.get(row).items.get(column).setText(Utils.unescapeUnicode(fImage.getTags().get(row * LEN_OF_TAGS_ROW + column)));
                    tagsLinesHandler.get(row).items.get(column).setOnClickListener(setTagToSearch);
                    tagsLinesHandler.get(row).items.get(column).setOnLongClickListener(addTagToSearch);
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
            mArtistEditText.setText(Utils.unescapeUnicode(Utils.joinList(fImage.getArtists(), ", ")));
            mDateEditText.setText(DATETIME_FORMAT.print(fImage.getDownloadedAt()));

            if (!fImage.getDescription().equals("")) {
                mDescriptionText.setText(getString(R.string.descriptionLabel) + " " + fImage.getDescription());
                mDescriptionButton.setEnabled(true);
                blocking.addViewToBlock(mDescriptionButton);
            }

            mFullscreenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fullIn();
                }
            });

            mFullscreenButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fullOut();
                }
            });

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

            mDescriptionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDescriptionText.isShown()) {
                        mDescriptionText.setVisibility(View.GONE);
                        mPictureImageView.setVisibility(View.VISIBLE);
                    } else {
                        mDescriptionText.setVisibility(View.VISIBLE);
                        mPictureImageView.setVisibility(View.GONE);
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

            //set first state not fullscreen
            if (!mSettings.contains(APP_PREFERENCES_FULLSCREEN)) {
                fullOut();
            }

            driver.downloadImageFile(fImage, new SubsamplingScaleImageViewAware(mPictureImageView), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    blocking.blockUI();
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    imageLoaded();
                    blocking.unblockUI();
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    imageLoaded();
                    blocking.unblockUI();
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    imageLoaded();
                    blocking.unblockUI();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Restore settings
        if (mSettings.contains(APP_PREFERENCES_FULLSCREEN)) {
            if (mSettings.getBoolean(APP_PREFERENCES_FULLSCREEN, false)) {
                fullIn();
            } else {
                fullOut();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Store settings
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(APP_PREFERENCES_FULLSCREEN, inFulscreenMode);
        editor.apply();
    }

    private void fullIn() {
        mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.background_floating_material_dark));
        inFulscreenMode = true;
        mRatingImageButton.setVisibility(View.GONE);
        mLayoutSearchBar.setVisibility(View.GONE);
        mLayoutInfoBar.setVisibility(View.GONE);
        mLayoutTagBar.setVisibility(View.GONE);
        mDescriptionButton.setVisibility(View.GONE);
        mLayoutFullscreenOut.setVisibility(View.VISIBLE);

        mDescriptionText.setVisibility(View.GONE);
        mPictureImageView.setVisibility(View.VISIBLE);
    }

    private void fullOut() {
        mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.background_floating_material_light));
        inFulscreenMode = false;
        mRatingImageButton.setVisibility(View.VISIBLE);
        mLayoutSearchBar.setVisibility(View.VISIBLE);
        mLayoutInfoBar.setVisibility(View.VISIBLE);
        mLayoutTagBar.setVisibility(View.VISIBLE);
        mDescriptionButton.setVisibility(View.VISIBLE);
        mLayoutFullscreenOut.setVisibility(View.GONE);
    }


    private void enableDeleteMode() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driver.deleteFromDBandStorage(fImage, database);
                enableDownloadMode();
            }
        };
        ((ImageButton) findViewById(R.id.buttonSaveDelInDB)).setImageResource(android.R.drawable.ic_menu_delete);
        ((ImageButton) findViewById(R.id.buttonSaveDelInDBFullscreen)).setImageResource(android.R.drawable.ic_menu_delete);
        findViewById(R.id.buttonSaveDelInDB).setOnClickListener(listener);
        findViewById(R.id.buttonSaveDelInDBFullscreen).setOnClickListener(listener);
    }

    private void enableDownloadMode() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driver.saveToDBandStorage(fImage, database);
                enableDeleteMode();
            }
        };
        ((ImageButton) findViewById(R.id.buttonSaveDelInDB)).setImageResource(android.R.drawable.ic_menu_save);
        ((ImageButton) findViewById(R.id.buttonSaveDelInDBFullscreen)).setImageResource(android.R.drawable.ic_menu_save);
        findViewById(R.id.buttonSaveDelInDB).setOnClickListener(listener);
        findViewById(R.id.buttonSaveDelInDBFullscreen).setOnClickListener(listener);
    }

    private void imageLoaded() {
        mProgress.setVisibility(View.GONE);
        database.searchByMD5(fImage.getMd5(), new AsyncHandlerUI<FurImage>() {
            @Override
            public void blockUI() {
                blocking.blockUI();
            }

            @Override
            public void unblockUI() {
                if (inFulscreenMode) {
                    findViewById(R.id.buttonSaveDelInDB).setEnabled(true);
                    findViewById(R.id.saveImageButtonProgressBar).setVisibility(View.GONE);
                    mButtonSaveDelInDB = (ImageButton) findViewById(R.id.buttonSaveDelInDBFullscreen);
                    mButtonSaveDelInDBProgress = (ProgressBar) findViewById(R.id.saveImageButtonProgressBar2);
                } else {
                    findViewById(R.id.buttonSaveDelInDBFullscreen).setEnabled(true);
                    findViewById(R.id.saveImageButtonProgressBar2).setVisibility(View.GONE);
                    mButtonSaveDelInDB = (ImageButton) findViewById(R.id.buttonSaveDelInDB);
                    mButtonSaveDelInDBProgress = (ProgressBar) findViewById(R.id.saveImageButtonProgressBar);
                }
                mButtonSaveDelInDB.setEnabled(true);
                mButtonSaveDelInDBProgress.setVisibility(View.GONE);
                blocking.unblockUI();
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
        getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
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
            case R.id.action_save:
                return true;
            case R.id.action_tags:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


}
