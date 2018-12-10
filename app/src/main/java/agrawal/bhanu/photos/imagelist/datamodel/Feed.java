package agrawal.bhanu.photos.imagelist.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bhanu on 12/9/17.
 */

public class Feed implements Serializable{

    private ArrayList<Post> posts;

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }
}
