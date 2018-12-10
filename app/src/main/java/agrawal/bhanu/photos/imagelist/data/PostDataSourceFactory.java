package agrawal.bhanu.photos.imagelist.data;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PostDataSourceFactory implements android.arch.paging.DataSource.Factory {

    ItemKeyedPostDataSource itemKeyedPostDataSource;
    Executor executor;
    MutableLiveData<ItemKeyedPostDataSource> mutableLiveData;
    Application application;

    public PostDataSourceFactory(Application application) {
        this.application = application;
        this.mutableLiveData = new MutableLiveData<ItemKeyedPostDataSource>();
        executor = Executors.newFixedThreadPool(5);
        itemKeyedPostDataSource = new ItemKeyedPostDataSource(application, executor);

    }

    public LiveData<ItemKeyedPostDataSource> getMutableLiveData() {
        return mutableLiveData;
    }

    @Override
    public android.arch.paging.DataSource create() {
        executor = Executors.newFixedThreadPool(5);
        itemKeyedPostDataSource = new ItemKeyedPostDataSource(application, executor);
        mutableLiveData.postValue(itemKeyedPostDataSource);
        return itemKeyedPostDataSource;
    }




}
