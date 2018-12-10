package agrawal.bhanu.photos.imagelist.data;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import agrawal.bhanu.photos.Constants;
import agrawal.bhanu.photos.imagelist.datamodel.Feed;
import agrawal.bhanu.photos.network.WebService;
import agrawal.bhanu.photos.network.model.RequestDetails;


public class PostRepository implements WebService.HtttpResponseListner {

    Application application;
    WebService webService;
    Gson gson;
    Uri.Builder urlBuilder;


    public PostRepository(Application application) {
        this.application = application;
        webService = new WebService(application);
        gson = new Gson();
        urlBuilder = new Uri.Builder();

    }

    public Object parseData(RequestDetails requestDetails, String response){
        if(requestDetails.getRequestID() == Constants.IMAGE_FEED_REQUEST){

        }

        return null;
    }



    @Override
    public void onResponse(RequestDetails requestDetails, Object object) {
    }

    @Override
    public void onError(RequestDetails requestDetails, VolleyError error) {
        Log.d("PostRepository", error.toString());
    }

    public void fetchPosts(String limit, String after, String count, Response.Listener onSuccess, Response.ErrorListener onError) {
        //networkState.postValue(NetworkState.LOADING);
        final RequestDetails requestDetails = new RequestDetails();
        requestDetails.setRequestBody(null);

        if(after == null){
            requestDetails.setUrl(Constants.IMAGE_FEED_REQUEST_URL + "&limit=" + limit);
        }
        else {
            requestDetails.setUrl(Constants.IMAGE_FEED_REQUEST_URL + "&limit=" + limit + "&after="+after);
        }
        requestDetails.setRequestID(Constants.IMAGE_FEED_REQUEST);
        requestDetails.setRequestType(Request.Method.GET);
        requestDetails.setOnSuccess(onSuccess);
        requestDetails.setOnError(onError);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("token", "7017881787 de-Rm1BBDoc:APA91bF4Ium0lXph4zMoD2NlN1lGQhXyXsySYTFsNZLyLUKgkaiw4SFq0d8kMf_go71Fdty-zLp2NU4MB6uMBQUxQCzk3tGL_qT9j7ffeFNNX3F3Kf2K8Qiwy4g_55iXuKg0FesJ8Ief");
        requestDetails.setHeaders(headers);
        webService.makeRequest(requestDetails, this);
    }

    public Object parsePostResponse(String response) {
        Feed feed = (Feed) gson.fromJson(response, new TypeToken<Feed>() {}.getType());
        return feed;
    }
}
