package ru.furry.furview2.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.furry.furview2.InitialScreen;
import ru.furry.furview2.R;
import ru.furry.furview2.proxy.ConnectionManager;
import ru.furry.furview2.proxy.ProxyTypes;

public class ManualProxy extends AppCompatActivity {

    Button mButtonSetManualProxy;
    EditText mManual_address, mManual_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!InitialScreen.isStarted) {
            Intent intent = new Intent("ru.furry.furview2.InitialScreen");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // won't work on 10 API
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_manual_proxy);

            mButtonSetManualProxy = (Button) findViewById(R.id.ButtonSetManualProxy);
            mManual_address = (EditText) findViewById(R.id.manual_address);
            mManual_port = (EditText) findViewById(R.id.manual_port);

            mManual_address.setText(ConnectionManager.manualProxyAddress);
            if (ConnectionManager.manualProxyPort != 0) {
                mManual_port.setText(String.valueOf(ConnectionManager.manualProxyPort));
            }

            View.OnClickListener OnClickListenerSetManualProxy = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!"".equals(mManual_address.getText().toString()) && !"".equals(mManual_port.getText().toString())) {
                        ConnectionManager.proxyType = ProxyTypes.manual;
                        ConnectionManager.manualProxyAddress = mManual_address.getText().toString();
                        ConnectionManager.manualProxyPort = Integer.valueOf(mManual_port.getText().toString());
                        String str = getString(R.string.success_set_manual_proxy) + ConnectionManager.manualProxyAddress + ":" + ConnectionManager.manualProxyPort;
                        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                        Log.d("fgsfds", "ConnectionManager.proxyType is: " + ConnectionManager.proxyType.name());
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ConnectionManager.manualProxyAddress = "";
                        ConnectionManager.manualProxyPort = 0;
                        Toast.makeText(getApplicationContext(), getString(R.string.fail_set_manual_proxy), Toast.LENGTH_SHORT).show();
                        Log.d("fgsfds", "Invalid manual proxy!");
                    }
                }
            };

            mButtonSetManualProxy.setOnClickListener(OnClickListenerSetManualProxy);
        }
    }

}
