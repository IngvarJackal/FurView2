package ru.furry.furview2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;

import ru.furry.furview2.system.ProxiedBaseImageDownloader;
import ru.furry.furview2.system.ProxySettings;
import ru.furry.furview2.system.Utils;

public class InitialScreen extends Activity implements View.OnClickListener {

    ImageButton mSearchButtonInitial;
    EditText mSearchFieldInitial;
    CheckBox mProxyBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);

        mSearchButtonInitial = (ImageButton)findViewById(R.id.SearchButtonInitial);
        mSearchFieldInitial = (EditText)findViewById(R.id.SearchFieldInitial);
        mProxyBox = (CheckBox)findViewById(R.id.proxyBox);
        mSearchButtonInitial.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.SearchButtonInitial)
        {
            Intent intent = new Intent("ru.furry.furview2.MainActivity");
            String mSearchQuery = String.valueOf(mSearchFieldInitial.getText());
            intent.putExtra("SearchQuery", mSearchQuery);

            // proxy init
            Proxy proxy = mProxyBox.isChecked() ? ProxySettings.getProxy() : null;
            intent.putExtra("Proxy", proxy.toString());

            // UIL initialization
            String cacheStorage = getApplicationContext().getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath();
            ImageLoaderConfiguration uilConfig = null;
            uilConfig = new ImageLoaderConfiguration.Builder(this)
                    .memoryCache(new LruMemoryCache(50 * 1024 * 1024))
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .imageDownloader(new ProxiedBaseImageDownloader(this, proxy))
                    .build();

            ImageLoader.getInstance().init(uilConfig);
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
