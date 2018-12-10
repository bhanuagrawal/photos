package agrawal.bhanu.photos.network.model;


public class NetworkState {

    private Status status;
    String message;


    public Status getStatus() {
        return status;
    }

    public static NetworkState LOADING = new NetworkState(Status.LOADING,"loading");
    public static NetworkState LOADED = new NetworkState(Status.LOADING, "loaded");
    public static NetworkState FAILDED = new NetworkState(Status.FAILDED, "failed");

    public NetworkState(Status status, String message){
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
