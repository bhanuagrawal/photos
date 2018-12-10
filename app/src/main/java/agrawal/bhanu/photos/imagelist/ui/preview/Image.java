package agrawal.bhanu.photos.imagelist.ui.preview;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import agrawal.bhanu.photos.Constants;
import agrawal.bhanu.photos.R;
import agrawal.bhanu.photos.imagelist.PostViewModel;
import agrawal.bhanu.photos.imagelist.datamodel.ImageDTO;
import agrawal.bhanu.photos.imagelist.datamodel.Post;
import androidx.navigation.NavController;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bluemobi.dylan.photoview.library.PhotoView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Image.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Image#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Image extends Fragment implements View.OnTouchListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters


    @BindView(R.id.capturedImage1)
    PhotoView myImage;


    int position;



    private OnFragmentInteractionListener mListener;
    private float mScaleFactor = 1.0f;
    private Unbinder uibinder;
    private PostViewModel postViewModel;
    private PagedList<Post> pagedList;
    private ImageDTO.mode mode;
    private ImageDTO imageDTO;
    private Observer<PagedList<Post>> postObserver;
    private Observer<ImageDTO> imageObserver;

    public Image() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Image newInstance(Bundle args) {
        Image fragment = new Image();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_PARAM1);
            mode = (ImageDTO.mode) getArguments().getSerializable(ARG_PARAM2);
        }

        postViewModel = ViewModelProviders.of(getActivity()).get(PostViewModel.class);

        postObserver = new Observer<PagedList<Post>>() {
            @Override
            public void onChanged(@Nullable final PagedList<Post> pagedList) {
                Image.this.pagedList = pagedList;
                if(mode == ImageDTO.mode.LOAD_FROM_SERVER){
                    loadFileFromServer();
                }
            }
        };


        imageObserver = new Observer<ImageDTO>() {
            @Override
            public void onChanged(@Nullable ImageDTO imageDTO) {
                Image.this.imageDTO = imageDTO;
                loadFile();
            }
        };

    }

    private void loadFileFromServer() {

        final String url = Constants.BASE_URL + pagedList.get(position).getImage();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_thumbnail);

        Glide.with(getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(url).into(myImage);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_image, container, false);
        uibinder = ButterKnife.bind(this, rootView);


        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        postViewModel.getPostList().observe(getActivity(), postObserver);


        postViewModel.getImageLiveData().observe(getActivity(), imageObserver);
    }

    private void loadFile() {



        if(mode == ImageDTO.mode.LOAD_LOCAL_FROM_URI){
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_thumbnail);
            Glide.with(getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(imageDTO.getUri()).into(myImage);

       }
       else if(mode == ImageDTO.mode.LOAD_LOCAL_FROM_PATH){

            final File f = new File(imageDTO.getPath());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_thumbnail);
            Glide.with(getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(f)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(myImage);


        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEnterVerificationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other com.example.bhanu.feedme.fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCofirmUpload(File path);
        void onCancelUpload(String path);
        void onBackPressed();
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        uibinder.unbind();
        postViewModel.getImageLiveData().removeObserver(imageObserver);
        postViewModel.getPostList().removeObserver(postObserver);
    }



    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
}