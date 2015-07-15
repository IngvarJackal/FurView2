package ru.furry.furview2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import ru.furry.furview2.system.GetProxiedConnection;
import ru.furry.furview2.system.ProxiedBaseImageDownloader;
import ru.furry.furview2.system.ProxyTypes;

public class InitialScreen extends Activity {

    ImageButton mSearchButtonInitial;
    EditText mSearchFieldInitial;
    CheckBox mProxyBox;
    ToggleButton sfwButton;
    ListView mDriversList;
    Context context;
    private MenuItem mItem1, mItem2, mItem3, mItem4, mItem5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);
        context = getApplicationContext();

        mSearchFieldInitial = (EditText) findViewById(R.id.searchFieldInitial);

        //CheckBox
        mProxyBox = (CheckBox) findViewById(R.id.proxyBox);
        mProxyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProxyBox.isChecked()) {
                    GetProxiedConnection.proxyType = ProxyTypes.foxtools;
                    Log.d("fgsfds", "Click on ProxyBox. The proxy will be used.");
                }
                else   {
                    GetProxiedConnection.proxyType = ProxyTypes.none;
                    Log.d("fgsfds", "Click on ProxyBox. The proxy will not be used.");
                }
            }
        });

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

        mSearchButtonInitial = (ImageButton) findViewById(R.id.searchButtonInitial);
        mSearchButtonInitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("ru.furry.furview2.MainActivity");
                String mSearchQuery = String.valueOf(mSearchFieldInitial.getText());
                MainActivity.searchQuery = mSearchQuery;
                //intent.putExtra("SearchQuery", mSearchQuery);
                MainActivity.swf = sfwButton.isChecked();
                intent.putExtra("driver", mDriversList.getItemAtPosition(mDriversList.getCheckedItemPosition()).toString());
                startActivity(intent);
            }

        });

        mDriversList = (ListView) findViewById(R.id.listOfDrivers);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice,
                new ArrayList<String>(Drivers.drivers.keySet()));
        mDriversList.setAdapter(adapter);
        mDriversList.setItemChecked(0, true);

        // UIL initialization
        Log.d("fgsfds", "UIL initialization");
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

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_initial_screen, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mItem1 = menu.findItem(R.id.sub_proxy_menu_1);
        mItem2 = menu.findItem(R.id.sub_proxy_menu_2);
        mItem3 = menu.findItem(R.id.sub_proxy_menu_3);
        mItem4 = menu.findItem(R.id.sub_proxy_menu_4);
        mItem5 = menu.findItem(R.id.sub_proxy_menu_5);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id)
        {
            case (R.id.action_settings): {
                Log.d("fgsfds", "Initial menu: " + item.getTitle());
                return true;
            }
            case (R.id.sub_proxy_menu_1): {
                GetProxiedConnection.proxyType = ProxyTypes.foxtools;
                mItem1.setChecked(true);
                mItem2.setChecked(false);
                mItem3.setChecked(false);
                mItem4.setChecked(false);
                mItem5.setChecked(false);
                Log.d("fgsfds", "Proxy used: " + item.getTitle());
                return true;
            }
            case (R.id.sub_proxy_menu_2): {
                GetProxiedConnection.proxyType = ProxyTypes.opera;
                mItem1.setChecked(false);
                mItem2.setChecked(true);
                mItem3.setChecked(false);
                mItem4.setChecked(false);
                mItem5.setChecked(false);
                Log.d("fgsfds", "Proxy used: " + item.getTitle());
                return true;
            }
            case (R.id.sub_proxy_menu_3): {
                GetProxiedConnection.proxyType = ProxyTypes.antizapret;
                mItem1.setChecked(false);
                mItem2.setChecked(false);
                mItem3.setChecked(true);
                mItem4.setChecked(false);
                mItem5.setChecked(false);
                Log.d("fgsfds", "Proxy used: " + item.getTitle());
                return true;
            }
            case (R.id.sub_proxy_menu_4): {
                GetProxiedConnection.proxyType = ProxyTypes.manual;
                mItem1.setChecked(false);
                mItem2.setChecked(false);
                mItem3.setChecked(false);
                mItem4.setChecked(true);
                mItem5.setChecked(false);
                Log.d("fgsfds", "Proxy used: " + item.getTitle());
                return true;
            }
            case (R.id.sub_proxy_menu_5): {
                GetProxiedConnection.proxyType = ProxyTypes.none;
                mItem1.setChecked(false);
                mItem2.setChecked(false);
                mItem3.setChecked(false);
                mItem4.setChecked(false);
                mItem5.setChecked(true);
                Log.d("fgsfds", "Proxy used: " + item.getTitle());
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

}
