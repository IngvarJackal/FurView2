package ru.furry.furview2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
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
import ru.furry.furview2.system.BlockingOrientationHandler;
import ru.furry.furview2.system.DefaultCreator;
import ru.furry.furview2.system.ExtendableWDef;
import ru.furry.furview2.system.Utils;


public class FullscreenActivity extends AppCompatActivity {

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final int LEN_OF_TAGS_ROW_PORT = 4;
    private static final int TAG_TEXT_LENGTH_PORT = 12;
    private static final int LEN_OF_TAGS_ROW_LAND = 8;
    private static final int TAG_TEXT_LENGTH_LAND = 12;

    int tag_text_lenght;
    int len_of_tags_row;
    SubsamplingScaleImageView mPictureImageView;
    ScrollView mScrollDescriptionText;
    TableLayout mTagsTable;
    Button mDescriptionButton;
    ProgressBar mProgress;
    EditText mScoreEditText;
    EditText mArtistEditText;
    TextView mDateTextView;
    EditText mTagsEditText;
    TextView mDescriptionText;
    TextView mDescriptionLabel;
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
    LinearLayout mLayoutFullscreenOut;
    int CurrentOrientation;

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_FULLSCREEN = "setFullscreen";
    private SharedPreferences mSettings;
    ru.furry.furview2.system.BlockingOrientationHandler blockingOrientationHandler;

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

    private class TagTextView extends TextView {
        CharSequence trueContent;

        public TagTextView(Context context, CharSequence incomingContetn) {
            super(context);
            this.trueContent = incomingContetn;
        }

        public TagTextView(Context context) {
            super(context);
        }

        public void setTrueContent(CharSequence incomingContetn) {
            this.trueContent = incomingContetn;
        }

        public CharSequence getTrueContent() {
            return trueContent;
        }

        @Override
        public CharSequence getText() {
            return trueContent;
        }

        @Override
        public void setText(CharSequence text, BufferType type) {
            this.trueContent = text;
            if (text.length() > tag_text_lenght) {
                text = text.subSequence(0, tag_text_lenght - 3) + "...";
            }
            super.setText(text, type);
        }
    }

    class Labelled6Row {
        public List<TagTextView> items = new ArrayList<>();

        public Labelled6Row(TableLayout table, Context context) {
            LinearLayout linLay = new LinearLayout(context);
            linLay.setOrientation(LinearLayout.HORIZONTAL);
            linLay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1));
            for (int i = 0; i < len_of_tags_row; i++) {
                TagTextView t = new TagTextView(context);
                t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                t.setGravity(Gravity.CENTER);
                t.setLines(1);
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

    public class BlockingOrientationHandler implements ru.furry.furview2.system.BlockingOrientationHandler{
        private boolean locked;

        @Override
        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        @Override
        public boolean getLocked() {
            return locked;
        }

        @Override
        public void lockOrientation(){
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @Override
        public void unlockOrientation(){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CurrentOrientation = getResources().getConfiguration().orientation;
        if (CurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            tag_text_lenght = TAG_TEXT_LENGTH_PORT;
            len_of_tags_row = LEN_OF_TAGS_ROW_PORT;
        } else {
            tag_text_lenght = TAG_TEXT_LENGTH_LAND;
            len_of_tags_row = LEN_OF_TAGS_ROW_LAND;
        }
        super.onCreate(savedInstanceState);
        if (!InitialScreenActivity.isStarted) {
            Intent intent = new Intent("ru.furry.furview2.InitialScreenActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // won't work on 10 API
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_fullscreen);

            mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

            Log.d("fgsfds", "Fulscreen cur. cursor = " + MainActivity.cursor);

            mPictureImageView = (SubsamplingScaleImageView) findViewById(R.id.picImgView);
            mTagsTable = (TableLayout) findViewById(R.id.tagsTableLayout);
            mDescriptionButton = (Button) findViewById(R.id.descriptionButton);
            mProgress = (ProgressBar) findViewById(R.id.progressBar);
            mScoreEditText = (EditText) findViewById(R.id.scoreEditText);
            mArtistEditText = (EditText) findViewById(R.id.artistEditText);
            mDateTextView = (TextView) findViewById(R.id.dateEditText);
            mTagsEditText = (EditText) findViewById(R.id.tagsEditText);
            mDescriptionLabel = (TextView) findViewById(R.id.descriptionLabel);
            mSearchButton = (ImageButton) findViewById(R.id.searchImageButton);
            mButtonSaveDelInDB = (ImageButton) findViewById(R.id.buttonSaveDelInDB);
            mButtonSaveDelInDBProgress = (ProgressBar) findViewById(R.id.saveImageButtonProgressBar);
            mFullscreenButton = (ImageButton) findViewById(R.id.fullscreenButton);
            mFullscreenButton2 = (ImageButton) findViewById(R.id.fullscreenButton2);
            mRelativeLayout = (RelativeLayout) findViewById(R.id.fullscreenLayout);
            mDescriptionText = (TextView) findViewById(R.id.descriptionText);
            mScrollDescriptionText = (ScrollView) findViewById(R.id.scrollDescriptionText);
            mLayoutSearchBar = (LinearLayout) findViewById(R.id.layoutSearchBar);
            mLayoutInfoBar = (LinearLayout) findViewById(R.id.layoutInfoBar);
            mLayoutFullscreenOut = (LinearLayout) findViewById(R.id.layoutFullscreenOut);

            blockingOrientationHandler = new BlockingOrientationHandler();
            blocking = new BlockUnblockUI(mRelativeLayout,blockingOrientationHandler);


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
                    mTagsEditText.setText(textView.getText() + " " + mTagsEditText.getText());
                    return true;
                }
            };

            for (int row = 0; row < Math.ceil(fImage.getTags().size() * 1.0 / len_of_tags_row); row++) {
                for (int column = 0; (column < len_of_tags_row) && (row * len_of_tags_row + column < fImage.getTags().size()); column++) {
                    tagsLinesHandler.get(row).items.get(column).setText(Utils.unescapeUnicode(fImage.getTags().get(row * len_of_tags_row + column)), TextView.BufferType.NORMAL);
                    tagsLinesHandler.get(row).items.get(column).setOnClickListener(setTagToSearch);
                    tagsLinesHandler.get(row).items.get(column).setOnLongClickListener(addTagToSearch);
                }
            }

            switch (fImage.getRating()) {
                case SAFE:
                    mScoreEditText.setBackgroundColor(0xCC31c128);
                    break;
                case QUESTIONABLE:
                    mScoreEditText.setBackgroundColor(0xCCe07b0a);
                    break;
                case EXPLICIT:
                    mScoreEditText.setBackgroundColor(0xCCe01d0a);
                    break;
                case NA:
                    mScoreEditText.setBackgroundColor(0xCCa2b6b5);
                    break;
            }

            mTagsEditText.setText(MainActivity.searchQuery);
            mScoreEditText.setText(Integer.toString(fImage.getScore()));
            mArtistEditText.setText(Utils.unescapeUnicode(Utils.joinList(fImage.getArtists(), ", ")));
            mDateTextView.setText(DATETIME_FORMAT.print(fImage.getDownloadedAt()));
            //mDescriptionText.setText(fImage.getDescription());

            if (!fImage.getDescription().equals("")) {
                mDescriptionText.setText(fImage.getDescription());
            } else {
                mDescriptionLabel.setVisibility(View.GONE);
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

            mDescriptionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mScrollDescriptionText.isShown()) {
                        mScrollDescriptionText.setVisibility(View.GONE);
                        mPictureImageView.setVisibility(View.VISIBLE);
                    } else {
                        mScrollDescriptionText.setVisibility(View.VISIBLE);
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
        mLayoutSearchBar.setVisibility(View.GONE);
        mScoreEditText.setVisibility(View.GONE);
        mArtistEditText.setVisibility(View.GONE);

        mDescriptionButton.setVisibility(View.GONE);
        mLayoutFullscreenOut.setVisibility(View.VISIBLE);

        mScrollDescriptionText.setVisibility(View.GONE);
        mPictureImageView.setVisibility(View.VISIBLE);
    }

    private void fullOut() {
        mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.background_floating_material_light));
        inFulscreenMode = false;
        mLayoutSearchBar.setVisibility(View.VISIBLE);
        mScoreEditText.setVisibility(View.VISIBLE);
        mArtistEditText.setVisibility(View.VISIBLE);

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
                Intent intent = new Intent("ru.furry.furview2.HelpScreenActivity");
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
