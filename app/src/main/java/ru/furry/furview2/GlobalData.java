// Class to store global variables
package ru.furry.furview2;

import android.app.Application;
import android.os.Environment;

public class GlobalData extends Application {
    private static String appPath;
    private static int orientation;
    private static int index;
    private static boolean orientationFlag;

    public String getState(){
        return appPath;
    }
    public void setState(String s){
        appPath = s;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        GlobalData.orientation = orientation;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        GlobalData.index = index;
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