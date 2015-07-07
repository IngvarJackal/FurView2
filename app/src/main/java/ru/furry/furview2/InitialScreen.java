package ru.furry.furview2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.ArrayList;

import ru.furry.furview2.drivers.Drivers;
import ru.furry.furview2.system.ProxiedBaseImageDownloader;

public class InitialScreen extends Activity implements View.OnClickListener {

    ImageButton mSearchButtonInitial;
    EditText mSearchFieldInitial;
    CheckBox mProxyBox;
    ToggleButton sfwButton;
    ListView mDriversList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);

        mSearchButtonInitial = (ImageButton) findViewById(R.id.searchButtonInitial);
        mSearchFieldInitial = (EditText) findViewById(R.id.searchFieldInitial);
        mProxyBox = (CheckBox) findViewById(R.id.proxyBox);
        sfwButton = (ToggleButton) findViewById(R.id.sfwButtonInitial);
        sfwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sfwButton.isChecked())
                    sfwButton.setBackgroundColor(0xff63ec4f);
                else
                    sfwButton.setBackgroundColor(0xccb3b3b3);
            }
        });
        mSearchButtonInitial.setOnClickListener(this);
        mDriversList = (ListView) findViewById(R.id.listOfDrivers);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice,
                new ArrayList<String>(Drivers.drivers.keySet()));
        mDriversList.setAdapter(adapter);
        mDriversList.setItemChecked(0, true);
        // UIL initialization
        ImageLoaderConfiguration uilConfig = null;

        File permanentStorage = new File(getApplicationContext().getExternalFilesDir(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .getAbsolutePath());
        // TODO: set internal storage for cache
        File reserveStorage = null;


        uilConfig = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new LruMemoryCache(50 * 1024 * 1024))
                        //.diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new LimitedAgeDiskCache(permanentStorage, reserveStorage, 604800)) // TODO: change 1 week time from hardcoded into system constant
                .imageDownloader(new ProxiedBaseImageDownloader(this))
                .build();

        ImageLoader.getInstance().init(uilConfig);
    }

    @Override
    protected void onResume() {
        super.onRestart();
        sfwButton.setChecked(MainActivity.swf);
        if (MainActivity.swf)
            sfwButton.setBackgroundColor(0xff63ec4f);
        else
            sfwButton.setBackgroundColor(0xccb3b3b3);
        mSearchFieldInitial.setText(MainActivity.searchQuery);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sfwButton.setChecked(MainActivity.swf);
        if (MainActivity.swf)
            sfwButton.setBackgroundColor(0xff63ec4f);
        else
            sfwButton.setBackgroundColor(0xccb3b3b3);
        mSearchFieldInitial.setText(MainActivity.searchQuery);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.searchButtonInitial) {
            Intent intent = new Intent("ru.furry.furview2.MainActivity");
            String mSearchQuery = String.valueOf(mSearchFieldInitial.getText());
            MainActivity.searchQuery = mSearchQuery;
            //intent.putExtra("SearchQuery", mSearchQuery);

            MainActivity.swf = sfwButton.isChecked();
            intent.putExtra("driver", mDriversList.getItemAtPosition(mDriversList.getCheckedItemPosition()).toString());

            startActivity(intent);
        }
    }

    //Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_initial_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
