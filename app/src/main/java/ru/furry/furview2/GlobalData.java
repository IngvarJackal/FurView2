// Class to store global variables
package ru.furry.furview2;

import android.app.Application;
import android.os.Environment;

public class GlobalData extends Application {
    private static String appPath;
    private static boolean orientationFlag;

    public String getState(){
        return appPath;
    }

    public void setState(String s){
        appPath = s;
    }

    public boolean getOrientationFlag() {
        return orientationFlag;
    }

    public void setOrientationFlag(boolean orientationFlag) {
        GlobalData.orientationFlag = orientationFlag;
    }
}

    /*
    *   //Example of use:
    *   GlobalData global = ((GlobalData)getApplicationContext());
    *   String s = global.getState();
    */