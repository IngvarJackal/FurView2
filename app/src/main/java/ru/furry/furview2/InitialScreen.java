package ru.furry.furview2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.furry.furview2.drivers.Drivers;
import ru.furry.furview2.system.GetProxiedConnection;
import ru.furry.furview2.system.ProxiedBaseImageDownloader;
import ru.furry.furview2.system.ProxyTypes;
import ru.furry.furview2.system.Utils;

public class InitialScreen extends AppCompatActivity {

    ImageButton mSearchButtonInitial;
    EditText mSearchFieldInitial;
    ToggleButton sfwButton;
    ListView mDriversList;
    Context context;
    //for menu
    private List<MenuItem> SubMenuProxy = new ArrayList();
    //for save and restore settings
    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_PROXY = "proxy";
    public static final String APP_PREFERENCES_MANUAL_ADDRESS = "manual_proxy_adress";
    public static final String APP_PREFERENCES_MANUAL_PORT = "manual_proxy_port";
    public static final String APP_PREFERENCES_SWF = "swf";
    private SharedPreferences mSettings;
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);
        context = getApplicationContext();

        //Initial settings
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        mSearchFieldInitial = (EditText) findViewById(R.id.searchFieldInitial);

        sfwButton = (ToggleButton) findViewById(R.id.sfwButtonInitial);
        sfwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sfwButton.isChecked())
                    sfwButton.setBackgroundColor(0xff63ec4f);
                else
                    sfwButton.setBackgroundColor(0xccb3b3b3);
                MainActivity.swf = sfwButton.isChecked();
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
                intent.putExtra("driver", mDriversList.getItemAtPosition(mDriversList.getCheckedItemPosition()).toString());
                startActivity(intent);
            }

        });

        mDriversList = (ListView) findViewById(R.id.listOfDrivers);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice,
                Drivers.getDriverList());
        mDriversList.setAdapter(adapter);
        mDriversList.setItemChecked(0, true);

        // UIL initialization
        Log.d("fgsfds", "UIL initialization");
        ImageLoaderConfiguration uilConfig = null;
        File permanentStorage = new File(getApplicationContext().getExternalFilesDir(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .getAbsolutePath() + "/cache");
        // TODO: set internal storage for cache
        File reserveStorage = null;
        try {
            uilConfig = new ImageLoaderConfiguration.Builder(this)
                    .memoryCache(new LruMemoryCache(50 * 1024 * 1024))
                            //.diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    //.diskCache(new LimitedAgeDiskCache(permanentStorage, reserveStorage, 60*60*24)) // TODO: change 1 day from hardcoded into system constant
                    .diskCache(new LruDiskCache(permanentStorage, new Md5FileNameGenerator(), 0))
                    .imageDownloader(new ProxiedBaseImageDownloader(this))
                    .build();
        } catch (IOException e) {
            Utils.printError(e);
        }
        ImageLoader.getInstance().init(uilConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Restore settings
        //Proxy type
        if (mSettings.contains(APP_PREFERENCES_PROXY)) {
            int num = mSettings.getInt(APP_PREFERENCES_PROXY, 0);
            GetProxiedConnection.proxyType = ProxyTypes.values()[num];
            Log.d("fgsfds", "Restore proxy setting: " + ProxyTypes.values()[num].name());
        }
        //Manual proxy adress
        if (mSettings.contains(APP_PREFERENCES_MANUAL_ADDRESS)) {
            GetProxiedConnection.ManualProxyAddress = mSettings.getString(APP_PREFERENCES_MANUAL_ADDRESS, "");
        }
        //Manual proxy port
        if (mSettings.contains(APP_PREFERENCES_MANUAL_PORT)) {
            GetProxiedConnection.ManualProxyPort = mSettings.getInt(APP_PREFERENCES_MANUAL_PORT, 0);
        }
        //swf button
        if (mSettings.contains(APP_PREFERENCES_SWF)) {
            MainActivity.swf = mSettings.getBoolean(APP_PREFERENCES_SWF, false);
        }

        sfwButton.setChecked(MainActivity.swf);
        if (MainActivity.swf)
            sfwButton.setBackgroundColor(0xff63ec4f);
        else
            sfwButton.setBackgroundColor(0xccb3b3b3);
        mSearchFieldInitial.setText(MainActivity.searchQuery);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Store settings
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_PROXY, GetProxiedConnection.proxyType.ordinal());
        editor.putString(APP_PREFERENCES_MANUAL_ADDRESS, GetProxiedConnection.ManualProxyAddress);
        editor.putInt(APP_PREFERENCES_MANUAL_PORT, GetProxiedConnection.ManualProxyPort);
        editor.putBoolean(APP_PREFERENCES_SWF, MainActivity.swf);
        editor.apply();
//        Log.d("fgsfds", "Save proxy setting: " + GetProxiedConnection.proxyType.name());
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
        SubMenuProxy.clear();
        SubMenuProxy.add(menu.findItem(R.id.sub_proxy_menu_1));
        SubMenuProxy.add(menu.findItem(R.id.sub_proxy_menu_2));
        SubMenuProxy.add(menu.findItem(R.id.sub_proxy_menu_3));
        SubMenuProxy.add(menu.findItem(R.id.sub_proxy_menu_4));
        SubMenuProxy.add(menu.findItem(R.id.sub_proxy_menu_5));
        setCheckingProxyMenu();
        return true;
    }

    private void setCheckingProxyMenu() {
        int i;
        String val1 = "", val2 = "";
        for (i = 0; i < SubMenuProxy.size(); i++) {
            val1 = SubMenuProxy.get(i).getTitle().toString();
            val2 = getString(GetProxiedConnection.proxyType.getProxyStringId());
            if (val1.equals(val2)) {
                SubMenuProxy.get(i).setChecked(true);
            } else {
                SubMenuProxy.get(i).setChecked(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case (R.id.action_settings): {
                Log.d("fgsfds", "Initial menu: " + item.getTitle());
                return true;
            }
            case (R.id.sub_proxy_menu_1): {
                GetProxiedConnection.proxyType = ProxyTypes.foxtools;
                setCheckingProxyMenu();
                Log.d("fgsfds", "Proxy used: " + item.getTitle());
                return true;
            }
            case (R.id.sub_proxy_menu_2): {
                GetProxiedConnection.proxyType = ProxyTypes.opera;
                setCheckingProxyMenu();
                Log.d("fgsfds", "Proxy used: " + item.getTitle());
                return true;
            }
            case (R.id.sub_proxy_menu_3): {
                GetProxiedConnection.proxyType = ProxyTypes.antizapret;
                setCheckingProxyMenu();
                Log.d("fgsfds", "Proxy used: " + item.getTitle());
                return true;
            }
            case (R.id.sub_proxy_menu_4): {
                setCheckingProxyMenu();
                Log.d("fgsfds", "Proxy used: " + item.getTitle());
                startActivityForResult(new Intent("ru.furry.furview2.ManualProxy"), REQUEST_CODE);
                return true;
            }
            case (R.id.sub_proxy_menu_5): {
                GetProxiedConnection.proxyType = ProxyTypes.none;
                setCheckingProxyMenu();
                Log.d("fgsfds", "Proxy used: " + item.getTitle());
                return true;
            }
            case (R.id.action_searchelp): {
                Intent intent = new Intent("ru.furry.furview2.HelpScreen");
                intent.putExtra("helptextId", Drivers.getDriver(mDriversList.getItemAtPosition(mDriversList.getCheckedItemPosition()).toString()).searchHelpId);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putInt(APP_PREFERENCES_PROXY, GetProxiedConnection.proxyType.ordinal());
                editor.putString(APP_PREFERENCES_MANUAL_ADDRESS, GetProxiedConnection.ManualProxyAddress);
                editor.putInt(APP_PREFERENCES_MANUAL_PORT, GetProxiedConnection.ManualProxyPort);
                editor.apply();
            }
        }
    }

}
