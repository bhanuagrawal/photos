package agrawal.bhanu.photos;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class MyApp extends Application {

    private RequestQueue requestQueue;

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

}
