package ru.furry.furview2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.net.Proxy;
import java.util.ArrayList;

import ru.furry.furview2.drivers.Drivers;
import ru.furry.furview2.system.GetProxyList;
import ru.furry.furview2.system.ProxiedBaseImageDownloader;
import ru.furry.furview2.system.RenewProxy;

public class InitialScreen extends Activity {

    ImageButton mSearchButtonInitial;
    EditText mSearchFieldInitial;
    CheckBox mProxyBox;
    ToggleButton sfwButton;
    ListView mDriversList;
    GlobalData globalData;
    Context context;
    Handler hTestingProxies, hGettingProxies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);
        context = getApplicationContext();

        globalData = (GlobalData) getApplicationContext();

        mSearchFieldInitial = (EditText) findViewById(R.id.searchFieldInitial);

        //CheckBox
        mProxyBox = (CheckBox) findViewById(R.id.proxyBox);
        mProxyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("fgsfds", "Click on ProxyBox");
                if (mProxyBox.isChecked()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.searching_proxy), Toast.LENGTH_LONG).show();
                    Log.d("fgsfds", "Locking views");
                    mSearchButtonInitial.setClickable(false);
                    mSearchFieldInitial.setClickable(false);
                    mProxyBox.setClickable(false);
                    Log.d("fgsfds", "Start GetProxyList");
                    GetProxyList getManyProxies = new GetProxyList(hGettingProxies, context);
                    Log.d("fgsfds", "1");
                    getManyProxies.GetingListOfProxies();
                }
                else   {
                    Log.d("fgsfds", "Set proxy to null");
                    Toast.makeText(getApplicationContext(),getString(R.string.disable_proxy),Toast.LENGTH_SHORT).show();
                    //Disabling proxy
                    mProxyBox.setText(getString(R.string.use_proxy));
                    globalData.setCurrentProxy(null);
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

        //For debug
        globalData.setCurrentProxy(null);
        globalData.setNumOfWorkingProxy(-1);
        //globalData.setCurrentProxyItem("112.25.10.146", 55336,0,"CN");
        //globalData.setCurrentProxy("221.182.62.115", 9999);


        //Handler for getting proxies
        hGettingProxies = new Handler() {
            public void handleMessage(android.os.Message msg) {
                int state =-1;
                state=msg.what;
                if (state!=-1){
                    //The proxies was found
                    Toast.makeText(getApplicationContext(),getString(R.string.yes_list_proxies),Toast.LENGTH_LONG).show();
                    RenewProxy renew = new RenewProxy(hTestingProxies,context);
                    renew.start();

                } else {
                    //The proxies was not found
                    Log.d("fgsfds", "FurView2 can't find proxy. May be Internet is offline");
                    Toast.makeText(getApplicationContext(),getString(R.string.no_connect_proxy), Toast.LENGTH_SHORT).show();
                    mProxyBox.setChecked(false);
                    //Disabling proxy
                    globalData.setCurrentProxy(null);
                }
                Toast.makeText(getApplicationContext(),getString(R.string.checking_proxy),Toast.LENGTH_LONG).show();

            }
        };

        //Handler for testing proxies
        hTestingProxies = new Handler() {
            public void handleMessage(android.os.Message msg) {
                int working=-1;
                working=msg.what;

                switch (working)
                {
                    case (-1):
                    {
                        //Fail
                        Log.d("fgsfds", "We have not working proxy.");
                        Log.d("fgsfds", "Unlocking views");
                        mSearchButtonInitial.setClickable(true);
                        mSearchFieldInitial.setClickable(true);
                        mProxyBox.setClickable(true);
                        break;
                    }
                    case (-2):
                    {
                        //Success
                        Log.d("fgsfds", "Working proxy is number: "+globalData.getNumOfWorkingProxy());
                        Toast.makeText(getApplicationContext(),getString(R.string.yes_connect_proxy),Toast.LENGTH_SHORT).show();
                        Log.d("fgsfds", "Unlocking views");
                        mSearchButtonInitial.setClickable(true);
                        mSearchFieldInitial.setClickable(true);
                        mProxyBox.setClickable(true);
                        break;
                    }
                    default:
                    {
                        mProxyBox.setText(getString(R.string.use_proxy)+" (#"+working+")");
                        break;
                    }
                }
            }
        };


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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
