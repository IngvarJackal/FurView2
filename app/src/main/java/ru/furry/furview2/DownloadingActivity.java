package ru.furry.furview2;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.furry.furview2.drivers.Drivers;

public class DownloadingActivity extends AppCompatActivity {

    DriverWrapperAdapter dataAdapter;
    String drivername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloading);

        drivername = getIntent().getStringExtra("drivername");

        displayListView();

        checkButtonClick();
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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                DriverContainer driverContainer = (DriverContainer) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + driverContainer.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });

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

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.driver_wrapper, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.type);
                holder.name = (CheckBox) convertView.findViewById(R.id.code);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        DriverContainer driverContainer = (DriverContainer) cb.getTag();
                        Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        driverContainer.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            DriverContainer driverContainer = driverContainerList.get(position);
            holder.code.setText(" (" +  driverContainer.getType() + ")");
            holder.name.setText(driverContainer.getName());
            holder.name.setChecked(driverContainer.isSelected());
            holder.name.setTag(driverContainer);

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.downloadButton);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<DriverContainer> driverContainerList = dataAdapter.driverContainerList;
                for(int i=0;i< driverContainerList.size();i++){
                    DriverContainer driverContainer = driverContainerList.get(i);
                    if(driverContainer.isSelected()){
                        responseText.append("\n" + driverContainer.getName());
                    }
                }

                Toast.makeText(getApplicationContext(),
                        responseText, Toast.LENGTH_LONG).show();

            }
        });

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
