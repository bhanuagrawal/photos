package agrawal.bhanu.photos.imagelist;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import agrawal.bhanu.photos.imagelist.data.ItemKeyedPostDataSource;
import agrawal.bhanu.photos.imagelist.data.PostDataSourceFactory;
import agrawal.bhanu.photos.imagelist.datamodel.ImageDTO;
import agrawal.bhanu.photos.imagelist.datamodel.Post;
import agrawal.bhanu.photos.network.model.NetworkState;

public class PostViewModel extends AndroidViewModel {

    private LiveData<PagedList<Post>> postList;
    private LiveData<NetworkState> networkState;
    private LiveData<NetworkState> initloading;
    private MutableLiveData<ImageDTO> imageLiveData;
    PostDataSourceFactory postDataSourceFactory;
    Executor executor;

    public PostViewModel(@NonNull Application application) {
        super(application);
        postDataSourceFactory = new PostDataSourceFactory(application);
        executor = Executors.newFixedThreadPool(5);
    }

    public LiveData<PagedList<Post>> getPostList() {

        if(postList == null){
            PagedList.Config pagedListConfig =
                    (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                            .setInitialLoadSizeHint(5)
                            .setPageSize(5).build();

            postList = (new LivePagedListBuilder(postDataSourceFactory, pagedListConfig))
                    .setBackgroundThreadExecutor(executor)
                    .build();
        }
        return postList;
    }

    public LiveData<NetworkState> getNetworkState() {

        networkState = Transformations.switchMap(postDataSourceFactory.getMutableLiveData(), new Function<ItemKeyedPostDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(ItemKeyedPostDataSource input) {
                return input.getNetworkState();
            }
        });

        return networkState;
    }

    public LiveData<NetworkState> getInitloading() {
       initloading = Transformations.switchMap(postDataSourceFactory.getMutableLiveData(), new Function<ItemKeyedPostDataSource, LiveData<NetworkState>>() {
           @Override
           public LiveData<NetworkState> apply(ItemKeyedPostDataSource input) {
               return input.getInitloading();
           }
       });
       return initloading;
    }




    public MutableLiveData<ImageDTO> getImageLiveData() {
        if(imageLiveData == null){
            imageLiveData = new MutableLiveData<>();

        }
        return imageLiveData;
    }


    public void onRefresh() {
        postDataSourceFactory.getMutableLiveData().getValue().invalidate();
    }

    public void retry(){
        postDataSourceFactory.getMutableLiveData().getValue().retry();
    }
}
