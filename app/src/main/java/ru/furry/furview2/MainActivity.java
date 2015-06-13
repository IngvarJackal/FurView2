package ru.furry.furview2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Iterator;

import ru.furry.furview2.drivers.e926.DriverE926;
import ru.furry.furview2.drivers.e926.RemoteFurImageE926;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.system.ProxySettings;
import ru.furry.furview2.system.TempClassForTest;
import ru.furry.furview2.system.Utils;



public class MainActivity extends Activity implements View.OnClickListener{

    EditText mSearchField;
    ImageButton mSearchButton;
    WebView mWebView1, mWebView2, mWebView3, mWebView4;
    String url, url2;
    String mSearchQuery;
    String mProxy;
    View.OnTouchListener mOnTouchListener;
    GlobalData appPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JodaTimeAndroid.init(this);

        mSearchField = (EditText)findViewById(R.id.SearchField);
        mSearchButton = (ImageButton)findViewById(R.id.SearchButton);

        mSearchField.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);

        mSearchQuery = getIntent().getExtras().getString("SearchQuery");
        mProxy = getIntent().getExtras().getString("Proxy");

        mSearchField.setText(mSearchQuery);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_to_mainscreen), Toast.LENGTH_SHORT).show();

        url = "http://freeicons.net.ru/wp-content/uploads/2013/08/catpurr_white.gif";
        url2 = "http://freeicons.net.ru/wp-content/uploads/2013/08/walkingcat_white.gif";

        mWebView1 = (WebView) findViewById(R.id.webView1);
        mWebView2 = (WebView) findViewById(R.id.webView2);
        mWebView3 = (WebView) findViewById(R.id.webView3);
        mWebView4 = (WebView) findViewById(R.id.webView4);

        appPath = ((GlobalData)getApplicationContext());
        appPath.setState("/mnt/sdcard/furview2");   //Example path

        // FOR DEBUG

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        String permanentStorage = getApplicationContext().getExternalFilesDir(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .getAbsolutePath();

        DriverE926 driver = null;
        try {
            driver = new DriverE926(permanentStorage, 150, 150);
            if (mProxy != null) {
                Proxy proxy = ProxySettings.getLastProxy();
                Log.d("fgsfds", "Use proxy: " + mProxy);
                driver.setProxy(proxy);
            }
        } catch (IOException e) {
            Utils.printError(e);
        }
        try {
            Log.d("fgsfds", "Searching...");
            Iterator<RemoteFurImageE926> posts = driver.search("fox");
            ArrayList<RemoteFurImageE926> images = new ArrayList<>();
            ArrayList<TempClassForTest> listeners = new ArrayList<>();
            for (int i = 0; i < 6 && posts.hasNext(); i++) {
                images.add(posts.next());
                listeners.add(new TempClassForTest());
            }

            for (FurImage image : driver.download(images, listeners)) {
                Log.d("fgsfds", image.getRootPath() + image.getFileName() + image.getMd5() + image.getDownloadedAt() + image.getFileSize());
            }

        } catch (Exception e) {
            Utils.printError(e);
        }

        //

        mWebView1.loadUrl(url);
        mWebView2.loadUrl(url2);

        mOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    String str = getResources().getString(R.string.toast_text)+" webView1"+getResources().getString(R.string.toast_to_fullscreen);
                    Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent("ru.furry.furview2.fullscreen");
                    intent.putExtra("target_url", url);
                    startActivity(intent);
                }
                return false;
            }
        };

        mWebView1.setOnTouchListener(mOnTouchListener);

    }

    @Override
    protected void onDestroy() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_to_OnDestroy), Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " action_settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.SearchButton:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " SearchButton", Toast.LENGTH_SHORT).show();
                break;
            case R.id.SearchField:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" SearchField",Toast.LENGTH_SHORT).show();
                break;
            /**
            case R.id.webView1:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView1",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("comingvarjackalfurview2.github.furview2.fullscreen");
                startActivity(intent);
                break;
            case R.id.webView2:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView2",Toast.LENGTH_SHORT).show();
                break;
            case R.id.webView3:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView3",Toast.LENGTH_SHORT).show();
                break;
            case R.id.webView4:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView4",Toast.LENGTH_SHORT).show();
                break;
             **/
        }

    }


}
