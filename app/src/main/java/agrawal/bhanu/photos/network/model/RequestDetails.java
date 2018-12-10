package agrawal.bhanu.photos.network.model;

import com.android.volley.Response;

import java.util.HashMap;

public class RequestDetails{
    private String url;
    private int requestType;
    private String requestBody;
    private int requestID;
    private HashMap<String, Object> objects;
    private Response.Listener onSuccess;
    private Response.ErrorListener onError;
    private HashMap<String, String> headers;

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public Response.Listener getOnSuccess() {
        return onSuccess;
    }

    public void setOnSuccess(Response.Listener onSuccess) {
        this.onSuccess = onSuccess;
    }

    public Response.ErrorListener getOnError() {
        return onError;
    }

    public void setOnError(Response.ErrorListener onError) {
        this.onError = onError;
    }

    public HashMap<String, Object> getObjects() {
        return objects;
    }

    public void setObjects(HashMap<String, Object> objects) {
        this.objects = objects;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
