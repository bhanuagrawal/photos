package agrawal.bhanu.photos.imagelist.datamodel;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;

import java.io.Serializable;

/**
 * Created by bhanu on 12/9/17.
 */

public class Post implements Serializable{
    private int id;
    private String image_url;
    private String image;
    private String time;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private Boolean show_info;
    private int receivers_count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getShow_info() {
        return show_info;
    }

    public void setShow_info(Boolean show_info) {
        this.show_info = show_info;
    }


    public int getReceivers_count() {
        return receivers_count;
    }

    public void setReceivers_count(int receivers_count) {
        this.receivers_count = receivers_count;
    }


    public static DiffCallback<Post> DIFF_CALLBACK = new DiffCallback<Post>() {
        @Override
        public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.equals(newItem);
        }
    };
}
