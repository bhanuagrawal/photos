package agrawal.bhanu.photos.imagelist.ui;

import android.app.Application;
import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import agrawal.bhanu.photos.Constants;
import agrawal.bhanu.photos.MainActivity;
import agrawal.bhanu.photos.R;
import agrawal.bhanu.photos.imagelist.datamodel.Post;
import agrawal.bhanu.photos.network.model.NetworkState;
import agrawal.bhanu.photos.network.model.Status;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemsAdapter extends PagedListAdapter<Post, RecyclerView.ViewHolder> {

    private final FragmentActivity activity;
    Application mApplication;
    private NetworkState networkState;
    private RetryCallback retryCallback;

    public ItemsAdapter(Application application, FragmentActivity activity) {
        super(Post.DIFF_CALLBACK);
        mApplication = application;
        this.activity = activity;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        if(viewType == R.layout.row_item){
            return new ItemViewHolder(layoutInflater.inflate(viewType, viewGroup, false));
        }
        else if(viewType == R.layout.network_state_item){
            return new NetworkStateViewHolder(layoutInflater.inflate(viewType, viewGroup, false));
        }
        else{
            return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        switch (getItemViewType(i)){
            case R.layout.row_item:
                Post post = getItem(i);
                if (post != null) {
                    ((ItemViewHolder)viewHolder).bindTo(post, i);
                } else {
                    // Null defines a placeholder item - PagedListAdapter automatically
                    // invalidates this row when the actual object is loaded from the
                    // database.
                }
                break;

            case R.layout.network_state_item:
                ((NetworkStateViewHolder)viewHolder).bindTo(networkState);
        }

    }

    public void setNetworkState(NetworkState networkState) {

/*        if(networkState == NetworkState.LOADED){
            notifyItemInserted(getItemCount());
        }*/
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = networkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != networkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }


    private boolean hasExtraRow() {
        if (networkState != null && networkState != NetworkState.LOADED) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.network_state_item;
        } else {
            return R.layout.row_item;
        }
    }

    public void setRetryCallback(RetryCallback retryCallback) {
        this.retryCallback = retryCallback;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title)
        public TextView titleTV;


        @BindView(R.id.parentlayout)
        public ConstraintLayout parentLayout;

        @BindView(R.id.image)
        public ImageView imageView;
        private int position;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            parentLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            ((MainActivity)activity).showImagePreview(position);
        }

        public void bindTo(final Post post, int postion) {

            titleTV.setText(post.getTime());

            this.position = postion;

            final String url = Constants.BASE_URL + post.getImage();
/*
            Picasso.get()
                    .load(R.drawable.ic_thumbnail) // thumbnail url goes here
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Picasso.get()
                                    .load(url) // Image url goes here
                                    .placeholder(imageView.getDrawable())
                                    .rotate(90)
                                    .into(imageView);
                        }

                        @Override
                        public void onError(Exception e) {

                        }

                    });
*/



            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_thumbnail);

            Glide.with(mApplication.getApplicationContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(url).into(imageView);
            //Picasso.get().load(post.getData().getUrl()).into(imageView);
        }

    }

    class NetworkStateViewHolder extends RecyclerView.ViewHolder{

        public TextView loadingTv;
        public TextView errorMsgTv;
        public TextView retryTv;
        public NetworkStateViewHolder(@NonNull View itemView) {
            super(itemView);
            loadingTv = (TextView) itemView.findViewById(R.id.loading);
            errorMsgTv = (TextView) itemView.findViewById(R.id.error);
            retryTv = (TextView) itemView.findViewById(R.id.retry);
            retryTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retryCallback.retry();
                }
            });
        }

        public void bindTo(NetworkState networkState) {
            loadingTv.setVisibility(networkState.getStatus() == Status.LOADING? View.VISIBLE: View.GONE);
            errorMsgTv.setVisibility(networkState.getStatus() == Status.FAILDED? View.VISIBLE: View.GONE);
            retryTv.setVisibility(networkState.getStatus() == Status.FAILDED? View.VISIBLE: View.GONE);
        }
    }

    public interface RetryCallback{
        void retry();
    }
}


