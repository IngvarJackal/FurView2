// ����� ��� �������� ���������� ����������
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
    *������ �������������:
    *   GlobalData appState = ((GlobalData)getApplicationContext());
    *   String s = appState.getState();
    */
