// Класс для хранения глобальной переменной
package ru.furry.furview2;

import android.app.Application;

public class GlobalData extends Application{
    private String mState;

    public String getState(){
        return mState;
    }
    public void setState(String s){
        mState = s;
    }
}

    /*
    *Пример использования:
    *   GlobalData appState = ((GlobalData)getApplicationContext());
    *   String s = appState.getState();
    */
