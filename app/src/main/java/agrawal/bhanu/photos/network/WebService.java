package agrawal.bhanu.photos.network;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import agrawal.bhanu.photos.Constants;
import agrawal.bhanu.photos.MyApp;
import agrawal.bhanu.photos.network.model.RequestDetails;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WebService {

    RequestQueue mRequestQueue;
    Application application;

    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
// add your other interceptors …
// add logging as last interceptor
            httpClient.addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public WebService(Application application) {
        this.application = application;
       mRequestQueue = ((MyApp)application).getRequestQueue();

    }


    public void makeRequest(final RequestDetails requestDetails, final HtttpResponseListner responseListner){

        if(requestDetails.getOnSuccess() == null){
            requestDetails.setOnSuccess(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseListner.onResponse(requestDetails, response);
                }
            });
        }

        if(requestDetails.getOnError() == null){
            requestDetails.setOnError(new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    responseListner.onError(requestDetails, error);
                }
            });
        }

        final StringRequest request = new StringRequest(requestDetails.getRequestType(), requestDetails.getUrl(), requestDetails.getOnSuccess(), requestDetails.getOnError()){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                if(requestDetails.getHeaders()!=null){
                    return requestDetails.getHeaders();
                }
                else{
                    return super.getHeaders();
                }

            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestDetails.getRequestBody() == null ? null : requestDetails.getRequestBody().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestDetails.getRequestBody(), "utf-8");
                    return null;
                }
            }
        };


        mRequestQueue.add(request);

    }


    public interface HtttpResponseListner {
        public void onResponse(RequestDetails requestDetails, Object object);
        public void onError(RequestDetails requestDetails, VolleyError error);
    }
}
