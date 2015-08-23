package ru.furry.furview2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.drivers.Driver;
import ru.furry.furview2.drivers.Drivers;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.RemoteFurImage;
import ru.furry.furview2.system.AsyncHandlerUI;
import ru.furry.furview2.system.BlockUnblockUI;
import ru.furry.furview2.system.Utils;

public class DownloadingActivity extends AppCompatActivity {

    private final static int MAX_NUM_OF_PICS = 20;

    DriverWrapperAdapter dataAdapter;
    String drivername;
    ToggleButton sfwButton;
    Button downloadButton;
    AlertDialog.Builder aBuilder;
    EditText numOfPicsEditText;
    ProgressBar massDownloadingProgressBar;
    EditText counterTextEdit;
    List<Drivers> drivers;
    SyncCounter syncCounter;
    EditText searchField;
    private int numOfPics;
    FurryDatabase database;
    RelativeLayout mMassDownloadLayout;
    ListView mMassDownloadDriverList;
    BlockUnblockUI blocking;

    AtomicInteger currentSavedPic = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloading);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        database = new FurryDatabase(this);

        searchField = (EditText) findViewById(R.id.searchField);
        counterTextEdit = (EditText) findViewById(R.id.counterTextEdit);
        massDownloadingProgressBar = (ProgressBar) findViewById(R.id.massDownloadingProgressBar);
        mMassDownloadLayout = (RelativeLayout) findViewById(R.id.massDownloadLayout);
        mMassDownloadDriverList =(ListView) findViewById(R.id.listView);

        blocking = new BlockUnblockUI(mMassDownloadLayout);

        drivername = getIntent().getStringExtra("drivername");
        displayListView();

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

        aBuilder = new AlertDialog.Builder(this);
        aBuilder.setTitle(R.string.too_many_pics);
        aBuilder.setMessage(R.string.too_many_pics_body);

        aBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startDownload();
            }

        });

        aBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                dialog.dismiss();
            }
        });

        numOfPicsEditText = (EditText) findViewById(R.id.numOfPicsEditText);

        downloadButton = (Button) findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                MainActivity.searchQuery = searchField.getText().toString();
                drivers = getDrivers();
                numOfPics = Integer.parseInt("0" + numOfPicsEditText.getText().toString());
                Log.d("fgsfds", "downloading #" + numOfPics + " pics");
                syncCounter = new SyncCounter(numOfPics);
                massDownloadingProgressBar.setMax(numOfPics);
                massDownloadingProgressBar.setProgress(0);
                currentSavedPic.set(0);
                if (numOfPics > 0) {
                    if (drivers.size() * numOfPics <= MAX_NUM_OF_PICS)
                        startDownload();
                    else
                        aBuilder.create().show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_zero,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class SyncCounter {
        int maxSize;
        public int size;
        public AtomicInteger blocking;

        public SyncCounter(int maxSize) {
            this.maxSize = maxSize;
            this.size = 0;
            counterTextEdit.setText("");
            //massDownloadingProgressBar.setMax(maxSize);
            Log.d("fgsfds", "max size: " + maxSize);
            blocking = new AtomicInteger(0);
        }

        public synchronized void increment() {
            size += 1;
            Log.d("fgsfds", "size: " + size);
            //counterTextEdit.setText(Integer.toString(size));
            //massDownloadingProgressBar.setProgress(size);
            if (size == maxSize) {
                unblockUI_();
                blocking.set(9999);
            }
        }
    }

    private void unblockUI_() {
        Log.d("fgsfds", "UI unblocked");
        blocking.unblockUI();
    }

    private void blockUI_() {
        Log.d("fgsfds", "UI blocked...");
        blocking.blockUI();
    }

    private void startDownload() {
        blockUI_();
        for (final Drivers driver : drivers) {
            try {
                Log.d("fgsfds", "init " + driver.drivername);
                final Driver driverInstance = driver.driverclass.newInstance();
                final AtomicInteger counter = new AtomicInteger(0);
                final List<RemoteFurImage> remoteImages = new ArrayList<>(numOfPics);
                driverInstance.init(MainActivity.permanentStorage, this);
                driverInstance.setSfw(sfwButton.isChecked());
                syncCounter.blocking.incrementAndGet();

                final AsyncHandlerUI<FurImage> imagesHandler = new AsyncHandlerUI<FurImage>() {
                    @Override
                    public void blockUI() {
                        // don't fill
                    }

                    @Override
                    public void unblockUI() {
                        // don't fill
                    }

                    @Override
                    public void retrieve(List<? extends FurImage> images) {
                        for (FurImage image : images) {
                            driverInstance.saveToDBandStorage(image, database, new AsyncHandlerUI<FurImage>() {
                                @Override
                                public void blockUI() {
                                    blocking.blockUI();
                                }

                                @Override
                                public void unblockUI() {
                                    //Log.d("fgsfds", "Downloaded " + currentSavedPic.get() + " pics !!! Yey!");
                                    massDownloadingProgressBar.setProgress(currentSavedPic.incrementAndGet());
                                    counterTextEdit.setText(Integer.toString(currentSavedPic.get()));
                                    blocking.unblockUI();
                                    if (currentSavedPic.get()==numOfPics){
                                        Toast.makeText(
                                                getApplicationContext(),
                                                getResources().getString(R.string.images_downloaded) + " " + syncCounter.size,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void retrieve(List<? extends FurImage> images) {
                                    Log.d("fgsfds", "This message don't shown( ");
                                    //massDownloadingProgressBar.setProgress(currentSavedPic.incrementAndGet());
                                    //massDownloadingProgressBar.setProgress(currentSavedPic.incrementAndGet());
                                }
                            });
                            syncCounter.increment();
                        }
                    }
                };

                driverInstance.search(searchField.getText().toString(), new AsyncHandlerUI<RemoteFurImage>() {
                    @Override
                    public void blockUI() {
                        // don't fill
                    }

                    @Override
                    public void unblockUI() {
                        // don't fill
                    }

                    @Override
                    public void retrieve(List<? extends RemoteFurImage> images) {
                        int c = counter.addAndGet(images.size());
                        remoteImages.addAll(images);
                        if (c < numOfPics) {
                            if (driverInstance.hasNext()) {
                                driverInstance.getNext(this);
                            } else {
                                if (c == 0) {
                                    Log.d("fgsfds", "Running " + syncCounter.blocking.get() + " threads");
                                    if (syncCounter.blocking.decrementAndGet() == 0) {
                                        unblockUI_();
                                    }
                                } else {
                                    driverInstance.downloadFurImage(remoteImages.subList(0, Math.min(remoteImages.size(), numOfPics)), Collections.nCopies(remoteImages.size(), imagesHandler));
                                }
                            }
                        } else {
                            driverInstance.downloadFurImage(remoteImages.subList(0, numOfPics), Collections.nCopies(remoteImages.size(), imagesHandler));
                        }
                    }
                });
            } catch (Exception e) {
                Utils.printError(e);
            }
        }
    }

    private void displayListView() {

        ArrayList<DriverContainer> driverContainerList = new ArrayList<DriverContainer>();
        for (Drivers driver : Drivers.values()) {
            DriverContainer driverContainer = new DriverContainer(getResources().getString(driver.type.nameId), driver.drivername, drivername.equals(driver.drivername));
            driverContainerList.add(driverContainer);
        }

        dataAdapter = new DriverWrapperAdapter(this,
                R.layout.driver_wrapper, driverContainerList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(dataAdapter);
    }

    private class DriverWrapperAdapter extends ArrayAdapter<DriverContainer> {

        private ArrayList<DriverContainer> driverContainerList;

        public DriverWrapperAdapter(Context context, int textViewResourceId,
                                    ArrayList<DriverContainer> driverContainerList) {
            super(context, textViewResourceId, driverContainerList);
            this.driverContainerList = new ArrayList<DriverContainer>();
            this.driverContainerList.addAll(driverContainerList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.driver_wrapper, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.type);
                holder.name = (CheckBox) convertView.findViewById(R.id.code);
                convertView.setTag(holder);

                //blocking.addViewToBlock((RelativeLayout) convertView.findViewById(R.id.driverWrapperAdapterLayout));
                blocking.addViewToBlock((CheckBox) convertView.findViewById(R.id.code));
                blocking.addViewToBlock((TextView) convertView.findViewById(R.id.type));

                holder.name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        DriverContainer driverContainer = (DriverContainer) cb.getTag();
                        driverContainer.setSelected(cb.isChecked());
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            DriverContainer driverContainer = driverContainerList.get(position);
            holder.code.setText(" (" + driverContainer.getType() + ")");
            holder.name.setText(driverContainer.getName());
            holder.name.setChecked(driverContainer.isSelected());
            holder.name.setTag(driverContainer);

            return convertView;
        }

    }

    private List<Drivers> getDrivers() {
        ArrayList<Drivers> drivers = new ArrayList<>();
        for (DriverContainer container : dataAdapter.driverContainerList) {
            if (container.isSelected()) {
                drivers.add(Drivers.getDriver(container.getName()));
            }
        }
        return drivers;
    }

    class DriverContainer {

        String type = null;
        String name = null;
        boolean selected = false;

        public DriverContainer(String type, String name, boolean selected) {
            super();
            this.type = type;
            this.name = name;
            this.selected = selected;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

    }
}
