package ru.furry.furview2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.system.GetProxyList;
import ru.furry.furview2.system.ProxiedBaseImageDownloader;
import ru.furry.furview2.system.ProxyItem;
import ru.furry.furview2.system.ProxySettings;
import ru.furry.furview2.system.RenewProxy;

public class InitialScreen extends Activity implements View.OnClickListener {

    ImageButton mSearchButtonInitial;
    EditText mSearchFieldInitial;
    CheckBox mProxyBox;
    GlobalData globalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);

        globalData = (GlobalData) getApplicationContext();

        mSearchButtonInitial = (ImageButton)findViewById(R.id.SearchButtonInitial);
        mSearchFieldInitial = (EditText)findViewById(R.id.SearchFieldInitial);
        mProxyBox = (CheckBox)findViewById(R.id.proxyBox);
        mSearchButtonInitial.setOnClickListener(this);
        mProxyBox.setOnClickListener(this);

        //For debug
        globalData.setCurrentProxy(null);
        //globalData.setCurrentProxyItem("112.25.10.146", 55336,0,"CN");
        //globalData.setCurrentProxy("221.182.62.115", 9999);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case (R.id.SearchButtonInitial):
            {
                // UIL initialization
                Log.d("fgsfds", "UIL initialization");
                ImageLoaderConfiguration uilConfig = null;
                uilConfig = new ImageLoaderConfiguration.Builder(this)
                        .memoryCache(new LruMemoryCache(50 * 1024 * 1024))
//                        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                        .imageDownloader(new ProxiedBaseImageDownloader(this, proxy))
                        .imageDownloader(new ProxiedBaseImageDownloader(this, globalData.getCurrentProxy()))
                        .build();
                ImageLoader.getInstance().init(uilConfig);

                //Starting MainActivity
                Intent intent = new Intent("ru.furry.furview2.MainActivity");
                Log.d("fgsfds", "Put extras to Intent for transfer to MainActivity");
                String mSearchQuery = String.valueOf(mSearchFieldInitial.getText());
                intent.putExtra("SearchQuery", mSearchQuery);
                //intent.putExtra("Proxy", (proxy != null) ? proxy.toString() : null);
                Log.d("fgsfds", "Start MainActivity");
                startActivity(intent);
                break;
            }
            case (R.id.proxyBox):
            {
                if (mProxyBox.isChecked()) {
                    Log.d("fgsfds", "Start GetProxyList");
                    Toast.makeText(getApplicationContext(), getString(R.string.searching_proxy),Toast.LENGTH_LONG).show();
                    mSearchButtonInitial.setClickable(false);
                    mSearchFieldInitial.setClickable(false);
                    new GetProxyList(this).execute();}
                else   {
                    Log.d("fgsfds", "Set proxy to null");
                    Toast.makeText(getApplicationContext(),getString(R.string.disable_proxy),Toast.LENGTH_SHORT).show();
                    //Disabling proxy
                    globalData.setCurrentProxy(null);
                }
//                startActivity(new Intent("ru.furry.furview2.LockActivity"));
                break;
            }
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

    //After searching proxy
public void resultGetProxyList(Boolean proxySearchingResult) {
    if (proxySearchingResult){
        //The proxies was found
        Toast.makeText(getApplicationContext(),getString(R.string.yes_connect_proxy),Toast.LENGTH_SHORT).show();
    } else {
        //The proxies was not found
        Log.d("fgsfds", "FurView2 can't find proxy. May be Internet is offline");
        Toast.makeText(getApplicationContext(),getString(R.string.no_connect_proxy), Toast.LENGTH_SHORT).show();
        mProxyBox.setChecked(false);
        //Disabling proxy
        globalData.setCurrentProxy(null);
    }
    Toast.makeText(getApplicationContext(),getString(R.string.checking_proxy),Toast.LENGTH_SHORT).show();

    new RenewProxy(this).execute();

    mSearchButtonInitial.setClickable(true);
    mSearchFieldInitial.setClickable(true);
}

}
