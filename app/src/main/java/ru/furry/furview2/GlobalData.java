// Class to store global variables
package ru.furry.furview2;

import android.app.Application;
import android.os.Environment;

public class GlobalData extends Application {
    private static String appPath;

    public String getState(){
        return appPath;
    }
    public void setState(String s){
        appPath = s;
    }
}

    /*
    *   //Example of use:
    *   GlobalData appPath = ((GlobalData)getApplicationContext());
    *   String s = appPath.getState();
    */