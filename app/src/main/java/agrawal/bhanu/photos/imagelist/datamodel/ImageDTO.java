package agrawal.bhanu.photos.imagelist.datamodel;

import android.net.Uri;

import java.io.Serializable;

public class ImageDTO implements Serializable {

    private String path;
    private Uri uri;
    private mode loadFrom;

    public enum mode{
        LOAD_FROM_SERVER, LOAD_LOCAL_FROM_PATH, LOAD_LOCAL_FROM_URI
    }

    public ImageDTO(mode loadFrom) {
        this.loadFrom = loadFrom;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
