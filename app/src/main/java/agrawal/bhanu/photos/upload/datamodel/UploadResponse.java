package agrawal.bhanu.photos.upload.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class UploadResponse implements Parcelable{
    String  message;
    boolean success;
    String url;
    int post_id;

    protected UploadResponse(Parcel in) {
        message = in.readString();
        success = in.readByte() != 0;
        url = in.readString();
        post_id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(url);
        dest.writeInt(post_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UploadResponse> CREATOR = new Creator<UploadResponse>() {
        @Override
        public UploadResponse createFromParcel(Parcel in) {
            return new UploadResponse(in);
        }

        @Override
        public UploadResponse[] newArray(int size) {
            return new UploadResponse[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }
}
