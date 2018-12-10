package agrawal.bhanu.photos.imagelist.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import agrawal.bhanu.photos.MainFragment;
import agrawal.bhanu.photos.MyBroadcastReceiver;
import agrawal.bhanu.photos.R;
import agrawal.bhanu.photos.imagelist.PostViewModel;
import agrawal.bhanu.photos.imagelist.datamodel.Post;
import agrawal.bhanu.photos.network.model.NetworkState;
import agrawal.bhanu.photos.network.model.Status;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageList extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RESULT_LOAD_IMAGE = 110;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.itemlist)
    RecyclerView itemRV;

    ItemsAdapter itemsAdapter;


    @BindView(R.id.errorMSG)
    TextView errorMsg;

    @BindView(R.id.error_layout)
    RelativeLayout errorLayout;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeToRefresh;

    @BindView(R.id.retry)
    TextView retry;

    @BindView(R.id.addfromcamera)
    FloatingActionButton camera;

    @BindView(R.id.addfromgallery)
    FloatingActionButton gallery;

    private Unbinder uibinder;

    private OnFragmentInteractionListener mListener;
    private PostViewModel postViewModel;
    private IntentFilter intentFilter;
    private MyBroadcastReceiver broadcastReceiver;
    private Observer<PagedList<Post>> postObserver;
    private Observer<NetworkState> initialLoadingObserver;
    private Observer<NetworkState> networkObserver;

    public ImageList() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageList.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageList newInstance(String param1, String param2) {
        ImageList fragment = new ImageList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        itemsAdapter = new ItemsAdapter(getActivity().getApplication(), getActivity());
        postViewModel = ViewModelProviders.of(getActivity()).get(PostViewModel.class);
        postObserver = new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(@Nullable final PagedList<Post> pagedList) {
                itemsAdapter.submitList(pagedList);
            }
        };


        networkObserver = new Observer<NetworkState>() {

            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                itemsAdapter.setNetworkState(networkState);
            }
        };


        initialLoadingObserver = new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if(networkState != null){
                    swipeToRefresh.setRefreshing(networkState == NetworkState.LOADING);
                    itemRV.setVisibility(networkState.getStatus() == Status.FAILDED?View.GONE:View.VISIBLE);
                    errorLayout.setVisibility(networkState.getStatus() == Status.FAILDED?View.VISIBLE:View.GONE);

                }

            }
        };

        itemsAdapter.setRetryCallback(new ItemsAdapter.RetryCallback() {
            @Override
            public void retry() {
                postViewModel.retry();
            }
        });

        broadcastReceiver = new MyBroadcastReceiver(new MyBroadcastReceiver.MyBroadcastListner() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("agrawal.bhanu.marplay.IMAGE_UPLOADED")) {
                    postViewModel.onRefresh();
                }
            }
        });
        intentFilter = new IntentFilter("agrawal.bhanu.marplay.IMAGE_UPLOADED");

    }



    private void selectImageFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image_list, container, false);
        uibinder = ButterKnife.bind(this, view);
        itemRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemRV.setAdapter(itemsAdapter);
        swipeToRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postViewModel.onRefresh();
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postViewModel.onRefresh();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainFragment)getParentFragment()).openCamera();
            }
        });


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromGallery();
            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        postViewModel.getInitloading().observe(this,initialLoadingObserver);
        postViewModel.getPostList().observe(getActivity(), postObserver);
        postViewModel.getNetworkState().observe(getActivity(), networkObserver);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void openImagePreviewFragment(Uri path);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        uibinder.unbind();

        postViewModel.getInitloading().removeObserver(initialLoadingObserver);
        postViewModel.getPostList().removeObserver(postObserver);
        postViewModel.getNetworkState().removeObserver(networkObserver);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == RESULT_LOAD_IMAGE){
                Uri returnUri = data.getData();
                mListener.openImagePreviewFragment(returnUri);
            }
        }
    }
}
