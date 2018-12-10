package agrawal.bhanu.photos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {

    MyBroadcastListner myBroadcastListner;

    public MyBroadcastReceiver(MyBroadcastListner myBroadcastListner) {
        this.myBroadcastListner = myBroadcastListner;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        myBroadcastListner.onReceive(context, intent);
    }

    public interface MyBroadcastListner{
        void onReceive(Context context, Intent intent);
    }
}
