package ru.furry.furview2.UI;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.furry.furview2.R;
import ru.furry.furview2.proxy.GetProxiedConnection;
import ru.furry.furview2.proxy.ProxyTypes;

public class ManualProxy extends Activity {

    Button mButtonSetManualProxy;
    EditText mManual_address, mManual_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_proxy);

        mButtonSetManualProxy = (Button)findViewById(R.id.ButtonSetManualProxy);
        mManual_address = (EditText)findViewById(R.id.manual_address);
        mManual_port = (EditText)findViewById(R.id.manual_port);

        mManual_address.setText(GetProxiedConnection.manualProxyAddress);
        if (GetProxiedConnection.manualProxyPort !=0){
            mManual_port.setText(String.valueOf(GetProxiedConnection.manualProxyPort));
        }

        View.OnClickListener OnClickListenerSetManualProxy = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( ! "".equals(mManual_address.getText().toString()) && ! "".equals(mManual_port.getText().toString()) )
                {
                    GetProxiedConnection.proxyType = ProxyTypes.manual;
                    GetProxiedConnection.manualProxyAddress =mManual_address.getText().toString();
                    GetProxiedConnection.manualProxyPort =Integer.valueOf(mManual_port.getText().toString());
                    String str = getString(R.string.success_set_manual_proxy) + GetProxiedConnection.manualProxyAddress +":"+GetProxiedConnection.manualProxyPort;
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                    Log.d("fgsfds", "GetProxiedConnection.proxyType is: "+GetProxiedConnection.proxyType.name());
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    GetProxiedConnection.manualProxyAddress ="";
                    GetProxiedConnection.manualProxyPort =0;
                    Toast.makeText(getApplicationContext(),getString(R.string.fail_set_manual_proxy),Toast.LENGTH_SHORT).show();
                    Log.d("fgsfds", "Invalid manual proxy!");
                }
            }
        };

        mButtonSetManualProxy.setOnClickListener(OnClickListenerSetManualProxy);
    }

}
