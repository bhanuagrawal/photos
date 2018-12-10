package agrawal.bhanu.photos.imagelist.data;
import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.concurrent.Executor;

import agrawal.bhanu.photos.imagelist.datamodel.Feed;
import agrawal.bhanu.photos.imagelist.datamodel.Post;
import agrawal.bhanu.photos.network.model.NetworkState;
import agrawal.bhanu.photos.network.model.Status;


public class ItemKeyedPostDataSource extends ItemKeyedDataSource<String, Post> {

    PostRepository postRepository;
    Executor retryExecuter;
    private MutableLiveData networkState;
    private MutableLiveData initloading;
    private Runnable retryTask;

    @Override
    public void invalidate() {
        super.invalidate();
    }

    public MutableLiveData getInitloading() {
        return initloading;
    }

    public ItemKeyedPostDataSource(Application application, Executor retryExecuter) {
        this.retryExecuter = retryExecuter;
        networkState = new MutableLiveData();
        initloading = new MutableLiveData();
        postRepository = new PostRepository(application);
    }

    public MutableLiveData getNetworkState() {
        return networkState;
    }


    public void retry(){
        if(retryTask!=null){
            retryExecuter.execute(retryTask);
        }
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams<String> params, @NonNull final LoadInitialCallback<Post> callback) {


        networkState.postValue(NetworkState.LOADING);
        initloading.postValue(NetworkState.LOADING);
        postRepository.fetchPosts(String.valueOf(params.requestedLoadSize), null,"5",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onResult(((Feed)postRepository.parsePostResponse(response)).getPosts());
                        networkState.postValue(NetworkState.LOADED);
                        initloading.postValue(NetworkState.LOADED);
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        networkState.postValue(new NetworkState(Status.FAILDED, error.getMessage()));
                        initloading.postValue(new NetworkState(Status.FAILDED, error.getMessage()));
                        retryTask = new Runnable() {
                            @Override
                            public void run() {
                                loadInitial(params, callback);
                            }
                        };

                    }
                }
        );
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<String> params, @NonNull final LoadCallback<Post> callback) {

        networkState.postValue(NetworkState.LOADING);
        postRepository.fetchPosts(String.valueOf(params.requestedLoadSize), String.valueOf(params.key),"5",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onResult(((Feed)postRepository.parsePostResponse(response)).getPosts());
                        networkState.postValue(NetworkState.LOADED);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        networkState.postValue(new NetworkState(Status.FAILDED, error.getMessage()));
                        retryTask = new Runnable() {
                            @Override
                            public void run() {
                                loadAfter(params, callback);
                            }
                        };
                    }
                }
        );
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<Post> callback) {

    }

    @NonNull
    @Override
    public String getKey(@NonNull Post item) {
        return String.valueOf(item.getId());
    }
}
